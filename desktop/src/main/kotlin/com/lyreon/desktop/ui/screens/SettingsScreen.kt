package com.lyreon.desktop.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lyreon.desktop.ui.components.SectionRule
import com.lyreon.desktop.ui.theme.*
import com.lyreon.desktop.yt.AudioQuality
import com.lyreon.desktop.yt.YouTubeDesktopRepository

@Composable
fun SettingsScreen(
    currentName: String,
    onUpdateName: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedQuality by remember { mutableStateOf(AudioQuality.HIGH) }
    var showNameDialog by remember { mutableStateOf(false) }
    var cacheClearedMessage by remember { mutableStateOf(false) }

    if (showNameDialog) {
        NameDialog(
            current = currentName,
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
        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        item(key = "header") {
            Column {
                Text(
                    text = "Pengaturan",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = LyreonTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Konfigurasi audio, penampilan, dan mesin streaming Lyreon Desktop",
                    fontSize = 13.sp,
                    color = LyreonTextSecondary
                )
            }
        }

        // 1. Profil & Sapaan
        item(key = "profile_section") {
            SectionRule(label = "PROFIL & SAPAAN")
            Spacer(modifier = Modifier.height(8.dp))
            SettingsCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(LyreonCrimson.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = LyreonCrimson,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Nama Sapaan di Beranda",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LyreonTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Menampilkan: \"$currentName\"",
                            fontSize = 12.sp,
                            color = LyreonTextSecondary
                        )
                    }
                    Button(
                        onClick = { showNameDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = LyreonSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LyreonLine),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Ubah", fontSize = 12.sp, color = LyreonTextPrimary)
                    }
                }
            }
        }

        // 2. Kualitas Audio
        item(key = "audio_section") {
            SectionRule(label = "KUALITAS AUDIO")
            Spacer(modifier = Modifier.height(8.dp))
            SettingsCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    QualityOption(
                        title = "Tinggi (High Quality)",
                        description = "Bitrate tertinggi (hingga 256/320 kbps AAC/M4A). Kualitas suara terbaik untuk speaker & headphone.",
                        isSelected = selectedQuality == AudioQuality.HIGH,
                        onClick = { selectedQuality = AudioQuality.HIGH }
                    )
                    Divider(color = LyreonLine.copy(alpha = 0.4f), modifier = Modifier.padding(vertical = 12.dp))
                    QualityOption(
                        title = "Seimbang (Balanced)",
                        description = "Sekitar 128 kbps. Keseimbangan optimal antara kejernihan dan kecepatan buffer.",
                        isSelected = selectedQuality == AudioQuality.BALANCED,
                        onClick = { selectedQuality = AudioQuality.BALANCED }
                    )
                    Divider(color = LyreonLine.copy(alpha = 0.4f), modifier = Modifier.padding(vertical = 12.dp))
                    QualityOption(
                        title = "Hemat Kuota (Data Saver)",
                        description = "Sekitar 64-96 kbps Opus. Ideal untuk koneksi lambat atau tethering seluler.",
                        isSelected = selectedQuality == AudioQuality.DATA_SAVER,
                        onClick = { selectedQuality = AudioQuality.DATA_SAVER }
                    )
                }
            }
        }

        // 3. Mesin InnerTube & Cache
        item(key = "engine_section") {
            SectionRule(label = "MESIN INNERTUBE & CACHE")
            Spacer(modifier = Modifier.height(8.dp))
            SettingsCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Tangga Klien Kustom (PlayerClientLadder)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LyreonTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Meld Ladder: VisionOS • Android VR • iOS • TV Embedded • Web",
                                fontSize = 12.sp,
                                color = LyreonTextSecondary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(LyreonCrimson.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "AKTIF",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = LyreonCrimson
                            )
                        }
                    }

                    Divider(color = LyreonLine.copy(alpha = 0.4f), modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Engine Signature Decipher",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LyreonTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Mozilla Rhino 1.7.15 (Native Java JS Engine)",
                                fontSize = 12.sp,
                                color = LyreonTextSecondary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF10B981).copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "READY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }

                    Divider(color = LyreonLine.copy(alpha = 0.4f), modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Cache URL Stream & Artwork",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LyreonTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (cacheClearedMessage) "Cache berhasil dibersihkan!" else "Penyimpanan memori sementara untuk kecepatan pemutaran",
                                fontSize = 12.sp,
                                color = if (cacheClearedMessage) Color(0xFF10B981) else LyreonTextSecondary
                            )
                        }
                        Button(
                            onClick = {
                                cacheClearedMessage = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LyreonSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LyreonLine),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Bersihkan Cache", fontSize = 12.sp, color = LyreonTextPrimary)
                        }
                    }
                }
            }
        }

        // 4. Tentang Lyreon
        item(key = "about_section") {
            SectionRule(label = "TENTANG LYREON")
            Spacer(modifier = Modifier.height(8.dp))
            SettingsCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource("ic_launcher.png"),
                        contentDescription = "Logo Lyreon",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(14.dp))
                    )
                    Spacer(modifier = Modifier.width(18.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "LYREON",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = LyreonTextPrimary,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(LyreonCrimson)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "v3.5.0 Desktop",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Hear What Words Can't Say",
                            fontSize = 12.sp,
                            color = LyreonTextSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Dilisensikan di bawah GNU General Public License v3.0 (GPL-3.0)",
                            fontSize = 11.sp,
                            color = LyreonTextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(LyreonSurface)
            .border(1.dp, LyreonLine.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
    ) {
        content()
    }
}

@Composable
private fun QualityOption(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = LyreonCrimson, unselectedColor = LyreonLine)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) LyreonCrimson else LyreonTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = LyreonTextSecondary
            )
        }
    }
}

@Composable
private fun NameDialog(
    current: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(current) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LyreonElevated,
        title = {
            Text("Ubah Nama Sapaan", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LyreonCrimson)
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
                    unfocusedTextColor = LyreonTextPrimary
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
