package com.lyreon.desktop.data

import com.lyreon.desktop.core.LyreonLog
import com.lyreon.desktop.model.LyreonTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class DesktopPlaylist(
    val id: Long,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val coverUrl: String = "",
    val tracks: List<LyreonTrack> = emptyList(),
)

@Serializable
data class DesktopDownloadEntry(
    val videoId: String,
    val title: String,
    val artist: String,
    val album: String = "",
    val thumbnailUrl: String = "",
    val durationSec: Long = 0L,
    val state: String = "QUEUED", // QUEUED, DOWNLOADING, DONE, ERROR, CANCELED
    val filePath: String? = null,
    val bytesTotal: Long = 0L,
    val bytesDone: Long = 0L,
    val errorMessage: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)

@Serializable
data class DesktopHistoryEntry(
    val track: LyreonTrack,
    val playCount: Int = 1,
    val lastPlayedAt: Long = System.currentTimeMillis(),
)

@Serializable
data class DesktopDatabaseState(
    val likedTracks: List<LyreonTrack> = emptyList(),
    val history: List<DesktopHistoryEntry> = emptyList(),
    val playlists: List<DesktopPlaylist> = emptyList(),
    val downloads: List<DesktopDownloadEntry> = emptyList(),
    val customLocalFolders: List<String> = emptyList(),
)

object DesktopDatabase {

    private const val TAG = "DesktopDatabase"
    private val scope = CoroutineScope(Dispatchers.IO)
    private val mutex = Mutex()

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val appDir: File by lazy {
        val userHome = System.getProperty("user.home") ?: "."
        File(userHome, ".lyreon").apply { mkdirs() }
    }

    private val dbFile: File by lazy {
        File(appDir, "data.json")
    }

    private val _likedTracks = MutableStateFlow<List<LyreonTrack>>(emptyList())
    val likedTracks: StateFlow<List<LyreonTrack>> = _likedTracks.asStateFlow()

    private val _history = MutableStateFlow<List<DesktopHistoryEntry>>(emptyList())
    val history: StateFlow<List<DesktopHistoryEntry>> = _history.asStateFlow()

    private val _playlists = MutableStateFlow<List<DesktopPlaylist>>(emptyList())
    val playlists: StateFlow<List<DesktopPlaylist>> = _playlists.asStateFlow()

    private val _downloads = MutableStateFlow<List<DesktopDownloadEntry>>(emptyList())
    val downloads: StateFlow<List<DesktopDownloadEntry>> = _downloads.asStateFlow()

