package com.lyreon.desktop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lyreon.desktop.data.DesktopDatabase
import com.lyreon.desktop.data.DesktopDownloadEntry
import com.lyreon.desktop.download.DesktopDownloadManager
import com.lyreon.desktop.model.LyreonTrack
import com.lyreon.desktop.player.DesktopAudioPlayer
import com.lyreon.desktop.ui.components.AsyncArtwork
import com.lyreon.desktop.ui.components.SectionRule
import com.lyreon.desktop.ui.theme.*

@Composable
fun DownloadsScreen(
    onPlayTrack: (LyreonTrack, List<LyreonTrack>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val downloads by DesktopDatabase.downloads.collectAsState()
    val currentSpeed by DesktopDownloadManager.currentSpeed.collectAsState()
    val currentTrack by DesktopAudioPlayer.currentTrack.collectAsState()
    val isPlaying by DesktopAudioPlayer.isPlaying.collectAsState()

    val active = downloads.filter { it.state == "QUEUED" || it.state == "DOWNLOADING" }
    val done = downloads.filter { it.state == "DONE" }
    val failed = downloads.filter { it.state == "ERROR" || it.state == "CANCELED" }

    val doneTracks = remember(done) {
        done.map { d ->
            LyreonTrack(
                videoId = d.videoId,
                title = d.title,
                artist = d.artist,
                album = d.album,
                durationSec = d.durationSec,
                thumbnailUrl = d.thumbnailUrl,
                localPath = d.filePath
            )
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LyreonBackground),
        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item(key = "header") {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Pusat Unduhan & Offline",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = LyreonTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Manajemen musik yang disimpan lokal untuk diputar tanpa koneksi internet",
                            fontSize = 13.sp,
                            color = LyreonTextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.15f))
                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${done.size} SIAP OFFLINE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }
                }
            }
        }

        // Action Buttons Card
        item(key = "actions") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (done.isNotEmpty()) {
                    Button(
                        onClick = {
                            if (doneTracks.isNotEmpty()) {
                                onPlayTrack(doneTracks.first(), doneTracks)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LyreonCrimson),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Putar Semua Offline (${done.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                if (active.isNotEmpty()) {
                    Button(
                        onClick = { DesktopDownloadManager.resumeAll() },
                        colors = ButtonDefaults.buttonColors(containerColor = LyreonElevated),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LyreonLine),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Lanjutkan Semua (${active.size})", fontSize = 12.sp, color = LyreonTextPrimary)
                    }

                    Button(
                        onClick = { DesktopDownloadManager.cancelAll() },
                        colors = ButtonDefaults.buttonColors(containerColor = LyreonElevated),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LyreonLine),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Batalkan Semua", fontSize = 12.sp, color = LyreonTextSecondary)
                    }
                }

                if (failed.isNotEmpty()) {
                    Button(
                        onClick = { DesktopDownloadManager.retryAllFailed() },
                        colors = ButtonDefaults.buttonColors(containerColor = LyreonElevated),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LyreonCrimson.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp), tint = LyreonCrimson)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ulangi Gagal (${failed.size})", fontSize = 12.sp, color = LyreonCrimson)
                    }
                }
            }
        }

        // Empty state
        if (downloads.isEmpty()) {
            item(key = "empty") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(LyreonSurface)
                        .border(1.dp, LyreonLine.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = null,
                            tint = LyreonTextMuted.copy(alpha = 0.5f),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Belum ada unduhan",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LyreonTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Klik tombol menu Lainnya (...) pada lagu atau tombol 'Unduh Semua' pada playlist untuk mengunduh audio lokal.",
                            fontSize = 12.sp,
                            color = LyreonTextSecondary
                        )
                    }
                }
            }
        }

        // 1. Sedang Diproses
        if (active.isNotEmpty()) {
            item(key = "header_active") {
                SectionRule(label = "SEDANG DIPROSES (${active.size})")
            }

            items(active, key = { "active_${it.videoId}" }) { entry ->
                DownloadItemRow(
                    entry = entry,
                    currentSpeed = if (entry.state == "DOWNLOADING") currentSpeed else null,
                    onCancel = { DesktopDownloadManager.cancel(entry.videoId) },
                    onDelete = { DesktopDownloadManager.remove(entry.videoId) },
                    onRetry = null,
                    onPlay = null
                )
            }
        }

        // 2. Tersimpan Offline
        if (done.isNotEmpty()) {
            item(key = "header_done") {
                SectionRule(label = "TERSIMPAN OFFLINE (${done.size})")
            }

            items(done, key = { "done_${it.videoId}" }) { entry ->
                val track = doneTracks.find { it.videoId == entry.videoId }
                val isTrackPlaying = currentTrack?.videoId == entry.videoId && isPlaying
                DownloadItemRow(
                    entry = entry,
                    currentSpeed = null,
                    onCancel = null,
                    onDelete = { DesktopDownloadManager.remove(entry.videoId) },
                    onRetry = null,
                    onPlay = {
                        if (track != null) {
                            onPlayTrack(track, doneTracks)
                        }
                    },
                    isActivePlaying = currentTrack?.videoId == entry.videoId,
                    isPlaying = isTrackPlaying
                )
            }
        }

        // 3. Perlu Tindakan
        if (failed.isNotEmpty()) {
            item(key = "header_failed") {
                SectionRule(label = "PERLU TINDAKAN (${failed.size})")
            }

            items(failed, key = { "failed_${it.videoId}" }) { entry ->
                DownloadItemRow(
                    entry = entry,
                    currentSpeed = null,
                    onCancel = null,
                    onDelete = { DesktopDownloadManager.remove(entry.videoId) },
                    onRetry = { DesktopDownloadManager.retry(entry.videoId) },
                    onPlay = null
                )
            }
        }
    }
}

