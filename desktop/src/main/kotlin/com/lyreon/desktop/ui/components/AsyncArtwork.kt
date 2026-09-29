package com.lyreon.desktop.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lyreon.desktop.ui.theme.LyreonCrimson
import com.lyreon.desktop.ui.theme.LyreonElevated
import com.lyreon.desktop.ui.theme.LyreonSurface
import com.lyreon.desktop.yt.YouTubeDesktopRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.io.ByteArrayInputStream
import java.util.concurrent.ConcurrentHashMap

object ArtworkCache {
    private val memoryCache = ConcurrentHashMap<String, ImageBitmap>()
    private val diskCacheDir: java.io.File by lazy {
        val userHome = System.getProperty("user.home") ?: "."
        java.io.File(userHome, ".lyreon/artwork_cache").apply { mkdirs() }
    }

    suspend fun getOrFetch(url: String): ImageBitmap? {
        if (url.isBlank()) return null
        memoryCache[url]?.let { return it }

        return withContext(Dispatchers.IO) {
            // 1. Cek disk cache
            val hashKey = url.hashCode().toString().replace("-", "n")
            val cacheFile = java.io.File(diskCacheDir, "$hashKey.img")
            if (cacheFile.exists() && cacheFile.length() > 0) {
                val cachedBmp = runCatching {
                    val bytes = cacheFile.readBytes()
                    val bitmap = loadImageBitmap(ByteArrayInputStream(bytes))
                    memoryCache[url] = bitmap
                    bitmap
                }.getOrNull()
                if (cachedBmp != null) return@withContext cachedBmp
            }

            // 2. Siapkan kandidat URL fallback (hqdefault, mqdefault jika maxres 404)
            val candidateUrls = mutableListOf(url)
            if (url.contains("i.ytimg.com/vi/")) {
                val videoId = url.substringAfter("i.ytimg.com/vi/").substringBefore('/')
                candidateUrls.add("https://i.ytimg.com/vi/$videoId/hqdefault.jpg")
                candidateUrls.add("https://i.ytimg.com/vi/$videoId/mqdefault.jpg")
                candidateUrls.add("https://i.ytimg.com/vi/$videoId/default.jpg")
            }

            for (targetUrl in candidateUrls.distinct()) {
                val bmp = runCatching {
                    val req = Request.Builder()
                        .url(targetUrl)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .build()
                    YouTubeDesktopRepository.httpClient.newCall(req).execute().use { resp ->
                        if (resp.isSuccessful) {
                            val bytes = resp.body?.bytes() ?: return@use null
                            if (bytes.isNotEmpty()) {
                                runCatching { cacheFile.writeBytes(bytes) }
                                loadImageBitmap(ByteArrayInputStream(bytes))
                            } else null
                        } else null
                    }
                }.getOrNull()

                if (bmp != null) {
                    memoryCache[url] = bmp
                    return@withContext bmp
                }
            }
            null
        }
    }
}

@Composable
fun AsyncArtwork(
    url: String?,
    title: String = "",
    size: Dp = 48.dp,
    cornerRadius: Dp = 8.dp,
    modifier: Modifier = Modifier
) {
    var bitmap by remember(url) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(url) {
        if (!url.isNullOrBlank()) {
            bitmap = ArtworkCache.getOrFetch(url)
        } else {
            bitmap = null
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
            .background(LyreonSurface),
        contentAlignment = Alignment.Center
    ) {
        val bmp = bitmap
        if (bmp != null) {
            Image(
                bitmap = bmp,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(LyreonElevated, LyreonSurface)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = title,
                    tint = LyreonCrimson.copy(alpha = 0.8f),
                    modifier = Modifier.size(size * 0.45f)
                )
            }
        }
    }
}
