/*
 * Copyright (C) 2026 rixz-dev
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */
package com.lyreon.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lyreon.app.R
import com.lyreon.app.ui.components.Artwork
import com.lyreon.app.ui.theme.LyreonBackground
import com.lyreon.app.ui.theme.LyreonCrimson
import com.lyreon.app.ui.theme.LyreonElevated
import com.lyreon.app.ui.theme.LyreonLine
import com.lyreon.app.ui.theme.LyreonSurface
import com.lyreon.app.ui.theme.LyreonTextMuted
import com.lyreon.app.ui.theme.LyreonTextPrimary
import com.lyreon.app.ui.theme.LyreonTextSecondary
import com.lyreon.app.ui.vm.SpotifyImportViewModel

@Composable
fun SpotifyImportDialog(
    vm: SpotifyImportViewModel,
    onOpenPlaylist: (Long, String) -> Unit,
    onDismiss: () -> Unit,
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current

    AlertDialog(
        onDismissRequest = {
            if (!state.matching) {
                vm.reset()
                onDismiss()
            }
        },
        containerColor = LyreonElevated,
        title = {
            Column {
                Text(
                    text = stringResource(R.string.spotify_import_kicker),
                    style = MaterialTheme.typography.labelMedium,
                    color = LyreonCrimson,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.spotify_import_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = LyreonTextPrimary,
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
            ) {
                // Input Tautan Spotify
                OutlinedTextField(
                    value = state.url,
                    onValueChange = { vm.setUrl(it) },
                    singleLine = true,
                    enabled = !state.matching && !state.loading,
                    placeholder = {
                        Text(
                            stringResource(R.string.spotify_import_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = LyreonTextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    trailingIcon = {
                        if (!state.matching && !state.loading) {
                            IconButton(
                                onClick = {
                                    clipboardManager.getText()?.text?.let { vm.setUrl(it) }
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ContentPaste,
                                    contentDescription = stringResource(R.string.spotify_import_paste),
                                    tint = LyreonTextSecondary,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LyreonCrimson,
                        unfocusedBorderColor = LyreonLine,
                        cursorColor = LyreonTextPrimary,
                        focusedTextColor = LyreonTextPrimary,
                        unfocusedTextColor = LyreonTextPrimary,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(12.dp))

                // Pesan Error
                state.error?.let { err ->
                    Text(
                        text = err,
                        style = MaterialTheme.typography.bodySmall,
                        color = LyreonCrimson,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }

                // Status Loading Metadata
                if (state.loading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp),
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = LyreonCrimson,
                            strokeWidth = 2.dp,
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.spotify_import_fetching),
                            style = MaterialTheme.typography.bodySmall,
                            color = LyreonTextSecondary,
                        )
                    }
                }

                // Pratinjau Playlist Spotify yang didapat
                val pl = state.playlist
                if (pl != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, LyreonLine)
                            .background(LyreonSurface.copy(alpha = 0.4f))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Artwork(
                            url = pl.coverUrl,
                            title = pl.name,
                            size = 56.dp,
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = pl.name,
                                style = MaterialTheme.typography.titleSmall,
                                color = LyreonTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.playlist_track_count, pl.tracks.size),
                                style = MaterialTheme.typography.labelSmall,
                                color = LyreonTextSecondary,
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Progress Bar Pencocokan Lagu ke YouTube Music
                    if (state.matching) {
                        Column(Modifier.fillMaxWidth()) {
                            val progress = if (state.totalCount > 0) {
                                state.matchedCount.toFloat() / state.totalCount.toFloat()
                            } else {
                                0f
                            }
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp),
                                color = LyreonCrimson,
                                trackColor = LyreonSurface,
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = stringResource(
                                    R.string.spotify_import_progress,
                                    state.matchedCount,
                                    state.totalCount,
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = LyreonCrimson,
                            )
                            state.currentMatchingTitle?.let { title ->
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LyreonTextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }

                    // Status Sukses Disimpan
                    if (state.saved && state.savedPlaylistId != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, LyreonCrimson)
                                .background(LyreonCrimson.copy(alpha = 0.15f))
                                .clickable {
                                    onOpenPlaylist(state.savedPlaylistId!!, pl.name)
                                    vm.reset()
                                    onDismiss()
                                }
                                .padding(12.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.spotify_import_saved_action),
                                style = MaterialTheme.typography.labelMedium,
                                color = LyreonTextPrimary,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            val pl = state.playlist
            if (pl == null) {
                // Tombol "Muat Playlist"
                TextButton(
                    onClick = { vm.loadPlaylist() },
                    enabled = !state.loading && state.url.isNotBlank(),
                ) {
                    Text(
                        stringResource(R.string.spotify_import_load),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (!state.loading && state.url.isNotBlank()) LyreonCrimson else LyreonTextMuted,
                    )
                }
            } else if (!state.matching && !state.saved) {
                // Tombol "Simpan ke Library" & "Putar Sekarang"
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = {
                            vm.playNow {
                                vm.reset()
                                onDismiss()
                            }
                        },
                    ) {
                        Icon(
                            Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = LyreonTextSecondary,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            stringResource(R.string.spotify_import_play_now),
                            style = MaterialTheme.typography.labelMedium,
                            color = LyreonTextSecondary,
                        )
                    }

                    TextButton(
                        onClick = { vm.importToLibrary() },
                    ) {
                        Icon(
                            Icons.Filled.Save,
                            contentDescription = null,
                            tint = LyreonCrimson,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            stringResource(R.string.spotify_import_save),
                            style = MaterialTheme.typography.labelMedium,
                            color = LyreonCrimson,
                        )
                    }
                }
            }
        },
        dismissButton = {
            if (!state.matching) {
                TextButton(
                    onClick = {
                        vm.reset()
                        onDismiss()
                    },
                ) {
                    Text(
                        stringResource(R.string.action_cancel),
                        style = MaterialTheme.typography.labelMedium,
                        color = LyreonTextSecondary,
                    )
                }
            }
        },
    )
}