@Composable
private fun DownloadItemRow(
    entry: DesktopDownloadEntry,
    currentSpeed: String?,
    onCancel: (() -> Unit)?,
    onDelete: () -> Unit,
    onRetry: (() -> Unit)?,
    onPlay: (() -> Unit)?,
    isActivePlaying: Boolean = false,
    isPlaying: Boolean = false,
) {
    val rowShape = RoundedCornerShape(12.dp)
    val bgColor = if (isActivePlaying) LyreonElevated else LyreonSurface
    val borderColor = if (isActivePlaying) LyreonCrimson else LyreonLine.copy(alpha = 0.4f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(rowShape)
            .background(bgColor)
            .border(1.dp, borderColor, rowShape)
            .clickable(enabled = onPlay != null) { onPlay?.invoke() }
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncArtwork(
                url = entry.thumbnailUrl,
                title = entry.title,
                size = 46.dp,
                cornerRadius = 8.dp
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.title,
                    fontSize = 14.sp,
                    fontWeight = if (isActivePlaying) FontWeight.Bold else FontWeight.Medium,
                    color = if (isActivePlaying) LyreonCrimson else LyreonTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = entry.artist.ifBlank { "YouTube Music" },
                    fontSize = 12.sp,
                    color = LyreonTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))

                val statusText = when (entry.state) {
                    "DONE" -> {
                        val mb = entry.bytesTotal / (1024 * 1024)
                        "Siap Offline • $mb MB"
                    }
                    "DOWNLOADING" -> {
                        val speedSuffix = if (!currentSpeed.isNullOrBlank()) " • $currentSpeed" else ""
                        if (entry.bytesTotal > 0) {
                            val pct = (entry.bytesDone * 100 / entry.bytesTotal).coerceIn(0, 100)
                            val mbDone = String.format(java.util.Locale.US, "%.1f", entry.bytesDone / (1024f * 1024f))
                            val mbTotal = String.format(java.util.Locale.US, "%.1f", entry.bytesTotal / (1024f * 1024f))
                            "Mengunduh • $pct% ($mbDone / $mbTotal MB)$speedSuffix"
                        } else {
                            "Mengunduh stream...$speedSuffix"
                        }
                    }
                    "QUEUED" -> "Dalam antrean unduhan..."
                    "ERROR" -> "Gagal: ${entry.errorMessage ?: "Terjadi kesalahan"}"
                    else -> "Dibatalkan"
                }

                val statusColor = when (entry.state) {
                    "DONE" -> Color(0xFF10B981)
                    "DOWNLOADING" -> LyreonCrimson
                    "ERROR" -> LyreonCrimson
                    else -> LyreonTextMuted
                }

                Text(
                    text = statusText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = statusColor
                )
            }

            if (onPlay != null) {
                IconButton(onClick = onPlay) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Putar",
                        tint = LyreonCrimson,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            if (onRetry != null) {
                IconButton(onClick = onRetry) {
                    Icon(Icons.Default.Refresh, contentDescription = "Ulangi", tint = LyreonTextSecondary, modifier = Modifier.size(18.dp))
                }
            }

            if (onCancel != null) {
                IconButton(onClick = onCancel) {
                    Icon(Icons.Default.Close, contentDescription = "Batal", tint = LyreonTextSecondary, modifier = Modifier.size(18.dp))
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = LyreonTextMuted, modifier = Modifier.size(18.dp))
            }
        }

        if (entry.state == "DOWNLOADING" || entry.state == "QUEUED") {
            Spacer(modifier = Modifier.height(10.dp))
            if (entry.bytesTotal > 0) {
                val p = (entry.bytesDone.toFloat() / entry.bytesTotal.toFloat()).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { p },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = LyreonCrimson,
                    trackColor = LyreonElevated
                )
            } else {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = LyreonCrimson,
                    trackColor = LyreonElevated
                )
            }
        }
    }
}
