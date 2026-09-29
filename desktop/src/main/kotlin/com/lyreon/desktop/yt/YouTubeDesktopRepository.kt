package com.lyreon.desktop.yt

import com.lyreon.desktop.core.LyreonLog
import com.lyreon.desktop.model.LyreonTrack
import com.lyreon.desktop.model.SearchFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.net.URLDecoder
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

enum class AudioQuality {
    HIGH,
    BALANCED,
    DATA_SAVER
}

data class ResolvedAudio(
    val videoId: String,
    val url: String,
    val mimeType: String,
    val suffix: String,
    val bitrateKbps: Int,
    val expiresAtMs: Long,
    val fallbackUrl: String? = null,
    val isManifest: Boolean = false,
    val contentLength: Long = 0L,
    val loudnessDb: Float? = null,
    val clientKey: String = "",
    val validated: Boolean = true,
)

class StreamUnavailableException(
    message: String,
    val sabrOnly: Boolean = false,
    val hlsOnly: Boolean = false,
    val playability: String = "",
    val attempts: List<String> = emptyList(),
    val drmOnly: Boolean = false,
    val cdnRejected: Boolean = false,
) : IOException(message)

object YouTubeDesktopRepository {

    private const val TAG = "YouTubeDesktopRepo"

    val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    private val streamCache = ConcurrentHashMap<String, ResolvedAudio>()

    private fun ensureReady() {
        InnertubeConfig.ensure(httpClient, PlayerClientLadder.WEB_UA_FIREFOX)
        InnertubeConfig.ensureVisitorData(httpClient, PlayerClientLadder.WEB_UA_FIREFOX)
    }

    val curatedInitialTracks: List<LyreonTrack> = listOf(
        LyreonTrack(
            videoId = "yjnSX_iUFVo",
            title = "Satu Bulan",
            artist = "Bernadya",
            album = "Sialnya, Hidup Harus Tetap Berjalan",
            durationSec = 194L,
            thumbnailUrl = "https://i.ytimg.com/vi/yjnSX_iUFVo/hqdefault.jpg"
        ),
        LyreonTrack(
            videoId = "AQpEIZ8dNcU",
            title = "Gala Bunga Matahari",
            artist = "Sal Priadi",
            album = "MARKERS AND SUCH PENS FLASHDISKS",
            durationSec = 227L,
            thumbnailUrl = "https://i.ytimg.com/vi/AQpEIZ8dNcU/hqdefault.jpg"
        ),
        LyreonTrack(
            videoId = "x3bfa3DZ8JM",
            title = "Secukupnya",
            artist = "Hindia",
            album = "Menari Dengan Bayangan",
            durationSec = 206L,
            thumbnailUrl = "https://i.ytimg.com/vi/x3bfa3DZ8JM/hqdefault.jpg"
        ),
        LyreonTrack(
            videoId = "gIsoLyQX7W8",
            title = "Rayuan Perempuan Gila",
            artist = "Nadin Amizah",
            album = "Untuk Dunia, Cinta, dan Kotornya",
            durationSec = 296L,
            thumbnailUrl = "https://i.ytimg.com/vi/gIsoLyQX7W8/hqdefault.jpg"
        ),
        LyreonTrack(
            videoId = "gNi_6U5Pm_o",
            title = "Hati-Hati di Jalan",
            artist = "Tulus",
            album = "Manusia",
            durationSec = 242L,
            thumbnailUrl = "https://i.ytimg.com/vi/gNi_6U5Pm_o/hqdefault.jpg"
        ),
        LyreonTrack(
            videoId = "LAOxuo6pkdo",
            title = "Mati-Matian",
            artist = "Mahalini",
            album = "FABULA",
            durationSec = 239L,
            thumbnailUrl = "https://i.ytimg.com/vi/LAOxuo6pkdo/hqdefault.jpg"
        ),
        LyreonTrack(
            videoId = "ba-XAIskH_g",
            title = "Lantas",
            artist = "Juicy Luicy",
            album = "Sentimental",
            durationSec = 231L,
            thumbnailUrl = "https://i.ytimg.com/vi/ba-XAIskH_g/hqdefault.jpg"
        ),
        LyreonTrack(
            videoId = "dGcGbF4ex5o",
            title = "Dan...",
            artist = "Sheila On 7",
            album = "Sheila on 7",
            durationSec = 286L,
            thumbnailUrl = "https://i.ytimg.com/vi/dGcGbF4ex5o/hqdefault.jpg"
        ),
        LyreonTrack(
            videoId = "YrtS8MESh0I",
            title = "Runtuh",
            artist = "Feby Putri, Fiersa Besari",
            album = "Riuh",
            durationSec = 224L,
            thumbnailUrl = "https://i.ytimg.com/vi/YrtS8MESh0I/hqdefault.jpg"
        ),
        LyreonTrack(
            videoId = "kYv9I443L9k",
            title = "To the Bone",
            artist = "Pamungkas",
            album = "Flying Solo",
            durationSec = 344L,
            thumbnailUrl = "https://i.ytimg.com/vi/kYv9I443L9k/hqdefault.jpg"
        ),
        LyreonTrack(
            videoId = "t9VWICGOD90",
            title = "Jiwa Yang Bersedih",
            artist = "Ghea Indrawari",
            album = "Berdamai",
            durationSec = 278L,
            thumbnailUrl = "https://i.ytimg.com/vi/t9VWICGOD90/hqdefault.jpg"
        ),
        LyreonTrack(
            videoId = "W5OPDlDJ2gk",
            title = "Risalah Hati",
            artist = "Yura Yunita",
            album = "Tutur Batin",
            durationSec = 293L,
            thumbnailUrl = "https://i.ytimg.com/vi/W5OPDlDJ2gk/hqdefault.jpg"
        ),
    )

