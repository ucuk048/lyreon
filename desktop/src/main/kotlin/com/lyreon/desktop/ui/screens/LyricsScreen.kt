package com.lyreon.desktop.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lyreon.desktop.lyrics.LrcLine
import com.lyreon.desktop.lyrics.LyricsRepository
import com.lyreon.desktop.lyrics.LyricsResult
import com.lyreon.desktop.model.LyreonTrack
import com.lyreon.desktop.ui.theme.Theme
import kotlinx.coroutines.launch

@Composable
fun LyricsScreen(
    currentTrack: LyreonTrack?,
    positionMs: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var lyricsResult by remember { mutableStateOf<LyricsResult?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(currentTrack?.videoId) {
        if (currentTrack != null) {
            isLoading = true
            lyricsResult = null
            lyricsResult = LyricsRepository.getLyrics(currentTrack)
            isLoading = false
        } else {
            lyricsResult = null
            isLoading = false
        }
    }

    val lines = lyricsResult?.synced ?: emptyList()
    val activeIndex = remember(positionMs, lines) {
        if (lines.isEmpty()) -1
        else {
            val idx = lines.indexOfLast { it.timeMs <= positionMs }
            if (idx >= 0) idx else 0
        }
    }

    LaunchedEffect(activeIndex) {
        if (activeIndex >= 0 && activeIndex < lines.size) {
            val target = (activeIndex - 2).coerceAtLeast(0)
            listState.animateScrollToItem(target)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        if (currentTrack != null) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = currentTrack.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Theme.LyreonTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentTrack.artist.ifBlank { "YouTube Music" },
                    fontSize = 14.sp,
                    color = Theme.LyreonTextSecondary
                )
            }
        } else {
            Text(
                text = "Lirik Lagu",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Theme.LyreonTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(Theme.LyreonSurface)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Theme.LyreonCrimson)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Mencari lirik...",
                            fontSize = 14.sp,
                            color = Theme.LyreonTextSecondary
                        )
                    }
                }
                currentTrack == null -> {
                    Text(
                        text = "Tidak ada lagu yang sedang diputar",
                        fontSize = 15.sp,
                        color = Theme.LyreonTextSecondary
                    )
                }
                lines.isNotEmpty() -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 40.dp)
                    ) {
                        itemsIndexed(lines) { index, line ->
                            val isActive = index == activeIndex
                            val textColor by animateColorAsState(
                                targetValue = if (isActive) Theme.LyreonCrimson else Theme.LyreonTextSecondary.copy(alpha = 0.5f),
                                animationSpec = tween(300)
                            )

                            Text(
                                text = line.text,
                                fontSize = if (isActive) 20.sp else 16.sp,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                color = textColor,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSeek(line.timeMs) }
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }
                }
                lyricsResult?.plain != null -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 24.dp)
                    ) {
                        item {
                            Text(
                                text = lyricsResult?.plain.orEmpty(),
                                fontSize = 16.sp,
                                color = Theme.LyreonTextPrimary,
                                lineHeight = 26.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                else -> {
                    Text(
                        text = "Lirik tidak ditemukan untuk lagu ini",
                        fontSize = 15.sp,
                        color = Theme.LyreonTextSecondary
                    )
                }
            }
        }
    }
}
