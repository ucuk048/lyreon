package com.lyreon.desktop.download

import com.lyreon.desktop.core.LyreonLog
import com.lyreon.desktop.data.DesktopDatabase
import com.lyreon.desktop.data.DesktopDownloadEntry
import com.lyreon.desktop.model.LyreonTrack
import com.lyreon.desktop.yt.YouTubeDesktopRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

object DesktopDownloadManager {

    private const val TAG = "DesktopDownloadManager"
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val runMutex = Mutex()

    private const val CHUNK_SIZE = 2 * 1024 * 1024L // 2 MiB per chunk
    private const val CONCURRENCY = 4

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .followRedirects(true)
            .build()
    }

    val downloadsDir: File by lazy {
        val userHome = System.getProperty("user.home") ?: "."
        File(userHome, ".lyreon/downloads").apply { mkdirs() }
    }

    private val _currentSpeed = MutableStateFlow("")
    val currentSpeed: StateFlow<String> = _currentSpeed.asStateFlow()

    private val _activeCount = MutableStateFlow(0)
    val activeCount: StateFlow<Int> = _activeCount.asStateFlow()

    init {
        // Cek jika ada antrean tertunda saat startup
        scope.launch {
            resumeAll()
        }
    }

    fun getDownloadedFile(videoId: String): File? {
        val entry = DesktopDatabase.getDownload(videoId) ?: return null
        if (entry.state == "DONE" && !entry.filePath.isNullOrBlank()) {
            val f = File(entry.filePath)
            if (f.exists() && f.length() > 0) return f
        }
        val fallback = File(downloadsDir, "$videoId.m4a")
        if (fallback.exists() && fallback.length() > 0) return fallback
        val fallbackMp4 = File(downloadsDir, "$videoId.mp4")
        if (fallbackMp4.exists() && fallbackMp4.length() > 0) return fallbackMp4
        return null
    }

    fun enqueue(track: LyreonTrack) {
        val existing = DesktopDatabase.getDownload(track.videoId)
        if (existing?.state == "DONE" && existing.filePath?.let { File(it).exists() } == true) {
            return
        }

        DesktopDatabase.upsertDownload(
            DesktopDownloadEntry(
                videoId = track.videoId,
                title = track.title,
                artist = track.artist,
                album = track.album,
                thumbnailUrl = track.thumbnailUrl,
                durationSec = track.durationSec,
                state = "QUEUED",
            )
        )
        triggerProcessQueue()
    }

    fun enqueueAll(tracks: List<LyreonTrack>): Int {
        var count = 0
        tracks.forEach { track ->
            val existing = DesktopDatabase.getDownload(track.videoId)
            if (existing?.state != "DONE" || existing.filePath?.let { File(it).exists() } != true) {
                DesktopDatabase.upsertDownload(
                    DesktopDownloadEntry(
                        videoId = track.videoId,
                        title = track.title,
                        artist = track.artist,
                        album = track.album,
                        thumbnailUrl = track.thumbnailUrl,
                        durationSec = track.durationSec,
                        state = "QUEUED",
                    )
                )
                count++
            }
        }
        if (count > 0) {
            triggerProcessQueue()
        }
        return count
    }

    private fun triggerProcessQueue() {
        scope.launch {
            processQueue()
        }
    }

    private suspend fun processQueue() {
        if (!runMutex.tryLock()) return
        try {
            while (true) {
                val queued = DesktopDatabase.downloads.value.firstOrNull { it.state == "QUEUED" } ?: break
                _activeCount.value = DesktopDatabase.downloads.value.count { it.state == "QUEUED" || it.state == "DOWNLOADING" }

                DesktopDatabase.upsertDownload(queued.copy(state = "DOWNLOADING"))
                var downloadedFile: File? = null
                var lastError: Throwable? = null

                for (attempt in 1..3) {
                    try {
                        downloadedFile = downloadTrack(queued)
                        lastError = null
                        break
                    } catch (e: Exception) {
                        lastError = e
                        if (attempt < 3) delay(attempt * 1000L)
                    }
                }

                _currentSpeed.value = ""

                if (downloadedFile != null) {
                    DesktopDatabase.upsertDownload(
                        queued.copy(
                            state = "DONE",
                            filePath = downloadedFile.absolutePath,
                            bytesTotal = downloadedFile.length(),
                            bytesDone = downloadedFile.length(),
                            errorMessage = null
                        )
                    )
                } else {
                    DesktopDatabase.upsertDownload(
                        queued.copy(
                            state = "ERROR",
                            errorMessage = lastError?.message ?: "Gagal mengunduh"
                        )
                    )
                }
            }
        } finally {
            _activeCount.value = DesktopDatabase.downloads.value.count { it.state == "QUEUED" || it.state == "DOWNLOADING" }
            _currentSpeed.value = ""
            runMutex.unlock()
        }
    }

    private suspend fun downloadTrack(entry: DesktopDownloadEntry): File = withContext(Dispatchers.IO) {
        val track = LyreonTrack(
            videoId = entry.videoId,
            title = entry.title,
            artist = entry.artist,
            album = entry.album,
            durationSec = entry.durationSec,
            thumbnailUrl = entry.thumbnailUrl
        )

        val resolved = YouTubeDesktopRepository.resolveStream(track)
        val tmpFile = File(downloadsDir, "${entry.videoId}.part")

        val suffix = if (resolved.isManifest) {
            var lastPush = 0L
            val result = HlsFlatDownloader.downloadFlat(
                client = httpClient,
                manifestUrl = resolved.url,
                target = tmpFile,
                isCancelled = { DesktopDatabase.getDownload(entry.videoId)?.state == "CANCELED" },
                onProgress = { done, estimated, _, _ ->
                    val now = System.currentTimeMillis()
                    if (now - lastPush > 800L) {
                        lastPush = now
                        DesktopDatabase.upsertDownload(
                            entry.copy(state = "DOWNLOADING", bytesTotal = estimated, bytesDone = done)
                        )
                        updateSpeed(done)
                    }
                }
            )
            result.container.suffix
        } else {
            writeProgressive(entry, resolved.url, tmpFile)
            "m4a"
        }

        val finalFile = File(downloadsDir, "${entry.videoId}.$suffix")
        if (!tmpFile.renameTo(finalFile)) {
            tmpFile.copyTo(finalFile, overwrite = true)
            tmpFile.delete()
        }
        finalFile
    }

    private data class ChunkRange(val start: Long, val end: Long)

    private suspend fun writeProgressive(entry: DesktopDownloadEntry, url: String, tmp: File) {
        val firstReq = Request.Builder()
            .url(url)
            .header("Range", "bytes=0-${CHUNK_SIZE - 1}")
            .get()
            .build()

        val firstResp = httpClient.newCall(firstReq).execute()
        firstResp.use { resp ->
            if (!resp.isSuccessful) throw IOException("HTTP ${resp.code} saat mulai unduh")

            val contentRange = resp.header("Content-Range")
            val totalBytes = contentRange?.substringAfterLast('/')?.trim()?.toLongOrNull()
                ?: resp.body?.contentLength()?.takeIf { it > 0 } ?: 0L

            val isPartial = resp.code == 206 && totalBytes > 0
            if (!isPartial) {
                writeStreamDirect(resp, entry, totalBytes, tmp)
                return
            }

            val chunk0Bytes = resp.body?.bytes() ?: ByteArray(0)
            val raf = RandomAccessFile(tmp, "rw")
            val fileLock = Any()

            try {
                raf.setLength(totalBytes)
                synchronized(fileLock) {
                    raf.seek(0L)
                    raf.write(chunk0Bytes)
                }

                val downloadedBytes = AtomicLong(chunk0Bytes.size.toLong())
                val lastUiPush = AtomicLong(System.currentTimeMillis())

                DesktopDatabase.upsertDownload(
                    entry.copy(state = "DOWNLOADING", bytesTotal = totalBytes, bytesDone = chunk0Bytes.size.toLong())
                )

                if (totalBytes > chunk0Bytes.size) {
                    val remainingChunks = mutableListOf<ChunkRange>()
                    var cursor = chunk0Bytes.size.toLong()
                    while (cursor < totalBytes) {
                        val end = minOf(cursor + CHUNK_SIZE - 1, totalBytes - 1)
                        remainingChunks.add(ChunkRange(cursor, end))
                        cursor = end + 1
                    }

                    val semaphore = Semaphore(CONCURRENCY)
                    coroutineScope {
                        val jobs = remainingChunks.map { chunk ->
                            async(Dispatchers.IO) {
                                semaphore.withPermit {
                                    if (DesktopDatabase.getDownload(entry.videoId)?.state == "CANCELED") {
                                        throw IOException("Dibatalkan")
                                    }
                                    val bytes = fetchChunkWithRetry(url, chunk.start, chunk.end)
                                    synchronized(fileLock) {
                                        raf.seek(chunk.start)
                                        raf.write(bytes)
                                    }
                                    val done = downloadedBytes.addAndGet(bytes.size.toLong())
                                    val now = System.currentTimeMillis()
                                    if (now - lastUiPush.get() > 600L || done >= totalBytes) {
                                        lastUiPush.set(now)
                                        DesktopDatabase.upsertDownload(
                                            entry.copy(state = "DOWNLOADING", bytesTotal = totalBytes, bytesDone = done)
                                        )
                                        updateSpeed(done)
                                    }
                                }
                            }
                        }
                        jobs.awaitAll()
                    }
                }
            } finally {
                runCatching { raf.close() }
            }
        }
    }

    private suspend fun fetchChunkWithRetry(url: String, start: Long, end: Long): ByteArray {
        var lastErr: Exception? = null
        for (attempt in 0..2) {
            val req = Request.Builder()
                .url(url)
                .header("Range", "bytes=$start-$end")
                .get()
                .build()
            try {
                httpClient.newCall(req).execute().use { r ->
                    if (r.isSuccessful) return r.body?.bytes() ?: ByteArray(0)
                    throw IOException("HTTP ${r.code}")
                }
            } catch (e: Exception) {
                lastErr = e
                if (attempt < 2) delay(250L * (attempt + 1))
            }
        }
        throw lastErr ?: IOException("Gagal mengunduh chunk $start-$end")
    }

    private fun writeStreamDirect(resp: Response, entry: DesktopDownloadEntry, total: Long, tmp: File) {
        val body = resp.body ?: throw IOException("Body respons kosong")
        tmp.outputStream().buffered().use { out ->
            body.byteStream().use { input ->
                val buffer = ByteArray(64 * 1024)
                var done = 0L
                var lastUiPush = 0L
                while (true) {
                    val read = input.read(buffer)
                    if (read < 0) break
                    out.write(buffer, 0, read)
                    done += read

                    val now = System.currentTimeMillis()
                    if (now - lastUiPush > 800L) {
                        lastUiPush = now
                        DesktopDatabase.upsertDownload(
                            entry.copy(state = "DOWNLOADING", bytesTotal = total, bytesDone = done)
                        )
                        updateSpeed(done)
                    }
                }
                out.flush()
            }
        }
    }

    @Volatile private var lastSpeedTimestamp = 0L
    @Volatile private var lastBytesSample = 0L

    private fun updateSpeed(done: Long) {
        val now = System.currentTimeMillis()
        val dt = now - lastSpeedTimestamp
        if (dt >= 800L) {
            val db = done - lastBytesSample
            if (db >= 0 && dt > 0) {
                val bytesPerSec = (db * 1000L) / dt
                _currentSpeed.value = if (bytesPerSec >= 1024 * 1024) {
                    String.format(Locale.US, "%.1f MB/s", bytesPerSec / (1024f * 1024f))
                } else if (bytesPerSec >= 1024) {
                    String.format(Locale.US, "%d KB/s", bytesPerSec / 1024)
                } else {
                    ""
                }
            }
            lastSpeedTimestamp = now
            lastBytesSample = done
        }
    }

    fun cancel(videoId: String) {
        val row = DesktopDatabase.getDownload(videoId) ?: return
        if (row.state == "QUEUED" || row.state == "DOWNLOADING") {
            DesktopDatabase.upsertDownload(row.copy(state = "CANCELED", errorMessage = "Dibatalkan"))
        }
    }

    fun remove(videoId: String) {
        cancel(videoId)
        val row = DesktopDatabase.getDownload(videoId)
        row?.filePath?.let { runCatching { File(it).delete() } }
        downloadsDir.listFiles { f -> f.name.startsWith("$videoId.") }?.forEach {
            runCatching { it.delete() }
        }
        DesktopDatabase.deleteDownload(videoId)
    }

    fun retry(videoId: String) {
        val row = DesktopDatabase.getDownload(videoId) ?: return
        DesktopDatabase.upsertDownload(row.copy(state = "QUEUED", errorMessage = null, bytesDone = 0L))
        triggerProcessQueue()
    }

    fun resumeAll() {
        val downloads = DesktopDatabase.downloads.value
        downloads.filter { it.state == "DOWNLOADING" }.forEach {
            DesktopDatabase.upsertDownload(it.copy(state = "QUEUED"))
        }
        if (DesktopDatabase.downloads.value.any { it.state == "QUEUED" }) {
            triggerProcessQueue()
        }
    }

    fun cancelAll() {
        DesktopDatabase.downloads.value
            .filter { it.state == "QUEUED" || it.state == "DOWNLOADING" }
            .forEach { DesktopDatabase.upsertDownload(it.copy(state = "CANCELED")) }
    }

    fun retryAllFailed() {
        DesktopDatabase.downloads.value
            .filter { it.state == "ERROR" || it.state == "CANCELED" }
            .forEach { DesktopDatabase.upsertDownload(it.copy(state = "QUEUED", errorMessage = null, bytesDone = 0L)) }
        triggerProcessQueue()
    }
}
