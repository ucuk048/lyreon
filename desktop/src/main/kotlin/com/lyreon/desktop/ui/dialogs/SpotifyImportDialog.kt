package com.lyreon.desktop.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.lyreon.desktop.core.LyreonLog
import com.lyreon.desktop.data.DesktopDatabase
import com.lyreon.desktop.download.DesktopDownloadManager
import com.lyreon.desktop.model.LyreonTrack
import com.lyreon.desktop.player.DesktopAudioPlayer
import com.lyreon.desktop.spotify.SpotifyMatcher
import com.lyreon.desktop.spotify.SpotifyPlaylist
import com.lyreon.desktop.spotify.SpotifyScraper
import com.lyreon.desktop.ui.components.AsyncArtwork
import com.lyreon.desktop.ui.theme.*
import kotlinx.coroutines.launch
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor

@Composable
fun SpotifyImportDialog(
    onDismiss: () -> Unit,
    onOpenPlaylist: ((Long) -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()

    var url by remember { mutableStateOf("") }
    var isLoadingMetadata by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var playlist by remember { mutableStateOf<SpotifyPlaylist?>(null) }

    var isMatching by remember { mutableStateOf(false) }
    var matchedCount by remember { mutableStateOf(0) }
    var totalCount by remember { mutableStateOf(0) }
    var currentMatchingTitle by remember { mutableStateOf<String?>(null) }
    var savedPlaylistId by remember { mutableStateOf<Long?>(null) }

    fun pasteFromClipboard() {
        try {
            val clip = Toolkit.getDefaultToolkit().systemClipboard
            if (clip.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                val text = clip.getData(DataFlavor.stringFlavor) as? String
                if (!text.isNullOrBlank()) {
                    url = text.trim()
                }
            }
        } catch (_: Exception) {}
    }

    fun loadPlaylist() {
        val trimmed = url.trim()
        val id = SpotifyScraper.extractPlaylistId(trimmed)
        if (id == null) {
            errorMessage = "Format tautan atau ID Spotify tidak valid"
            return
        }

        scope.launch {
            isLoadingMetadata = true
            errorMessage = null
            try {
                val pl = SpotifyScraper.fetchPlaylist(id)
                playlist = pl
                totalCount = pl.tracks.size
            } catch (e: Exception) {
                LyreonLog.e("SpotifyImport", "Gagal memuat playlist: ${e.message}", e)
                errorMessage = e.message ?: "Gagal memuat playlist Spotify"
            } finally {
                isLoadingMetadata = false
            }
        }
    }

    fun startImport(andDownload: Boolean, playImmediate: Boolean) {
        val pl = playlist ?: return
        scope.launch {
            isMatching = true
            matchedCount = 0
            totalCount = pl.tracks.size
            val matchedTracks = mutableListOf<LyreonTrack>()

            for (track in pl.tracks) {
                currentMatchingTitle = "${track.artist} - ${track.title}"
                try {
                    val match = SpotifyMatcher.matchTrack(track)
                    if (match != null) {
                        matchedTracks.add(match)
                        if (playImmediate && matchedTracks.size == 1) {
                            DesktopAudioPlayer.playTrack(match, listOf(match))
                        }
                    }
                } catch (e: Exception) {
                    LyreonLog.w("SpotifyImport", "Gagal mencocokkan trek: ${e.message}")
                }
                matchedCount++
            }

            isMatching = false

            if (matchedTracks.isNotEmpty()) {
                val createdId = DesktopDatabase.createPlaylist(
                    name = pl.name,
                    initialTracks = matchedTracks,
                    coverUrl = pl.coverUrl
                )
                savedPlaylistId = createdId

                if (andDownload) {
                    DesktopDownloadManager.enqueueAll(matchedTracks)
                }

                if (playImmediate) {
                    DesktopAudioPlayer.playTrack(matchedTracks.first(), matchedTracks)
                    onDismiss()
                }
            } else {
                errorMessage = "Tidak ada lagu yang berhasil dicocokkan ke YouTube Music."
            }
        }
    }

    AlertDialog(
        onDismissRequest = {
            if (!isMatching) onDismiss()
        },
        containerColor = LyreonElevated,
        title = {
            Column {
                Text(
                    text = "SPOTIFY IMPORT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LyreonCrimson,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Impor Playlist Spotify",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = LyreonTextPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it; errorMessage = null },
                    singleLine = true,
                    enabled = !isMatching && !isLoadingMetadata,
                    placeholder = {
                        Text(
                            text = "https://open.spotify.com/playlist/...",
                            fontSize = 12.sp,
                            color = LyreonTextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    trailingIcon = {
                        if (!isMatching && !isLoadingMetadata) {
                            IconButton(onClick = ::pasteFromClipboard) {
                                Icon(
                                    imageVector = Icons.Default.ContentPaste,
                                    contentDescription = "Tempel",
                                    tint = LyreonTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LyreonCrimson,
                        unfocusedBorderColor = LyreonLine,
                        cursorColor = LyreonTextPrimary,
                        focusedTextColor = LyreonTextPrimary,
                        unfocusedTextColor = LyreonTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                errorMessage?.let { err ->
                    Text(
                        text = err,
                        fontSize = 12.sp,
                        color = LyreonCrimson,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                if (isLoadingMetadata) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = LyreonCrimson,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "Mengekstrak playlist dari Spotify...",
                            fontSize = 12.sp,
                            color = LyreonTextSecondary
                        )
                    }
                }

                playlist?.let { pl ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(LyreonSurface)
                            .border(1.dp, LyreonLine.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncArtwork(
                            url = pl.coverUrl,
                            title = pl.name,
                            size = 52.dp,
                            cornerRadius = 6.dp
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = pl.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = LyreonTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "${pl.tracks.size} trek • dibuat oleh ${pl.author}",
                                fontSize = 12.sp,
                                color = LyreonTextSecondary
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    if (isMatching) {
                        Column(Modifier.fillMaxWidth()) {
                            val progress = if (totalCount > 0) matchedCount.toFloat() / totalCount.toFloat() else 0f
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = LyreonCrimson,
                                trackColor = LyreonSurface
                            )
                            Spacer(Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Mencocokkan: $matchedCount / $totalCount lagu",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = LyreonCrimson
                                )
                                Text(
                                    text = "${(progress * 100).toInt()}%",
                                    fontSize = 11.sp,
                                    color = LyreonTextSecondary
                                )
                            }
                            currentMatchingTitle?.let { title ->
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    color = LyreonTextMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    savedPlaylistId?.let { id ->
                        Spacer(Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF10B981).copy(alpha = 0.15f))
                                .border(1.dp, Color(0xFF10B981).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .clickable {
                                    onOpenPlaylist?.invoke(id)
                                    onDismiss()
                                }
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Playlist Berhasil Diimpor! Klik di sini untuk membuka.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            val pl = playlist
            if (pl == null) {
                TextButton(
                    onClick = ::loadPlaylist,
                    enabled = !isLoadingMetadata && url.isNotBlank()
                ) {
                    Text(
                        text = "Muat Playlist",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!isLoadingMetadata && url.isNotBlank()) LyreonCrimson else LyreonTextMuted
                    )
                }
            } else if (!isMatching && savedPlaylistId == null) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TextButton(onClick = { startImport(andDownload = false, playImmediate = true) }) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp), tint = LyreonTextSecondary)
                        Spacer(Modifier.width(4.dp))
                        Text("Putar Sekarang", fontSize = 12.sp, color = LyreonTextSecondary)
                    }

                    TextButton(onClick = { startImport(andDownload = false, playImmediate = false) }) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp), tint = LyreonTextSecondary)
                        Spacer(Modifier.width(4.dp))
                        Text("Simpan Playlist", fontSize = 12.sp, color = LyreonTextSecondary)
                    }

                    Button(
                        onClick = { startImport(andDownload = true, playImmediate = false) },
                        colors = ButtonDefaults.buttonColors(containerColor = LyreonCrimson),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(Modifier.width(6.dp))
                        Text("Simpan & Unduh", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        },
        dismissButton = {
            if (!isMatching) {
                TextButton(onClick = onDismiss) {
                    Text("Tutup", color = LyreonTextSecondary)
                }
            }
        }
    )
}
