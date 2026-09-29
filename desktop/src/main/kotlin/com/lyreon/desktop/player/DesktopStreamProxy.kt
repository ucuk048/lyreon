package com.lyreon.desktop.player

import com.lyreon.desktop.core.LyreonLog
import com.lyreon.desktop.download.DesktopDownloadManager
import com.lyreon.desktop.model.LyreonTrack
import com.lyreon.desktop.yt.PlayerClientLadder
import com.lyreon.desktop.yt.StreamUrlValidator
import com.lyreon.desktop.yt.YouTubeDesktopRepository
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.net.InetAddress
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object DesktopStreamProxy {

    private const val TAG = "DesktopStreamProxy"

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .followRedirects(true)
            .retryOnConnectionFailure(true)
            .build()
    }

    val cacheDir: File by lazy {
        val userHome = System.getProperty("user.home") ?: "."
        File(userHome, ".lyreon/cache").apply { mkdirs() }
    }

    private val trackRegistry = ConcurrentHashMap<String, LyreonTrack>()
    private val urlRegistry = ConcurrentHashMap<String, String>()

    private var server: HttpServer? = null
    var serverPort: Int = 0
        private set

    init {
        try {
            val s = HttpServer.create(InetSocketAddress(InetAddress.getByName("127.0.0.1"), 0), 0)
            serverPort = s.address.port
            s.createContext("/stream") { exchange ->
                handleStreamRequest(exchange)
            }
            s.executor = Executors.newCachedThreadPool { r ->
                Thread(r, "Lyreon-StreamProxy-Worker").apply { isDaemon = true }
            }
            s.start()
            server = s
            LyreonLog.i(TAG, "DesktopStreamProxy berjalan di http://127.0.0.1:$serverPort")
        } catch (e: Exception) {
            LyreonLog.e(TAG, "Gagal memulai DesktopStreamProxy: ${e.message}", e)
        }
    }

    fun getStreamUrl(track: LyreonTrack, directUrl: String? = null): String {
        trackRegistry[track.videoId] = track
        if (!directUrl.isNullOrBlank()) {
            urlRegistry[track.videoId] = directUrl
        }
        return "http://127.0.0.1:$serverPort/stream/${track.videoId}.m4a"
    }

    private fun handleStreamRequest(exchange: HttpExchange) {
        val method = exchange.requestMethod
        val uri = exchange.requestURI
        val range = exchange.requestHeaders.getFirst("Range")
        val ua = exchange.requestHeaders.getFirst("User-Agent")
        LyreonLog.i(TAG, "Incoming Proxy Req: $method $uri (Range: $range, UA: $ua)")
        try {
            val path = exchange.requestURI.path ?: ""
            val videoId = path.substringAfterLast('/')
                .removeSuffix(".m4a")
                .removeSuffix(".mp4")
                .takeIf { it.isNotBlank() }
                ?: exchange.requestURI.query?.substringAfter("v=")?.substringBefore('&')


            if (videoId.isNullOrBlank()) {
                exchange.sendResponseHeaders(400, -1)
                exchange.close()
                return
            }

            // 1. Periksa berkas lokal (Download selesai atau Cache tersimpan)
            val downloaded = DesktopDownloadManager.getDownloadedFile(videoId)
            val cached = File(cacheDir, "$videoId.m4a").takeIf { it.exists() && it.length() > 0 }
            val localFile = downloaded ?: cached

            if (localFile != null && localFile.exists()) {
                serveLocalFile(exchange, localFile)
                return
            }

            // 2. Stream online dari YouTube GoogleVideo CDN
            serveUpstream(exchange, videoId)
        } catch (e: Exception) {
            LyreonLog.w(TAG, "Error saat menangani request proxy: ${e.message}")
            try {
                exchange.sendResponseHeaders(500, -1)
            } catch (_: Exception) {}
            exchange.close()
        }
    }

    private fun serveLocalFile(exchange: HttpExchange, file: File) {
        val isHead = exchange.requestMethod.equals("HEAD", ignoreCase = true)
        val totalLength = file.length()
        val rangeHeader = exchange.requestHeaders.getFirst("Range")

        exchange.responseHeaders.set("Content-Type", "audio/mp4")
        exchange.responseHeaders.set("Accept-Ranges", "bytes")
        exchange.responseHeaders.set("Access-Control-Allow-Origin", "*")

        if (isHead) {
            exchange.responseHeaders.set("Content-Length", totalLength.toString())
            exchange.sendResponseHeaders(200, -1)
            exchange.close()
            return
        }

        if (!rangeHeader.isNullOrBlank() && rangeHeader.startsWith("bytes=")) {
            val rangeStr = rangeHeader.removePrefix("bytes=").trim()
            val parts = rangeStr.split("-")
            val start = parts[0].toLongOrNull() ?: 0L
            val end = if (parts.size > 1 && parts[1].isNotBlank()) {
                parts[1].toLongOrNull() ?: (totalLength - 1)
            } else {
                totalLength - 1
            }
            val clampedEnd = minOf(end, totalLength - 1)
            val sendLength = maxOf(0L, clampedEnd - start + 1)

            exchange.responseHeaders.set("Content-Range", "bytes $start-$clampedEnd/$totalLength")
            exchange.sendResponseHeaders(206, sendLength)

            try {
                RandomAccessFile(file, "r").use { raf ->
                    raf.seek(start)
                    val buffer = ByteArray(64 * 1024)
                    var remaining = sendLength
                    while (remaining > 0) {
                        val read = raf.read(buffer, 0, minOf(buffer.size.toLong(), remaining).toInt())
                        if (read == -1) break
                        exchange.responseBody.write(buffer, 0, read)
                        remaining -= read
                    }
                }
            } catch (_: java.io.IOException) {
                // Client terputus atau melakukan seek
            }
        } else {
            exchange.sendResponseHeaders(200, totalLength)
            try {
                file.inputStream().use { input ->
                    input.copyTo(exchange.responseBody)
                }
            } catch (_: java.io.IOException) {
                // Client terputus
            }
        }
        exchange.close()
    }

    private fun serveUpstream(exchange: HttpExchange, videoId: String) {
        var streamUrl = urlRegistry[videoId]
        if (streamUrl.isNullOrBlank()) {
            val track = trackRegistry[videoId] ?: LyreonTrack(videoId = videoId, title = "Stream", artist = "")
            val resolved = runCatching {
                kotlinx.coroutines.runBlocking {
                    YouTubeDesktopRepository.resolveStream(track)
                }
            }.getOrNull()

            if (resolved == null) {
                exchange.sendResponseHeaders(502, -1)
                exchange.close()
                return
            }
            streamUrl = resolved.url
            urlRegistry[videoId] = streamUrl
        }

        val isHead = exchange.requestMethod.equals("HEAD", ignoreCase = true)
        val ua = StreamUrlValidator.userAgentForUrl(streamUrl) ?: PlayerClientLadder.WEB_UA_FIREFOX
        val clientRange = exchange.requestHeaders.getFirst("Range")

        val reqBuilder = Request.Builder()
            .url(streamUrl)
            .header("User-Agent", ua)
            .header("Accept", "*/*")

        if (isHead) {
            reqBuilder.header("Range", "bytes=0-0")
        } else if (!clientRange.isNullOrBlank()) {
            reqBuilder.header("Range", clientRange)
        }

        var upstreamResp = httpClient.newCall(reqBuilder.build()).execute()

        // Jika URL kedaluwarsa (403 atau 410), perbarui URL sekali
        if (upstreamResp.code in listOf(403, 410)) {
            upstreamResp.close()
            val track = trackRegistry[videoId] ?: LyreonTrack(videoId = videoId, title = "Stream", artist = "")
            val refreshed = runCatching {
                kotlinx.coroutines.runBlocking {
                    YouTubeDesktopRepository.resolveStream(track)
                }
            }.getOrNull()

            if (refreshed != null) {
                streamUrl = refreshed.url
                urlRegistry[videoId] = streamUrl
                val newUa = StreamUrlValidator.userAgentForUrl(streamUrl) ?: PlayerClientLadder.WEB_UA_FIREFOX
                val retryReq = Request.Builder()
                    .url(streamUrl)
                    .header("User-Agent", newUa)
                    .header("Accept", "*/*")
                if (isHead) {
                    retryReq.header("Range", "bytes=0-0")
                } else if (!clientRange.isNullOrBlank()) {
                    retryReq.header("Range", clientRange)
                }
                upstreamResp = httpClient.newCall(retryReq.build()).execute()
            }
        }

        upstreamResp.use { resp ->
            val code = resp.code
            val totalLength = StreamUrlValidator.contentLengthFromUrl(streamUrl)
                ?: resp.header("Content-Range")?.substringAfterLast('/')?.toLongOrNull()
                ?: resp.body?.contentLength()
                ?: -1L

            exchange.responseHeaders.set("Content-Type", "audio/mp4")
            exchange.responseHeaders.set("Accept-Ranges", "bytes")
            exchange.responseHeaders.set("Access-Control-Allow-Origin", "*")

            if (isHead) {
                if (totalLength > 0) {
                    exchange.responseHeaders.set("Content-Length", totalLength.toString())
                }
                exchange.sendResponseHeaders(200, -1)
                exchange.close()
                return
            }

            resp.header("Content-Range")?.let {
                exchange.responseHeaders.set("Content-Range", it)
            }
            val clen = resp.body?.contentLength() ?: -1L
            exchange.sendResponseHeaders(code, if (clen > 0) clen else 0L)

            val bodyStream = resp.body?.byteStream()
            if (bodyStream != null) {
                val isFullStream = clientRange.isNullOrBlank() || clientRange == "bytes=0-"
                val partFile = if (isFullStream) File(cacheDir, "$videoId.part") else null
                val partOut = partFile?.let { runCatching { FileOutputStream(it) }.getOrNull() }

                val buffer = ByteArray(64 * 1024)
                val out = exchange.responseBody
                var bytesRead: Int
                var totalWritten = 0L

                try {
                    while (bodyStream.read(buffer).also { bytesRead = it } != -1) {
                        out.write(buffer, 0, bytesRead)
                        partOut?.write(buffer, 0, bytesRead)
                        totalWritten += bytesRead
                    }
                    out.flush()
                } catch (_: java.io.IOException) {
                    // Client terputus atau seek
                } finally {
                    partOut?.close()
                    if (isFullStream && partFile != null && totalWritten > 500_000L && (clen <= 0L || totalWritten == clen)) {
                        val finalCache = File(cacheDir, "$videoId.m4a")
                        partFile.renameTo(finalCache)
                    }
                }
            }
        }
        exchange.close()
    }
}
