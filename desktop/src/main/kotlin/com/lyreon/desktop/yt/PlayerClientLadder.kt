package com.lyreon.desktop.yt

import com.lyreon.desktop.core.LyreonLog
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

enum class ClientVerdict {
    USABLE,
    REJECTED_BY_CDN,
    SABR_ONLY,
    HLS_ONLY,
    DRM_ONLY,
    PLAYABILITY_BLOCKED,
    NO_STREAMING_DATA,
    INVALID_RESPONSE,
    TRANSPORT_ERROR,
}

data class PlayerClientSpec(
    val key: String,
    val clientName: String,
    val clientVersion: String,
    val clientId: String,
    val userAgent: String,
    val host: String = "music.youtube.com",
    val altHost: String? = "www.youtube.com",
    val deviceMake: String? = null,
    val deviceModel: String? = null,
    val osName: String? = null,
    val osVersion: String? = null,
    val androidSdkVersion: Int = 0,
    val useSignatureTimestamp: Boolean = false,
    val useWebPoTokens: Boolean = false,
    val loginRequired: Boolean = false,
    val preferManifest: Boolean = false,
    val embedUrlValue: String? = null,
    val probeOnly: Boolean = false,
    val note: String = ""
) {
    val playableAnonymous: Boolean get() = !loginRequired && !useWebPoTokens && !probeOnly
}

internal object PlayerClientLadder {
    private const val TAG = "PlayerClientLadder"
    private const val COOLDOWN_MS = 180_000L
    private const val EXTRACTOR_BYPASS_MS = 600_000L
    private const val EXTRACTOR_BYPASS_AFTER = 2
    private const val DIAGNOSTIC_LIMIT = 40

