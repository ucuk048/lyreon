package com.lyreon.ios.player

import com.lyreon.ios.model.LyreonTrack
import com.lyreon.ios.model.RepeatMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemStatusReadyToPlay
import platform.AVFoundation.currentTime
import platform.AVFoundation.duration
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.seekToTime
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSURL
import platform.MediaPlayer.MPChangePlaybackPositionCommandEvent
import platform.MediaPlayer.MPMediaItemPropertyArtist
import platform.MediaPlayer.MPMediaItemPropertyPlaybackDuration
import platform.MediaPlayer.MPMediaItemPropertyTitle
import platform.MediaPlayer.MPNowPlayingInfoCenter
import platform.MediaPlayer.MPNowPlayingInfoPropertyElapsedPlaybackTime
import platform.MediaPlayer.MPNowPlayingInfoPropertyPlaybackRate
import platform.MediaPlayer.MPRemoteCommandCenter
import platform.MediaPlayer.MPRemoteCommandHandlerStatusCommandFailed
import platform.MediaPlayer.MPRemoteCommandHandlerStatusSuccess

class IosAudioPlayerImpl : AudioPlayer {

    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private var avPlayer: AVPlayer? = null
    private var progressJob: Job? = null

    private val _currentTrack = MutableStateFlow<LyreonTrack?>(null)
    override val currentTrack: StateFlow<LyreonTrack?> = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isBuffering = MutableStateFlow(false)
    override val isBuffering: StateFlow<Boolean> = _isBuffering.asStateFlow()

    private val _positionMs = MutableStateFlow(0L)
    override val positionMs: StateFlow<Long> = _positionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    override val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _queue = MutableStateFlow<List<LyreonTrack>>(emptyList())
    override val queue: StateFlow<List<LyreonTrack>> = _queue.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    override val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.OFF)
    override val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    private val _isShuffle = MutableStateFlow(false)
    override val isShuffle: StateFlow<Boolean> = _isShuffle.asStateFlow()

    init {
        setupAudioSession()
        setupRemoteCommands()
    }

    private fun setupAudioSession() {
        try {
            val session = AVAudioSession.sharedInstance()
            session.setCategory(AVAudioSessionCategoryPlayback, error = null)
            session.setActive(true, error = null)
        } catch (_: Throwable) {}
    }

    private fun setupRemoteCommands() {
        val center = MPRemoteCommandCenter.sharedCommandCenter()
        center.playCommand.addTargetWithHandler {
            resume()
            MPRemoteCommandHandlerStatusSuccess
        }
        center.pauseCommand.addTargetWithHandler {
            pause()
            MPRemoteCommandHandlerStatusSuccess
        }
        center.togglePlayPauseCommand.addTargetWithHandler {
            togglePlayPause()
            MPRemoteCommandHandlerStatusSuccess
        }
        center.nextTrackCommand.addTargetWithHandler {
            playNext()
            MPRemoteCommandHandlerStatusSuccess
        }
        center.previousTrackCommand.addTargetWithHandler {
            playPrevious()
            MPRemoteCommandHandlerStatusSuccess
        }
        center.changePlaybackPositionCommand.addTargetWithHandler { event ->
            val posEvent = event as? MPChangePlaybackPositionCommandEvent
            if (posEvent != null) {
                seekTo((posEvent.positionTime * 1000.0).toLong())
                MPRemoteCommandHandlerStatusSuccess
            } else {
                MPRemoteCommandHandlerStatusCommandFailed
            }
        }
    }

    override fun play(track: LyreonTrack, queue: List<LyreonTrack>, startIndex: Int) {
        _queue.value = if (queue.isEmpty()) listOf(track) else queue
        _currentIndex.value = startIndex.coerceIn(0, (_queue.value.size - 1).coerceAtLeast(0))
        _currentTrack.value = track
        _isBuffering.value = true
        _isPlaying.value = false

        scope.launch {
            try {
                // Resolved direct stream URL or proxy
                val streamUrl = "https://music.youtube.com/watch?v=${track.id}"
                val nsUrl = NSURL.URLWithString(streamUrl) ?: return@launch
                val playerItem = AVPlayerItem(uRL = nsUrl)
                
                if (avPlayer == null) {
                    avPlayer = AVPlayer(playerItem = playerItem)
                } else {
                    avPlayer?.replaceCurrentItemWithPlayerItem(playerItem)
                }

                avPlayer?.play()
                _isPlaying.value = true
                _isBuffering.value = false
                updateNowPlaying(track, 1.0)
                startProgressObserver()
            } catch (_: Throwable) {
                _isBuffering.value = false
                _isPlaying.value = false
            }
        }
    }

    override fun togglePlayPause() {
        if (_isPlaying.value) pause() else resume()
    }

    override fun pause() {
        avPlayer?.pause()
        _isPlaying.value = false
        _currentTrack.value?.let { updateNowPlaying(it, 0.0) }
    }

    override fun resume() {
        avPlayer?.play()
        _isPlaying.value = true
        _currentTrack.value?.let { updateNowPlaying(it, 1.0) }
    }

    override fun seekTo(positionMs: Long) {
        _positionMs.value = positionMs
        val seconds = positionMs / 1000.0
        val cmTime = CMTimeMakeWithSeconds(seconds, preferredTimescale = 1000)
        avPlayer?.seekToTime(cmTime)
    }

    override fun playNext() {
        val q = _queue.value
        if (q.isEmpty()) return
        val nextIdx = (_currentIndex.value + 1) % q.size
        play(q[nextIdx], q, nextIdx)
    }

    override fun playPrevious() {
        val q = _queue.value
        if (q.isEmpty()) return
        val prevIdx = if (_currentIndex.value - 1 < 0) q.size - 1 else _currentIndex.value - 1
        play(q[prevIdx], q, prevIdx)
    }

    override fun setRepeatMode(mode: RepeatMode) {
        _repeatMode.value = mode
    }

    override fun toggleShuffle() {
        _isShuffle.value = !_isShuffle.value
    }

    private fun startProgressObserver() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                val player = avPlayer
                if (player != null && _isPlaying.value) {
                    val currentSec = CMTimeGetSeconds(player.currentTime())
                    val totalSec = player.currentItem?.duration?.let { CMTimeGetSeconds(it) } ?: 0.0
                    if (!currentSec.isNaN() && currentSec >= 0) {
                        _positionMs.value = (currentSec * 1000.0).toLong()
                    }
                    if (!totalSec.isNaN() && totalSec > 0) {
                        _durationMs.value = (totalSec * 1000.0).toLong()
                    }
                }
                delay(500)
            }
        }
    }

    private fun updateNowPlaying(track: LyreonTrack, playbackRate: Double) {
        val durationSec = track.durationSeconds.toDouble()
        val elapsedSec = _positionMs.value / 1000.0
        val info = mutableMapOf<Any?, Any?>()
        info[MPMediaItemPropertyTitle] = track.title
        info[MPMediaItemPropertyArtist] = track.artist
        info[MPMediaItemPropertyPlaybackDuration] = durationSec
        info[MPNowPlayingInfoPropertyElapsedPlaybackTime] = elapsedSec
        info[MPNowPlayingInfoPropertyPlaybackRate] = playbackRate
        MPNowPlayingInfoCenter.defaultCenter().nowPlayingInfo = info
    }
}

private val globalAudioPlayer = IosAudioPlayerImpl()

actual fun getAudioPlayer(): AudioPlayer = globalAudioPlayer
