package com.lyreon.desktop.lyrics

import com.lyreon.desktop.model.LyreonTrack
import com.lyreon.desktop.yt.YouTubeDesktopRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs
import kotlin.math.min

data class LrcLine(val timeMs: Long, val text: String)

data class LyricsResult(
    val synced: List<LrcLine>,
    val plain: String?,
    val instrumental: Boolean,
    val source: String? = null,
) {
    val hasSynced: Boolean get() = synced.isNotEmpty()
    val isEmpty: Boolean get() = !hasSynced && plain.isNullOrBlank() && !instrumental

    fun withSource(name: String): LyricsResult = copy(source = name)
}

object LrcParser {
    private val TIME_TAG = Regex("\\[(\\d{1,2}):(\\d{2})(?:\\.(\\d{1,3}))?]")

    fun parse(raw: String): List<LrcLine> {
        val lines = mutableListOf<LrcLine>()
        raw.lines().forEach { line ->
            val matches = TIME_TAG.findAll(line).toList()
            if (matches.isEmpty()) return@forEach
            val text = line.substring(matches.last().range.last + 1).trim()
            if (text.isEmpty()) return@forEach
            matches.forEach { m ->
                val min = m.groupValues[1].toLongOrNull() ?: 0L
                val sec = m.groupValues[2].toLongOrNull() ?: 0L
                val fracRaw = m.groupValues[3]
                val frac = when (fracRaw.length) {
                    1 -> (fracRaw.toLongOrNull() ?: 0L) * 100L
                    2 -> (fracRaw.toLongOrNull() ?: 0L) * 10L
                    3 -> fracRaw.toLongOrNull() ?: 0L
                    else -> 0L
                }
                lines.add(LrcLine(timeMs = (min * 60L + sec) * 1000L + frac, text = text))
            }
        }
        return lines.sortedBy { it.timeMs }
    }
}

object LyricsRepository {

    private val memoryCache = ConcurrentHashMap<String, LyricsResult>()
    private val client get() = YouTubeDesktopRepository.httpClient

    suspend fun getLyrics(track: LyreonTrack): LyricsResult? = withContext(Dispatchers.IO) {
        memoryCache[track.videoId]?.let { return@withContext it }

        val cleanTitle = cleanTrackTitle(track.title)
        val cleanArtist = track.artist.replace(Regex(" - Topic|Official.*", RegexOption.IGNORE_CASE), "").trim()

        // 1) LRCLIB Provider
        val lrc = fetchLrcLib(cleanTitle, cleanArtist, track.durationSec)
        if (lrc != null && !lrc.isEmpty) {
            memoryCache[track.videoId] = lrc
            return@withContext lrc
        }

        // 2) KuGou Provider (Fallback cakupan luas untuk lagu Asia, Anime, dan Indonesia)
        val kugou = fetchKuGou(cleanTitle, cleanArtist, track.durationSec.toInt())
        if (kugou != null && !kugou.isEmpty) {
            memoryCache[track.videoId] = kugou
            return@withContext kugou
        }

        null
    }

    private fun fetchLrcLib(title: String, artist: String, durationSec: Long): LyricsResult? {
        // 1) Exact match
        val exactUrl = buildString {
            append("https://lrclib.net/api/get?track_name=").append(enc(title))
            if (artist.isNotBlank()) append("&artist_name=").append(enc(artist))
            if (durationSec > 0) append("&duration=").append(durationSec)
        }
        getHttp(exactUrl)?.let { body ->
            if (body.startsWith("{")) {
                runCatching { toResult(JSONObject(body)) }.getOrNull()?.let { return it.withSource("LRCLIB") }
            }
        }

        // 2) Fuzzy search
        val q = if (artist.isBlank()) title else "$title $artist"
        val body = getHttp("https://lrclib.net/api/search?q=" + enc(q)) ?: return null
        if (!body.startsWith("[")) return null
        val arr = runCatching { JSONArray(body) }.getOrNull() ?: return null

        var bestSynced: LyricsResult? = null
        var bestSyncedDiff = Long.MAX_VALUE
        for (i in 0 until arr.length()) {
            val item = arr.optJSONObject(i) ?: continue
            val itemDuration = item.optDouble("duration", 0.0).toLong()
            val synced = item.optString("syncedLyrics")
            if (!synced.isNullOrBlank()) {
                val res = toResult(item) ?: continue
                val diff = if (durationSec > 0 && itemDuration > 0) abs(itemDuration - durationSec) else Long.MAX_VALUE / 2
                if (diff < bestSyncedDiff) {
                    bestSynced = res
                    bestSyncedDiff = diff
                }
            }
        }
        return bestSynced?.withSource("LRCLIB")
    }

    // ------------------------------------------------------------------
    // Jalur KuGou: cari lagu -> ambil kandidat -> unduh LRC (base64)
    // ------------------------------------------------------------------
    private data class KuGouKeyword(val title: String, val artist: String) {
        val query: String get() = "$title - $artist"
    }
    private data class KuGouSongInfo(val hash: String, val duration: Int)
    private data class KuGouCandidate(val id: Long, val accessKey: String)

    private fun fetchKuGou(title: String, artist: String, durationSec: Int): LyricsResult? {
        return runCatching {
            val keyword = generateKuGouKeyword(title, artist)
            val candidate = getKuGouCandidate(keyword, durationSec) ?: return null
            val raw = downloadKuGouLyrics(candidate.id, candidate.accessKey) ?: return null
            val normalized = normalizeKuGou(raw)
            if (normalized.isBlank()) return null
            val lines = LrcParser.parse(normalized)
            if (lines.isEmpty()) return null
            LyricsResult(synced = lines, plain = null, instrumental = false, source = "KuGou")
        }.getOrNull()
    }

