package com.lyreon.ios.model

import kotlinx.serialization.Serializable

@Serializable
data class LyreonTrack(
    val id: String,
    val title: String,
    val artist: String,
    val durationSeconds: Long = 0L,
    val thumbnailUrl: String = "",
    val albumTitle: String = "",
    val isDownloaded: Boolean = false,
    val playCount: Long = 0L,
    val lastPlayedEpochMs: Long = 0L
) {
    val durationFormatted: String
        get() {
            if (durationSeconds <= 0) return "--:--"
            val m = durationSeconds / 60
            val s = durationSeconds % 60
            return "$m:${s.toString().padStart(2, '0')}"
        }
}

@Serializable
data class LyreonPlaylist(
    val id: String,
    val title: String,
    val description: String = "",
    val trackIds: List<String> = emptyList(),
    val isCustom: Boolean = true
)

enum class RepeatMode {
    OFF,
    ONE,
    ALL
}

enum class BottomTab {
    HOME,
    EXPLORE,
    SEARCH,
    LIBRARY
}
