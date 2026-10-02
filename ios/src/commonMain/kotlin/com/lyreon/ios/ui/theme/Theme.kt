package com.lyreon.ios.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val CrimsonRed = Color(0xFFE50914)
val CrimsonRedDim = Color(0xFF991218)
val DarkBackground = Color(0xFF0B0C0E)
val DarkSurface = Color(0xFF16181D)
val DarkSurfaceVariant = Color(0xFF20232A)
val DarkBorder = Color(0xFF2E333D)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF9CA3AF)
val TextMuted = Color(0xFF6B7280)

private val LyreonDarkColorScheme: ColorScheme = darkColorScheme(
    primary = CrimsonRed,
    onPrimary = Color.White,
    primaryContainer = CrimsonRedDim,
    onPrimaryContainer = Color.White,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder
)

@Composable
fun LyreonTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LyreonDarkColorScheme,
        typography = Typography(),
        content = content
    )
}