    suspend fun search(query: String, filter: SearchFilter = SearchFilter.ALL): List<LyreonTrack> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        ensureReady()

        val visitor = InnertubeConfig.visitor()
        val webVersion = InnertubeConfig.webClientVersion()

        // 1. Coba pencarian WEB standard YouTube (paling stabil & cepat)
        val webParam = when (filter) {
            SearchFilter.SONGS -> "EgWKAQIIAWoKEAkQBRAKEAMQBA=="
            SearchFilter.ARTISTS -> "EgWKAQIgAWoKEAkQChAFEAMQBA=="
            SearchFilter.ALBUMS -> "EgWKAQIYAWoKEAkQChAFEAMQBA=="
            SearchFilter.PLAYLISTS -> "EgeKAQQoAEABagoQAxAEEAoQCRAF"
            SearchFilter.ALL -> null
        }
        try {
            val reqWeb = InnertubeRequest.search(
                client = InnertubeRequest.Client.WEB,
                version = webVersion,
                query = query,
                params = webParam,
                visitorData = visitor,
            )
            httpClient.newCall(reqWeb).execute().use { resp ->
                if (resp.isSuccessful) {
                    val body = resp.body?.string().orEmpty()
                    if (body.isNotBlank()) {
                        val root = JSONObject(body)
                        InnertubeConfig.adoptVisitor(root)
                        val tracks = parseSearchWeb(root, 25)
                        if (tracks.isNotEmpty()) return@withContext tracks
                    }
                }
            }
        } catch (e: Exception) {
            LyreonLog.w(TAG, "Search WEB error: ${e.message}")
        }

        // 2. Jika pencarian dengan filter spesifik kosong, otomatis coba tanpa parameter (query murni)
        if (webParam != null) {
            try {
                val reqWebFallback = InnertubeRequest.search(
                    client = InnertubeRequest.Client.WEB,
                    version = webVersion,
                    query = query,
                    params = null,
                    visitorData = visitor,
                )
                httpClient.newCall(reqWebFallback).execute().use { resp ->
                    if (resp.isSuccessful) {
                        val body = resp.body?.string().orEmpty()
                        if (body.isNotBlank()) {
                            val root = JSONObject(body)
                            InnertubeConfig.adoptVisitor(root)
                            val tracks = parseSearchWeb(root, 25)
                            if (tracks.isNotEmpty()) return@withContext tracks
                        }
                    }
                }
            } catch (e: Exception) {
                LyreonLog.w(TAG, "Search WEB fallback error: ${e.message}")
            }
        }

