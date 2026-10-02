/*
 * Copyright (C) 2026 rixz-dev
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */
package com.lyreon.app.spotify

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object SpotifyScraper {

    private const val TAG = "SpotifyScraper"

    private const val RAW_SECRET = ",7/*F(\"rLJ2oxaKL^f+E1xvP@N"
    private const val TOTP_VER = "61"
    private const val PERSISTED_QUERY_HASH = "8964e8eafb21aa992a7d951d256d83285c04be2105d209262901de70cb97584a"
    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(25, TimeUnit.SECONDS)
            .followRedirects(true)
            .build()
    }

    private val PLAYLIST_ID_PATTERN = Pattern.compile(
        """(?:spotify(?:\.com)?/(?:[a-zA-Z-]+/)?playlist/|spotify:playlist:)([a-zA-Z0-9]{15,30})"""
    )

    @Volatile
    private var cachedAccessToken: String? = null
    @Volatile
    private var tokenExpiryMs: Long = 0L

    /**
     * Ekstrak Spotify playlist ID dari URL atau URI string.
     */
    fun extractPlaylistId(input: String): String? {
        val trimmed = input.trim()
        if (trimmed.length in 15..30 && trimmed.matches(Regex("^[a-zA-Z0-9]+$"))) {
            return trimmed
        }
        val matcher = PLAYLIST_ID_PATTERN.matcher(trimmed)
        return if (matcher.find()) {
            matcher.group(1)
        } else {
            null
        }
    }

    private fun getSecretBytes(): ByteArray {
        val sb = StringBuilder()
        for (i in RAW_SECRET.indices) {
            val c = RAW_SECRET[i]
            val xorVal = (c.code xor ((i % 33) + 9))
            sb.append(xorVal)
        }
        return sb.toString().toByteArray(Charsets.UTF_8)
    }

    private fun generateTotp(secretBytes: ByteArray, timestampMs: Long = System.currentTimeMillis()): String {
        val counter = timestampMs / 1000L / 30L
        val buffer = ByteBuffer.allocate(8).putLong(counter)
        val mac = Mac.getInstance("HmacSHA1")
        mac.init(SecretKeySpec(secretBytes, "HmacSHA1"))
        val hash = mac.doFinal(buffer.array())
        val offset = hash[hash.size - 1].toInt() and 0x0F
        val binary = ((hash[offset].toInt() and 0x7F) shl 24) or
                ((hash[offset + 1].toInt() and 0xFF) shl 16) or
                ((hash[offset + 2].toInt() and 0xFF) shl 8) or
                (hash[offset + 3].toInt() and 0xFF)
        val otp = binary % 1_000_000
        return String.format("%06d", otp)
    }

    private fun getAccessToken(): String {
        val now = System.currentTimeMillis()
        val current = cachedAccessToken
        if (current != null && now < tokenExpiryMs - 60_000L) {
            return current
        }
        synchronized(this) {
            val checkNow = System.currentTimeMillis()
            if (cachedAccessToken != null && checkNow < tokenExpiryMs - 60_000L) {
                return cachedAccessToken!!
            }
            val totp = generateTotp(getSecretBytes(), checkNow)
            val tokenUrl = "https://open.spotify.com/api/token?reason=transport&productType=web-player&totp=$totp&totpServer=unavailable&totpVer=$TOTP_VER"
            val req = Request.Builder()
                .url(tokenUrl)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                .header("Accept", "application/json")
                .header("Referer", "https://open.spotify.com/")
                .header("Origin", "https://open.spotify.com")
                .build()

            val res = client.newCall(req).execute()
            if (!res.isSuccessful) {
                throw IllegalStateException("HTTP ${res.code}: gagal mendapatkan token otentikasi Spotify")
            }
            val json = JSONObject(res.body?.string().orEmpty())
            val token = json.getString("accessToken")
            val exp = json.optLong("accessTokenExpirationTimestampMs", checkNow + 3600_000L)
            cachedAccessToken = token
            tokenExpiryMs = exp
            return token
        }
    }

    /**
     * Mengambil seluruh lagu playlist via Spotify Pathfinder GraphQL API (tanpa batas 100 lagu).
     */
    private fun fetchPlaylistPathfinder(
        playlistId: String,
        onProgress: ((loaded: Int, total: Int) -> Unit)? = null
    ): SpotifyPlaylist {
        val token = getAccessToken()
        val uri = "spotify:playlist:$playlistId"
        val queryUrl = "https://api-partner.spotify.com/pathfinder/v2/query"

        var offset = 0
        val limit = 200
        var total: Int? = null
        var name = ""
        var owner = ""
        var coverUrl = ""
        val tracks = mutableListOf<SpotifyTrack>()

        while (total == null || offset < total) {
            val variables = JSONObject().apply {
                put("uri", uri)
                put("offset", offset)
                put("limit", limit)
                put("enableWatchFeedEntrypoint", false)
                put("includeEpisodeContentRatingsV2", true)
            }
            val extensions = JSONObject().apply {
                put("persistedQuery", JSONObject().apply {
                    put("version", 1)
                    put("sha256Hash", PERSISTED_QUERY_HASH)
                })
            }
            val bodyJson = JSONObject().apply {
                put("variables", variables)
                put("operationName", "fetchPlaylist")
                put("extensions", extensions)
            }

            val req = Request.Builder()
                .url(queryUrl)
                .header("Authorization", "Bearer $token")
                .header("Content-Type", "application/json")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                .header("Origin", "https://open.spotify.com")
                .header("Referer", "https://open.spotify.com/")
                .post(bodyJson.toString().toRequestBody(JSON_MEDIA_TYPE))
                .build()

            val res = client.newCall(req).execute()
            if (!res.isSuccessful) {
                throw IllegalStateException("HTTP ${res.code}: gagal memuat data trek Spotify via Pathfinder")
            }

            val resJson = JSONObject(res.body?.string().orEmpty())
            val plObj = resJson.optJSONObject("data")?.optJSONObject("playlistV2")
                ?: throw IllegalStateException("Playlist Spotify tidak ditemukan atau berstatus privat")

            if (offset == 0) {
                name = plObj.optString("name").ifBlank { "Spotify Playlist" }
                owner = plObj.optJSONObject("ownerV2")?.optJSONObject("data")?.optString("name")
                    ?: plObj.optJSONObject("members")?.optJSONArray("items")?.optJSONObject(0)?.optJSONObject("user")?.optJSONObject("data")?.optString("name")
                    ?: "Spotify"
                val imageItems = plObj.optJSONObject("images")?.optJSONArray("items")
                if (imageItems != null && imageItems.length() > 0) {
                    val sources = imageItems.optJSONObject(0)?.optJSONArray("sources")
                    coverUrl = sources?.optJSONObject(0)?.optString("url").orEmpty()
                }
            }

            val content = plObj.optJSONObject("content") ?: break
            if (total == null) {
                total = content.optInt("totalCount", 0)
            }

            val items = content.optJSONArray("items") ?: break
            if (items.length() == 0) break

            for (i in 0 until items.length()) {
                val itemObj = items.optJSONObject(i) ?: continue
                val itemV2 = itemObj.optJSONObject("itemV2") ?: continue
                val trackData = itemV2.optJSONObject("data") ?: continue
                val title = trackData.optString("name").trim()
                if (title.isBlank()) continue

                val artistsArr = trackData.optJSONObject("artists")?.optJSONArray("items")
                val artistNames = mutableListOf<String>()
                if (artistsArr != null) {
                    for (a in 0 until artistsArr.length()) {
                        val artName = artistsArr.optJSONObject(a)?.optJSONObject("profile")?.optString("name")
                        if (!artName.isNullOrBlank()) {
                            artistNames.add(artName)
                        }
                    }
                }
                val artist = artistNames.joinToString(", ").ifBlank { "Unknown Artist" }
                val durationMs = trackData.optJSONObject("trackDuration")?.optLong("totalMilliseconds", 0L) ?: 0L
                val durationSec = (durationMs / 1000L).toInt()
                val trackUri = trackData.optString("uri")

                tracks.add(
                    SpotifyTrack(
                        title = title,
                        artist = artist,
                        durationSec = durationSec,
                        uri = trackUri
                    )
                )
            }

            offset += items.length()
            onProgress?.invoke(tracks.size, total ?: tracks.size)
        }

        Log.d(TAG, "Pathfinder: Berhasil memuat '${name}' total ${tracks.size} lagu (dari $total)")
        return SpotifyPlaylist(
            id = playlistId,
            name = name,
            coverUrl = coverUrl,
            author = owner,
            tracks = tracks
        )
    }

    /**
     * Fallback parsing melalui embed Spotify jika Pathfinder mengalami kendala.
     */
    private fun fetchPlaylistEmbed(playlistId: String): SpotifyPlaylist {
        val embedUrl = "https://open.spotify.com/embed/playlist/$playlistId"
        val request = Request.Builder()
            .url(embedUrl)
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
            .header("Accept-Language", "en-US,en;q=0.9")
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IllegalStateException("HTTP ${response.code}: gagal memuat playlist Spotify ($embedUrl)")
        }

        val html = response.body?.string().orEmpty()
        val tagMarker = "id=\"__NEXT_DATA__\""
        val tagIndex = html.indexOf(tagMarker)
        if (tagIndex == -1) {
            throw IllegalStateException("Struktur halaman Spotify tidak dikenali (NEXT_DATA tidak ditemukan)")
        }

        val start = html.indexOf('>', tagIndex) + 1
        val end = html.indexOf("</script>", start)
        if (start <= 0 || end <= start) {
            throw IllegalStateException("Gagal mengekstrak script payload dari halaman Spotify")
        }

        val jsonStr = html.substring(start, end)
        val root = JSONObject(jsonStr)
        val pageProps = root.optJSONObject("props")?.optJSONObject("pageProps")
            ?: throw IllegalStateException("Payload Spotify kosong (props.pageProps missing)")

        val status = pageProps.optInt("status", 200)
        if (status == 404) {
            throw IllegalArgumentException("Playlist Spotify tidak ditemukan (404 / privat)")
        }

        val entity = pageProps.optJSONObject("state")?.optJSONObject("data")?.optJSONObject("entity")
            ?: throw IllegalStateException("Entitas playlist Spotify tidak ditemukan di data payload")

        val name = entity.optString("title").ifBlank { entity.optString("name").ifBlank { "Spotify Playlist" } }
        val author = entity.optString("subtitle").ifBlank { "Spotify" }

        val coverSources = entity.optJSONObject("coverArt")?.optJSONArray("sources")
        val coverUrl = coverSources?.optJSONObject(0)?.optString("url").orEmpty()

        val rawTracks = entity.optJSONArray("trackList") ?: org.json.JSONArray()
        val tracks = mutableListOf<SpotifyTrack>()

        for (i in 0 until rawTracks.length()) {
            val item = rawTracks.optJSONObject(i) ?: continue
            val title = item.optString("title").trim()
            if (title.isBlank()) continue

            val rawSubtitle = item.optString("subtitle")
            val artist = rawSubtitle.replace("\u00a0", " ").trim()
            val durationMs = item.optLong("duration", 0L)
            val durationSec = (durationMs / 1000L).toInt()
            val uri = item.optString("uri")

            tracks.add(
                SpotifyTrack(
                    title = title,
                    artist = artist,
                    durationSec = durationSec,
                    uri = uri,
                )
            )
        }

        Log.d(TAG, "Embed Fallback: Berhasil memuat '${name}' dengan ${tracks.size} lagu")
        return SpotifyPlaylist(
            id = playlistId,
            name = name,
            coverUrl = coverUrl,
            author = author,
            tracks = tracks,
        )
    }

    /**
     * Mengambil metadata & seluruh daftar lagu playlist Spotify (> 100 lagu didukung penuh).
     */
    suspend fun fetchPlaylist(
        playlistId: String,
        onProgress: ((loaded: Int, total: Int) -> Unit)? = null
    ): SpotifyPlaylist = withContext(Dispatchers.IO) {
        try {
            fetchPlaylistPathfinder(playlistId, onProgress)
        } catch (e: Exception) {
            Log.w(TAG, "Pathfinder query gagal (${e.message}), beralih ke embed fallback", e)
            fetchPlaylistEmbed(playlistId)
        }
    }
}
