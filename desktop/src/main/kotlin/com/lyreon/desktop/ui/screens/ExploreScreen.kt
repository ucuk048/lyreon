package com.lyreon.desktop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lyreon.desktop.core.LyreonLog
import com.lyreon.desktop.download.DesktopDownloadManager
import com.lyreon.desktop.model.LyreonTrack
import com.lyreon.desktop.model.SearchFilter
import com.lyreon.desktop.player.DesktopAudioPlayer
import com.lyreon.desktop.ui.components.SectionRule
import com.lyreon.desktop.ui.components.TrackRow
import com.lyreon.desktop.ui.theme.*
import com.lyreon.desktop.yt.YouTubeDesktopRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ExploreCategory(
    val id: String,
    val title: String,
    val subtitle: String,
    val query: String,
    val color: Color,
)

val exploreCategories = listOf(
    ExploreCategory("top_id", "Tangga Lagu Indonesia", "Hits terpopuler di tanah air", "Top Hits Indonesia 2026", LyreonCrimson),
    ExploreCategory("koplo", "Dangdut & Koplo", "Goyang & irama kendang terbaik", "Dangdut Koplo Terbaru", Color(0xFFE11D48)),
    ExploreCategory("indie", "Indie Lokal", "Senja, puisi, dan melodi hangat", "Lagu Indie Indonesia", Color(0xFFD97706)),
    ExploreCategory("galau", "Galau & Melankolis", "Ketika kata tak sanggup terucap", "Lagu Galau Indonesia Populer", Color(0xFF6366F1)),
    ExploreCategory("pop_hits", "Pop Global", "Trek terpanas internasional", "Top Global Pop Hits", Color(0xFF2563EB)),
    ExploreCategory("lofi", "Lofi & Santai", "Irama fokus, belajar, dan rebahan", "Lofi Hip Hop Chill Beats", Color(0xFF0D9488)),
    ExploreCategory("kpop", "K-Pop Viral", "Idol terpopuler & comeback terbaru", "K-Pop Viral Hits", Color(0xFFEC4899)),
    ExploreCategory("rock", "Rock & Metal", "Distorsi gitar dan energi panggung", "Rock Metal Hits", Color(0xFFDC2626)),
    ExploreCategory("phonk", "Drift Phonk", "Bass jedag-jedug cowbell kencang", "Drift Phonk Music", Color(0xFF9333EA)),
    ExploreCategory("anime", "Anime & J-Pop", "Opening, ending, dan soundtrack epik", "Anime Opening J-Pop", Color(0xFFEA580C)),
)

