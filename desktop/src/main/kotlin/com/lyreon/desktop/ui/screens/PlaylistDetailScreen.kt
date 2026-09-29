package com.lyreon.desktop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.lyreon.desktop.model.formatDuration
import com.lyreon.desktop.player.DesktopAudioPlayer
import com.lyreon.desktop.ui.components.AsyncArtwork
import com.lyreon.desktop.ui.components.TrackRow
import com.lyreon.desktop.ui.theme.*

@Composable
fun PlaylistDetailScreen(
    playlistId: Long,
    onBack: () -> Unit,
    onPlayTrack: (LyreonTrack, List<LyreonTrack>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val playlists by DesktopDatabase.playlists.collectAsState()
    val playlist = playlists.find { it.id == playlistId }

    val currentTrack by DesktopAudioPlayer.currentTrack.collectAsState()
    val isPlaying by DesktopAudioPlayer.isPlaying.collectAsState()

    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (playlist == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(LyreonBackground),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Playlist tidak ditemukan atau telah dihapus",
                    fontSize = 15.sp,
                    color = LyreonTextSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = LyreonSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LyreonLine),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Kembali ke Pustaka", color = LyreonTextPrimary)
                }
            }
        }
        return
    }

    val tracks = playlist.tracks
    val totalSec = tracks.sumOf { it.durationSec }

    if (showRenameDialog) {
        RenamePlaylistDialog(
            currentName = playlist.name,
            onSave = {
                DesktopDatabase.renamePlaylist(playlist.id, it)
                showRenameDialog = false
            },
            onDismiss = { showRenameDialog = false }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = LyreonElevated,
            title = {
                Text(
                    text = "Hapus Playlist",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = LyreonCrimson
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin menghapus playlist \"${playlist.name}\"? Tindakan ini tidak dapat dibatalkan.",
                    fontSize = 13.sp,
                    color = LyreonTextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    DesktopDatabase.deletePlaylist(playlist.id)
                    showDeleteDialog = false
                    onBack()
                }) {
                    Text("Hapus", color = LyreonCrimson, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal", color = LyreonTextSecondary)
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LyreonBackground)
            .padding(horizontal = 28.dp, vertical = 24.dp)
    ) {
        // Back Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(LyreonSurface)
                    .border(1.dp, LyreonLine.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = LyreonTextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "PLAYLIST PUSTAKA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LyreonCrimson,
                    letterSpacing = 1.sp
                )
                Text(
                    text = playlist.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = LyreonTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Hero Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(LyreonSurface)
                .border(1.dp, LyreonLine.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncArtwork(
                url = playlist.coverUrl,
                title = playlist.name,
                size = 110.dp,
                cornerRadius = 12.dp
            )

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = playlist.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = LyreonTextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${tracks.size} Lagu • Total Durasi ${formatDuration(totalSec)}",
                    fontSize = 13.sp,
                    color = LyreonTextSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons Row
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Putar Semua
                    Button(
                        onClick = {
                            if (tracks.isNotEmpty()) {
                                onPlayTrack(tracks.first(), tracks)
                            }
                        },
                        enabled = tracks.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = LyreonCrimson),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Putar Semua", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    // Acak
                    Button(
                        onClick = {
                            if (tracks.isNotEmpty()) {
                                val shuffled = tracks.shuffled()
                                onPlayTrack(shuffled.first(), shuffled)
                            }
                        },
                        enabled = tracks.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = LyreonElevated),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LyreonLine),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Shuffle, contentDescription = null, modifier = Modifier.size(16.dp), tint = LyreonTextPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Acak", fontSize = 12.sp, color = LyreonTextPrimary)
                    }

                    // Unduh Semua Playlist
                    Button(
                        onClick = {
                            if (tracks.isNotEmpty()) {
                                DesktopDownloadManager.enqueueAll(tracks)
                            }
                        },
                        enabled = tracks.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = LyreonElevated),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LyreonLine),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp), tint = LyreonCrimson)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Unduh Semua", fontSize = 12.sp, color = LyreonTextPrimary)
                    }

                    // Ubah Nama
                    IconButton(
                        onClick = { showRenameDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(LyreonElevated)
                            .border(1.dp, LyreonLine, RoundedCornerShape(8.dp))
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Ubah Nama", tint = LyreonTextSecondary, modifier = Modifier.size(16.dp))
                    }

                    // Hapus Playlist
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(LyreonElevated)
                            .border(1.dp, LyreonLine, RoundedCornerShape(8.dp))
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = LyreonCrimson, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Tracks List
        if (tracks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = LyreonTextMuted.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Playlist masih kosong",
                        fontSize = 15.sp,
                        color = LyreonTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Cari lagu di Beranda atau Pencarian, lalu klik menu Lainnya (...) untuk menambahkannya ke playlist ini.",
                        fontSize = 12.sp,
                        color = LyreonTextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(tracks, key = { index, t -> "${t.videoId}_$index" }) { index, track ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Tombol urutan Reorder
                        Column(modifier = Modifier.padding(end = 6.dp)) {
                            IconButton(
                                onClick = {
                                    if (index > 0) DesktopDatabase.moveTrackInPlaylist(playlist.id, index, index - 1)
                                },
                                enabled = index > 0,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Naik",
                                    tint = if (index > 0) LyreonTextSecondary else LyreonLine.copy(alpha = 0.3f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    if (index < tracks.lastIndex) DesktopDatabase.moveTrackInPlaylist(playlist.id, index, index + 1)
                                },
                                enabled = index < tracks.lastIndex,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Turun",
                                    tint = if (index < tracks.lastIndex) LyreonTextSecondary else LyreonLine.copy(alpha = 0.3f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        // Track Row
                        TrackRow(
                            track = track,
                            isActive = currentTrack?.videoId == track.videoId,
                            isPlaying = isPlaying,
                            index = index,
                            onPlay = { onPlayTrack(track, tracks) },
                            onAddToQueue = { DesktopAudioPlayer.addToQueue(track) },
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        // Hapus dari playlist
                        IconButton(
                            onClick = { DesktopDatabase.removeTrackFromPlaylist(playlist.id, track.videoId) },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Hapus dari playlist",
                                tint = LyreonTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RenamePlaylistDialog(
    currentName: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LyreonElevated,
        title = {
            Text("Ubah Nama Playlist", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LyreonCrimson)
        },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
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
            TextButton(onClick = { if (name.isNotBlank()) onSave(name.trim()) }) {
                Text("Simpan", color = LyreonCrimson, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = LyreonTextSecondary)
            }
        }
    )
}
