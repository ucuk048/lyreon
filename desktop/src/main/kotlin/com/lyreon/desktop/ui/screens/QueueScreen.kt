package com.lyreon.desktop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lyreon.desktop.model.LyreonTrack
import com.lyreon.desktop.model.formatDuration
import com.lyreon.desktop.player.DesktopAudioPlayer
import com.lyreon.desktop.ui.components.AsyncArtwork
import com.lyreon.desktop.ui.theme.Theme

@Composable
fun QueueScreen(
    queue: List<LyreonTrack>,
    currentIndex: Int,
    onPlayTrack: (LyreonTrack) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Antrean Putar",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Theme.LyreonTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${queue.size} lagu dalam antrean",
                    fontSize = 13.sp,
                    color = Theme.LyreonTextSecondary
                )
            }

            if (queue.isNotEmpty()) {
                Button(
                    onClick = { DesktopAudioPlayer.clearQueue() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Theme.LyreonElevated,
                        contentColor = Theme.LyreonCrimson
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Hapus Antrean", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (queue.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Antrean putar kosong",
                    fontSize = 15.sp,
                    color = Theme.LyreonTextSecondary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                itemsIndexed(queue) { index, track ->
                    val isPlaying = index == currentIndex
                    val rowShape = RoundedCornerShape(10.dp)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(rowShape)
                            .background(if (isPlaying) Theme.LyreonElevated else Color.Transparent)
                            .clickable { onPlayTrack(track) }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = (index + 1).toString(),
                            fontSize = 13.sp,
                            fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Normal,
                            color = if (isPlaying) Theme.LyreonCrimson else Theme.LyreonTextSecondary,
                            modifier = Modifier.width(32.dp)
                        )

                        AsyncArtwork(
                            url = track.thumbnailUrl,
                            title = track.title,
                            size = 44.dp,
                            cornerRadius = 6.dp
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = track.title,
                                fontSize = 14.sp,
                                fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Medium,
                                color = if (isPlaying) Theme.LyreonCrimson else Theme.LyreonTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = track.artist.ifBlank { "YouTube Music" },
                                fontSize = 12.sp,
                                color = Theme.LyreonTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = formatDuration(track.durationSec),
                            fontSize = 12.sp,
                            color = Theme.LyreonTextSecondary
                        )
                    }
                }
            }
        }
    }
}
