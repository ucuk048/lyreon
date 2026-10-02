package com.lyreon.ios

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lyreon.ios.data.IosDatabase
import com.lyreon.ios.model.BottomTab
import com.lyreon.ios.model.LyreonTrack
import com.lyreon.ios.player.getAudioPlayer
import com.lyreon.ios.ui.components.PlayerBar
import com.lyreon.ios.ui.components.TrackRow
import com.lyreon.ios.ui.theme.*

@Composable
fun LyreonIosApp() {
    val player = remember { getAudioPlayer() }
    var currentTab by remember { mutableStateOf(BottomTab.HOME) }
    var searchQuery by remember { mutableStateOf("") }

    val currentTrack by player.currentTrack.collectAsState()
    val likedTrackIds by IosDatabase.likedTrackIds.collectAsState()

    // Sample catalogue for instant playback
    val initialTracks = remember {
        listOf(
            LyreonTrack("sample_1", "Die With A Smile", "Lady Gaga, Bruno Mars", 251),
            LyreonTrack("sample_2", "Birds of a Feather", "Billie Eilish", 192),
            LyreonTrack("sample_3", "Espresso", "Sabrina Carpenter", 175),
            LyreonTrack("sample_4", "Taste", "Sabrina Carpenter", 157),
            LyreonTrack("sample_5", "Good Luck, Babe!", "Chappell Roan", 218),
            LyreonTrack("sample_6", "Not Like Us", "Kendrick Lamar", 274),
            LyreonTrack("sample_7", "Beautiful Things", "Benson Boone", 180),
            LyreonTrack("sample_8", "Too Sweet", "Hozier", 251),
            LyreonTrack("sample_9", "Lose Control", "Teddy Swims", 210),
            LyreonTrack("sample_10", "Gata Only", "FloyyMenor, Cris Mj", 222)
        )
    }

    LyreonTheme {
        Scaffold(
            containerColor = DarkBackground,
            bottomBar = {
                Column {
                    // Mini player bar if track selected
                    if (currentTrack != null) {
                        PlayerBar(player = player)
                    }

                    // Native iOS style bottom navigation bar
                    NavigationBar(
                        containerColor = DarkSurface,
                        contentColor = TextPrimary,
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            selected = currentTab == BottomTab.HOME,
                            onClick = { currentTab = BottomTab.HOME },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
                            label = { Text("Beranda", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CrimsonRed,
                                selectedTextColor = CrimsonRed,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = DarkSurfaceVariant
                            )
                        )
                        NavigationBarItem(
                            selected = currentTab == BottomTab.EXPLORE,
                            onClick = { currentTab = BottomTab.EXPLORE },
                            icon = { Icon(Icons.Default.Explore, contentDescription = "Jelajah") },
                            label = { Text("Jelajah", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CrimsonRed,
                                selectedTextColor = CrimsonRed,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = DarkSurfaceVariant
                            )
                        )
                        NavigationBarItem(
                            selected = currentTab == BottomTab.SEARCH,
                            onClick = { currentTab = BottomTab.SEARCH },
                            icon = { Icon(Icons.Default.Search, contentDescription = "Cari") },
                            label = { Text("Cari", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CrimsonRed,
                                selectedTextColor = CrimsonRed,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = DarkSurfaceVariant
                            )
                        )
                        NavigationBarItem(
                            selected = currentTab == BottomTab.LIBRARY,
                            onClick = { currentTab = BottomTab.LIBRARY },
                            icon = { Icon(Icons.Default.LibraryMusic, contentDescription = "Koleksi") },
                            label = { Text("Koleksi", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CrimsonRed,
                                selectedTextColor = CrimsonRed,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = DarkSurfaceVariant
                            )
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentTab) {
                    BottomTab.HOME -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            item {
                                Text(
                                    text = "LYREON",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = CrimsonRed,
                                    letterSpacing = 2.sp
                                )
                                Text(
                                    text = "Hear What Words Can't Say",
                                    fontSize = 13.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(bottom = 20.dp)
                                )
                                Text(
                                    text = "Rekomendasi Teratas",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                            }

                            items(initialTracks) { track ->
                                TrackRow(
                                    track = track,
                                    isCurrent = currentTrack?.id == track.id,
                                    isLiked = likedTrackIds.contains(track.id),
                                    onTrackClick = {
                                        player.play(track, initialTracks, initialTracks.indexOf(track))
                                        IosDatabase.addToHistory(track)
                                    },
                                    onLikeClick = { IosDatabase.toggleLike(track) }
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }

                    BottomTab.EXPLORE -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            item {
                                Text(
                                    text = "Jelajah Musik",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }
                            val genres = listOf("Pop", "Hip-Hop", "Rock", "R&B", "Indie", "Jazz", "Electronic", "Acoustic")
                            items(genres) { genre ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(64.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(DarkSurfaceVariant)
                                        .padding(horizontal = 16.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = genre,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }

                    BottomTab.SEARCH -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 16.dp)
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Cari lagu, artis, atau album...", color = TextMuted) },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CrimsonRed,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedContainerColor = DarkSurfaceVariant,
                                    unfocusedContainerColor = DarkSurfaceVariant,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            val filtered = initialTracks.filter {
                                it.title.contains(searchQuery, ignoreCase = true) ||
                                it.artist.contains(searchQuery, ignoreCase = true)
                            }

                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(filtered) { track ->
                                    TrackRow(
                                        track = track,
                                        isCurrent = currentTrack?.id == track.id,
                                        isLiked = likedTrackIds.contains(track.id),
                                        onTrackClick = {
                                            player.play(track, filtered, filtered.indexOf(track))
                                            IosDatabase.addToHistory(track)
                                        },
                                        onLikeClick = { IosDatabase.toggleLike(track) }
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                    }

                    BottomTab.LIBRARY -> {
                        val history by IosDatabase.history.collectAsState()
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            item {
                                Text(
                                    text = "Koleksi Saya",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                Text(
                                    text = "Riwayat Putar (${history.size})",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CrimsonRed,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                            }
                            if (history.isEmpty()) {
                                item {
                                    Text(
                                        text = "Belum ada riwayat putar.",
                                        fontSize = 14.sp,
                                        color = TextMuted
                                    )
                                }
                            } else {
                                items(history) { track ->
                                    TrackRow(
                                        track = track,
                                        isCurrent = currentTrack?.id == track.id,
                                        isLiked = likedTrackIds.contains(track.id),
                                        onTrackClick = { player.play(track, history, history.indexOf(track)) },
                                        onLikeClick = { IosDatabase.toggleLike(track) }
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
