package com.lyreon.desktop.model

import kotlinx.serialization.Serializable

@Serializable
data class LyreonTrack(
    val videoId: String,
    val title: String,
    val artist: String,
    val album: String = "",
    val durationSec: Long = 0L,
    val thumbnailUrl: String = "",
    val isLocal: Boolean = false,
    val localPath: String? = null,
) {
    val watchUrl: String get() = "https://www.youtube.com/watch?v=$videoId"
}

enum class SearchFilter(val label: String, val param: String) {
    ALL("Semua", "all"),
    SONGS("Lagu", "music_songs"),
    ARTISTS("Artis", "music_artists"),
    ALBUMS("Album", "music_albums"),
    PLAYLISTS("Playlist", "music_playlists"),
}

fun formatDuration(sec: Long): String {
    if (sec <= 0) return "--:--"
    val h = sec / 3600
    val m = (sec % 3600) / 60
    val s = sec % 60
    return if (h > 0) {
        String.format("%d:%02d:%02d", h, m, s)
    } else {
        String.format("%02d:%02d", m, s)
    }
}