        // 3. Cadangan: WEB_REMIX (YouTube Music)
        val remixParam = when (filter) {
            SearchFilter.ALL -> null
            SearchFilter.SONGS -> "Eg-KAQwIABAAGAAgACgB"
            SearchFilter.ARTISTS -> "Eg-KAQwIAhABGAAgACgB"
            SearchFilter.ALBUMS -> "Eg-KAQwIAxABGAAgACgB"
            SearchFilter.PLAYLISTS -> "Eg-KAQwIBAABGAAgACgB"
        }
        try {
            val request = InnertubeRequest.search(
                client = InnertubeRequest.Client.WEB_REMIX,
                version = webVersion,
                query = query,
                params = remixParam,
                visitorData = visitor,
            )
            httpClient.newCall(request).execute().use { resp ->
                if (resp.isSuccessful) {
                    val body = resp.body?.string().orEmpty()
                    if (body.isNotBlank()) {
                        val root = JSONObject(body)
                        InnertubeConfig.adoptVisitor(root)
                        val summary = MusicSearchParser.parse(root)
                        if (summary.songs.isNotEmpty()) return@withContext summary.songs
                    }
                }
            }
        } catch (e: Exception) {
            LyreonLog.w(TAG, "Search REMIX error: ${e.message}")
        }

