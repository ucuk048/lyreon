/*
 * Copyright (C) 2026 rixz-dev
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */
package com.lyreon.app.spotify

data class SpotifyTrack(
    val title: String,
    val artist: String,
    val durationSec: Int,
    val uri: String = "",
)

data class SpotifyPlaylist(
    val id: String,
    val name: String,
    val coverUrl: String,
    val author: String,
    val tracks: List<SpotifyTrack>,
)
