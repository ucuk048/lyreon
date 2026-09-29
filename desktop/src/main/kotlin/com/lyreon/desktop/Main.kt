package com.lyreon.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.lyreon.desktop.core.LyreonLog
import com.lyreon.desktop.data.DesktopDatabase
import com.lyreon.desktop.model.LyreonTrack
import com.lyreon.desktop.player.DesktopAudioPlayer
import com.lyreon.desktop.ui.components.DesktopNavSection
import com.lyreon.desktop.ui.components.PlayerBar
import com.lyreon.desktop.ui.components.Sidebar
import com.lyreon.desktop.ui.dialogs.SpotifyImportDialog
import com.lyreon.desktop.ui.screens.*
import com.lyreon.desktop.ui.theme.DarkBorder
import com.lyreon.desktop.ui.theme.LyreonTheme
import java.awt.Dimension

fun main() {
    LyreonLog.i("Main", "Starting Lyreon Desktop...")
    // Warm up Innertube config & cache directory in background
    com.lyreon.desktop.yt.InnertubeConfig.warmUp(com.lyreon.desktop.yt.YouTubeDesktopRepository.httpClient)
    com.lyreon.desktop.player.DesktopStreamProxy.cacheDir

    application {
        val windowState = remember {
            WindowState(
                size = DpSize(1240.dp, 820.dp)
            )
        }


    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "Lyreon - Hear What Words Can't Say",
        icon = painterResource("ic_launcher.png"),
        onKeyEvent = { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Spacebar) {
                DesktopAudioPlayer.togglePlayPause()
                true
            } else {
                false
            }
        }
    ) {
        window.minimumSize = Dimension(960, 640)

        LyreonTheme(darkTheme = true) {
            LyreonDesktopApp()
        }
    }
}
}



@Composable
fun LyreonDesktopApp() {
    var currentSection by remember { mutableStateOf(DesktopNavSection.HOME) }
    var selectedPlaylistId by remember { mutableStateOf<Long?>(null) }
    var showSpotifyImportDialog by remember { mutableStateOf(false) }

    val currentTrack by DesktopAudioPlayer.currentTrack.collectAsState()
    val isPlaying by DesktopAudioPlayer.isPlaying.collectAsState()
    val isBuffering by DesktopAudioPlayer.isBuffering.collectAsState()
    val positionMs by DesktopAudioPlayer.positionMs.collectAsState()
    val durationMs by DesktopAudioPlayer.durationMs.collectAsState()
    val volume by DesktopAudioPlayer.volume.collectAsState()
    val queue by DesktopAudioPlayer.queue.collectAsState()
    val currentIndex by DesktopAudioPlayer.currentIndex.collectAsState()
    val isShuffle by DesktopAudioPlayer.isShuffle.collectAsState()
    val repeatMode by DesktopAudioPlayer.repeatMode.collectAsState()

    val downloads by DesktopDatabase.downloads.collectAsState()
    val activeDownloadsCount = remember(downloads) {
        downloads.count { it.state == "QUEUED" || it.state == "DOWNLOADING" }
    }

    var userName by remember { mutableStateOf("Ucuk") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Main Area (Sidebar + Content)
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Sidebar(
                currentSection = currentSection,
                onSectionSelected = { section ->
                    currentSection = section
                    selectedPlaylistId = null
                },
                onOpenSpotifyImport = {
                    showSpotifyImportDialog = true
                },
                queueCount = queue.size,
                downloadActiveCount = activeDownloadsCount
            )

            Divider(
                color = DarkBorder,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                if (selectedPlaylistId != null) {
                    PlaylistDetailScreen(
                        playlistId = selectedPlaylistId!!,
                        onBack = { selectedPlaylistId = null },
                        onPlayTrack = { track, list ->
                            DesktopAudioPlayer.playTrack(track, list)
                        }
                    )
                } else {
                    when (currentSection) {
                        DesktopNavSection.HOME -> {
                            HomeScreen(
                                onPlayTrack = { track, list ->
                                    DesktopAudioPlayer.playTrack(track, list)
                                },
                                onNavigateSearch = {
                                    currentSection = DesktopNavSection.SEARCH
                                },
                                userName = userName,
                                onUpdateName = { userName = it }
                            )
                        }
                        DesktopNavSection.EXPLORE,
                        DesktopNavSection.SEARCH -> {
                            ExploreScreen(
                                onPlayTrack = { track, list ->
                                    DesktopAudioPlayer.playTrack(track, list)
                                }
                            )
                        }
                        DesktopNavSection.LIBRARY -> {
                            LibraryScreen(
                                onPlayTrack = { track, list ->
                                    DesktopAudioPlayer.playTrack(track, list)
                                },
                                onOpenPlaylistDetail = { id ->
                                    selectedPlaylistId = id
                                },
                                onNavigateDownloads = {
                                    currentSection = DesktopNavSection.DOWNLOADS
                                }
                            )
                        }
                        DesktopNavSection.DOWNLOADS -> {
                            DownloadsScreen(
                                onPlayTrack = { track, list ->
                                    DesktopAudioPlayer.playTrack(track, list)
                                }
                            )
                        }
                        DesktopNavSection.QUEUE -> {
                            QueueScreen(
                                queue = queue,
                                currentIndex = currentIndex,
                                onPlayTrack = { track ->
                                    DesktopAudioPlayer.playTrack(track)
                                }
                            )
                        }
                        DesktopNavSection.LYRICS -> {
                            LyricsScreen(
                                currentTrack = currentTrack,
                                positionMs = positionMs,
                                onSeek = { DesktopAudioPlayer.seekTo(it) }
                            )
                        }
                        DesktopNavSection.PROFILE -> {
                            ProfileScreen(
                                userName = userName,
                                onUpdateName = { userName = it },
                                onPlayTrack = { track, list ->
                                    DesktopAudioPlayer.playTrack(track, list)
                                }
                            )
                        }
                        DesktopNavSection.SETTINGS -> {
                            SettingsScreen(
                                currentName = userName,
                                onUpdateName = { userName = it }
                            )
                        }
                    }
                }
            }
        }

        // Bottom Persistent Player Bar
        PlayerBar(
            currentTrack = currentTrack,
            isPlaying = isPlaying,
            isBuffering = isBuffering,
            positionMs = positionMs,
            durationMs = durationMs,
            volume = volume,
            isShuffle = isShuffle,
            repeatMode = repeatMode,
            onTogglePlayPause = { DesktopAudioPlayer.togglePlayPause() },
            onPrevious = { DesktopAudioPlayer.playPrevious() },
            onNext = { DesktopAudioPlayer.playNext() },
            onSeek = { DesktopAudioPlayer.seekTo(it) },
            onVolumeChange = { DesktopAudioPlayer.setVolume(it) },
            onToggleShuffle = { DesktopAudioPlayer.toggleShuffle() },
            onToggleRepeat = { DesktopAudioPlayer.toggleRepeat() },
            onOpenLyrics = {
                currentSection = DesktopNavSection.LYRICS
                selectedPlaylistId = null
            }
        )
    }

    if (showSpotifyImportDialog) {
        SpotifyImportDialog(
            onDismiss = { showSpotifyImportDialog = false },
            onOpenPlaylist = { playlistId ->
                showSpotifyImportDialog = false
                selectedPlaylistId = playlistId
                currentSection = DesktopNavSection.LIBRARY
            }
        )
    }
}
