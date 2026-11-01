package com.teenagemoney.app.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Design token system for Teenage Money.
 *
 * Border-radius relationship: Outer radius = Inner radius + Padding
 * e.g. inner 16dp + padding 8dp → outer 24dp
 */
object TMDimens {
    // ── Spacing Scale ───────────────────────────────────────────────────
    val Space2  = 2.dp
    val Space4  = 4.dp
    val Space6  = 6.dp
    val Space8  = 8.dp
    val Space10 = 10.dp
    val Space12 = 12.dp
    val Space14 = 14.dp
    val Space16 = 16.dp
    val Space20 = 20.dp
    val Space24 = 24.dp
    val Space28 = 28.dp
    val Space32 = 32.dp
    val Space40 = 40.dp
    val Space48 = 48.dp

    // ── Border Radius Scale ─────────────────────────────────────────────
    val Radius8  = 8.dp
    val Radius12 = 12.dp
    val Radius16 = 16.dp
    val Radius20 = 20.dp
    val Radius24 = 24.dp
    val Radius28 = 28.dp
    val Radius32 = 32.dp

    // ── Card Dimensions ─────────────────────────────────────────────────
    val CardPaddingInner  = 16.dp   // Content inside a card
    val CardPaddingOuter  = 20.dp   // Outer container padding
    val CardElevation     = 1.dp    // Subtle shadow
    val CardElevationHigh = 4.dp    // Elevated elements (sheets, nav)

    // ── Touch Targets ───────────────────────────────────────────────────
    val MinTouch = 48.dp

    // ── Component Sizes ─────────────────────────────────────────────────
    val AvatarLarge  = 80.dp
    val AvatarMedium = 44.dp
    val AvatarSmall  = 36.dp
    val IconCircle   = 42.dp
    val ButtonHeight = 52.dp
    val NavBarHeight = 56.dp

    // ── Divider ─────────────────────────────────────────────────────────
    val Divider = 1.dp
}
