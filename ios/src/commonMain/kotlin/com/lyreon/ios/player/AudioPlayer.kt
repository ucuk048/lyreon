package com.lyreon.ios.player

import com.lyreon.ios.model.LyreonTrack
import com.lyreon.ios.model.RepeatMode
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayer {
    val currentTrack: StateFlow<LyreonTrack?>
    val isPlaying: StateFlow<Boolean>
    val isBuffering: StateFlow<Boolean>
    val positionMs: StateFlow<Long>
    val durationMs: StateFlow<Long>
    val queue: StateFlow<List<LyreonTrack>>
    val currentIndex: StateFlow<Int>
    val repeatMode: StateFlow<RepeatMode>
    val isShuffle: StateFlow<Boolean>

    fun play(track: LyreonTrack, queue: List<LyreonTrack> = listOf(track), startIndex: Int = 0)
    fun togglePlayPause()
    fun pause()
    fun resume()
    fun seekTo(positionMs: Long)
    fun playNext()
    fun playPrevious()
    fun setRepeatMode(mode: RepeatMode)
    fun toggleShuffle()
}

expect fun getAudioPlayer(): AudioPlayer