        emptyList()
    }

    private fun parseSearchWeb(root: JSONObject, limit: Int): List<LyreonTrack> {
        val out = ArrayList<LyreonTrack>(limit)
        val contents = root.optJSONObject("contents")
            ?.optJSONObject("twoColumnSearchResultsRenderer")
            ?.optJSONObject("primaryContents")
            ?.optJSONObject("sectionListRenderer")
            ?.optJSONArray("contents") ?: return out

        fun textOf(node: JSONObject?, key: String): String {
            if (node == null) return ""
            val runs = node.optJSONArray("runs") ?: return node.optString("simpleText")
            val sb = StringBuilder()
            for (i in 0 until runs.length()) sb.append(runs.optJSONObject(i)?.optString("text").orEmpty())
            return sb.toString()
        }

        fun extractTrack(vr: JSONObject?): LyreonTrack? {
            if (vr == null) return null
            val videoId = vr.optString("videoId").orEmpty()
            if (videoId.isBlank() || videoId.length !in 6..20) return null
            val title = textOf(vr.optJSONObject("title"), "text").ifBlank { return null }
            val thumb = vr.optJSONObject("thumbnail")?.optJSONArray("thumbnails")
                ?.let { arr -> if (arr.length() > 0) arr.optJSONObject(arr.length() - 1)?.optString("url").orEmpty() else "" }
                .orEmpty()
            val duration = parseDuration(textOf(vr.optJSONObject("lengthText"), "simpleText"))
            val artist = vr.optJSONObject("ownerText")?.let { textOf(it, "runs") }.orEmpty()
            return LyreonTrack(
                videoId = videoId,
                title = title,
                artist = artist,
                durationSec = duration,
                thumbnailUrl = if (thumb.isNotBlank()) thumb else "https://i.ytimg.com/vi/$videoId/hqdefault.jpg",
            )
        }

        for (s in 0 until contents.length()) {
            val itemSection = contents.optJSONObject(s)
                ?.optJSONObject("itemSectionRenderer")
                ?.optJSONArray("contents") ?: continue
            for (c in 0 until itemSection.length()) {
                val item = itemSection.optJSONObject(c) ?: continue
                val vr = item.optJSONObject("videoRenderer") ?: item.optJSONObject("compactVideoRenderer")
                if (vr != null) {
                    extractTrack(vr)?.let {
                        out += it
                        if (out.size >= limit) return out
                    }
                } else {
                    // Check shelfRenderer
                    val shelfContents = item.optJSONObject("shelfRenderer")
                        ?.optJSONObject("content")
                        ?.optJSONObject("verticalListRenderer")
                        ?.optJSONArray("items")
                    if (shelfContents != null) {
                        for (k in 0 until shelfContents.length()) {
                            val svr = shelfContents.optJSONObject(k)?.optJSONObject("videoRenderer")
                            extractTrack(svr)?.let {
                                out += it
                                if (out.size >= limit) return out
                            }
                        }
                    }
                }
            }
        }
        return out
    }


    private fun parseDuration(raw: String): Long {
        if (raw.isBlank()) return 0L
        val parts = raw.split(":").mapNotNull { it.trim().toLongOrNull() }
        return when (parts.size) {
            3 -> parts[0] * 3600L + parts[1] * 60L + parts[2]
            2 -> parts[0] * 60L + parts[1]
            1 -> parts[0]
            else -> 0L
        }
    }

    suspend fun getQuickPicks(): List<LyreonTrack> = withContext(Dispatchers.IO) {
        try {
            val res = search("lagu pop indonesia terpopuler 2026", SearchFilter.SONGS)
            if (res.isNotEmpty()) {
                return@withContext (res + curatedInitialTracks).distinctBy { it.videoId }
            }
        } catch (e: Exception) {
            LyreonLog.w(TAG, "getQuickPicks error: ${e.message}")
        }
        curatedInitialTracks
    }

    suspend fun resolveStream(track: LyreonTrack, quality: AudioQuality = AudioQuality.HIGH): ResolvedAudio = withContext(Dispatchers.IO) {
        val cached = streamCache[track.videoId]
        val now = System.currentTimeMillis()
        if (cached != null && cached.expiresAtMs > now + 300_000L) {
            return@withContext cached
        }

        ensureReady()
        val audio = fetchPlayer(track.videoId, quality)
        streamCache[track.videoId] = audio
        audio
    }

    private fun fetchPlayer(videoId: String, quality: AudioQuality): ResolvedAudio {
        var visitor = InnertubeConfig.visitor()
        val webVersion = InnertubeConfig.webClientVersion()
        val sts = InnertubeConfig.signatureTimestamp()

        val ladder = PlayerClientLadder.ordered(forPlayback = true)
        val attempts = ArrayList<String>()
        var sawSabr = false
        var sawHls = false
        var sawDrm = false
        var sawCdnReject = false
        var playability = ""
        var lastResort: ResolvedAudio? = null
        var lastResortClient: String? = null

        for (spec in ladder) {
            val request = InnertubeRequest.playerFromSpec(spec, videoId, visitor, webVersion, sts)
            val attempt = executePlayer(request, videoId)
            if (visitor == null && InnertubeConfig.adoptVisitor(attempt.root)) {
                visitor = InnertubeConfig.visitor()
            }

            var verdict = attempt.verdict
            val detail = attempt.detail
            val streamingData = attempt.root?.optJSONObject("streamingData")
            val hlsUrl = PlayerClientLadder.hlsManifest(attempt.root)
            if (hlsUrl != null) sawHls = true

            when (verdict) {
                ClientVerdict.SABR_ONLY -> sawSabr = true
                ClientVerdict.DRM_ONLY -> sawDrm = true
                ClientVerdict.PLAYABILITY_BLOCKED -> playability = detail
                ClientVerdict.USABLE -> {
                    val picked = streamingData?.let { pickAudio(it, quality) }
                    if (picked != null) {
                        val probe = StreamUrlValidator.validate(picked.url, picked.contentLength)
                        if (probe.accepted) {
                            LyreonLog.i(TAG, "Stream validated via client '${spec.key}' (code ${probe.code}, ${picked.bitrateKbps}kbps, ${picked.suffix})")
                            return picked.copy(
                                videoId = videoId,
                                clientKey = spec.key,
                                validated = true,
                            )
                        } else {
                            sawCdnReject = true
                            if (lastResort == null) {
                                lastResort = picked.copy(videoId = videoId, clientKey = spec.key)
                                lastResortClient = spec.key
                            }
                            attempts += "${spec.key}=CDN_REJECT(${probe.code})"
                        }
                    } else {
                        attempts += "${spec.key}=NO_AUDIO"
                    }
                }
                else -> {
                    attempts += "${spec.key}=$verdict"
                }
            }
        }

        lastResort?.let {
            LyreonLog.w(TAG, "Using unvalidated stream from $lastResortClient as fallback")
            return it.copy(validated = false)
        }

        val reason = "Tidak ada stream audio yang dapat diputar untuk video $videoId (attempts: ${attempts.joinToString()})"
        LyreonLog.e(TAG, reason)
        throw StreamUnavailableException(
            message = reason,
            sabrOnly = sawSabr,
            hlsOnly = sawHls,
            playability = playability,
            attempts = attempts,
            drmOnly = sawDrm,
            cdnRejected = sawCdnReject,
        )
    }

    private class ClientAttempt(val verdict: ClientVerdict, val root: JSONObject?, val detail: String)

    private fun executePlayer(request: Request, videoId: String): ClientAttempt = try {
        httpClient.newCall(request).execute().use { resp ->
            if (!resp.isSuccessful) {
                ClientAttempt(ClientVerdict.TRANSPORT_ERROR, null, "HTTP ${resp.code}")
            } else {
                val body = resp.body?.string().orEmpty()
                val root = if (body.isBlank()) null else runCatching { JSONObject(body) }.getOrNull()
                if (root == null) {
                    ClientAttempt(ClientVerdict.TRANSPORT_ERROR, null, "body bukan JSON")
                } else {
                    val verdict = PlayerClientLadder.inspect(root, videoId)
                    val detail = if (verdict == ClientVerdict.PLAYABILITY_BLOCKED) PlayerClientLadder.playabilityReason(root) else ""
                    ClientAttempt(verdict, root, detail)
                }
            }
        }
    } catch (e: Exception) {
        ClientAttempt(ClientVerdict.TRANSPORT_ERROR, null, e.javaClass.simpleName)
    }

    private fun pickAudio(sd: JSONObject, quality: AudioQuality): ResolvedAudio? {
        val formats = sd.optJSONArray("adaptiveFormats") ?: sd.optJSONArray("formats") ?: return null
        val baseJs = InnertubeConfig.baseJsUrl()?.let { fetchBaseJs(it) }

        data class Candidate(
            val url: String,
            val mime: String,
            val suffix: String,
            val bitrate: Int,
            val contentLength: Long,
            val loudnessDb: Float?,
        )

        val candidates = ArrayList<Candidate>(formats.length())
        for (i in 0 until formats.length()) {
            val f = formats.optJSONObject(i) ?: continue
            val mime = f.optString("mimeType").orEmpty()
            if (!mime.startsWith("audio/")) continue
            if (PlayerClientLadder.isDrmLocked(f)) continue

            var url = f.optString("url").orEmpty()
            if (url.isBlank()) {
                val cipher = f.optString("signatureCipher").ifBlank { f.optString("cipher") }
                url = resolveCipher(cipher, baseJs) ?: continue
            }
            if (url.isBlank()) continue

            val bitrate = f.optInt("bitrate", 0)
            val contentLength = f.optString("contentLength").toLongOrNull() ?: 0L
            val loudnessDb = ((f.opt("loudnessDb") ?: f.opt("perceptualLoudnessDb")) as? Number)?.toFloat()
            val suffix = if (mime.contains("mp4") || mime.contains("m4a")) "m4a" else "webm"

            candidates.add(
                Candidate(
                    url = url,
                    mime = mime,
                    suffix = suffix,
                    bitrate = bitrate,
                    contentLength = contentLength,
                    loudnessDb = loudnessDb,
                )
            )
        }
        if (candidates.isEmpty()) return null

        // Untuk desktop, utamakan format m4a (AAC) karena didukung 100% oleh Windows Media Foundation/JavaFX Media
        val chosen = candidates.filter { it.suffix == "m4a" }.maxByOrNull { it.bitrate }
            ?: candidates.maxByOrNull { it.bitrate }
            ?: candidates.first()

        val bitrateKbps = (chosen.bitrate.takeIf { it > 0 } ?: 128_000) / 1000
        val expiresAtMs = parseExpire(chosen.url)

        return ResolvedAudio(
            videoId = "",
            url = chosen.url,
            mimeType = chosen.mime,
            suffix = chosen.suffix,
            bitrateKbps = bitrateKbps,
            expiresAtMs = expiresAtMs,
            contentLength = chosen.contentLength,
            loudnessDb = chosen.loudnessDb,
        )
    }

    private fun resolveCipher(cipher: String, baseJs: String?): String? {
        if (cipher.isBlank()) return null
        val params = parseQuery(cipher)
        val url = params["url"] ?: return null
        var s = params["s"] ?: return url
        val sp = params["sp"] ?: "sig"
        if (baseJs != null) {
            s = SignatureDecipher.decipherS(baseJs, s) ?: return null
        }
        val delim = if (url.contains("?")) "&" else "?"
        return "$url$delim$sp=$s"
    }

    private fun parseQuery(query: String): Map<String, String> {
        val map = HashMap<String, String>()
        for (pair in query.split("&")) {
            val idx = pair.indexOf('=')
            if (idx > 0) {
                val k = URLDecoder.decode(pair.substring(0, idx), "UTF-8")
                val v = URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
                map[k] = v
            }
        }
        return map
    }

    private val baseJsCache = ConcurrentHashMap<String, String>()

    private fun fetchBaseJs(url: String): String? = baseJsCache.getOrPut(url) {
        runCatching {
            val req = Request.Builder().url(url).header("User-Agent", PlayerClientLadder.WEB_UA_FIREFOX).build()
            httpClient.newCall(req).execute().use { resp ->
                if (resp.isSuccessful) resp.body?.string().orEmpty() else ""
            }
        }.getOrNull().orEmpty()
    }.takeIf { it.isNotBlank() }

    private fun parseExpire(url: String): Long =
        Regex("[?&]expire=(\\d+)").find(url)?.groupValues?.getOrNull(1)?.toLongOrNull()?.times(1000L)
            ?: (System.currentTimeMillis() + 6L * 3600_000L)
}
