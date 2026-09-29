package com.lyreon.desktop.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lyreon.desktop.ui.theme.CrimsonPrimary
import com.lyreon.desktop.ui.theme.DarkBorder
import com.lyreon.desktop.ui.theme.LyreonElevated
import com.lyreon.desktop.ui.theme.LyreonSurface

enum class DesktopNavSection {
    HOME,
    EXPLORE,
    SEARCH,
    LIBRARY,
    DOWNLOADS,
    QUEUE,
    LYRICS,
    PROFILE,
    SETTINGS
}

@Composable
fun Sidebar(
    currentSection: DesktopNavSection,
    onSectionSelected: (DesktopNavSection) -> Unit,
    onOpenSpotifyImport: () -> Unit,
    queueCount: Int,
    downloadActiveCount: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(250.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        // App Title & Official Logo
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            Image(
                painter = painterResource("ic_launcher.png"),
                contentDescription = "Lyreon Logo",
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "LYREON",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Hear What Words Can't Say",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Tombol Aksi Cepat Import Spotify
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CrimsonPrimary.copy(alpha = 0.12f))
                .border(1.dp, CrimsonPrimary.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                .clickable(onClick = onOpenSpotifyImport)
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Link,
                contentDescription = null,
                tint = CrimsonPrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Import Spotify",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = CrimsonPrimary
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        Divider(color = DarkBorder, thickness = 1.dp)
        Spacer(modifier = Modifier.height(12.dp))

        // Navigation Items
        SidebarItem(
            icon = Icons.Default.Home,
            title = "Beranda",
            isSelected = currentSection == DesktopNavSection.HOME,
            onClick = { onSectionSelected(DesktopNavSection.HOME) }
        )

        SidebarItem(
            icon = Icons.Default.Explore,
            title = "Jelajah & Pencarian",
            isSelected = currentSection == DesktopNavSection.EXPLORE || currentSection == DesktopNavSection.SEARCH,
            onClick = { onSectionSelected(DesktopNavSection.EXPLORE) }
        )

        SidebarItem(
            icon = Icons.Default.LibraryMusic,
            title = "Pustaka",
            isSelected = currentSection == DesktopNavSection.LIBRARY,
            onClick = { onSectionSelected(DesktopNavSection.LIBRARY) }
        )

        SidebarItem(
            icon = Icons.Default.Download,
            title = "Unduhan & Offline",
            badge = if (downloadActiveCount > 0) "$downloadActiveCount" else null,
            badgeColor = Color(0xFF10B981),
            isSelected = currentSection == DesktopNavSection.DOWNLOADS,
            onClick = { onSectionSelected(DesktopNavSection.DOWNLOADS) }
        )

        SidebarItem(
            icon = Icons.Default.QueueMusic,
            title = "Antrean Lagu",
            badge = if (queueCount > 0) queueCount.toString() else null,
            isSelected = currentSection == DesktopNavSection.QUEUE,
            onClick = { onSectionSelected(DesktopNavSection.QUEUE) }
        )

        SidebarItem(
            icon = Icons.Default.MusicNote,
            title = "Lirik Lagu",
            isSelected = currentSection == DesktopNavSection.LYRICS,
            onClick = { onSectionSelected(DesktopNavSection.LYRICS) }
        )

        SidebarItem(
            icon = Icons.Default.Person,
            title = "Profil & Selera",
            isSelected = currentSection == DesktopNavSection.PROFILE,
            onClick = { onSectionSelected(DesktopNavSection.PROFILE) }
        )

        SidebarItem(
            icon = Icons.Default.Settings,
            title = "Pengaturan",
            isSelected = currentSection == DesktopNavSection.SETTINGS,
            onClick = { onSectionSelected(DesktopNavSection.SETTINGS) }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Info
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(LyreonSurface)
                .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = "INNER TUBE ENGINE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = CrimsonPrimary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Streaming & Offline • 0 Akun",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SidebarItem(
    icon: ImageVector,
    title: String,
    badge: String? = null,
    badgeColor: Color = CrimsonPrimary,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val bg = if (isSelected) CrimsonPrimary.copy(alpha = 0.15f) else Color.Transparent
    val contentColor = if (isSelected) CrimsonPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 9.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = contentColor,
            modifier = Modifier.size(19.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.onSurface else contentColor,
            modifier = Modifier.weight(1f)
        )
        if (badge != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(badgeColor)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badge,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
