/*
 * Copyright (C) 2026 rixz-dev
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */
package com.lyreon.app.spotify

import android.util.Log
import com.lyreon.app.data.model.LyreonTrack
import com.lyreon.app.data.model.SearchFilter
import com.lyreon.app.data.taste.MusicTextAnalyzer
import com.lyreon.app.yt.YouTubeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs

object SpotifyMatcher {

    private const val TAG = "SpotifyMatcher"

    /**
     * Mencocokkan satu SpotifyTrack ke LyreonTrack (YouTube Music).
     */
    suspend fun matchTrack(
        ytRepo: YouTubeRepository,
        spotifyTrack: SpotifyTrack,
    ): LyreonTrack? = withContext(Dispatchers.IO) {
        val query = "${spotifyTrack.artist} ${spotifyTrack.title}".trim()
        if (query.isBlank()) return@withContext null

        // 1. Coba pencarian YouTube Music (musicSearch - format lagu resmi)
        val musicSummary = runCatching {
            ytRepo.musicSearch(query, SearchFilter.SONGS)
        }.getOrNull()

        val candidates = mutableListOf<LyreonTrack>()
        if (musicSummary != null && musicSummary.songs.isNotEmpty()) {
            candidates.addAll(musicSummary.songs)
        }

        // 2. Fallback pencarian biasa bila musicSearch tidak menemukan hasil
        if (candidates.isEmpty()) {
            val fallbackTracks = runCatching {
                ytRepo.searchTracks(query, 20)
            }.getOrNull()
            if (!fallbackTracks.isNullOrEmpty()) {
                candidates.addAll(fallbackTracks)
            }
        }

        if (candidates.isEmpty()) {
            Log.w(TAG, "matchTrack: tidak ada kandidat ditemukan untuk '$query'")
            return@withContext null
        }

        // 3. Cari kandidat terbaik dengan scoring judul & durasi
        val targetCore = MusicTextAnalyzer.coreTitle(spotifyTrack.title).lowercase()
        val targetArtist = spotifyTrack.artist.lowercase()

        var bestCandidate = candidates.first()
        var bestScore = -1

        for (candidate in candidates) {
            var score = 0
            val candidateCore = MusicTextAnalyzer.coreTitle(candidate.title).lowercase()
            val candidateArtist = candidate.artist.lowercase()

            // Kesesuaian judul
            if (candidateCore == targetCore) {
                score += 50
            } else if (candidateCore.contains(targetCore) || targetCore.contains(candidateCore)) {
                score += 30
            }

            // Kesesuaian artis
            if (candidateArtist.contains(targetArtist) || targetArtist.contains(candidateArtist)) {
                score += 30
            }

            // Kesesuaian durasi (toleransi ±15 detik)
            if (spotifyTrack.durationSec > 0 && candidate.durationSec > 0) {
                val diffSec = abs(candidate.durationSec - spotifyTrack.durationSec)
                when {
                    diffSec <= 4 -> score += 25
                    diffSec <= 10 -> score += 15
                    diffSec <= 20 -> score += 5
                    diffSec > 60 -> score -= 20 // Kemungkinan video klip panjang / extended
                }
            }

            if (score > bestScore) {
                bestScore = score
                bestCandidate = candidate
            }
        }

        Log.d(TAG, "matchTrack: '$query' cocok dengan '${bestCandidate.artist} - ${bestCandidate.title}' (${bestCandidate.videoId}, score: $bestScore)")
        bestCandidate
    }
}
