package com.lyreon.desktop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lyreon.desktop.ui.theme.Theme

data class DesktopGenre(
    val id: String,
    val name: String,
    val searchQuery: String
)

val defaultDesktopGenres: List<DesktopGenre> = listOf(
    DesktopGenre("all", "Semua", "lagu pop indonesia"),
    DesktopGenre("pop", "Pop Indonesia", "pop indonesia terpopuler"),
    DesktopGenre("indie", "Indie Lokal", "indie indonesia populer"),
    DesktopGenre("rock", "Rock", "rock indonesia hits"),
    DesktopGenre("dangdut", "Dangdut Koplo", "dangdut koplo terpopuler"),
    DesktopGenre("rnb", "R&B / Soul", "rnb indonesia hits"),
    DesktopGenre("hiphop", "Hip-Hop", "indonesian hip hop"),
    DesktopGenre("acoustic", "Akustik Santai", "akustik indonesia galau"),
    DesktopGenre("lofi", "Lofi Beats", "lofi chill study beats"),
    DesktopGenre("night", "Malam Hari", "lagu santai pengantar tidur"),
    DesktopGenre("kpop", "K-Pop", "kpop top hits"),
    DesktopGenre("jpop", "J-Pop", "jpop trending hits")
)

@Composable
fun GenreReelSlider(
    genres: List<DesktopGenre> = defaultDesktopGenres,
    selectedId: String,
    onSelectGenre: (DesktopGenre) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(genres, key = { it.id }) { genre ->
            val isSelected = genre.id == selectedId
            val chipShape = RoundedCornerShape(20.dp)
            Box(
                modifier = Modifier
                    .clip(chipShape)
                    .background(if (isSelected) Theme.LyreonCrimson else Theme.LyreonSurface)
                    .border(
                        1.dp,
                        if (isSelected) Theme.LyreonCrimson else Theme.LyreonLine.copy(alpha = 0.5f),
                        chipShape
                    )
                    .clickable { onSelectGenre(genre) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = genre.name,
                    color = if (isSelected) Color.White else Theme.LyreonTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}
