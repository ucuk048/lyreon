package com.lyreon.desktop.yt

import com.lyreon.desktop.core.LyreonLog as Log
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

internal object StreamUrlValidator {

    private const val TAG = "StreamUrlValidator"

    private val validatorHttp: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()
    }

    private const val PROBE_CHUNK_BYTES = 512L * 1024L

    data class Verdict(
        val accepted: Boolean,
        val code: Int,
        val range: String,
        val isFullLength: Boolean = false,
    )

    fun validate(url: String, contentLength: Long? = null): Verdict {
        if (url.isBlank()) return Verdict(accepted = false, code = 0, range = "-")
        val clen = contentLength?.takeIf { it > 0 } ?: contentLengthFromUrl(url)
        val (rangeHeader, isFull) = if (clen != null && clen > 1) {
            val lastByte = clen - 1
            "bytes=$lastByte-$lastByte" to true
        } else {
            "bytes=0-${PROBE_CHUNK_BYTES - 1}" to false
        }

        val ua = userAgentForUrl(url) ?: PlayerClientLadder.WEB_UA_FIREFOX
        val reqBuilder = Request.Builder()
            .url(url)
            .header("Range", rangeHeader)
            .header("User-Agent", ua)
            .header("Accept", "*/*")
            .header("Accept-Language", "en-US,en;q=0.9")
            .get()

        return try {
            validatorHttp.newCall(reqBuilder.build()).execute().use { resp ->
                val code = resp.code
                val accepted = code in 200..299
                Verdict(accepted = accepted, code = code, range = rangeHeader, isFullLength = isFull)
            }
        } catch (e: IOException) {
            Log.w(TAG, "validasi URL gagal IO: ${e.javaClass.simpleName}: ${e.message}")
            Verdict(accepted = false, code = -1, range = rangeHeader, isFullLength = isFull)
        } catch (e: Exception) {
            Log.e(TAG, "validasi URL gagal tak terduga: ${e.javaClass.simpleName}: ${e.message}")
            Verdict(accepted = false, code = -1, range = rangeHeader, isFullLength = isFull)
        }
    }

    fun validateManifest(manifestUrl: String): Verdict = try {
        val req = Request.Builder()
            .url(manifestUrl)
            .header("User-Agent", PlayerClientLadder.WEB_UA_FIREFOX)
            .header("Accept", "*/*")
            .get()
            .build()
        validatorHttp.newCall(req).execute().use { resp ->
            val code = resp.code
            val body = resp.body?.string().orEmpty()
            val hasM3uTag = body.contains("#EXTM3U")
            val accepted = code in 200..299 && hasM3uTag
            Verdict(accepted = accepted, code = code, range = "m3u8-probe")
        }
    } catch (e: Exception) {
        Log.e(TAG, "validasi manifest gagal: ${e.javaClass.simpleName}: ${e.message}")
        Verdict(accepted = false, code = -1, range = "-")
    }

    fun userAgentForUrl(url: String): String? = try {
        val httpUrl = url.toHttpUrlOrNull()
        PlayerClientLadder.streamUserAgentFor(httpUrl?.queryParameter("c"), httpUrl?.queryParameter("cver"))
    } catch (e: Exception) {
        null
    }

    fun contentLengthFromUrl(url: String): Long? =
        Regex("[?&]clen=(\\d+)").find(url)?.groupValues?.getOrNull(1)?.toLongOrNull()

    fun describe(url: String): String = try {
        val httpUrl = url.toHttpUrlOrNull()
        val expire = httpUrl?.queryParameter("expire")?.toLongOrNull()
        val nowSec = System.currentTimeMillis() / 1000
        buildString {
            append("host=").append(httpUrl?.host ?: "?")
            append(" itag=").append(httpUrl?.queryParameter("itag") ?: "-")
            append(" mime=").append(httpUrl?.queryParameter("mime") ?: "-")
            append(" c=").append(httpUrl?.queryParameter("c") ?: "-")
            append(" cver=").append(httpUrl?.queryParameter("cver") ?: "-")
            append(" expire=").append(expire ?: "-")
            if (expire != null) append("(sisa ").append(expire - nowSec).append("s)")
            append(" hasPot=").append(httpUrl?.queryParameter("pot") != null)
            append(" nLen=").append(httpUrl?.queryParameter("n")?.length ?: -1)
            append(" cpn=").append(httpUrl?.queryParameter("cpn") ?: "-")
            append(" lmt=").append(httpUrl?.queryParameter("lmt") ?: "-")
            append(" sabr=").append(httpUrl?.queryParameter("sabr") ?: "-")
            append(" clen=").append(httpUrl?.queryParameter("clen") ?: "-")
        }
    } catch (e: Exception) {
        "url tak bisa diurai (${e.javaClass.simpleName})"
    }
}
