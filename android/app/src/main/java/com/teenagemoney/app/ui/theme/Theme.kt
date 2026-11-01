package com.teenagemoney.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val TMColorScheme = lightColorScheme(
    primary = TMCoral,
    onPrimary = TMWhite,
    primaryContainer = TMCoralLight,
    onPrimaryContainer = TMCoral,
    secondary = TMPurple,
    onSecondary = TMWhite,
    background = TMBackground,
    onBackground = TMInk,
    surface = TMSurface,
    onSurface = TMInk,
    surfaceVariant = TMLine,
    onSurfaceVariant = TMMuted,
    outline = TMBorder,
    outlineVariant = TMLine
)

@Composable
fun TeenageMoneyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TMColorScheme,
        typography = TMTypography,
        content = content
    )
}
