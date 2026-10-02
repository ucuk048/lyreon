package com.lyreon.desktop.player

import com.lyreon.desktop.core.LyreonLog
import com.lyreon.desktop.model.LyreonTrack
import com.lyreon.desktop.yt.PlayerClientLadder
import com.lyreon.desktop.yt.StreamUrlValidator
import com.lyreon.desktop.yt.YouTubeDesktopRepository

import javafx.application.Platform
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.util.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class RepeatMode {
    OFF,
    ONE,
    ALL
}

object DesktopAudioPlayer {

    private const val TAG = "DesktopAudioPlayer"
    private val scope = CoroutineScope(Dispatchers.Default + Job())

    init {
        try {
            Platform.startup {}
            Platform.setImplicitExit(false)
        } catch (_: IllegalStateException) {
            // JavaFX Platform already initialized
        } catch (t: Throwable) {
            LyreonLog.e(TAG, "JavaFX Platform startup error: ${t.message}", t)
        }
    }

    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null

    private val _currentTrack = MutableStateFlow<LyreonTrack?>(null)
    val currentTrack = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering = _isBuffering.asStateFlow()

    private val _positionMs = MutableStateFlow(0L)
    val positionMs = _positionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs = _durationMs.asStateFlow()

    private val _volume = MutableStateFlow(0.85f)
    val volume = _volume.asStateFlow()

    private val _queue = MutableStateFlow<List<LyreonTrack>>(emptyList())
    val queue = _queue.asStateFlow()

