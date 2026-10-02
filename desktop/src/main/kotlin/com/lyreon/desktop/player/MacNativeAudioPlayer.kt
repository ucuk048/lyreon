package com.lyreon.desktop.player

import com.lyreon.desktop.core.LyreonLog
import java.io.File
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Native macOS audio playback engine using Apple's built-in /usr/bin/afplay.
 * Provides a 100% crash-proof fallback when JavaFX native media libraries
 * fail or are unavailable on macOS.
 */
object MacNativeAudioPlayer {

    private const val TAG = "MacNativeAudioPlayer"

    val isMacOs: Boolean by lazy {
        val os = System.getProperty("os.name", "").lowercase(Locale.ROOT)
        (os.contains("mac") || os.contains("darwin")) && File("/usr/bin/afplay").canExecute()
    }

    private var currentProcess: Process? = null
    private var currentFile: File? = null
    private var startPositionMs: Long = 0L
    private var startTimestampMs: Long = 0L
    private var currentVolume: Float = 0.85f
    private val isPaused = AtomicBoolean(false)
    private var onEndCallback: (() -> Unit)? = null

    val isPlaying: Boolean
        get() = currentProcess?.isAlive == true && !isPaused.get()

    fun play(file: File, positionMs: Long = 0L, volume: Float = 0.85f, onEnded: () -> Unit) {
        stop()
        if (!file.exists()) {
            LyreonLog.e(TAG, "File audio tidak ditemukan: ${file.absolutePath}")
            return
        }

        currentFile = file
        currentVolume = volume.coerceIn(0f, 1f)
        startPositionMs = positionMs
        isPaused.set(false)
        onEndCallback = onEnded

        launchProcess(positionMs)
    }

    private fun launchProcess(fromPositionMs: Long) {
        val file = currentFile ?: return
        val startSec = String.format(Locale.US, "%.3f", maxOf(0.0, fromPositionMs / 1000.0))
        val volStr = String.format(Locale.US, "%.2f", currentVolume)

        val cmd = mutableListOf("/usr/bin/afplay", "-v", volStr)
        if (fromPositionMs > 500L) {
            cmd.add("-t")
            cmd.add(startSec)
        }
        cmd.add(file.absolutePath)

        try {
            LyreonLog.i(TAG, "Memulai afplay: ${cmd.joinToString(" ")}")
            val pb = ProcessBuilder(cmd)
            pb.redirectErrorStream(true)
            val proc = pb.start()
            currentProcess = proc
            startPositionMs = fromPositionMs
            startTimestampMs = System.currentTimeMillis()

            Thread({
                try {
                    val exit = proc.waitFor()
                    LyreonLog.i(TAG, "afplay selesai dengan kode exit: $exit")
                    if (exit == 0 && !isPaused.get()) {
                        onEndCallback?.invoke()
                    }
                } catch (_: InterruptedException) {
                } catch (t: Throwable) {
                    LyreonLog.w(TAG, "afplay wait error: ${t.message}")
                }
            }, "Lyreon-Afplay-Monitor").apply {
                isDaemon = true
                start()
            }
        } catch (t: Throwable) {
            LyreonLog.e(TAG, "Gagal menjalankan /usr/bin/afplay: ${t.message}", t)
        }
    }

    fun pause() {
        if (isPlaying) {
            val elapsed = System.currentTimeMillis() - startTimestampMs
            startPositionMs += elapsed
            isPaused.set(true)
            killProcess()
        }
    }

    fun resume() {
        if (isPaused.get() && currentFile != null) {
            isPaused.set(false)
            launchProcess(startPositionMs)
        }
    }

    fun seekTo(positionMs: Long) {
        startPositionMs = positionMs
        if (isPlaying) {
            killProcess()
            launchProcess(positionMs)
        }
    }

    fun setVolume(vol: Float) {
        val clamped = vol.coerceIn(0f, 1f)
        currentVolume = clamped
        if (isPlaying) {
            val currentPos = getPositionMs()
            killProcess()
            launchProcess(currentPos)
        }
    }

    fun getPositionMs(): Long {
        if (!isPlaying) return startPositionMs
        val elapsed = System.currentTimeMillis() - startTimestampMs
        return maxOf(0L, startPositionMs + elapsed)
    }

    fun stop() {
        isPaused.set(false)
        killProcess()
        currentFile = null
        startPositionMs = 0L
        startTimestampMs = 0L
        onEndCallback = null
    }

    private fun killProcess() {
        currentProcess?.let { proc ->
            if (proc.isAlive) {
                proc.destroy()
                try {
                    proc.destroyForcibly()
                } catch (_: Throwable) {}
            }
        }
        currentProcess = null
    }
}