    const val WEB_UA_FIREFOX = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:140.0) Gecko/20100101 Firefox/140.0"
    const val VISIONOS_UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.0 Safari/605.1.15"
    const val TIZEN_TV_UA = "Mozilla/5.0(SMART-TV; Linux; Tizen 4.0.0.2) AppleWebkit/605.1.15 (KHTML, like Gecko) SamsungBrowser/9.2 TV Safari/605.1.15"
    const val SAFARI_MAC_UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.5 Safari/605.1.15,gzip(gfe)"
    const val MWEB_UA = "Mozilla/5.0 (iPad; CPU OS 16_7_10 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1,gzip(gfe)"
    const val CHROME_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36,gzip(gfe)"

    private val SPECS: List<PlayerClientSpec> = listOf(
        PlayerClientSpec(
            key = "visionos",
            clientName = "VISIONOS",
            clientVersion = "0.1",
            clientId = "101",
            userAgent = VISIONOS_UA,
            host = "music.youtube.com",
            altHost = "www.youtube.com",
            deviceMake = "Apple",
            deviceModel = "RealityDevice14,1",
            osName = "visionOS",
            osVersion = "1.3.21O771",
            androidSdkVersion = 0,
            useSignatureTimestamp = false,
            useWebPoTokens = false,
            loginRequired = false,
            preferManifest = false,
            embedUrlValue = null,
            probeOnly = false,
            note = "satu-satunya klien yang terukur melayani file utuh (100% 206)"
        ),
        PlayerClientSpec(
            key = "android_vr",
            clientName = "ANDROID_VR",
            clientVersion = "1.65.10",
            clientId = "28",
            userAgent = "com.google.android.apps.youtube.vr.oculus/1.65.10 (Linux; U; Android 12L; eureka-user Build/SQ3A.220605.009.A1) gzip",
            host = "music.youtube.com",
            altHost = "www.youtube.com",
            deviceMake = "Oculus",
            deviceModel = "Quest 3",
            osName = "Android",
            osVersion = "12L",
            androidSdkVersion = 32,
            useSignatureTimestamp = false,
            useWebPoTokens = false,
            loginRequired = false,
            preferManifest = false,
            embedUrlValue = null,
            probeOnly = false,
            note = "pin yt-dlp/YouTube.js — WAJIB visitorData, tanpa buildId/cronet/packageName"
        ),
        PlayerClientSpec(
            key = "android_vr_1_43_32",
            clientName = "ANDROID_VR",
            clientVersion = "1.43.32",
            clientId = "28",
            userAgent = "com.google.android.apps.youtube.vr.oculus/1.43.32 (Linux; U; Android 12; en_US; Quest 3; Build/SQ3A.220605.009.A1; Cronet/107.0.5284.2)",
            host = "music.youtube.com",
            altHost = "www.youtube.com",
            deviceMake = "Oculus",
            deviceModel = "Quest 3",
            osName = "Android",
            osVersion = "12",
            androidSdkVersion = 32,
            useSignatureTimestamp = false,
            useWebPoTokens = false,
            loginRequired = false,
            preferManifest = false,
            embedUrlValue = null,
            probeOnly = false,
            note = "bitrate non-adaptive (audio tak patah), tanpa AV1; ter-gate per versi"
        ),
        PlayerClientSpec(
            key = "ipados",
            clientName = "IOS",
            clientVersion = "21.03.3",
            clientId = "5",
            userAgent = "com.google.ios.youtube/21.03.3 (iPad7,6; U; CPU iPadOS 17_7_10 like Mac OS X; en-US)",
            host = "music.youtube.com",
            altHost = "www.youtube.com",
            deviceMake = "Apple",
            deviceModel = "iPad7,6",
            osName = "iPadOS",
            osVersion = "17.7.10.21H450",
            androidSdkVersion = 0,
            useSignatureTimestamp = false,
            useWebPoTokens = false,
            loginRequired = false,
            preferManifest = false,
            embedUrlValue = null,
            probeOnly = false,
            note = "pratinjau ~1 MiB — hanya bila semua di atasnya gagal"
        ),
        PlayerClientSpec(
            key = "ios",
            clientName = "IOS",
            clientVersion = "21.03.1",
            clientId = "5",
            userAgent = "com.google.ios.youtube/21.03.1 (iPhone16,2; U; CPU iOS 18_2 like Mac OS X;)",
            host = "music.youtube.com",
            altHost = "www.youtube.com",
            deviceMake = "Apple",
            deviceModel = "iPhone16,2",
            osName = "iOS",
            osVersion = "18.2.22C152",
            androidSdkVersion = 0,
            useSignatureTimestamp = false,
            useWebPoTokens = false,
            loginRequired = false,
            preferManifest = false,
            embedUrlValue = null,
            probeOnly = false,
            note = "pratinjau ~1 MiB — cadangan paling akhir"
        ),
        PlayerClientSpec(
            key = "web_safari",
            clientName = "WEB",
            clientVersion = "",
            clientId = "1",
            userAgent = SAFARI_MAC_UA,
            host = "www.youtube.com",
            altHost = null,
            deviceMake = null,
            deviceModel = null,
            osName = null,
            osVersion = null,
            androidSdkVersion = 0,
            useSignatureTimestamp = false,
            useWebPoTokens = true,
            loginRequired = false,
            preferManifest = true,
            embedUrlValue = null,
            probeOnly = false,
            note = "Safari UA -> HLS pre-merged (m3u8); URL langsungnya butuh poToken"
        ),
        PlayerClientSpec(
            key = "tv_simply",
            clientName = "TVHTML5_SIMPLY",
            clientVersion = "1.0",
            clientId = "75",
            userAgent = CHROME_UA,
            host = "www.youtube.com",
            altHost = null,
            deviceMake = null,
            deviceModel = null,
            osName = null,
            osVersion = null,
            androidSdkVersion = 0,
            useSignatureTimestamp = false,
            useWebPoTokens = true,
            loginRequired = false,
            preferManifest = true,
            embedUrlValue = null,
            probeOnly = false,
            note = "HTTPS butuh poToken, HLS tidak"
        ),
        PlayerClientSpec(
            key = "tvhtml5",
            clientName = "TVHTML5",
            clientVersion = "7.20260213.00.00",
            clientId = "7",
            userAgent = TIZEN_TV_UA,
            host = "music.youtube.com",
            altHost = "www.youtube.com",
            deviceMake = null,
            deviceModel = null,
            osName = null,
            osVersion = null,
            androidSdkVersion = 0,
            useSignatureTimestamp = true,
            useWebPoTokens = true,
            loginRequired = true,
            preferManifest = false,
            embedUrlValue = null,
            probeOnly = false,
            note = "Meld: untuk track unggahan pribadi (MLPT); butuh login"
        ),
        PlayerClientSpec(
            key = "web_creator",
            clientName = "WEB_CREATOR",
            clientVersion = "1.20260213.00.00",
            clientId = "62",
            userAgent = WEB_UA_FIREFOX,
            host = "music.youtube.com",
            altHost = "www.youtube.com",
            deviceMake = null,
            deviceModel = null,
            osName = null,
            osVersion = null,
            androidSdkVersion = 0,
            useSignatureTimestamp = true,
            useWebPoTokens = true,
            loginRequired = true,
            preferManifest = false,
            embedUrlValue = null,
            probeOnly = false,
            note = "satu-satunya klien OK untuk konten dibatasi umur — butuh login + pot="
        ),
        PlayerClientSpec(
            key = "web_remix",
            clientName = "WEB_REMIX",
            clientVersion = "1.20260213.01.00",
            clientId = "67",
            userAgent = WEB_UA_FIREFOX,
            host = "music.youtube.com",
            altHost = "www.youtube.com",
            deviceMake = null,
            deviceModel = null,
            osName = null,
            osVersion = null,
            androidSdkVersion = 0,
            useSignatureTimestamp = true,
            useWebPoTokens = true,
            loginRequired = false,
            preferManifest = false,
            embedUrlValue = null,
            probeOnly = false,
            note = "klien metadata Meld; formatnya di balik cipher/n-challenge"
        ),
        PlayerClientSpec(
            key = "web",
            clientName = "WEB",
            clientVersion = "2.20260213.00.00",
            clientId = "1",
            userAgent = WEB_UA_FIREFOX,
            host = "music.youtube.com",
            altHost = "www.youtube.com",
            deviceMake = null,
            deviceModel = null,
            osName = null,
            osVersion = null,
            androidSdkVersion = 0,
            useSignatureTimestamp = false,
            useWebPoTokens = false,
            loginRequired = false,
            preferManifest = false,
            embedUrlValue = null,
            probeOnly = false,
            note = "SABR-only tanpa poToken"
        ),
        PlayerClientSpec(
            key = "android_vr_1_61_48",
            clientName = "ANDROID_VR",
            clientVersion = "1.61.48",
            clientId = "28",
            userAgent = "com.google.android.apps.youtube.vr.oculus/1.61.48 (Linux; U; Android 12; en_US; Quest 3; Build/SQ3A.220605.009.A1; Cronet/132.0.6808.3)",
            host = "music.youtube.com",
            altHost = "www.youtube.com",
            deviceMake = "Oculus",
            deviceModel = "Quest 3",
            osName = "Android",
            osVersion = "12",
            androidSdkVersion = 32,
            useSignatureTimestamp = false,
            useWebPoTokens = false,
            loginRequired = false,
            preferManifest = false,
            embedUrlValue = null,
            probeOnly = true,
            note = "KONTROL: versi ini ter-gate (LOGIN_REQUIRED) sedangkan 1.65.10 OK"
        ),
        PlayerClientSpec(
            key = "mweb",
            clientName = "MWEB",
            clientVersion = "",
            clientId = "2",
            userAgent = MWEB_UA,
            host = "www.youtube.com",
            altHost = null,
            deviceMake = null,
            deviceModel = null,
            osName = null,
            osVersion = null,
            androidSdkVersion = 0,
            useSignatureTimestamp = false,
            useWebPoTokens = true,
            loginRequired = false,
            preferManifest = false,
            embedUrlValue = null,
            probeOnly = true,
            note = "sering memberi URL, tapi URL web butuh n-transform -> 403/throttle"
        )
    )

    private val lastGood = AtomicReference<String?>(null)
    private val cooldownUntil = ConcurrentHashMap<String, Long>()
    private val diagnostics = ConcurrentLinkedDeque<String>()
    @Volatile private var lastSabrAtMs = 0L
    private val sabrTotal = AtomicInteger(0)
    private val extractorSabrStreak = AtomicInteger(0)
    @Volatile private var extractorBypassUntilMs = 0L

    private fun timestamp(): String = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())

    fun ordered(forPlayback: Boolean = true): List<PlayerClientSpec> {
        val base = SPECS.filter { !forPlayback || it.playableAnonymous }.toMutableList()
        val good = lastGood.get()
        if (good != null) {
            val index = base.indexOfFirst { it.key == good }
            if (index > 0) {
                base.add(0, base.removeAt(index))
            }
        }
        val now = System.currentTimeMillis()
        val warm = ArrayList<PlayerClientSpec>(base.size)
        val cold = ArrayList<PlayerClientSpec>(4)
        for (spec in base) {
            val cd = cooldownUntil[spec.key] ?: 0L
            if (cd > now) {
                cold.add(spec)
            } else {
                warm.add(spec)
            }
        }
        return warm + cold
    }

    fun specOf(key: String): PlayerClientSpec? = SPECS.firstOrNull { it.key == key }

    fun hlsSpecs(): List<PlayerClientSpec> = SPECS.filter { it.preferManifest }

    fun streamUserAgentFor(clientName: String?, clientVersion: String?): String? {
        if (clientName.isNullOrBlank()) return null
        val exact = SPECS.firstOrNull {
            it.clientName.equals(clientName, ignoreCase = true) &&
                    !clientVersion.isNullOrBlank() && it.clientVersion == clientVersion
        }
        return (exact ?: SPECS.firstOrNull { it.clientName.equals(clientName, ignoreCase = true) })?.userAgent
    }

    fun allSpecs(): List<PlayerClientSpec> = SPECS

    fun note(key: String, verdict: ClientVerdict, elapsedMs: Long, detail: String = "") {
        if (verdict == ClientVerdict.USABLE || verdict == ClientVerdict.HLS_ONLY) {
            lastGood.set(key)
            cooldownUntil.remove(key)
        }
        if (verdict == ClientVerdict.SABR_ONLY || verdict == ClientVerdict.DRM_ONLY || verdict == ClientVerdict.REJECTED_BY_CDN) {
            lastSabrAtMs = System.currentTimeMillis()
            cooldownUntil[key] = lastSabrAtMs + COOLDOWN_MS
        }
        if (verdict == ClientVerdict.SABR_ONLY) {
            sabrTotal.incrementAndGet()
        }
        if (verdict == ClientVerdict.TRANSPORT_ERROR) {
            cooldownUntil[key] = System.currentTimeMillis() + COOLDOWN_MS
        }
        val suffix = if (detail.isBlank()) "" else " · $detail"
        push("${timestamp()} $key -> $verdict (${elapsedMs}ms)$suffix")
        if (verdict == ClientVerdict.SABR_ONLY) {
            LyreonLog.w(TAG, "klien '$key' membalas SABR-only (tanpa URL stream). Total kejadian SABR sesi ini: ${sabrTotal.get()}")
        }
        if (verdict == ClientVerdict.DRM_ONLY) {
            LyreonLog.w(TAG, "klien '$key' membalas format ber-DRM (butuh cookie guest) — dilewati")
        }
        if (verdict == ClientVerdict.REJECTED_BY_CDN) {
            LyreonLog.w(TAG, "klien '$key' memberi URL tetapi CDN menolaknya ($detail) — lanjut ke klien berikutnya")
        }
    }

    fun noteExtractorSabr(detail: String) {
        lastSabrAtMs = System.currentTimeMillis()
        sabrTotal.incrementAndGet()
        val streak = extractorSabrStreak.incrementAndGet()
        if (streak >= EXTRACTOR_BYPASS_AFTER) {
            extractorBypassUntilMs = System.currentTimeMillis() + EXTRACTOR_BYPASS_MS
            push("${timestamp()} extractor di-bypass 10 menit (SABR ${streak}x beruntun) — langsung ke tangga klien")
            LyreonLog.w(TAG, "extractor di-bypass $EXTRACTOR_BYPASS_MS ms setelah $streak kegagalan SABR")
        } else {
            push("${timestamp()} extractor -> SABR_ONLY · $detail")
        }
        LyreonLog.w(TAG, "extractor membalas SABR-only ($detail) — total sesi ini: ${sabrTotal.get()}")
    }

    fun noteExtractorSuccess() {
        if (extractorSabrStreak.get() > 0 || extractorBypassUntilMs > 0L) {
            push("${timestamp()} extractor pulih — bypass dicabut")
        }
        extractorSabrStreak.set(0)
        extractorBypassUntilMs = 0L
    }

    fun extractorBypassed(): Boolean = System.currentTimeMillis() < extractorBypassUntilMs

    fun sabrPressureRecently(windowMs: Long = 600_000L): Boolean =
        System.currentTimeMillis() - lastSabrAtMs < windowMs

    fun sabrCount(): Int = sabrTotal.get()

    fun report(): List<String> = diagnostics.toList()

    fun push(line: String) {
        diagnostics.addFirst(line)
        while (diagnostics.size > DIAGNOSTIC_LIMIT) {
            diagnostics.pollLast()
        }
    }

    fun reset() {
        diagnostics.clear()
        cooldownUntil.clear()
        lastGood.set(null)
        lastSabrAtMs = 0L
        sabrTotal.set(0)
        extractorSabrStreak.set(0)
        extractorBypassUntilMs = 0L
    }

    fun inspect(root: JSONObject, videoId: String): ClientVerdict {
        val returnedId = root.optJSONObject("videoDetails")?.optString("videoId").orEmpty()
        if (returnedId.isNotBlank() && returnedId != videoId) {
            return ClientVerdict.INVALID_RESPONSE
        }
        val status = root.optJSONObject("playabilityStatus")?.optString("status").orEmpty()
        if (status.isNotBlank() && !status.equals("OK", ignoreCase = true)) {
            return ClientVerdict.PLAYABILITY_BLOCKED
        }
        val streamingData = root.optJSONObject("streamingData") ?: return ClientVerdict.NO_STREAMING_DATA
        if (hasDirectStreamUrl(streamingData)) {
            return ClientVerdict.USABLE
        }
        val hls = streamingData.optString("hlsManifestUrl")
        if (!hls.isNullOrBlank()) {
            return ClientVerdict.HLS_ONLY
        }
        val adaptive = streamingData.optJSONArray("adaptiveFormats")
        val formats = streamingData.optJSONArray("formats")
        val formatCount = (adaptive?.length() ?: 0) + (formats?.length() ?: 0)
        if (formatCount > 0 && allFormatsDrmLocked(streamingData)) {
            return ClientVerdict.DRM_ONLY
        }
        val sabrUrl = streamingData.optString("serverAbrStreamingUrl").orEmpty()
        return if (formatCount > 0 || sabrUrl.isNotBlank()) ClientVerdict.SABR_ONLY else ClientVerdict.NO_STREAMING_DATA
    }

    fun hasDirectStreamUrl(streamingData: JSONObject): Boolean {
        listOf("adaptiveFormats", "formats").forEach { key ->
            val array = streamingData.optJSONArray(key) ?: return@forEach
            for (i in 0 until array.length()) {
                val format = array.optJSONObject(i) ?: continue
                if (isDrmLocked(format)) continue
                if (!format.optString("url").isNullOrBlank()) return true
                if (!format.optString("signatureCipher").isNullOrBlank()) return true
                if (!format.optString("cipher").isNullOrBlank()) return true
            }
        }
        return false
    }

    fun isDrmLocked(format: JSONObject): Boolean {
        val families = format.optJSONArray("drmFamilies")
        if (families != null && families.length() > 0) return true
        if (format.optInt("drmTrackCount", 0) > 0) return true
        if (format.optString("drmFamilies").isNotBlank()) return true
        if (format.optString("drmTrackType").isNotBlank()) return true
        return false
    }

    fun allFormatsDrmLocked(streamingData: JSONObject): Boolean {
        var total = 0
        var locked = 0
        listOf("adaptiveFormats", "formats").forEach { key ->
            val array = streamingData.optJSONArray(key) ?: return@forEach
            for (i in 0 until array.length()) {
                val format = array.optJSONObject(i) ?: continue
                total++
                if (isDrmLocked(format)) locked++
            }
        }
        return total > 0 && locked == total
    }

    fun hlsManifest(root: JSONObject?): String? =
        root?.optJSONObject("streamingData")?.optString("hlsManifestUrl")?.takeIf { it.isNotBlank() }

    fun playabilityReason(root: JSONObject): String {
        val status = root.optJSONObject("playabilityStatus") ?: return ""
        val reason = status.optString("reason").orEmpty()
        val s = status.optString("status").orEmpty()
        return if (reason.isBlank()) s else "$s: $reason"
    }

    fun snapshot(): String = buildString {
        append("ladder: playback=").append(ordered(true).joinToString(",") { it.key }).append('\n')
        append("hls=").append(hlsSpecs().joinToString(",") { it.key }).append('\n')
        append("probe=").append(ordered(false).joinToString(",") { it.key }).append('\n')
        append("lastGood=").append(lastGood.get() ?: "-").append('\n')
        append("visitor=").append(InnertubeConfig.visitorOrigin())
            .append(" present=").append(InnertubeConfig.visitor() != null)
            .append(" sts=").append(InnertubeConfig.signatureTimestamp() ?: "-").append('\n')
        append("sabrTotal=").append(sabrTotal.get())
            .append(" extractorBypassed=").append(extractorBypassed()).append('\n')
    }
}
