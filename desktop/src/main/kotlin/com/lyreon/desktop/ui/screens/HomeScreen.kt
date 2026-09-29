package com.lyreon.desktop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lyreon.desktop.core.LyreonLog
import com.lyreon.desktop.data.DesktopDatabase
import com.lyreon.desktop.model.LyreonTrack
import com.lyreon.desktop.model.SearchFilter
import com.lyreon.desktop.player.DesktopAudioPlayer
import com.lyreon.desktop.ui.components.*
import com.lyreon.desktop.ui.theme.*
import com.lyreon.desktop.yt.YouTubeDesktopRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun HomeScreen(
    onPlayTrack: (LyreonTrack, List<LyreonTrack>) -> Unit,
    onNavigateSearch: () -> Unit = {},
    userName: String = "Ucuk",
    onUpdateName: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val currentTrack by DesktopAudioPlayer.currentTrack.collectAsState()
    val isPlaying by DesktopAudioPlayer.isPlaying.collectAsState()
    val historyEntries by DesktopDatabase.history.collectAsState()
    val historyTracks = remember(historyEntries) { historyEntries.map { it.track } }

    var showNameDialog by remember { mutableStateOf(false) }

    // Pencarian Langsung di Beranda
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<LyreonTrack>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var searchJob by remember { mutableStateOf<Job?>(null) }

    // Data awal & trek kurasi
    var quickPicks by remember { mutableStateOf(YouTubeDesktopRepository.curatedInitialTracks) }
    var trendingTracks by remember { mutableStateOf(YouTubeDesktopRepository.curatedInitialTracks.take(8)) }
    var selectedGenreId by remember { mutableStateOf("all") }
    var isGenreLoading by remember { mutableStateOf(false) }

    fun handleSearch(q: String) {
        searchQuery = q
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
                val results = YouTubeDesktopRepository.search(q, SearchFilter.SONGS)
                searchResults = results
            } catch (e: Exception) {
                LyreonLog.w("HomeScreen", "Live search error: ${e.message}")
            } finally {
                isSearching = false
            }
        }
    }

    // Ambil pembaruan online di latar belakang tanpa memblokir UI
    LaunchedEffect(Unit) {
        try {
            val livePicks = YouTubeDesktopRepository.getQuickPicks()
            if (livePicks.isNotEmpty()) {
                quickPicks = livePicks
                if (selectedGenreId == "all") {
                    trendingTracks = livePicks.take(10)
                }
            }
        } catch (e: Exception) {
            LyreonLog.w("HomeScreen", "Background quick picks fetch: ${e.message}")
        }
    }

    if (showNameDialog) {
        NameEditDialog(
            currentName = userName,
            onSave = {
                onUpdateName(it)
                showNameDialog = false
            },
            onDismiss = { showNameDialog = false }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LyreonBackground),
        contentPadding = PaddingValues(start = 28.dp, end = 28.dp, top = 24.dp, bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. GREETING BLOCK
        item(key = "greeting") {
            GreetingHeader(
                name = userName,
                onEditName = { showNameDialog = true }
            )
        }

        // 2. SEARCH BAR LANGSUNG DI BERANDA (Tidak perlu pindah navigasi)
        item(key = "home_search_bar") {
            HomeSearchBar(
                query = searchQuery,
                onQueryChange = { handleSearch(it) },
                onClear = { handleSearch("") }
            )
        }

        // KONDISI 1: JIKA USER SEDANG MENCARI DI BERANDA -> TAMPILKAN HASIL LANGSUNG DI BAWAHNYA
        if (searchQuery.isNotBlank()) {
            item(key = "search_header") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "HASIL PENCARIAN UNTUK \"${searchQuery.uppercase()}\"",
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
                item(key = "search_loading") {
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
                item(key = "search_empty") {
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
                itemsIndexed(searchResults, key = { index, t -> "home_search_${t.videoId}_$index" }) { index, track ->
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
            // KONDISI 2: TAMPILAN NORMAL BERANDA
            // 3. PILIHAN UNTUKMU / QUICK PICKS
            item(key = "quick_picks_header") {
                SectionRule(label = "PILIHAN UNTUKMU")
            }

            item(key = "quick_picks_row") {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    itemsIndexed(quickPicks, key = { _, t -> t.videoId }) { index, track ->
                        QuickCard(
                            track = track,
                            active = currentTrack?.videoId == track.videoId,
                            onClick = { onPlayTrack(track, quickPicks) }
                        )
                    }
                }
            }

            // Baru saja diputar
            if (historyTracks.isNotEmpty()) {
                item(key = "history_header") {
                    SectionRule(label = "BARU SAJA DIPUTAR")
                }
                item(key = "history_row") {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        itemsIndexed(historyTracks, key = { _, t -> "hist_${t.videoId}" }) { _, track ->
                            QuickCard(
                                track = track,
                                active = currentTrack?.videoId == track.videoId,
                                onClick = { onPlayTrack(track, historyTracks) }
                            )
                        }
                    }
                }
            }

            // 4. JELAJAHI GENRE & SUASANA (Slider Chip Genre)
            item(key = "genres_header") {
                SectionRule(label = "JELAJAHI GENRE & SUASANA")
            }

            item(key = "genres_slider") {
                GenreReelSlider(
                    selectedId = selectedGenreId,
                    onSelectGenre = { genre ->
                        selectedGenreId = genre.id
                        scope.launch {
                            isGenreLoading = true
                            try {
                                if (genre.id == "all") {
                                    trendingTracks = quickPicks.take(10)
                                } else {
                                    val res = YouTubeDesktopRepository.search(genre.searchQuery, SearchFilter.SONGS)
                                    if (res.isNotEmpty()) {
                                        trendingTracks = res.take(10)
                                    }
                                }
                            } catch (e: Exception) {
                                LyreonLog.w("HomeScreen", "Genre fetch error: ${e.message}")
                            } finally {
                                isGenreLoading = false
                            }
                        }
                    }
                )
            }

            // 5. TREN HARI INI / POPULER LIST
            item(key = "trending_header") {
                val genreName = defaultDesktopGenres.find { it.id == selectedGenreId }?.name ?: "Populer"
                SectionRule(label = if (selectedGenreId == "all") "TREN DI INDONESIA" else "TREK $genreName")
            }

            if (isGenreLoading) {
                item(key = "loading_genre") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = LyreonCrimson,
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 3.dp
                        )
                    }
                }
            } else {
                itemsIndexed(trendingTracks, key = { i, t -> "${t.videoId}_$i" }) { index, track ->
                    TrackRow(
                        track = track,
                        isActive = currentTrack?.videoId == track.videoId,
                        isPlaying = isPlaying,
                        index = index,
                        onPlay = { onPlayTrack(track, trendingTracks) },
                        onAddToQueue = { DesktopAudioPlayer.addToQueue(track) }
                    )
                }
            }

            // 6. FOOTER
            item(key = "footer") {
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = "LYREON",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = LyreonTextMuted,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Hear What Words Can't Say • Versi Desktop",
                        fontSize = 11.sp,
                        color = LyreonTextMuted.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun GreetingHeader(
    name: String,
    onEditName: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greetingText = when (hour) {
        in 4..10 -> "Selamat Pagi,"
        in 11..14 -> "Selamat Siang,"
        in 15..18 -> "Selamat Sore,"
        else -> "Selamat Malam,"
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = greetingText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = LyreonTextSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = LyreonTextPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Ganti Nama",
                    tint = LyreonCrimson,
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onEditName)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ada kata-kata yang tak sempat terucap, dan melodi yang mengatakannya untukmu.",
                fontSize = 12.sp,
                color = LyreonTextMuted
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(LyreonSurface)
                .border(1.dp, LyreonLine.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Desktop v3.5 • Anonim",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = LyreonCrimson
            )
        }
    }
}

