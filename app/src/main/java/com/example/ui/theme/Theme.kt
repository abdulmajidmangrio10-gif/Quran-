package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

fun getDarkColorScheme(accentColor: Color = QuranGold) = darkColorScheme(
    primary = accentColor,
    onPrimary = TextDark,
    primaryContainer = Color(0xFF2C2410),
    onPrimaryContainer = QuranGoldLight,
    secondary = AccentTeal,
    onSecondary = TextWhite,
    tertiary = AccentBlue,
    onTertiary = TextWhite,
    background = DarkBackground,
    onBackground = TextWhite,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkCard,
    onSurfaceVariant = TextGray,
    outline = DarkBorder,
    outlineVariant = DarkBorderLight
)

fun getLightColorScheme(accentColor: Color = QuranGold) = lightColorScheme(
    primary = accentColor,
    onPrimary = TextWhite,
    secondary = AccentTeal,
    background = Color(0xFFF7F7FA),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFEBEBF0),
    outline = Color(0xFFD0D0DC),
    onBackground = Color(0xFF141418),
    onSurface = Color(0xFF141418)
)

@Composable
fun QuranVideoEditorTheme(
    darkTheme: Boolean = true,
    accentColor: Color = QuranGold,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) getDarkColorScheme(accentColor) else getLightColorScheme(accentColor)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = DarkBackground.toArgb()
                window.navigationBarColor = DarkBackground.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
