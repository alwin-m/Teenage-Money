package com.teenagemoney.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val TMColorScheme = lightColorScheme(
    primary = TMCoral,
    onPrimary = TMWhite,
    background = TMBackground,
    onBackground = TMInk,
    surface = TMWhite,
    onSurface = TMInk,
    surfaceVariant = TMCardDarkSurface,
    onSurfaceVariant = TMWhite
)

@Composable
fun TeenageMoneyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TMColorScheme,
        typography = TMTypography,
        content = content
    )
}
