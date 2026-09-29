package com.lyreon.desktop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.lyreon.desktop.download.DesktopDownloadManager
import com.lyreon.desktop.model.LyreonTrack
import com.lyreon.desktop.player.DesktopAudioPlayer
import com.lyreon.desktop.player.RepeatMode
import com.lyreon.desktop.ui.theme.CrimsonPrimary

@Composable
fun PlayerBar(
    currentTrack: LyreonTrack?,
    isPlaying: Boolean,
    isBuffering: Boolean,
    positionMs: Long,
    durationMs: Long,
    volume: Float,
    isShuffle: Boolean,
    repeatMode: RepeatMode,
    onTogglePlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSeek: (Long) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onOpenLyrics: () -> Unit,
    modifier: Modifier = Modifier
) {
    val downloads by DesktopDatabase.downloads.collectAsState()
    val likedTracks by DesktopDatabase.likedTracks.collectAsState()

    val currentVideoId = currentTrack?.videoId.orEmpty()
    val isLiked = remember(likedTracks, currentVideoId) {
        if (currentVideoId.isNotBlank()) DesktopDatabase.isLiked(currentVideoId) else false
    }
    val downloadEntry = remember(downloads, currentVideoId) {
        if (currentVideoId.isNotBlank()) downloads.firstOrNull { it.videoId == currentVideoId } else null
    }
    val isDownloaded = remember(downloadEntry, currentTrack) {
        downloadEntry?.state == "DONE" || (currentTrack?.isLocal == true && !currentTrack.localPath.isNullOrBlank()) ||
            (currentVideoId.isNotBlank() && DesktopDownloadManager.getDownloadedFile(currentVideoId) != null)
    }
    val isDownloading = remember(downloadEntry) {
        downloadEntry?.state == "DOWNLOADING" || downloadEntry?.state == "QUEUED"
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Track Info, Like Button, and Download Button
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncArtwork(
                    url = currentTrack?.thumbnailUrl,
                    title = currentTrack?.title ?: "",
                    size = 52.dp,
                    cornerRadius = 8.dp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = currentTrack?.title ?: "Tidak ada lagu",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = currentTrack?.artist?.ifBlank { "Lyreon Music" } ?: "Pilih lagu untuk memutar",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Like Button
                IconButton(
                    onClick = {
                        currentTrack?.let { DesktopAudioPlayer.toggleLike(it) }
                    },
                    enabled = currentTrack != null,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isLiked) "Hapus dari Favorit" else "Sukai",
                        tint = if (isLiked) CrimsonPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Download Button (Foto 1 Requirement)
                IconButton(
                    onClick = {
                        if (currentTrack != null && !isDownloaded && !isDownloading) {
                            DesktopDownloadManager.enqueue(currentTrack)
                        }
                    },
                    enabled = currentTrack != null && !isDownloaded && !isDownloading,
                    modifier = Modifier.size(32.dp)
                ) {
                    when {
                        isDownloaded -> {
                            Icon(
                                imageVector = Icons.Default.DownloadDone,
                                contentDescription = "Tersimpan Offline",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        isDownloading -> {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                color = CrimsonPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        else -> {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Unduh Lagu",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Center: Playback Controls & Timeline
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(2f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(onClick = onToggleShuffle) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Shuffle",
                            tint = if (isShuffle) CrimsonPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(onClick = onPrevious) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CrimsonPrimary)
                            .clickable(onClick = onTogglePlayPause),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isBuffering) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    IconButton(onClick = onNext) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(onClick = onToggleRepeat) {
                        Icon(
                            imageVector = when (repeatMode) {
                                RepeatMode.ONE -> Icons.Default.RepeatOne
                                else -> Icons.Default.Repeat
                            },
                            contentDescription = "Repeat",
                            tint = if (repeatMode != RepeatMode.OFF) CrimsonPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Timeline seek bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    val posSec = positionMs / 1000L
                    val durSec = durationMs / 1000L

                    Text(
                        text = if (posSec >= 0) "%02d:%02d".format(posSec / 60, posSec % 60) else "00:00",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(42.dp)
                    )

                    val maxSlider = if (durationMs > 0) durationMs.toFloat() else 1f
                    val currentSlider = positionMs.toFloat().coerceIn(0f, maxSlider)

                    Slider(
                        value = currentSlider,
                        onValueChange = { onSeek(it.toLong()) },
                        valueRange = 0f..maxSlider,
                        colors = SliderDefaults.colors(
                            thumbColor = CrimsonPrimary,
                            activeTrackColor = CrimsonPrimary,
                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.weight(1f).height(20.dp)
                    )

                    Text(
                        text = if (durSec > 0) "%02d:%02d".format(durSec / 60, durSec % 60) else "--:--",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(42.dp)
                    )
                }
            }

            // Right: Volume & Lyrics Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = onOpenLyrics) {
                    Icon(
                        imageVector = Icons.Default.Lyrics,
                        contentDescription = "Lyrics",
                        tint = CrimsonPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = if (volume == 0f) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                    contentDescription = "Volume",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )

                Slider(
                    value = volume,
                    onValueChange = onVolumeChange,
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = CrimsonPrimary,
                        activeTrackColor = CrimsonPrimary,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.width(100.dp).height(20.dp)
                )
            }
        }
    }
}
