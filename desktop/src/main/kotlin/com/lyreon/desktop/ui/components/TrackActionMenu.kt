package com.lyreon.desktop.ui.components

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
import com.lyreon.desktop.download.DesktopDownloadManager
import com.lyreon.desktop.model.LyreonTrack
import com.lyreon.desktop.player.DesktopAudioPlayer
import com.lyreon.desktop.ui.theme.*

@Composable
fun TrackActionDialog(
    track: LyreonTrack,
    onDismiss: () -> Unit,
    onPlayNext: () -> Unit = {},
) {
    var showAddToPlaylist by remember { mutableStateOf(false) }
    var showNewPlaylistDialog by remember { mutableStateOf(false) }
    val playlists by DesktopDatabase.playlists.collectAsState()
    val isDownloaded = DesktopDatabase.isDownloaded(track.videoId)
    val isLiked = DesktopAudioPlayer.isLiked(track.videoId)

    if (showNewPlaylistDialog) {
        NewPlaylistDialog(
            onCreate = { name ->
                val id = DesktopDatabase.createPlaylist(name, initialTracks = listOf(track))
                showNewPlaylistDialog = false
                showAddToPlaylist = false
                onDismiss()
            },
            onDismiss = { showNewPlaylistDialog = false }
        )
    }

    if (showAddToPlaylist) {
        AlertDialog(
            onDismissRequest = { showAddToPlaylist = false },
            containerColor = LyreonElevated,
            title = {
                Text(
                    text = "Tambahkan ke Playlist",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = LyreonCrimson
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                ) {
                    // Tombol Buat Playlist Baru
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(LyreonCrimson.copy(alpha = 0.15f))
                            .clickable { showNewPlaylistDialog = true }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = LyreonCrimson,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Buat Playlist Baru...",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LyreonTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (playlists.isEmpty()) {
                        Text(
                            text = "Belum ada playlist tersimpan.",
                            fontSize = 12.sp,
                            color = LyreonTextMuted,
                            modifier = Modifier.padding(8.dp)
                        )
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(playlists, key = { it.id }) { pl ->
                                val containsTrack = pl.tracks.any { it.videoId == track.videoId }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(LyreonSurface)
                                        .border(1.dp, LyreonLine.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            if (containsTrack) {
                                                DesktopDatabase.removeTrackFromPlaylist(pl.id, track.videoId)
                                            } else {
                                                DesktopDatabase.addTrackToPlaylist(pl.id, track)
                                            }
                                            showAddToPlaylist = false
                                            onDismiss()
                                        }
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlaylistPlay,
                                        contentDescription = null,
                                        tint = if (containsTrack) LyreonCrimson else LyreonTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = pl.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = LyreonTextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${pl.tracks.size} trek",
                                            fontSize = 11.sp,
                                            color = LyreonTextSecondary
                                        )
                                    }
                                    if (containsTrack) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Sudah ditambahkan",
                                            tint = LyreonCrimson,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showAddToPlaylist = false }) {
                    Text("Tutup", color = LyreonTextSecondary)
                }
            }
        )
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LyreonElevated,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncArtwork(
                    url = track.thumbnailUrl,
                    title = track.title,
                    size = 42.dp,
                    cornerRadius = 8.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = LyreonTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = track.artist,
                        fontSize = 12.sp,
                        color = LyreonTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 1. Suka / Favorit
                ActionItem(
                    icon = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    iconTint = if (isLiked) LyreonCrimson else LyreonTextPrimary,
                    title = if (isLiked) "Hapus dari Disukai" else "Sukai Lagu Ini",
                    onClick = {
                        DesktopAudioPlayer.toggleLike(track)
                        onDismiss()
                    }
                )

                // 2. Tambah ke Antrean
                ActionItem(
                    icon = Icons.Default.QueueMusic,
                    title = "Tambahkan ke Antrean",
                    onClick = {
                        DesktopAudioPlayer.addToQueue(track)
                        onDismiss()
                    }
                )

                // 3. Tambahkan ke Playlist
                ActionItem(
                    icon = Icons.Default.PlaylistAdd,
                    title = "Tambahkan ke Playlist...",
                    onClick = {
                        showAddToPlaylist = true
                    }
                )

                // 4. Unduh Offline
                ActionItem(
                    icon = if (isDownloaded) Icons.Default.DownloadDone else Icons.Default.Download,
                    iconTint = if (isDownloaded) Color(0xFF10B981) else LyreonTextPrimary,
                    title = if (isDownloaded) "Tersimpan Offline" else "Unduh untuk Offline",
                    subtitle = if (isDownloaded) "Berkas audio siap diputar tanpa internet" else "Simpan audio lokal kualitas tinggi",
                    onClick = {
                        if (!isDownloaded) {
                            DesktopDownloadManager.enqueue(track)
                        }
                        onDismiss()
                    }
                )

                // 5. Salin Tautan
                ActionItem(
                    icon = Icons.Default.Share,
                    title = "Salin Tautan Lagu",
                    onClick = {
                        try {
                            val sel = java.awt.datatransfer.StringSelection(track.watchUrl)
                            java.awt.Toolkit.getDefaultToolkit().systemClipboard.setContents(sel, sel)
                        } catch (_: Exception) {}
                        onDismiss()
                    }
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup", color = LyreonTextSecondary)
            }
        }
    )
}

@Composable
private fun ActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color = LyreonTextPrimary,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = LyreonTextPrimary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = LyreonTextSecondary
                )
            }
        }
    }
}

@Composable
fun NewPlaylistDialog(
    onCreate: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LyreonElevated,
        title = {
            Text("Playlist Baru", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LyreonCrimson)
        },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                placeholder = { Text("Nama playlist...", color = LyreonTextMuted) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LyreonCrimson,
                    unfocusedBorderColor = LyreonLine,
                    cursorColor = LyreonTextPrimary,
                    focusedTextColor = LyreonTextPrimary,
                    unfocusedTextColor = LyreonTextPrimary,
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onCreate(name.trim()) }) {
                Text("Buat", color = LyreonCrimson, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = LyreonTextSecondary)
            }
        }
    )
}
