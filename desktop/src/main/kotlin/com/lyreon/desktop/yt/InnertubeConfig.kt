package com.lyreon.desktop.yt

import com.lyreon.desktop.core.LyreonLog
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.util.concurrent.ConcurrentHashMap

internal object InnertubeConfig {

    private const val TAG = "InnertubeConfig"

    private const val FALLBACK_KEY = "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8"
    private const val FALLBACK_WEB_VERSION = "2.20260917.01.00"
    private const val FALLBACK_ANDROID_VERSION = "19.45.38"
    const val FALLBACK_VISITOR = "CgtEOF8zTERaSW8xMCjM9rDVBjIKCgJJRBIEGgAgbg%3D%3D"

    @Volatile private var apiKey: String = FALLBACK_KEY
    @Volatile private var webVersion: String = FALLBACK_WEB_VERSION
    @Volatile private var androidVersion: String = FALLBACK_ANDROID_VERSION
    @Volatile private var visitorData: String? = FALLBACK_VISITOR
    @Volatile private var playerJsUrl: String? = null
    @Volatile private var signatureTs: Int = 20710

    private val fetched = ConcurrentHashMap.newKeySet<String>()
    private const val RETRY_AFTER_MS = 5L * 60_000L

    @Volatile private var scrapeOk = false
    @Volatile private var lastScrapeAtMs = 0L

    @Volatile private var visitorSource: String = "-"
    private const val VISITOR_TTL_MS = 12L * 60L * 60L * 1000L
    @Volatile private var visitorFetchedAtMs = 0L
    private val VISITOR_REGEX = Regex("^Cg[t|s]")

    fun warmUp(ioClient: okhttp3.OkHttpClient, userAgent: String = PlayerClientLadder.WEB_UA_FIREFOX) {
        ensure(ioClient, userAgent)
        Thread {
            ensureVisitorData(ioClient, userAgent)
        }.start()
    }

    fun ensure(ioClient: okhttp3.OkHttpClient, userAgent: String) {
        if (scrapeOk) return
        val now = System.currentTimeMillis()
        if (lastScrapeAtMs > 0L && now - lastScrapeAtMs < RETRY_AFTER_MS) return
        lastScrapeAtMs = now
        Thread {
            runCatching {
                val req = Request.Builder()
                    .url("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
                    .header("User-Agent", userAgent)
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .get()
                    .build()
                ioClient.newCall(req).execute().use { resp ->
                    val html = resp.body?.string().orEmpty()
                    apiKey = Regex("\"INNERTUBE_API_KEY\"\\s*:\\s*\"([^\"]+)\"").find(html)
                        ?.groupValues?.getOrNull(1) ?: apiKey
                    webVersion = Regex("\"INNERTUBE_CONTEXT_CLIENT_VERSION\"\\s*:\\s*\"([^\"]+)\"").find(html)
                        ?.groupValues?.getOrNull(1) ?: webVersion
                    playerJsUrl = Regex("\"PLAYER_JS_URL\"\\s*:\\s*\"([^\"]+)\"").find(html)
                        ?.groupValues?.getOrNull(1)
                        ?: Regex("src=\"(/s/player/[^\"]+/base\\.js)\"").find(html)?.groupValues?.getOrNull(1)
                        ?: Regex("src=\"(https://www\\.youtube\\.com/s/player/[^\"]+/base\\.js)\"").find(html)?.groupValues?.getOrNull(1)
                    signatureTs = Regex("\"STS\"\\s*:\\s*(\\d{4,6})").find(html)
                        ?.groupValues?.getOrNull(1)?.toIntOrNull() ?: signatureTs
                    scrapeOk = html.isNotBlank()
                    if (scrapeOk) LyreonLog.i(TAG, "scrape ytcfg OK: versi=$webVersion sts=$signatureTs")
                }
            }.onFailure { e ->
                LyreonLog.w(TAG, "ensure() gagal scrape: ${e.message} — pakai nilai cadangan")
            }
        }.start()
    }

    @Synchronized
    fun invalidateVisitor() {
        visitorData = null
        visitorSource = "-"
        visitorFetchedAtMs = 0L
        fetched.remove("visitor")
        LyreonLog.i(TAG, "visitorData dibuang — akan diambil ulang pada permintaan berikut")
    }

    @Synchronized
    fun ensureVisitorData(ioClient: okhttp3.OkHttpClient, userAgent: String) {
        val existing = visitorData
        if (existing != null && existing != FALLBACK_VISITOR && !isVisitorStale()) return

        val fromSwJs = runCatching { fetchVisitorFromSwJs(ioClient) }.getOrNull()
        if (fromSwJs != null) {
            visitorData = fromSwJs
            visitorSource = "sw.js"
            visitorFetchedAtMs = System.currentTimeMillis()
            LyreonLog.i(TAG, "visitorData dari sw.js_data: ${fromSwJs.take(12)}… (${fromSwJs.length} char)")
            return
        }

        runCatching {
            val req = Request.Builder()
                .url("https://www.youtube.com/youtubei/v1/guide?prettyPrint=false")
                .header("User-Agent", userAgent)
                .header("Origin", "https://www.youtube.com")
                .header("Referer", "https://www.youtube.com/")
                .post(
                    InnertubeRequest.baseContextJson(webVersion)
                        .toRequestBody("application/json".toMediaType()),
                )
                .build()
            ioClient.newCall(req).execute().use { resp ->
                val body = JSONObject(resp.body?.string().orEmpty())
                val v = body.optString("visitorData").takeIf { it.isNotBlank() }
                    ?: body.optJSONObject("responseContext")?.optString("visitorData")
                        ?.takeIf { it.isNotBlank() }
                if (v != null) {
                    visitorData = v
                    visitorSource = "guide"
                    visitorFetchedAtMs = System.currentTimeMillis()
                    LyreonLog.i(TAG, "visitorData dari guide: ${v.take(12)}…")
                }
            }
        }.onFailure { e ->
            LyreonLog.w(TAG, "gagal ambil visitorData: ${e.javaClass.simpleName}: ${e.message}")
        }
    }

    private fun isVisitorStale(): Boolean =
        visitorFetchedAtMs > 0L && System.currentTimeMillis() - visitorFetchedAtMs > VISITOR_TTL_MS

    private fun fetchVisitorFromSwJs(ioClient: okhttp3.OkHttpClient): String? {
        val req = Request.Builder()
            .url("https://music.youtube.com/sw.js_data")
            .header("User-Agent", PlayerClientLadder.WEB_UA_FIREFOX)
            .header("Accept", "application/json")
            .header("Accept-Language", "en-US,en;q=0.9")
            .header("Referer", "https://music.youtube.com/")
            .get()
            .build()
        val text = ioClient.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) return null
            resp.body?.string().orEmpty()
        }
        if (text.isBlank()) return null
        val start = text.indexOfFirst { it == '[' || it == '{' }
        if (start < 0) return null
        val root = runCatching { JSONTokener(text.substring(start)).nextValue() }.getOrNull()
            ?: return null