    private fun getKuGouCandidate(keyword: KuGouKeyword, durationSec: Int): KuGouCandidate? {
        searchKuGouSongs(keyword).forEach { song ->
            if (durationSec <= 0 || abs(song.duration - durationSec) <= 8) {
                searchKuGouLyrics(byHash = song.hash, keyword = null, durationSec = 0)
                    .firstOrNull()
                    ?.let { return it }
            }
        }
        return searchKuGouLyrics(byHash = null, keyword = keyword, durationSec = durationSec).firstOrNull()
    }

    private fun searchKuGouSongs(keyword: KuGouKeyword): List<KuGouSongInfo> {
        val url = "https://mobileservice.kugou.com/api/v3/search/song?version=9108&plat=0&pagesize=8&showtype=0&keyword=" + enc(keyword.query)
        val body = getHttp(url) ?: return emptyList()
        return runCatching {
            val info = JSONObject(body).optJSONObject("data")?.optJSONArray("info") ?: return emptyList()
            (0 until info.length()).mapNotNull { i ->
                val o = info.optJSONObject(i) ?: return@mapNotNull null
                val hash = o.optString("hash").takeIf { it.isNotBlank() } ?: return@mapNotNull null
                KuGouSongInfo(hash = hash, duration = o.optInt("duration", 0))
            }
        }.getOrDefault(emptyList())
    }

    private fun searchKuGouLyrics(byHash: String?, keyword: KuGouKeyword?, durationSec: Int): List<KuGouCandidate> {
        val url = buildString {
            append("https://lyrics.kugou.com/search?ver=1&man=yes&client=pc")
            if (byHash != null) {
                append("&hash=").append(enc(byHash))
            } else {
                if (durationSec > 0) append("&duration=").append(durationSec * 1000)
                if (keyword != null) append("&keyword=").append(enc(keyword.query))
            }
        }
        val body = getHttp(url) ?: return emptyList()
        return runCatching {
            val arr = JSONObject(body).optJSONArray("candidates") ?: return emptyList()
            (0 until arr.length()).mapNotNull { i ->
                val o = arr.optJSONObject(i) ?: return@mapNotNull null
                val id = o.optLong("id", 0L).takeIf { it > 0L } ?: return@mapNotNull null
                val key = o.optString("accesskey").takeIf { it.isNotBlank() } ?: return@mapNotNull null
                KuGouCandidate(id = id, accessKey = key)
            }
        }.getOrDefault(emptyList())
    }

    private fun downloadKuGouLyrics(id: Long, accessKey: String): String? {
        val url = "https://lyrics.kugou.com/download?fmt=lrc&charset=utf8&client=pc&ver=1&id=$id&accesskey=" + enc(accessKey)
        val body = getHttp(url) ?: return null
        val content = runCatching { JSONObject(body).optString("content") }.getOrNull()
        if (content.isNullOrBlank()) return null
        return runCatching {
            String(java.util.Base64.getDecoder().decode(content), Charsets.UTF_8)
        }.getOrNull()
    }

    private fun generateKuGouKeyword(title: String, artist: String): KuGouKeyword {
        val t = title
            .replace(Regex("\\(.*?\\)|\\[.*?\\]|「.*?」|『.*?』|<.*?>|《.*?》"), "")
            .trim()
        val a = artist
            .replace(", ", "、")
            .replace(" & ", "、")
            .replace(".", "")
            .trim()
        return KuGouKeyword(t.ifBlank { title }, a.ifBlank { artist })
    }

    private fun normalizeKuGou(raw: String): String {
        val acceptedRegex = Regex("\\[(\\d\\d):(\\d\\d)\\.(\\d{2,3})\\].*")
        val bannedRegex = Regex(".+].+[:：].+")
        val lines = raw.lines().filter { it.matches(acceptedRegex) }
        if (lines.isEmpty()) return ""

        var headCut = 0
        for (i in min(30, lines.lastIndex) downTo 0) {
            if (lines[i].matches(bannedRegex)) {
                headCut = i + 1
                break
            }
        }
        val filtered = lines.drop(headCut)
        if (filtered.isEmpty()) return ""

        var tailCut = 0
        for (i in min(filtered.size - 30, filtered.lastIndex) downTo 0) {
            if (filtered[filtered.lastIndex - i].matches(bannedRegex)) {
                tailCut = i + 1
                break
            }
        }
        return filtered.dropLast(tailCut).joinToString("\n")
    }

    private fun toResult(json: JSONObject): LyricsResult? {
        val instrumental = json.optBoolean("instrumental", false)
        val syncedRaw = json.optString("syncedLyrics").takeIf { it.isNotBlank() }
        val plain = json.optString("plainLyrics").takeIf { it.isNotBlank() }
        val lines = syncedRaw?.let { LrcParser.parse(it) } ?: emptyList()
        if (lines.isEmpty() && plain == null && !instrumental) return null
        return LyricsResult(synced = lines, plain = plain, instrumental = instrumental)
    }

    private fun getHttp(url: String): String? = try {
        val req = Request.Builder().url(url).header("User-Agent", "LyreonDesktop/3.5.0").get().build()
        client.newCall(req).execute().use { resp ->
            if (resp.isSuccessful) resp.body?.string() else null
        }
    } catch (_: Exception) {
        null
    }

    private fun enc(s: String) = URLEncoder.encode(s, "UTF-8")

    private fun cleanTrackTitle(title: String): String {
        return title
            .replace(Regex("\\[.*?\\]|\\(.*?\\)"), "")
            .replace(Regex("(?i)official\\s*(music\\s*video|audio|video|lyric\\s*video)?"), "")
            .replace(Regex("(?i)ft\\.?|feat\\.?", RegexOption.IGNORE_CASE), "")
            .trim()
    }
}
