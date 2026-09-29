package com.lyreon.desktop.download

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

/**
 * Mengunduh manifest HLS menjadi satu file audio yang utuh (.mp4 atau .ts)
 * tanpa memerlukan FFmpeg eksternal.
 */
object HlsFlatDownloader {

    data class Result(val bytes: Long, val container: Container)

    enum class Container(val suffix: String) {
        FMP4("mp4"),
        TS("ts"),
        UNKNOWN("bin"),
    }

    private const val MIN_VARIANT_BANDWIDTH = 400_000

    suspend fun downloadFlat(
        client: OkHttpClient,
        manifestUrl: String,
        target: File,
        isCancelled: suspend () -> Boolean,
        onProgress: suspend (doneBytes: Long, estimatedTotal: Long, segmentsDone: Int, segmentsTotal: Int) -> Unit,
    ): Result = withContext(Dispatchers.IO) {
        val master = fetchText(client, manifestUrl)

        val mediaUrl = pickVariantUrl(master, manifestUrl) ?: manifestUrl
        val media = if (mediaUrl == manifestUrl) master else fetchText(client, mediaUrl)

        val initUrl = parseInitSegment(media, mediaUrl)
        val segments = parseSegments(media, mediaUrl)
        if (segments.isEmpty()) {
            throw IOException("Manifest HLS tidak memuat segmen apa pun")
        }

        var written = 0L
        var firstSegmentSize = 0L
        val concurrency = 4
        target.outputStream().buffered().use { out ->
            if (initUrl != null) {
                val bytes = fetchBytes(client, initUrl)
                out.write(bytes)
                written += bytes.size
            }
            coroutineScope {
                val deferreds = HashMap<Int, Deferred<ByteArray>>()

                for (i in 0 until minOf(concurrency, segments.size)) {
                    deferreds[i] = async(Dispatchers.IO) {
                        fetchBytes(client, segments[i])
                    }
                }

                segments.indices.forEach { index ->
                    if (isCancelled()) throw IOException("Dibatalkan")

                    val ahead = index + concurrency
                    if (ahead < segments.size) {
                        deferreds[ahead] = async(Dispatchers.IO) {
                            fetchBytes(client, segments[ahead])
                        }
                    }

                    val bytes = deferreds.remove(index)!!.await()
                    out.write(bytes)
                    written += bytes.size
                    if (index == 0) firstSegmentSize = bytes.size.toLong()
                    val estimated = if (firstSegmentSize > 0) firstSegmentSize * segments.size else written
                    onProgress(written, estimated, index + 1, segments.size)
                }
            }
            out.flush()
        }

        val container = sniff(target)
        if (container == Container.UNKNOWN) {
            target.delete()
            throw IOException("Segmen HLS tidak membentuk file audio yang dikenali")
        }
        Result(written, container)
    }

    private fun pickVariantUrl(playlist: String, baseUrl: String): String? {
        val lines = playlist.lines()

        lines.forEach { line ->
            if (!line.startsWith("#EXT-X-MEDIA:")) return@forEach
            if (!line.contains("TYPE=AUDIO")) return@forEach
            val uri = attributeValue(line, "URI") ?: return@forEach
            return resolve(baseUrl, uri)
        }

        data class Variant(val bandwidth: Long, val url: String)
        val variants = ArrayList<Variant>()
        for (i in lines.indices) {
            val line = lines[i]
            if (!line.startsWith("#EXT-X-STREAM-INF:")) continue
            val next = lines.getOrNull(i + 1)?.trim().orEmpty()
            if (next.isEmpty() || next.startsWith("#")) continue
            val bandwidth = attributeValue(line, "BANDWIDTH")?.toLongOrNull() ?: 0L
            variants += Variant(bandwidth, resolve(baseUrl, next))
        }
        if (variants.isEmpty()) return null
        val decent = variants.filter { it.bandwidth >= MIN_VARIANT_BANDWIDTH }
        return (decent.minByOrNull { it.bandwidth } ?: variants.minByOrNull { it.bandwidth })?.url
    }

    private fun parseInitSegment(playlist: String, baseUrl: String): String? =
        playlist.lineSequence()
            .firstOrNull { it.startsWith("#EXT-X-MAP:") }
            ?.let { attributeValue(it, "URI") }
            ?.let { resolve(baseUrl, it) }

    private fun parseSegments(playlist: String, baseUrl: String): List<String> =
        playlist.lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() && !it.startsWith("#") }
            .map { resolve(baseUrl, it) }
            .toList()

    private fun attributeValue(line: String, key: String): String? {
        val idx = line.indexOf("$key=")
        if (idx < 0) return null
        val remainder = line.substring(idx + key.length + 1)
        if (remainder.startsWith("\"")) {
            val closeQuote = remainder.indexOf('\"', startIndex = 1)
            if (closeQuote > 0) {
                return remainder.substring(1, closeQuote).takeIf { it.isNotBlank() }
            }
        }
        return remainder.substringBefore(',').trim().takeIf { it.isNotBlank() }
    }

    private fun resolve(baseUrl: String, spec: String): String =
        if (spec.startsWith("http://") || spec.startsWith("https://")) {
            spec
        } else {
            runCatching { java.net.URL(java.net.URL(baseUrl), spec).toString() }
                .getOrDefault(spec)
        }

    private fun fetchText(client: OkHttpClient, url: String): String =
        fetchBytes(client, url).toString(Charsets.UTF_8)

    private fun fetchBytes(client: OkHttpClient, url: String): ByteArray {
        val request = Request.Builder().url(url).get().build()
        client.newCall(request).execute().use { resp ->
            if (!resp.isSuccessful) throw IOException("HTTP ${resp.code} saat mengambil $url")
            return resp.body?.bytes() ?: ByteArray(0)
        }
    }

    private fun sniff(file: File): Container {
        val head = ByteArray(192)
        val read = runCatching {
            file.inputStream().use { it.read(head) }
        }.getOrDefault(-1)
        if (read < 8) return Container.UNKNOWN
        if (head[4] == 'f'.code.toByte() && head[5] == 't'.code.toByte() &&
            head[6] == 'y'.code.toByte() && head[7] == 'p'.code.toByte()
        ) {
            return Container.FMP4
        }
        if (head[0] == 0x47.toByte() && (read < 189 || head[188] == 0x47.toByte())) {
            return Container.TS
        }
        return Container.UNKNOWN
    }
}
