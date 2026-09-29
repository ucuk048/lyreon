package com.lyreon.desktop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lyreon.desktop.data.DesktopDatabase
import com.lyreon.desktop.data.DesktopPlaylist
import com.lyreon.desktop.download.DesktopDownloadManager
import com.lyreon.desktop.local.LocalMusicScanner
import com.lyreon.desktop.model.LyreonTrack
import com.lyreon.desktop.player.DesktopAudioPlayer
import com.lyreon.desktop.ui.components.AsyncArtwork
import com.lyreon.desktop.ui.components.NewPlaylistDialog
import com.lyreon.desktop.ui.components.TrackRow
import com.lyreon.desktop.ui.dialogs.SpotifyImportDialog
import com.lyreon.desktop.ui.theme.*
import kotlinx.coroutines.launch
import javax.swing.JFileChooser

enum class DesktopLibraryTab(val title: String, val icon: ImageVector) {
    FAVORIT("Lagu Disukai", Icons.Default.Favorite),
    PLAYLIST("Playlist", Icons.Default.PlaylistPlay),
    RIWAYAT("Riwayat", Icons.Default.History),
    OFFLINE("Offline", Icons.Default.DownloadDone),
    LOKAL("Musik Lokal", Icons.Default.Folder),
}

@Composable
fun LibraryScreen(
    onPlayTrack: (LyreonTrack, List<LyreonTrack>) -> Unit,
    onOpenPlaylistDetail: (Long) -> Unit,
    onNavigateDownloads: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(DesktopLibraryTab.FAVORIT) }

    val likedTracks by DesktopDatabase.likedTracks.collectAsState()
    val historyEntries by DesktopDatabase.history.collectAsState()
    val playlists by DesktopDatabase.playlists.collectAsState()
    val downloads by DesktopDatabase.downloads.collectAsState()
    val localTracks by LocalMusicScanner.scannedTracks.collectAsState()
    val isScanningLocal by LocalMusicScanner.isScanning.collectAsState()

    val currentTrack by DesktopAudioPlayer.currentTrack.collectAsState()
    val isPlaying by DesktopAudioPlayer.isPlaying.collectAsState()

    var showNewPlaylistDialog by remember { mutableStateOf(false) }
    var showSpotifyImportDialog by remember { mutableStateOf(false) }
    var playlistToDelete by remember { mutableStateOf<DesktopPlaylist?>(null) }

    val historyTracks = remember(historyEntries) { historyEntries.map { it.track } }
    val doneDownloads = remember(downloads) {
        downloads.filter { it.state == "DONE" }.map { d ->
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

    LaunchedEffect(Unit) {
        if (localTracks.isEmpty()) {
            LocalMusicScanner.scan()
        }
    }

    if (showNewPlaylistDialog) {
        NewPlaylistDialog(
            onCreate = { name ->
                val id = DesktopDatabase.createPlaylist(name)
                showNewPlaylistDialog = false
                onOpenPlaylistDetail(id)
            },
            onDismiss = { showNewPlaylistDialog = false }
        )
    }

    if (showSpotifyImportDialog) {
        SpotifyImportDialog(
            onDismiss = { showSpotifyImportDialog = false },
            onOpenPlaylist = { onOpenPlaylistDetail(it) }
        )
    }

    playlistToDelete?.let { pl ->
        AlertDialog(
            onDismissRequest = { playlistToDelete = null },
            containerColor = LyreonElevated,
            title = {
                Text("Hapus Playlist", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LyreonCrimson)
            },
            text = {
                Text(
                    "Apakah Anda yakin ingin menghapus playlist \"${pl.name}\"?",
                    fontSize = 13.sp,
                    color = LyreonTextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    DesktopDatabase.deletePlaylist(pl.id)
                    playlistToDelete = null
                }) {
                    Text("Hapus", color = LyreonCrimson, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { playlistToDelete = null }) {
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
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Pustaka Musik",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = LyreonTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Koleksi playlist pribadi, lagu disukai, riwayat, unduhan offline, dan musik lokal",
                    fontSize = 13.sp,
                    color = LyreonTextSecondary
                )
            }

            // Quick Action Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { showNewPlaylistDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = LyreonSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LyreonCrimson.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = LyreonCrimson, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Playlist Baru", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = LyreonTextPrimary)
                }

                Button(
                    onClick = { showSpotifyImportDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = LyreonCrimson),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Link, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Import Spotify", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // Tabs Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DesktopLibraryTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                val shape = RoundedCornerShape(20.dp)
                val countText = when (tab) {
                    DesktopLibraryTab.FAVORIT -> "(${likedTracks.size})"
                    DesktopLibraryTab.PLAYLIST -> "(${playlists.size})"
                    DesktopLibraryTab.RIWAYAT -> "(${historyTracks.size})"
                    DesktopLibraryTab.OFFLINE -> "(${doneDownloads.size})"
                    DesktopLibraryTab.LOKAL -> "(${localTracks.size})"
                }

                Box(
                    modifier = Modifier
                        .clip(shape)
                        .background(if (isSelected) LyreonCrimson else LyreonSurface)
                        .border(
                            1.dp,
                            if (isSelected) LyreonCrimson else LyreonLine.copy(alpha = 0.5f),
                            shape
                        )
                        .clickable { selectedTab = tab }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = null,
                            tint = if (isSelected) Color.White else LyreonTextSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${tab.title} $countText",
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else LyreonTextPrimary
                        )
                    }
                }
            }
        }

        // Tab Content
        when (selectedTab) {
            DesktopLibraryTab.FAVORIT -> {
                if (likedTracks.isEmpty()) {
                    EmptyLibraryState(
                        icon = Icons.Default.Favorite,
                        title = "Belum ada lagu disukai",
                        subtitle = "Klik ikon hati pada bar pemutar atau menu lagu untuk menambahkan lagu ke favorit."
                    )
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Koleksi Lagu Favorit (${likedTracks.size})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LyreonTextSecondary
                            )

                            Button(
                                onClick = { onPlayTrack(likedTracks.first(), likedTracks) },
                                colors = ButtonDefaults.buttonColors(containerColor = LyreonCrimson),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Putar Semua", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            itemsIndexed(likedTracks, key = { index, track -> "${track.videoId}_$index" }) { index, track ->
                                TrackRow(
                                    track = track,
                                    isActive = currentTrack?.videoId == track.videoId,
                                    isPlaying = isPlaying,
                                    index = index,
                                    onPlay = { onPlayTrack(track, likedTracks) },
                                    onAddToQueue = { DesktopAudioPlayer.addToQueue(track) }
                                )
                            }
                        }
                    }
                }
            }

            DesktopLibraryTab.PLAYLIST -> {
                if (playlists.isEmpty()) {
                    EmptyLibraryState(
                        icon = Icons.Default.PlaylistPlay,
                        title = "Belum ada playlist tersimpan",
                        subtitle = "Buat playlist kustom baru atau impor langsung dari tautan playlist Spotify."
                    )
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(playlists, key = { it.id }) { pl ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(LyreonSurface)
                                    .border(1.dp, LyreonLine.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .clickable { onOpenPlaylistDetail(pl.id) }
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncArtwork(
                                    url = pl.coverUrl,
                                    title = pl.name,
                                    size = 56.dp,
                                    cornerRadius = 8.dp
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = pl.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LyreonTextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "${pl.tracks.size} lagu • Dibuat secara lokal",
                                        fontSize = 12.sp,
                                        color = LyreonTextSecondary
                                    )
                                }

                                if (pl.tracks.isNotEmpty()) {
                                    IconButton(
                                        onClick = { onPlayTrack(pl.tracks.first(), pl.tracks) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = "Putar", tint = LyreonCrimson)
                                    }
                                }

                                IconButton(
                                    onClick = { playlistToDelete = pl },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = LyreonTextMuted)
                                }
                            }
                        }
                    }
                }
            }

            DesktopLibraryTab.RIWAYAT -> {
                if (historyTracks.isEmpty()) {
                    EmptyLibraryState(
                        icon = Icons.Default.History,
                        title = "Belum ada riwayat pemutaran",
                        subtitle = "Lagu yang Anda dengarkan akan otomatis dicatat di sini."
                    )
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Lagu Terakhir Diputar (${historyTracks.size})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LyreonTextSecondary
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { onPlayTrack(historyTracks.first(), historyTracks) },
                                    colors = ButtonDefaults.buttonColors(containerColor = LyreonCrimson),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Putar Ulang", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }

                                Button(
                                    onClick = { DesktopDatabase.clearHistory() },
                                    colors = ButtonDefaults.buttonColors(containerColor = LyreonSurface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, LyreonLine),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Bersihkan", fontSize = 12.sp, color = LyreonTextSecondary)
                                }
                            }
                        }

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            itemsIndexed(historyTracks, key = { index, track -> "${track.videoId}_$index" }) { index, track ->
                                TrackRow(
                                    track = track,
                                    isActive = currentTrack?.videoId == track.videoId,
                                    isPlaying = isPlaying,
                                    index = index,
                                    onPlay = { onPlayTrack(track, historyTracks) },
                                    onAddToQueue = { DesktopAudioPlayer.addToQueue(track) }
                                )
                            }
                        }
                    }
                }
            }

            DesktopLibraryTab.OFFLINE -> {
                if (doneDownloads.isEmpty()) {
                    EmptyLibraryState(
                        icon = Icons.Default.DownloadDone,
                        title = "Belum ada trek tersimpan offline",
                        subtitle = "Unduh lagu favorit atau playlist untuk menikmati pemutaran offline seketika tanpa koneksi internet."
                    )
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Trek Siap Diputar Offline (${doneDownloads.size})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LyreonTextSecondary
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { onPlayTrack(doneDownloads.first(), doneDownloads) },
                                    colors = ButtonDefaults.buttonColors(containerColor = LyreonCrimson),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Putar Offline", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }

                                Button(
                                    onClick = onNavigateDownloads,
                                    colors = ButtonDefaults.buttonColors(containerColor = LyreonSurface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, LyreonLine),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Kelola Unduhan", fontSize = 12.sp, color = LyreonTextPrimary)
                                }
                            }
                        }

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            itemsIndexed(doneDownloads, key = { index, track -> "${track.videoId}_$index" }) { index, track ->
                                TrackRow(
                                    track = track,
                                    isActive = currentTrack?.videoId == track.videoId,
                                    isPlaying = isPlaying,
                                    index = index,
                                    onPlay = { onPlayTrack(track, doneDownloads) },
                                    onAddToQueue = { DesktopAudioPlayer.addToQueue(track) }
                                )
                            }
                        }
                    }
                }
            }

            DesktopLibraryTab.LOKAL -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Musik dari Penyimpanan Komputer",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LyreonTextSecondary
                            )
                            Text(
                                text = "${localTracks.size} Berkas audio ditemukan",
                                fontSize = 12.sp,
                                color = LyreonTextMuted
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    scope.launch { LocalMusicScanner.scan() }
                                },
                                enabled = !isScanningLocal,
                                colors = ButtonDefaults.buttonColors(containerColor = LyreonSurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, LyreonLine),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp), tint = LyreonTextPrimary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isScanningLocal) "Memindai..." else "Pindai Ulang", fontSize = 12.sp, color = LyreonTextPrimary)
                            }

                            Button(
                                onClick = {
                                    val chooser = JFileChooser().apply {
                                        fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                                        dialogTitle = "Pilih Folder Musik Kustom"
                                    }
                                    val res = chooser.showOpenDialog(null)
                                    if (res == JFileChooser.APPROVE_OPTION) {
                                        chooser.selectedFile?.absolutePath?.let {
                                            DesktopDatabase.addCustomLocalFolder(it)
                                            scope.launch { LocalMusicScanner.scan() }
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = LyreonSurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, LyreonLine),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.CreateNewFolder, contentDescription = null, modifier = Modifier.size(16.dp), tint = LyreonCrimson)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tambah Folder", fontSize = 12.sp, color = LyreonTextPrimary)
                            }

                            if (localTracks.isNotEmpty()) {
                                Button(
                                    onClick = { onPlayTrack(localTracks.first(), localTracks) },
                                    colors = ButtonDefaults.buttonColors(containerColor = LyreonCrimson),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Putar Semua", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }

                    if (localTracks.isEmpty()) {
                        EmptyLibraryState(
                            icon = Icons.Default.Folder,
                            title = "Tidak ada berkas audio lokal ditemukan",
                            subtitle = "Simpan lagu di folder 'Music' Windows Anda atau klik 'Tambah Folder' untuk memindai direktori kustom."
                        )
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            itemsIndexed(localTracks, key = { index, track -> "${track.videoId}_$index" }) { index, track ->
                                TrackRow(
                                    track = track,
                                    isActive = currentTrack?.videoId == track.videoId,
                                    isPlaying = isPlaying,
                                    index = index,
                                    onPlay = { onPlayTrack(track, localTracks) },
                                    onAddToQueue = { DesktopAudioPlayer.addToQueue(track) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyLibraryState(
    icon: ImageVector,
    title: String,
    subtitle: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LyreonTextMuted.copy(alpha = 0.4f),
                modifier = Modifier.size(54.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = LyreonTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = LyreonTextSecondary
            )
        }
    }
}
