package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Pro Video Editor Dark Palette - Deep Slate Black with Neon Purple & Electric Blue
val DarkBackground = Color(0xFF0D0B14)
val DarkSurface = Color(0xFF13111C)
val DarkCard = Color(0xFF1B1828)
val DarkCardLighter = Color(0xFF252136)
val DarkBorder = Color(0xFF2E2A44)
val DarkBorderLight = Color(0xFF3F3A5C)

// Neon & Purple / Blue Accents (Matching Reference UI)
val NeonPurple = Color(0xFFA855F7)
val NeonPurpleDark = Color(0xFF7E22CE)
val NeonBlue = Color(0xFF00D2FF)
val NeonCyan = Color(0xFF38BDF8)
val NeonPink = Color(0xFFEC4899)
val NeonGreen = Color(0xFF10B981)

val AccentPurple = NeonPurple
val AccentBlue = NeonBlue
val AccentTeal = Color(0xFF14B8A6)
val AccentRed = Color(0xFFEF4444)

// Islamic Dark Palette - Charcoal Black, Emerald Green, Premium Gold, Text White
val IslamicCharcoal = Color(0xFF0F0F12)
val EmeraldGreen = Color(0xFF0D5C3A)
val EmeraldGreenLight = Color(0xFF10B981)
val PremiumGold = Color(0xFFD4AF37)
val PremiumGoldLight = Color(0xFFFFD700)

// Quran Gold Accents
val QuranGold = Color(0xFFE6B74A)
val QuranGoldLight = Color(0xFFF7D57F)
val QuranGoldDark = Color(0xFFB58925)

val IslamicGoldGradient = Brush.linearGradient(
    colors = listOf(PremiumGoldLight, PremiumGold, Color(0xFFB58925))
)
val IslamicEmeraldGradient = Brush.linearGradient(
    colors = listOf(EmeraldGreenLight, EmeraldGreen)
)

// Gradients
val ProPurpleGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF9333EA), Color(0xFF3B82F6))
)
val ProNeonGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFA855F7), Color(0xFF00D2FF))
)
val ProGoldGradient = Brush.linearGradient(
    colors = listOf(QuranGold, Color(0xFFF59E0B))
)
val ProCardGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF221F33), Color(0xFF181525))
)

// Text Colors
val TextWhite = Color(0xFFFFFFFF)
val TextGray = Color(0xFF9E9EA8)
val TextDark = Color(0xFF121216)

// Timeline Track Colors
val TrackVideoBg = Color(0xFF16152B)
val TrackVideoClip = Color(0xFF2C2852)
val TrackQuranBg = Color(0xFF1E1729)
val TrackQuranClip = Color(0xFF4C1D95)
val TrackQuranBorder = Color(0xFF7C3AED)
val TrackTextBg = Color(0xFF122033)
val TrackTextClip = Color(0xFF1E40AF)
val TrackAudioBg = Color(0xFF10282E)
val TrackAudioClip = Color(0xFF065F46)
val TrackWaveform = Color(0xFF00D2FF)
val PlayheadRed = Color(0xFFFF3B30)