    private val _currentIndex = MutableStateFlow(-1)
    val currentIndex = _currentIndex.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.OFF)
    val repeatMode = _repeatMode.asStateFlow()

    private val _isShuffle = MutableStateFlow(false)
    val isShuffle = _isShuffle.asStateFlow()

    private val _statusMessage = MutableStateFlow("")
    val statusMessage = _statusMessage.asStateFlow()

    val likedTracks: StateFlow<List<LyreonTrack>> = com.lyreon.desktop.data.DesktopDatabase.likedTracks

    val history: StateFlow<List<com.lyreon.desktop.data.DesktopHistoryEntry>> = com.lyreon.desktop.data.DesktopDatabase.history

    fun isLiked(videoId: String): Boolean = com.lyreon.desktop.data.DesktopDatabase.isLiked(videoId)

    fun toggleLike(track: LyreonTrack) {
        val liked = com.lyreon.desktop.data.DesktopDatabase.toggleLike(track)
        if (liked) {
            com.lyreon.desktop.taste.DesktopTasteRepository.recordLike(track)
        }
    }

    fun playTrack(track: LyreonTrack, newQueue: List<LyreonTrack>? = null) {
        com.lyreon.desktop.data.DesktopDatabase.recordHistory(track)
        com.lyreon.desktop.taste.DesktopTasteRepository.recordPlay(track, 1.0f)

        if (newQueue != null) {
            _queue.value = newQueue
            _currentIndex.value = newQueue.indexOfFirst { it.videoId == track.videoId }
        } else if (!_queue.value.any { it.videoId == track.videoId }) {
            _queue.value = _queue.value + track
            _currentIndex.value = _queue.value.lastIndex
        }

        _currentTrack.value = track
        _positionMs.value = 0L
        _durationMs.value = track.durationSec * 1000L
        _isBuffering.value = true

        // 1. Cek pemutaran offline lokal terlebih dahulu (download selesai atau cache tersimpan)
        val offlineFile = if (track.isLocal && !track.localPath.isNullOrBlank()) {
            java.io.File(track.localPath).takeIf { it.exists() }
        } else {
            com.lyreon.desktop.download.DesktopDownloadManager.getDownloadedFile(track.videoId)
                ?: java.io.File(DesktopStreamProxy.cacheDir, "${track.videoId}.m4a").takeIf { it.exists() && it.length() > 50_000L }
        }

        if (offlineFile != null && offlineFile.exists() && offlineFile.length() > 50_000L) {
            _statusMessage.value = "Memutar ${offlineFile.extension.uppercase()}..."
            startMediaPlayer(offlineFile.toURI().toString(), track)
            preloadNextTrack()
            return
        }

        _statusMessage.value = "Menghubungkan stream..."

        scope.launch(Dispatchers.IO) {
            try {
                val resolved = YouTubeDesktopRepository.resolveStream(track)
                _statusMessage.value = "Menghubungkan ${resolved.clientKey} (${resolved.bitrateKbps} kbps)..."

                val proxyUrl = DesktopStreamProxy.getStreamUrl(track, resolved.url)
                startMediaPlayer(proxyUrl, track)
                preloadNextTrack()
            } catch (e: Exception) {
                LyreonLog.e(TAG, "Gagal memutar track: ${e.message}", e)
                _statusMessage.value = "Gagal memuat: ${e.message}"
                _isBuffering.value = false
            }
        }
    }

    private fun preloadNextTrack() {
        val q = _queue.value
        val idx = _currentIndex.value
        if (idx < 0 || idx >= q.lastIndex) return
        val nextTrack = q[idx + 1]
        scope.launch(Dispatchers.IO) {
            try {
                val cached = java.io.File(DesktopStreamProxy.cacheDir, "${nextTrack.videoId}.m4a")
                val downloaded = com.lyreon.desktop.download.DesktopDownloadManager.getDownloadedFile(nextTrack.videoId)
                if (downloaded != null || (cached.exists() && cached.length() > 50_000L)) return@launch

                val resolved = YouTubeDesktopRepository.resolveStream(nextTrack)
                val part = java.io.File(DesktopStreamProxy.cacheDir, "${nextTrack.videoId}.part")
                val req = okhttp3.Request.Builder()
                    .url(resolved.url)
                    .header("User-Agent", StreamUrlValidator.userAgentForUrl(resolved.url) ?: PlayerClientLadder.WEB_UA_FIREFOX)
                    .build()
                YouTubeDesktopRepository.httpClient.newCall(req).execute().use { resp ->
                    if (resp.isSuccessful) {
                        val body = resp.body
                        if (body != null) {
                            part.outputStream().use { out -> body.byteStream().copyTo(out) }
                            if (part.length() > 50_000L) part.renameTo(cached)
                        }
                    }
                }
                LyreonLog.i(TAG, "Preload siap untuk trek berikutnya: ${nextTrack.title}")
            } catch (_: Exception) {}
        }
    }

    private fun startMediaPlayer(streamUrl: String, track: LyreonTrack) {
        Platform.runLater {
            try {
                mediaPlayer?.stop()
                mediaPlayer?.dispose()
                progressJob?.cancel()

                val media = Media(streamUrl)
                media.setOnError {
                    LyreonLog.e(TAG, "JavaFX Media source error: ${media.error?.message}")
                }

                val player = MediaPlayer(media).apply {
                    volume = _volume.value.toDouble()
                    setOnReady {
                        val dur = media.duration.toMillis().toLong()
                        if (dur > 0) _durationMs.value = dur
                        _isBuffering.value = false
                        _isPlaying.value = true
                        _statusMessage.value = "Memutar: ${track.title}"
                        play()
                    }
                    setOnEndOfMedia {
                        onTrackEnded()
                    }
                    setOnError {
                        LyreonLog.e(TAG, "JavaFX MediaPlayer error: ${error?.message}")
                        _statusMessage.value = "Error: ${error?.message}"
                        _isBuffering.value = false
                        _isPlaying.value = false
                    }
                }
                mediaPlayer = player

                // Start position tracker
                progressJob = scope.launch {
                    while (isActive) {
                        Platform.runLater {
                            val pos = player.currentTime?.toMillis()?.toLong() ?: 0L
                            _positionMs.value = pos
                            val isStillPlaying = player.status == MediaPlayer.Status.PLAYING
                            if (_isPlaying.value != isStillPlaying && player.status != MediaPlayer.Status.UNKNOWN) {
                                _isPlaying.value = isStillPlaying
                            }
                        }
                        delay(250)
                    }
                }
            } catch (e: Exception) {
                LyreonLog.e(TAG, "Error saat inisialisasi MediaPlayer: ${e.message}", e)
                _isBuffering.value = false
            }
        }
    }

    private fun onTrackEnded() {
        when (_repeatMode.value) {
            RepeatMode.ONE -> {
                seekTo(0)
                resume()
            }
            RepeatMode.ALL -> {
                playNext()
            }
            RepeatMode.OFF -> {
                if (_currentIndex.value < _queue.value.lastIndex) {
                    playNext()
                } else {
                    _isPlaying.value = false
                    _positionMs.value = 0L
                }
            }
        }
    }

    fun togglePlayPause() {
        val player = mediaPlayer ?: return
        Platform.runLater {
            if (player.status == MediaPlayer.Status.PLAYING) {
                player.pause()
                _isPlaying.value = false
            } else {
                player.play()
                _isPlaying.value = true
            }
        }
    }

    fun pause() {
        Platform.runLater {
            mediaPlayer?.pause()
            _isPlaying.value = false
        }
    }

    fun resume() {
        Platform.runLater {
            mediaPlayer?.play()
            _isPlaying.value = true
        }
    }

    fun seekTo(positionMs: Long) {
        val player = mediaPlayer ?: return
        Platform.runLater {
            player.seek(Duration.millis(positionMs.toDouble()))
            _positionMs.value = positionMs
        }
    }

    fun setVolume(v: Float) {
        val clamped = v.coerceIn(0f, 1f)
        _volume.value = clamped
        Platform.runLater {
            mediaPlayer?.volume = clamped.toDouble()
        }
    }

    fun playNext() {
        val q = _queue.value
        if (q.isEmpty()) return
        val nextIdx = if (_isShuffle.value) {
            q.indices.random()
        } else {
            (_currentIndex.value + 1) % q.size
        }
        _currentIndex.value = nextIdx
        playTrack(q[nextIdx])
    }

    fun playPrevious() {
        val q = _queue.value
        if (q.isEmpty()) return
        val prevIdx = if (_currentIndex.value > 0) _currentIndex.value - 1 else q.lastIndex
        _currentIndex.value = prevIdx
        playTrack(q[prevIdx])
    }

    fun toggleShuffle() {
        _isShuffle.value = !_isShuffle.value
    }

    fun toggleRepeat() {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
    }

    fun addToQueue(track: LyreonTrack) {
        _queue.value = _queue.value + track
    }

    fun clearQueue() {
        _queue.value = emptyList()
        _currentIndex.value = -1
    }
}
