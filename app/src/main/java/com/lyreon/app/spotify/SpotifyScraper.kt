/*
 * Copyright (C) 2026 rixz-dev
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */
package com.lyreon.app.spotify

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

object SpotifyScraper {

    private const val TAG = "SpotifyScraper"

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .followRedirects(true)
            .build()
    }

    private val PLAYLIST_ID_PATTERN = Pattern.compile(
        """(?:spotify(?:\.com)?/(?:[a-zA-Z-]+/)?playlist/|spotify:playlist:)([a-zA-Z0-9]{15,30})"""
    )

    /**
     * Ekstrak Spotify playlist ID dari URL atau URI string.
     * Mendukung:
     * - https://open.spotify.com/playlist/37i9dQZF1DXcBWIGoYBM5M?si=...
     * - https://open.spotify.com/intl-id/playlist/37i9dQZF1DXcBWIGoYBM5M
     * - spotify:playlist:37i9dQZF1DXcBWIGoYBM5M
     * - 37i9dQZF1DXcBWIGoYBM5M (raw ID)
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

    /**
     * Mengambil metadata playlist dari halaman embed resmi Spotify (tanpa login / token berbayar).
     */
    suspend fun fetchPlaylist(playlistId: String): SpotifyPlaylist = withContext(Dispatchers.IO) {
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
            // Bersihkan non-breaking spaces (\u00a0) dan entity
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

        Log.d(TAG, "fetchPlaylist: berhasil memuat '${name}' dengan ${tracks.size} lagu")
        SpotifyPlaylist(
            id = playlistId,
            name = name,
            coverUrl = coverUrl,
            author = author,
            tracks = tracks,
        )
    }
}
