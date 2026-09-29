package com.lyreon.desktop.spotify

import kotlinx.serialization.Serializable

@Serializable
data class SpotifyTrack(
    val title: String,
    val artist: String,
    val durationSec: Int,
    val uri: String = "",
)

@Serializable
data class SpotifyPlaylist(
    val id: String,
    val name: String,
    val coverUrl: String,
    val author: String,
    val tracks: List<SpotifyTrack>,
)
