/*
 * Copyright (C) 2026 rixz-dev
 *
 * SPDX-License-Identifier: GPL-3.0-only
 */
package com.lyreon.app.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lyreon.app.R
import com.lyreon.app.ui.theme.LyreonBackground
import com.lyreon.app.ui.theme.LyreonCrimson
import com.lyreon.app.ui.theme.LyreonLine
import com.lyreon.app.ui.theme.LyreonSurface
import com.lyreon.app.ui.theme.LyreonTextMuted
import com.lyreon.app.ui.theme.LyreonTextPrimary
import com.lyreon.app.ui.theme.LyreonTextSecondary
import com.lyreon.app.version.VersionChecker

@Composable
fun VersionOutdatedScreen(
    currentVersion: String,
    requiredVersion: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = context as? Activity

    // Intersepsi tombol kembali perangkat untuk langsung menutup aplikasi
    BackHandler {
        activity?.finishAffinity()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LyreonBackground)
            .padding(28.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            // Ikon Pembaruan
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .border(1.dp, LyreonCrimson)
                    .background(LyreonCrimson.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.SystemUpdate,
                    contentDescription = null,
                    tint = LyreonCrimson,
                    modifier = Modifier.size(36.dp),
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.version_outdated_title).uppercase(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = LyreonTextPrimary,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.version_outdated_msg, currentVersion, requiredVersion),
                style = MaterialTheme.typography.bodyMedium,
                color = LyreonTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp),
            )

            Spacer(Modifier.height(16.dp))

            // Kotak info versi
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LyreonLine)
                    .background(LyreonSurface.copy(alpha = 0.35f))
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = "VERSI SAAT INI",
                        style = MaterialTheme.typography.labelSmall,
                        color = LyreonTextMuted,
                    )
                    Text(
                        text = "v$currentVersion",
                        style = MaterialTheme.typography.titleSmall,
                        color = LyreonCrimson,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "VERSI TERBARU",
                        style = MaterialTheme.typography.labelSmall,
                        color = LyreonTextMuted,
                    )
                    Text(
                        text = "v$requiredVersion",
                        style = MaterialTheme.typography.titleSmall,
                        color = LyreonTextPrimary,
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Tombol Perbarui (Opsional untuk unduh langsung)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LyreonCrimson)
                    .background(LyreonCrimson)
                    .clickable {
                        runCatching {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(VersionChecker.UPDATE_URL)),
                            )
                        }
                    }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.SystemUpdate,
                        contentDescription = null,
                        tint = LyreonBackground,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.version_outdated_update),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = LyreonBackground,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Tombol Keluar (Exit)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LyreonLine)
                    .background(LyreonSurface.copy(alpha = 0.4f))
                    .clickable {
                        activity?.finishAffinity()
                    }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.ExitToApp,
                        contentDescription = null,
                        tint = LyreonTextSecondary,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.version_outdated_exit),
                        style = MaterialTheme.typography.labelLarge,
                        color = LyreonTextSecondary,
                    )
                }
            }
        }
    }
}
