package com.lyreon.desktop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lyreon.desktop.data.DesktopDatabase
import com.lyreon.desktop.model.LyreonTrack
import com.lyreon.desktop.player.DesktopAudioPlayer
import com.lyreon.desktop.taste.DesktopTasteRepository
import com.lyreon.desktop.ui.components.SectionRule
import com.lyreon.desktop.ui.components.TrackRow
import com.lyreon.desktop.ui.theme.*

@Composable
fun ProfileScreen(
    userName: String,
    onUpdateName: (String) -> Unit,
    onPlayTrack: (LyreonTrack, List<LyreonTrack>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tasteProfile by DesktopTasteRepository.profile.collectAsState()
    val history by DesktopDatabase.history.collectAsState()
    val currentTrack by DesktopAudioPlayer.currentTrack.collectAsState()
    val isPlaying by DesktopAudioPlayer.isPlaying.collectAsState()

    var showEditNameDialog by remember { mutableStateOf(false) }

    if (showEditNameDialog) {
        NameDialog(
            current = userName,
            onSave = {
                onUpdateName(it)
                showEditNameDialog = false
            },
            onDismiss = { showEditNameDialog = false }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LyreonBackground),
        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Header
        item(key = "header") {
            Column {
                Text(
                    text = "Profil & Selera Musik",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = LyreonTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Analisis pola mendengarkan, artis favorit, dan preferensi gaya musik Anda",
                    fontSize = 13.sp,
                    color = LyreonTextSecondary
                )
            }
        }

        // Profile Identity Card
        item(key = "id_card") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(LyreonSurface)
                    .border(1.dp, LyreonLine.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(LyreonCrimson.copy(alpha = 0.2f))
                        .border(2.dp, LyreonCrimson, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = LyreonCrimson,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = userName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = LyreonTextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { showEditNameDialog = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Ubah Nama", tint = LyreonCrimson, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Pendengar Anonim • 100% Data Tersimpan di Komputer Anda",
                        fontSize = 12.sp,
                        color = LyreonTextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(LyreonElevated)
                        .border(1.dp, LyreonLine, RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "DESKTOP PRO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LyreonCrimson
                    )
                }
            }
        }

        // Stats Summary Cards Row
        item(key = "stats_row") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                StatCard(
                    title = "TOTAL PEMUTARAN",
                    value = "${tasteProfile.totalPlays} Lagu",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "ARTIS TERLACAK",
                    value = "${tasteProfile.artists.size} Artis",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "GENRE DIJELAJAHI",
                    value = "${tasteProfile.genres.size} Genre",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Top Artists
        item(key = "top_artists_section") {
            SectionRule(label = "ARTIS PALING SERING DIDENGAR")
            Spacer(modifier = Modifier.height(6.dp))

            val topArtists = tasteProfile.topArtists(5)
            if (topArtists.isEmpty()) {
                Text(
                    text = "Belum ada cukup data artis. Putar beberapa lagu untuk melihat statistik.",
                    fontSize = 12.sp,
                    color = LyreonTextMuted,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    topArtists.forEachIndexed { idx, (artist, score) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(LyreonSurface)
                                .border(1.dp, LyreonLine.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "#${idx + 1}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = LyreonCrimson,
                                modifier = Modifier.width(32.dp)
                            )
                            Text(
                                text = artist.replaceFirstChar { it.uppercase() },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LyreonTextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Skor Afinitas: ${score.toInt()}",
                                fontSize = 12.sp,
                                color = LyreonTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Top Genres & Vibe Tags
        item(key = "genres_section") {
            SectionRule(label = "GENRE & VIBE FAVORIT")
            Spacer(modifier = Modifier.height(6.dp))

            val chips = tasteProfile.personalityChips(8)
            if (chips.isEmpty()) {
                Text(
                    text = "Pola vibe musik akan muncul seiring dengan lagu-lagu yang Anda nikmati.",
                    fontSize = 12.sp,
                    color = LyreonTextMuted,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    chips.forEach { chip ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(LyreonSurface)
                                .border(1.dp, LyreonCrimson.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = chip,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = LyreonCrimson
                            )
                        }
                    }
                }
            }
        }

        // Recently Played Preview
        if (history.isNotEmpty()) {
            item(key = "recent_section") {
                SectionRule(label = "TERAKHIR DIPUTAR")
            }

            val previewTracks = history.take(5).map { it.track }
            items(previewTracks.size) { index ->
                val track = previewTracks[index]
                TrackRow(
                    track = track,
                    isActive = currentTrack?.videoId == track.videoId,
                    isPlaying = isPlaying,
                    index = index,
                    onPlay = { onPlayTrack(track, previewTracks) },
                    onAddToQueue = { DesktopAudioPlayer.addToQueue(track) }
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(LyreonSurface)
            .border(1.dp, LyreonLine.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = LyreonTextMuted,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = LyreonTextPrimary
            )
        }
    }
}

@Composable
private fun NameDialog(
    current: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember { mutableStateOf(current) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LyreonElevated,
        title = {
            Text("Ubah Nama Panggilan", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LyreonCrimson)
        },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { if (it.length <= 20) text = it },
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
            TextButton(onClick = { if (text.isNotBlank()) onSave(text.trim()) }) {
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
