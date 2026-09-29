package com.lyreon.desktop.spotify

import com.lyreon.desktop.core.LyreonLog
import com.lyreon.desktop.model.LyreonTrack
import com.lyreon.desktop.model.SearchFilter
import com.lyreon.desktop.taste.MusicTextAnalyzer
import com.lyreon.desktop.yt.YouTubeDesktopRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs

object SpotifyMatcher {

    private const val TAG = "SpotifyMatcher"

    suspend fun matchTrack(spotifyTrack: SpotifyTrack): LyreonTrack? = withContext(Dispatchers.IO) {
        val query = "${spotifyTrack.artist} ${spotifyTrack.title}".trim()
        if (query.isBlank()) return@withContext null

        val candidates = runCatching {
            YouTubeDesktopRepository.search(query, SearchFilter.SONGS)
        }.getOrNull() ?: emptyList()

        if (candidates.isEmpty()) {
            LyreonLog.w(TAG, "Tidak ada kandidat ditemukan untuk: '$query'")
            return@withContext null
        }

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

            // Kesesuaian nama artis
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
                    diffSec > 60 -> score -= 20
                }
            }

            if (score > bestScore) {
                bestScore = score
                bestCandidate = candidate
            }
        }

        LyreonLog.i(TAG, "'$query' cocok dengan '${bestCandidate.artist} - ${bestCandidate.title}' (score: $bestScore)")
        bestCandidate
    }
}
