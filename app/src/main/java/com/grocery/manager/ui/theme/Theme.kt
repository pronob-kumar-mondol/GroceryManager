package com.grocery.manager.ui.theme

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

// Brand colors
val Green700 = Color(0xFF2E7D32)
val Green500 = Color(0xFF4CAF50)
val Green100 = Color(0xFFC8E6C9)
val Teal600  = Color(0xFF00897B)
val Amber600 = Color(0xFFFFB300)
val Red600   = Color(0xFFE53935)

private val LightColors = lightColorScheme(
    primary          = Green700,
    onPrimary        = Color.White,
    primaryContainer = Green100,
    onPrimaryContainer = Color(0xFF1B5E20),
    secondary        = Teal600,
    onSecondary      = Color.White,
    tertiary         = Amber600,
    background       = Color(0xFFF6FBF6),
    surface          = Color.White,
    error            = Red600
)

private val DarkColors = darkColorScheme(
    primary          = Green500,
    onPrimary        = Color(0xFF1B5E20),
    primaryContainer = Color(0xFF2E7D32),
    onPrimaryContainer = Green100,
    secondary        = Color(0xFF4DB6AC),
    tertiary         = Amber600,
    background       = Color(0xFF111411),
    surface          = Color(0xFF1A1F1A),
    error            = Color(0xFFEF9A9A)
)

@Composable
fun GroceryManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}