    private val _customLocalFolders = MutableStateFlow<List<String>>(emptyList())
    val customLocalFolders: StateFlow<List<String>> = _customLocalFolders.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        try {
            if (dbFile.exists() && dbFile.length() > 0) {
                val raw = dbFile.readText(Charsets.UTF_8)
                val state = json.decodeFromString<DesktopDatabaseState>(raw)
                _likedTracks.value = state.likedTracks
                _history.value = state.history
                _playlists.value = state.playlists
                _downloads.value = state.downloads
                _customLocalFolders.value = state.customLocalFolders
                LyreonLog.i(TAG, "Data loaded: ${state.likedTracks.size} liked, ${state.playlists.size} playlists, ${state.downloads.size} downloads")
            } else {
                saveDataSync()
            }
        } catch (e: Exception) {
            LyreonLog.e(TAG, "Error loading database: ${e.message}", e)
        }
    }

    private fun persist() {
        scope.launch {
            mutex.withLock {
                saveDataSync()
            }
        }
    }

    private fun saveDataSync() {
        try {
            val state = DesktopDatabaseState(
                likedTracks = _likedTracks.value,
                history = _history.value,
                playlists = _playlists.value,
                downloads = _downloads.value,
                customLocalFolders = _customLocalFolders.value,
            )
            val text = json.encodeToString(state)
            val tmp = File(appDir, "data.json.tmp")
            tmp.writeText(text, Charsets.UTF_8)
            if (tmp.exists()) {
                if (dbFile.exists()) dbFile.delete()
                tmp.renameTo(dbFile)
            }
        } catch (e: Exception) {
            LyreonLog.e(TAG, "Error persisting database: ${e.message}", e)
        }
    }

    // Liked tracks operations
    fun isLiked(videoId: String): Boolean {
        return _likedTracks.value.any { it.videoId == videoId }
    }

    fun toggleLike(track: LyreonTrack): Boolean {
        val current = _likedTracks.value
        val alreadyLiked = current.any { it.videoId == track.videoId }
        if (alreadyLiked) {
            _likedTracks.value = current.filterNot { it.videoId == track.videoId }
            persist()
            return false
        } else {
            _likedTracks.value = listOf(track) + current
            persist()
            return true
        }
    }

    fun like(track: LyreonTrack) {
        val current = _likedTracks.value
        if (!current.any { it.videoId == track.videoId }) {
            _likedTracks.value = listOf(track) + current
            persist()
        }
    }

    fun unlike(videoId: String) {
        val current = _likedTracks.value
        if (current.any { it.videoId == videoId }) {
            _likedTracks.value = current.filterNot { it.videoId == videoId }
            persist()
        }
    }

    // History operations
    fun recordHistory(track: LyreonTrack) {
        val current = _history.value
        val existingIndex = current.indexOfFirst { it.track.videoId == track.videoId }
        val newEntry = if (existingIndex >= 0) {
            val old = current[existingIndex]
            old.copy(playCount = old.playCount + 1, lastPlayedAt = System.currentTimeMillis())
        } else {
            DesktopHistoryEntry(track = track, playCount = 1, lastPlayedAt = System.currentTimeMillis())
        }
        val remaining = current.filterNot { it.track.videoId == track.videoId }
        _history.value = (listOf(newEntry) + remaining).take(100)
        persist()
    }

    fun clearHistory() {
        _history.value = emptyList()
        persist()
    }

    // Playlist operations
    fun createPlaylist(name: String, initialTracks: List<LyreonTrack> = emptyList(), coverUrl: String = ""): Long {
        val id = System.currentTimeMillis()
        val pl = DesktopPlaylist(
            id = id,
            name = name,
            createdAt = System.currentTimeMillis(),
            coverUrl = if (coverUrl.isNotBlank()) coverUrl else initialTracks.firstOrNull()?.thumbnailUrl.orEmpty(),
            tracks = initialTracks,
        )
        _playlists.value = listOf(pl) + _playlists.value
        persist()
        return id
    }

    fun renamePlaylist(id: Long, newName: String) {
        _playlists.value = _playlists.value.map {
            if (it.id == id) it.copy(name = newName) else it
        }
        persist()
    }

    fun deletePlaylist(id: Long) {
        _playlists.value = _playlists.value.filterNot { it.id == id }
        persist()
    }

    fun getPlaylist(id: Long): DesktopPlaylist? {
        return _playlists.value.find { it.id == id }
    }

    fun addTrackToPlaylist(playlistId: Long, track: LyreonTrack) {
        _playlists.value = _playlists.value.map { pl ->
            if (pl.id == playlistId) {
                if (pl.tracks.any { it.videoId == track.videoId }) pl
                else {
                    val updatedTracks = pl.tracks + track
                    val newCover = if (pl.coverUrl.isBlank()) track.thumbnailUrl else pl.coverUrl
                    pl.copy(tracks = updatedTracks, coverUrl = newCover)
                }
            } else pl
        }
        persist()
    }

    fun addTracksToPlaylist(playlistId: Long, tracks: List<LyreonTrack>) {
        if (tracks.isEmpty()) return
        _playlists.value = _playlists.value.map { pl ->
            if (pl.id == playlistId) {
                val existingIds = pl.tracks.map { it.videoId }.toSet()
                val newUnique = tracks.filterNot { it.videoId in existingIds }
                val updatedTracks = pl.tracks + newUnique
                val newCover = if (pl.coverUrl.isBlank()) tracks.firstOrNull()?.thumbnailUrl.orEmpty() else pl.coverUrl
                pl.copy(tracks = updatedTracks, coverUrl = newCover)
            } else pl
        }
        persist()
    }

    fun removeTrackFromPlaylist(playlistId: Long, videoId: String) {
        _playlists.value = _playlists.value.map { pl ->
            if (pl.id == playlistId) {
                val updated = pl.tracks.filterNot { it.videoId == videoId }
                pl.copy(tracks = updated)
            } else pl
        }
        persist()
    }

    fun moveTrackInPlaylist(playlistId: Long, fromIndex: Int, toIndex: Int) {
        _playlists.value = _playlists.value.map { pl ->
            if (pl.id == playlistId) {
                val list = pl.tracks.toMutableList()
                if (fromIndex in list.indices && toIndex in list.indices) {
                    val item = list.removeAt(fromIndex)
                    list.add(toIndex, item)
                    pl.copy(tracks = list)
                } else pl
            } else pl
        }
        persist()
    }

    // Downloads operations
    fun upsertDownload(entry: DesktopDownloadEntry) {
        val current = _downloads.value.toMutableList()
        val idx = current.indexOfFirst { it.videoId == entry.videoId }
        if (idx >= 0) {
            current[idx] = entry
        } else {
            current.add(entry)
        }
        _downloads.value = current
        persist()
    }

    fun deleteDownload(videoId: String) {
        _downloads.value = _downloads.value.filterNot { it.videoId == videoId }
        persist()
    }

    fun getDownload(videoId: String): DesktopDownloadEntry? {
        return _downloads.value.find { it.videoId == videoId }
    }

    fun isDownloaded(videoId: String): Boolean {
        val d = getDownload(videoId) ?: return false
        return d.state == "DONE" && d.filePath?.let { File(it).exists() } == true
    }

    // Local custom folders operations
    fun addCustomLocalFolder(path: String) {
        if (path.isNotBlank() && !_customLocalFolders.value.contains(path)) {
            _customLocalFolders.value = _customLocalFolders.value + path
            persist()
        }
    }

    fun removeCustomLocalFolder(path: String) {
        _customLocalFolders.value = _customLocalFolders.value.filterNot { it == path }
        persist()
    }
}
