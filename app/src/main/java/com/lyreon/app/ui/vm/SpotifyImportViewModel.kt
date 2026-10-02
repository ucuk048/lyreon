/*
 * Copyright (C) 2026 rixz-dev
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */
package com.lyreon.app.ui.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyreon.app.core.ServiceLocator
import com.lyreon.app.data.model.LyreonTrack
import com.lyreon.app.spotify.SpotifyMatcher
import com.lyreon.app.spotify.SpotifyPlaylist
import com.lyreon.app.spotify.SpotifyScraper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SpotifyImportUiState(
    val url: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val playlist: SpotifyPlaylist? = null,
    val matching: Boolean = false,
    val matchedCount: Int = 0,
    val totalCount: Int = 0,
    val currentMatchingTitle: String? = null,
    val saved: Boolean = false,
    val savedPlaylistId: Long? = null,
)

class SpotifyImportViewModel(private val locator: ServiceLocator) : ViewModel() {

    private val _state = MutableStateFlow(SpotifyImportUiState())
    val state: StateFlow<SpotifyImportUiState> = _state.asStateFlow()

    fun setUrl(url: String) {
        _state.update { it.copy(url = url, error = null) }
    }

    fun loadPlaylist() {
        val rawInput = _state.value.url.trim()
        if (rawInput.isBlank()) {
            _state.update { it.copy(error = "Tautan Spotify belum diisi") }
            return
        }

        val playlistId = SpotifyScraper.extractPlaylistId(rawInput)
        if (playlistId == null) {
            _state.update { it.copy(error = "Format tautan playlist Spotify tidak valid") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null, playlist = null, saved = false) }
            runCatching {
                SpotifyScraper.fetchPlaylist(playlistId)
            }.onSuccess { pl ->
                _state.update {
                    it.copy(
                        loading = false,
                        playlist = pl,
                        totalCount = pl.tracks.size,
                        error = if (pl.tracks.isEmpty()) "Playlist tidak memiliki lagu publik" else null,
                    )
                }
            }.onFailure { err ->
                Log.e("SpotifyImportVM", "Gagal fetch Spotify playlist: ${err.message}", err)
                _state.update {
                    it.copy(
                        loading = false,
                        error = err.message ?: "Gagal memuat playlist Spotify",
                    )
                }
            }
        }
    }

    fun importToLibrary(onFinished: ((Long) -> Unit)? = null) {
        val pl = _state.value.playlist ?: return
        if (pl.tracks.isEmpty() || _state.value.matching) return

        viewModelScope.launch {
            _state.update {
                it.copy(
                    matching = true,
                    matchedCount = 0,
                    totalCount = pl.tracks.size,
                    currentMatchingTitle = null,
                    error = null,
                )
            }

            val playlistId = locator.library.createPlaylist(pl.name.ifBlank { "Spotify Playlist" })
            val matchedTracks = mutableListOf<LyreonTrack>()

            for ((index, track) in pl.tracks.withIndex()) {
                _state.update {
                    it.copy(
                        currentMatchingTitle = "${track.artist} - ${track.title}",
                        matchedCount = index,
                    )
                }
                val matched = SpotifyMatcher.matchTrack(locator.youtube, track)
                if (matched != null) {
                    matchedTracks.add(matched)
                }
            }

            for (track in matchedTracks) {
                locator.library.addToPlaylist(playlistId, track)
            }

            _state.update {
                it.copy(
                    matching = false,
                    matchedCount = matchedTracks.size,
                    currentMatchingTitle = null,
                    saved = true,
                    savedPlaylistId = playlistId,
                )
            }
            onFinished?.invoke(playlistId)
        }
    }

    fun playNow(onPlaybackStarted: (() -> Unit)? = null) {
        val pl = _state.value.playlist ?: return
        if (pl.tracks.isEmpty()) return

        viewModelScope.launch {
            _state.update {
                it.copy(
                    matching = true,
                    matchedCount = 0,
                    totalCount = pl.tracks.size,
                    error = null,
                )
            }

            // 1. Cocokkan lagu pertama agar langsung bisa diputar tanpa jeda panjang
            val firstTrack = pl.tracks.first()
            _state.update { it.copy(currentMatchingTitle = "${firstTrack.artist} - ${firstTrack.title}") }
            val firstMatched = SpotifyMatcher.matchTrack(locator.youtube, firstTrack)

            if (firstMatched != null) {
                withContext(Dispatchers.Main) {
                    locator.player.playQueue(listOf(firstMatched), 0)
                    onPlaybackStarted?.invoke()
                }
                _state.update { it.copy(matchedCount = 1) }
            }

            // 2. Cocokkan sisa lagu secara asinkron dan tambahkan ke antrean player
            val remainingTracks = pl.tracks.drop(1)
            for ((idx, track) in remainingTracks.withIndex()) {
                _state.update {
                    it.copy(
                        currentMatchingTitle = "${track.artist} - ${track.title}",
                        matchedCount = 1 + idx,
                    )
                }
                val matched = SpotifyMatcher.matchTrack(locator.youtube, track)
                if (matched != null) {
                    withContext(Dispatchers.Main) {
                        locator.player.addToQueue(matched)
                    }
                }
            }

            _state.update {
                it.copy(
                    matching = false,
                    currentMatchingTitle = null,
                )
            }
        }
    }

    fun reset() {
        _state.value = SpotifyImportUiState()
    }
}
