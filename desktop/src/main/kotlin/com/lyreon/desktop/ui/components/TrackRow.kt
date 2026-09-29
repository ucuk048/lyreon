package com.lyreon.desktop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import com.lyreon.desktop.model.LyreonTrack
import com.lyreon.desktop.model.formatDuration
import com.lyreon.desktop.player.DesktopAudioPlayer
import com.lyreon.desktop.ui.theme.*

@Composable
fun TrackRow(
    track: LyreonTrack,
    isActive: Boolean,
    isPlaying: Boolean,
    index: Int?,
    onPlay: () -> Unit,
    onAddToQueue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showActionMenu by remember { mutableStateOf(false) }
    val downloads by DesktopDatabase.downloads.collectAsState()
    val likedTracks by DesktopDatabase.likedTracks.collectAsState()

    val isDownloaded = remember(downloads, track.videoId) {
        DesktopDatabase.isDownloaded(track.videoId) || (track.isLocal && !track.localPath.isNullOrBlank())
    }
    val isLiked = remember(likedTracks, track.videoId) {
        DesktopDatabase.isLiked(track.videoId)
    }

    if (showActionMenu) {
        TrackActionDialog(
            track = track,
            onDismiss = { showActionMenu = false }
        )
    }

    val rowShape = RoundedCornerShape(12.dp)
    val bgColor = if (isActive) LyreonElevated else LyreonSurface.copy(alpha = 0.6f)
    val borderColor = if (isActive) LyreonCrimson.copy(alpha = 0.5f) else LyreonLine.copy(alpha = 0.3f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(rowShape)
            .background(bgColor)
            .border(1.dp, borderColor, rowShape)
            .clickable(onClick = onPlay)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (index != null) {
            Text(
                text = "%02d".format(index + 1),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isActive) LyreonCrimson else LyreonTextMuted,
                modifier = Modifier.width(28.dp)
            )
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            AsyncArtwork(
                url = track.thumbnailUrl,
                title = track.title,
                size = 48.dp,
                cornerRadius = 8.dp,
                modifier = Modifier.fillMaxSize()
            )

            if (isActive) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = LyreonCrimson,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = track.title,
                    fontSize = 14.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                    color = if (isActive) LyreonCrimson else LyreonTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                if (isDownloaded) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.15f))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = if (track.isLocal) "LOKAL" else "OFFLINE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = track.artist.ifBlank { "YouTube Music" },
                fontSize = 12.sp,
                color = LyreonTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (track.durationSec > 0) {
            Text(
                text = formatDuration(track.durationSec),
                fontSize = 12.sp,
                color = LyreonTextMuted,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        // Tombol Like
        IconButton(
            onClick = { DesktopAudioPlayer.toggleLike(track) },
            modifier = Modifier.size(34.dp)
        ) {
            Icon(
                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Suka",
                tint = if (isLiked) LyreonCrimson else LyreonTextMuted,
                modifier = Modifier.size(18.dp)
            )
        }

        // Tombol Download Cepat
        val downloadEntry = remember(downloads, track.videoId) {
            downloads.firstOrNull { it.videoId == track.videoId }
        }
        val isDownloading = remember(downloadEntry) {
            downloadEntry?.state == "DOWNLOADING" || downloadEntry?.state == "QUEUED"
        }

        IconButton(
            onClick = {
                if (!isDownloaded && !isDownloading) {
                    com.lyreon.desktop.download.DesktopDownloadManager.enqueue(track)
                }
            },
            enabled = !isDownloaded && !isDownloading,
            modifier = Modifier.size(34.dp)
        ) {
            when {
                isDownloaded -> {
                    Icon(
                        imageVector = Icons.Default.DownloadDone,
                        contentDescription = "Telah diunduh",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(18.dp)
                    )
                }
                isDownloading -> {
                    androidx.compose.material3.CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        color = LyreonCrimson,
                        modifier = Modifier.size(16.dp)
                    )
                }
                else -> {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Unduh lagu",
                        tint = LyreonTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Tombol Tambah Antrean
        IconButton(
            onClick = onAddToQueue,
            modifier = Modifier.size(34.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlaylistAdd,
                contentDescription = "Tambah ke Antrean",
                tint = LyreonTextSecondary,
                modifier = Modifier.size(19.dp)
            )
        }

        // Tombol Menu Lainnya
        IconButton(
            onClick = { showActionMenu = true },
            modifier = Modifier.size(34.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Lainnya",
                tint = LyreonTextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Tombol Putar
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(if (isActive) LyreonCrimson else LyreonElevated)
                .clickable(onClick = onPlay),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isActive && isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "Putar",
                tint = if (isActive) Color.White else LyreonTextPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
