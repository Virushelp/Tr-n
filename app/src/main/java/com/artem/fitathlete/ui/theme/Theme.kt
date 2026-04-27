package com.artem.fitathlete.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Violet,
    onPrimary = WhitePure,
    secondary = BlackRich,
    background = White,
    onBackground = BlackRich,
    surface = WhitePure,
    onSurface = BlackRich,
    tertiary = VioletGlow
)

private val DarkColors = darkColorScheme(
    primary = VioletGlow,
    onPrimary = WhitePure,
    secondary = White,
    background = BlackRich,
    onBackground = White,
    surface = BlackSoft,
    onSurface = WhitePure,
    tertiary = Violet
)

private val WatermelonLightColors = lightColorScheme(
    primary = WatermelonGreen,
    onPrimary = WhitePure,
    secondary = WatermelonRed,
    onSecondary = WhitePure,
    tertiary = WatermelonRed,
    background = WatermelonBg,
    onBackground = BlackRich,
    surface = WatermelonSurface,
    onSurface = BlackRich
)

private val WatermelonDarkColors = darkColorScheme(
    primary = WatermelonGreen,
    onPrimary = WhitePure,
    secondary = WatermelonRed,
    onSecondary = WhitePure,
    tertiary = WatermelonRedDark,
    background = Color(0xFF121714),
    onBackground = WhitePure,
    surface = Color(0xFF1A211C),
    onSurface = WhitePure
)

@Composable
fun FitAthleteTheme(
    isWatermelonTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = when {
        isWatermelonTheme && isSystemInDarkTheme() -> WatermelonDarkColors
        isWatermelonTheme -> WatermelonLightColors
        isSystemInDarkTheme() -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}
