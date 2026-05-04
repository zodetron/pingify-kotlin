package com.example.pingify.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary          = Indigo500,
    onPrimary        = White,
    primaryContainer = Indigo600,
    secondary        = Violet500,
    onSecondary      = White,
    background       = Slate900,
    onBackground     = Slate100,
    surface          = Slate800,
    onSurface        = Slate100,
    surfaceVariant   = Slate700,
    onSurfaceVariant = Slate200,
    error            = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary          = Indigo600,
    onPrimary        = White,
    primaryContainer = Violet400,
    secondary        = Violet500,
    onSecondary      = White,
    background       = Slate50,
    onBackground     = Slate900,
    surface          = White,
    onSurface        = Slate900,
    surfaceVariant   = Slate100,
    onSurfaceVariant = Slate700,
    error            = ErrorRed
)

@Composable
fun PingifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
