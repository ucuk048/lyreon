package com.lyreon.desktop.local

import com.lyreon.desktop.core.LyreonLog
import com.lyreon.desktop.data.DesktopDatabase
import com.lyreon.desktop.model.LyreonTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File

object LocalMusicScanner {

    private const val TAG = "LocalMusicScanner"
    private val AUDIO_EXTENSIONS = setOf("mp3", "m4a", "aac", "flac", "wav", "ogg")

    private val _scannedTracks = MutableStateFlow<List<LyreonTrack>>(emptyList())
    val scannedTracks: StateFlow<List<LyreonTrack>> = _scannedTracks.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    suspend fun scan() = withContext(Dispatchers.IO) {
        _isScanning.value = true
        try {
            val foldersToScan = mutableListOf<File>()

            // Folder Music bawaan pengguna Windows / Mac / Linux
            val userHome = System.getProperty("user.home") ?: "."
            val defaultMusicDir = File(userHome, "Music")
            if (defaultMusicDir.exists() && defaultMusicDir.isDirectory) {
                foldersToScan.add(defaultMusicDir)
            }

            // Folder kustom dari database
            DesktopDatabase.customLocalFolders.value.forEach { path ->
                val f = File(path)
                if (f.exists() && f.isDirectory && !foldersToScan.contains(f)) {
                    foldersToScan.add(f)
                }
            }

            val foundTracks = mutableListOf<LyreonTrack>()

            for (dir in foldersToScan) {
                dir.walkTopDown()
                    .maxDepth(4)
                    .filter { it.isFile && it.extension.lowercase() in AUDIO_EXTENSIONS }
                    .forEach { file ->
                        val track = fileToTrack(file)
                        foundTracks.add(track)
                    }
            }

            _scannedTracks.value = foundTracks.sortedBy { it.title.lowercase() }
            LyreonLog.i(TAG, "Pemindaian selesai: ditemukan ${foundTracks.size} lagu lokal")
        } catch (e: Exception) {
            LyreonLog.e(TAG, "Gagal memindai musik lokal: ${e.message}", e)
        } finally {
            _isScanning.value = false
        }
    }

    private fun fileToTrack(file: File): LyreonTrack {
        val baseName = file.nameWithoutExtension
        val parts = baseName.split(" - ", limit = 2)
        val (artist, title) = if (parts.size == 2) {
            parts[0].trim() to parts[1].trim()
        } else {
            "Lokal" to baseName.trim()
        }

        val videoId = "local_${file.absolutePath.hashCode()}"
        return LyreonTrack(
            videoId = videoId,
            title = title,
            artist = artist,
            album = file.parentFile?.name ?: "Penyimpanan Lokal",
            durationSec = 0L,
            thumbnailUrl = "",
            isLocal = true,
            localPath = file.absolutePath
        )
    }
}