@Composable
fun ExploreScreen(
    onPlayTrack: (LyreonTrack, List<LyreonTrack>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val currentTrack by DesktopAudioPlayer.currentTrack.collectAsState()
    val isPlaying by DesktopAudioPlayer.isPlaying.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(SearchFilter.ALL) }
    var searchResults by remember { mutableStateOf<List<LyreonTrack>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var searchJob by remember { mutableStateOf<Job?>(null) }

    var selectedCategory by remember { mutableStateOf(exploreCategories.first()) }
    var categoryTracks by remember { mutableStateOf<List<LyreonTrack>>(emptyList()) }
    var isLoadingCategory by remember { mutableStateOf(false) }

    fun doSearch(q: String, filter: SearchFilter) {
        searchJob?.cancel()
        if (q.isBlank()) {
            searchResults = emptyList()
            isSearching = false
            return
        }
        searchJob = scope.launch {
            delay(350)
            isSearching = true
            try {
                val results = YouTubeDesktopRepository.search(q, filter)
                searchResults = results
            } catch (e: Exception) {
                LyreonLog.e("ExploreScreen", "Search error: ${e.message}")
            } finally {
                isSearching = false
            }
        }
    }

    fun loadCategory(cat: ExploreCategory) {
        selectedCategory = cat
        scope.launch {
            isLoadingCategory = true
            try {
                val results = YouTubeDesktopRepository.search(cat.query, SearchFilter.SONGS)
                categoryTracks = results
            } catch (e: Exception) {
                LyreonLog.e("ExploreScreen", "Gagal memuat kategori ${cat.title}: ${e.message}")
            } finally {
                isLoadingCategory = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadCategory(exploreCategories.first())
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LyreonBackground),
        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item(key = "header") {
            Column {
                Text(
                    text = "Jelajah & Pencarian",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = LyreonTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Cari musik atau telusuri kategori genre, suasana hati, dan rilisan terbaik",
                    fontSize = 13.sp,
                    color = LyreonTextSecondary
                )
            }
        }

        // Search Bar Terintegrasi
        item(key = "integrated_search_bar") {
            val shape = RoundedCornerShape(12.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(LyreonSurface)
                    .border(
                        1.dp,
                        if (searchQuery.isNotBlank()) LyreonCrimson else LyreonLine.copy(alpha = 0.5f),
                        shape
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Cari",
                    tint = if (searchQuery.isNotBlank()) LyreonCrimson else LyreonTextMuted,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                BasicTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        doSearch(it, selectedFilter)
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = LyreonTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Cari lagu, artis, atau album...",
                                fontSize = 14.sp,
                                color = LyreonTextMuted
                            )
                        }
                        innerTextField()
                    }
                )
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            searchQuery = ""
                            searchResults = emptyList()
                            isSearching = false
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Hapus",
                            tint = LyreonTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Filter Pills (saat mengetik pencarian)
        if (searchQuery.isNotBlank()) {
            item(key = "search_filter_pills") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SearchFilter.entries.forEach { filter ->
                        val isSelected = selectedFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) LyreonCrimson else LyreonSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) LyreonCrimson else LyreonLine.copy(alpha = 0.5f),
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    selectedFilter = filter
                                    doSearch(searchQuery, filter)
                                }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = filter.label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else LyreonTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // KONDISI 1: Hasil Pencarian Aktif
        if (searchQuery.isNotBlank()) {
            item(key = "search_results_title") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "HASIL PENCARIAN (${searchResults.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LyreonCrimson,
                        letterSpacing = 1.sp,
                        modifier = Modifier.weight(1f)
                    )
                    if (isSearching) {
                        CircularProgressIndicator(
                            color = LyreonCrimson,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            if (isSearching && searchResults.isEmpty()) {
                item(key = "search_loading_state") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = LyreonCrimson,
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 3.dp
                        )
                    }
                }
            } else if (!isSearching && searchResults.isEmpty()) {
                item(key = "search_empty_state") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tidak ada lagu ditemukan untuk '$searchQuery'",
                            fontSize = 14.sp,
                            color = LyreonTextMuted
                        )
                    }
                }
            } else {
                itemsIndexed(searchResults, key = { index, t -> "explore_search_${t.videoId}_$index" }) { index, track ->
                    TrackRow(
                        track = track,
                        isActive = currentTrack?.videoId == track.videoId,
                        isPlaying = isPlaying,
                        index = index,
                        onPlay = { onPlayTrack(track, searchResults) },
                        onAddToQueue = { DesktopAudioPlayer.addToQueue(track) }
                    )
                }
            }
        } else {
            // KONDISI 2: Tampilan Kategori Jelajah Musik (Query Kosong)
            item(key = "categories_header") {
                SectionRule(label = "GENRE & SUASANA HATI")
            }

            item(key = "categories_row") {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(exploreCategories, key = { it.id }) { cat ->
                        val isSelected = selectedCategory.id == cat.id
                        val cardShape = RoundedCornerShape(12.dp)
                        val cardBg = if (isSelected) cat.color.copy(alpha = 0.22f) else LyreonSurface
                        val borderColor = if (isSelected) cat.color else LyreonLine.copy(alpha = 0.4f)

                        Box(
                            modifier = Modifier
                                .width(180.dp)
                                .clip(cardShape)
                                .background(cardBg)
                                .border(1.dp, borderColor, cardShape)
                                .clickable { loadCategory(cat) }
                                .padding(14.dp)
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(cat.color.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Explore,
                                        contentDescription = null,
                                        tint = cat.color,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = cat.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LyreonTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = cat.subtitle,
                                    fontSize = 11.sp,
                                    color = LyreonTextSecondary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // Header Kategori Aktif
            item(key = "selected_cat_header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = selectedCategory.title.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = selectedCategory.color,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Koleksi Lagu Pilihan",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = LyreonTextPrimary
                        )
                    }

                    if (categoryTracks.isNotEmpty()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Tombol Unduh Semua
                            OutlinedButton(
                                onClick = {
                                    DesktopDownloadManager.enqueueAll(categoryTracks)
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp), tint = LyreonTextPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Unduh Semua", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LyreonTextPrimary)
                            }

                            // Tombol Putar Semua
                            Button(
                                onClick = { onPlayTrack(categoryTracks.first(), categoryTracks) },
                                colors = ButtonDefaults.buttonColors(containerColor = selectedCategory.color),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Putar Semua", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            if (isLoadingCategory) {
                item(key = "loading_indicator") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = selectedCategory.color,
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 3.dp
                        )
                    }
                }
            } else if (categoryTracks.isEmpty()) {
                item(key = "empty_cat") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Tidak ada lagu ditemukan untuk kategori ini.", color = LyreonTextSecondary)
                    }
                }
            } else {
                itemsIndexed(categoryTracks, key = { index, t -> "${selectedCategory.id}_${t.videoId}_$index" }) { index, track ->
                    TrackRow(
                        track = track,
                        isActive = currentTrack?.videoId == track.videoId,
                        isPlaying = isPlaying,
                        index = index,
                        onPlay = { onPlayTrack(track, categoryTracks) },
                        onAddToQueue = { DesktopAudioPlayer.addToQueue(track) }
                    )
                }
            }
        }
    }
}