        val arr0 = (root as? JSONArray)?.optJSONArray(0)
        val arr2 = arr0?.optJSONArray(2)
        findVisitorIn(arr2)?.let { return it }
        return findVisitorIn(root, depth = 0)
    }

    private fun findVisitorIn(node: Any?, depth: Int = 0): String? {
        if (node == null || depth > 12) return null
        return when (node) {
            is String -> if (VISITOR_REGEX.containsMatchIn(node) && node.length >= 20) node else null
            is JSONArray -> {
                for (i in 0 until node.length()) {
                    val found = findVisitorIn(node.opt(i), depth + 1)
                    if (found != null) return found
                }
                null
            }
            is JSONObject -> {
                val v = node.optString("visitorData").takeIf { it.isNotBlank() && VISITOR_REGEX.containsMatchIn(it) }
                    ?: node.optJSONObject("responseContext")?.optString("visitorData")?.takeIf { it.isNotBlank() }
                v
            }
            else -> null
        }
    }

    fun adoptVisitor(root: JSONObject?): Boolean {
        if (root == null || visitorData != null) return false
        val v = root.optJSONObject("responseContext")?.optString("visitorData")?.takeIf { it.isNotBlank() }
            ?: return false
        visitorData = v
        visitorSource = "response"
        visitorFetchedAtMs = System.currentTimeMillis()
        LyreonLog.i(TAG, "visitorData diadopsi dari responseContext: ${v.take(12)}…")
        return true
    }

    fun refreshVisitor(ioClient: okhttp3.OkHttpClient, userAgent: String) {
        invalidateVisitor()
        ensureVisitorData(ioClient, userAgent)
    }

    fun visitorOrigin(): String = if (visitorData == null) "-" else visitorSource
    fun apiKey(): String = apiKey
    fun signatureTimestamp(): Int? = if (signatureTs > 0) signatureTs else null
    fun webClientVersion(): String = webVersion
    fun androidClientVersion(): String = androidVersion
    fun visitor(): String? = visitorData
    fun baseJsUrl(): String? = playerJsUrl?.let { if (it.startsWith("//")) "https:$it" else it }
}
