package com.lyreon.ios.data

import com.lyreon.ios.model.LyreonPlaylist
import com.lyreon.ios.model.LyreonTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class DatabaseState(
    val likedTrackIds: List<String> = emptyList(),
    val playlists: List<LyreonPlaylist> = listOf(
        LyreonPlaylist("fav", "Favorit Saya", "Koleksi lagu favorit", emptyList(), false)
    ),
    val history: List<LyreonTrack> = emptyList(),
    val cachedTracks: List<LyreonTrack> = emptyList()
)

object IosDatabase {
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    private val _likedTrackIds = MutableStateFlow<Set<String>>(emptySet())
    val likedTrackIds = _likedTrackIds.asStateFlow()

    private val _playlists = MutableStateFlow<List<LyreonPlaylist>>(
        listOf(LyreonPlaylist("fav", "Favorit Saya", "Koleksi lagu favorit", emptyList(), false))
    )
    val playlists = _playlists.asStateFlow()

    private val _history = MutableStateFlow<List<LyreonTrack>>(emptyList())
    val history = _history.asStateFlow()

    fun isLiked(trackId: String): Boolean = _likedTrackIds.value.contains(trackId)

    fun toggleLike(track: LyreonTrack) {
        val current = _likedTrackIds.value.toMutableSet()
        if (current.contains(track.id)) {
            current.remove(track.id)
        } else {
            current.add(track.id)
        }
        _likedTrackIds.value = current
    }

    fun addToHistory(track: LyreonTrack) {
        val list = _history.value.toMutableList()
        list.removeAll { it.id == track.id }
        list.add(0, track.copy(lastPlayedEpochMs = 0L, playCount = track.playCount + 1))
        if (list.size > 100) {
            _history.value = list.subList(0, 100)
        } else {
            _history.value = list
        }
    }
}
