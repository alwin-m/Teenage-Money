package com.teenagemoney.app.ui.theme

import androidx.compose.ui.graphics.Color

// ── Canvas & Surface ────────────────────────────────────────────────────────
val TMBackground      = Color(0xFFF7F7F8)   // Near-white canvas
val TMSurface         = Color(0xFFFFFFFF)   // Card surface
val TMSurfaceElevated = Color(0xFFFFFFFF)   // Elevated surface (sheets, dialogs)

// ── Ink & Text ──────────────────────────────────────────────────────────────
val TMInk             = Color(0xFF111111)   // Primary text — near-black
val TMMuted           = Color(0xFF8A8A8E)   // Secondary text — neutral gray
val TMLightMuted      = Color(0xFFAAAAAE)   // Tertiary text

// ── Lines & Borders ─────────────────────────────────────────────────────────
val TMLine            = Color(0xFFEEEEF0)   // Subtle dividers
val TMBorder          = Color(0xFFE5E5E8)   // Card borders (only when needed)

// ── Brand Accent ────────────────────────────────────────────────────────────
val TMCoral           = Color(0xFFE8441E)   // Primary action — warm red-orange
val TMCoralDark       = Color(0xFFC23515)   // Pressed state
val TMCoralLight      = Color(0xFFFEF0EC)   // Coral tint background

// ── Secondary Accent ────────────────────────────────────────────────────────
val TMPurple          = Color(0xFF7B6CF0)   // Charts, secondary highlights
val TMYellowSun       = Color(0xFFF5C342)   // Warm accent

// ── Money Space Tints ───────────────────────────────────────────────────────
// Spending (Coral family)
val TMPeachBg         = Color(0xFFFEF0EC)
val TMPeachBorder     = Color(0xFFFDD8CC)
val TMPeachText       = Color(0xFFE8441E)

// Savings (Mint family)
val TMMintBg          = Color(0xFFECF9F2)
val TMMintBorder      = Color(0xFFCBEDD9)
val TMMintText        = Color(0xFF1B9E5A)

// Emergency (Lavender family)
val TMLavenderBg      = Color(0xFFF0EDFC)
val TMLavenderBorder  = Color(0xFFDAD4F7)
val TMLavenderText    = Color(0xFF7B6CF0)

// ── Category Icon Backgrounds ───────────────────────────────────────────────
val TMIconsFoodBg       = Color(0xFFFFF0E6)
val TMIconsFoodFg       = Color(0xFFE95E3F)

val TMIconsTransportBg  = Color(0xFFEAF1FC)
val TMIconsTransportFg  = Color(0xFF3B82F6)

val TMIconsEducationBg  = Color(0xFFECF9F2)
val TMIconsEducationFg  = Color(0xFF1B9E5A)

val TMIconsShoppingBg   = Color(0xFFF0EDFC)
val TMIconsShoppingFg   = Color(0xFF7B6CF0)

// ── Status Colors ───────────────────────────────────────────────────────────
val TMSuccess         = Color(0xFF1B9E5A)
val TMWarning         = Color(0xFFF5C342)
val TMError           = Color(0xFFE8441E)

// ── Dark Surface (legacy compat) ────────────────────────────────────────────
val TMCardDark        = Color(0xFF1A1A1E)
val TMCardDarkSurface = Color(0xFF232328)
val TMOnDarkMuted     = Color(0xFFD8D7DC)
val TMWhite           = Color(0xFFFFFFFF)