@Composable
private fun HomeSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(50)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(LyreonSurface.copy(alpha = 0.85f))
            .border(
                1.dp,
                if (query.isNotBlank()) LyreonCrimson else LyreonLine.copy(alpha = 0.4f),
                shape
            )
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Cari",
            tint = if (query.isNotBlank()) LyreonCrimson else LyreonTextMuted,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = TextStyle(
                color = LyreonTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.weight(1f),
            decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                    Text(
                        text = "Cari lagu, artis, atau album YouTube Music...",
                        fontSize = 13.sp,
                        color = LyreonTextMuted
                    )
                }
                innerTextField()
            }
        )
        if (query.isNotEmpty()) {
            IconButton(
                onClick = onClear,
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

@Composable
private fun NameEditDialog(
    currentName: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LyreonElevated,
        title = {
            Text(
                text = "Ubah Nama Profil",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = LyreonTextPrimary
            )
        },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Nama Pengguna") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LyreonCrimson,
                    unfocusedBorderColor = LyreonLine,
                    focusedTextColor = LyreonTextPrimary,
                    unfocusedTextColor = LyreonTextPrimary,
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { if (text.isNotBlank()) onSave(text.trim()) },
                colors = ButtonDefaults.buttonColors(containerColor = LyreonCrimson)
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = LyreonTextSecondary)
            }
        }
    )
}
