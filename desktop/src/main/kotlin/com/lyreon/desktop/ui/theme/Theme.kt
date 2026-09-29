package com.lyreon.desktop.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LyreonCrimson = Color(4293477728L)
val LyreonBackground = Color(4278913808L)
val LyreonSurface = Color(4279505948L)
val LyreonElevated = Color(4280032294L)
val LyreonLine = Color(4280821810L)
val LyreonLineSoft = Color(0xFF22222CL)
val LyreonTextPrimary = Color(0xFFF4F4F8L)
val LyreonTextSecondary = Color(0xFFA3A3AFL)
val LyreonTextMuted = Color(4285624701L)
val LyreonHairline = Color(0x14FFFFFF)

object Theme {
    val LyreonCrimson = com.lyreon.desktop.ui.theme.LyreonCrimson
    val LyreonBackground = com.lyreon.desktop.ui.theme.LyreonBackground
    val LyreonSurface = com.lyreon.desktop.ui.theme.LyreonSurface
    val LyreonElevated = com.lyreon.desktop.ui.theme.LyreonElevated
    val LyreonLine = com.lyreon.desktop.ui.theme.LyreonLine
    val LyreonLineSoft = com.lyreon.desktop.ui.theme.LyreonLineSoft
    val LyreonTextPrimary = com.lyreon.desktop.ui.theme.LyreonTextPrimary
    val LyreonTextSecondary = com.lyreon.desktop.ui.theme.LyreonTextSecondary
    val LyreonTextMuted = com.lyreon.desktop.ui.theme.LyreonTextMuted
    val LyreonHairline = com.lyreon.desktop.ui.theme.LyreonHairline
}

val CrimsonPrimary = LyreonCrimson
val CrimsonHover = Color(4292227917L)
val CrimsonLight = Color(4294929281L)
val DarkBackground = LyreonBackground
val DarkSurface = LyreonSurface
val DarkSurfaceElevated = LyreonElevated
val DarkBorder = LyreonLine
val DarkTextPrimary = LyreonTextPrimary
val DarkTextSecondary = LyreonTextSecondary
val DarkTextMuted = LyreonTextMuted

private val DarkColors = darkColorScheme(
    primary = LyreonCrimson,
    onPrimary = Color.White,
    primaryContainer = Color(4283172121L),
    onPrimaryContainer = Color(0xFFFFD9DFL),
    inversePrimary = CrimsonLight,
    secondary = Color.Black,
    background = LyreonBackground,
    onBackground = LyreonTextPrimary,
    surface = LyreonSurface,
    onSurface = LyreonTextPrimary,
    surfaceVariant = LyreonElevated,
    onSurfaceVariant = LyreonTextSecondary,
    outline = LyreonLine
)

private val LightColors = lightColorScheme(
    primary = LyreonCrimson,
    onPrimary = Color.White,
    background = Color(4293979108L),
    onBackground = Color(4280492062L),
    surface = Color(4294439917L),
    onSurface = Color(4280492062L),
    surfaceVariant = Color(4294769398L),
    onSurfaceVariant = Color(4283846729L),
    outline = Color(4292925387L)
)

@Composable
fun LyreonTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
