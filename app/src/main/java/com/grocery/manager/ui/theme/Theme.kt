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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

// ── Core Palette ──────────────────────────────────────────
val Charcoal900 = Color(0xFF121212)
val Charcoal800 = Color(0xFF1E1E1E)
val Charcoal700 = Color(0xFF2C2C2C)
val Charcoal600 = Color(0xFF3A3A3A)

val Teal400     = Color(0xFF26C6DA)
val Teal500     = Color(0xFF00BCD4)
val Teal700     = Color(0xFF0097A7)
val TealLight   = Color(0xFFE0F7FA)

val Gold        = Color(0xFFFFD700)
val GoldSoft    = Color(0xFFFFF176)

val ErrorRed    = Color(0xFFCF6679)
val SuccessGreen = Color(0xFF4CAF50)

val TextPrimary   = Color(0xFFECECEC)
val TextSecondary = Color(0xFF9E9E9E)

// ── Color Schemes ─────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary            = Teal500,
    onPrimary          = Charcoal900,
    primaryContainer   = Teal700,
    onPrimaryContainer = TealLight,
    secondary          = Teal400,
    onSecondary        = Charcoal900,
    tertiary           = Gold,
    onTertiary         = Charcoal900,
    background         = Charcoal900,
    onBackground       = TextPrimary,
    surface            = Charcoal800,
    onSurface          = TextPrimary,
    surfaceVariant     = Charcoal700,
    onSurfaceVariant   = TextSecondary,
    outline            = Charcoal600,
    error              = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary            = Teal700,
    onPrimary          = Color.White,
    primaryContainer   = TealLight,
    onPrimaryContainer = Teal700,
    secondary          = Teal500,
    onSecondary        = Color.White,
    tertiary           = Color(0xFFF9A825),
    onTertiary         = Color.White,
    background         = Color(0xFFF4F4F4),
    onBackground       = Color(0xFF1A1A1A),
    surface            = Color.White,
    onSurface          = Color(0xFF1A1A1A),
    surfaceVariant     = Color(0xFFEEEEEE),
    onSurfaceVariant   = Color(0xFF555555),
    outline            = Color(0xFFCCCCCC),
    error              = Color(0xFFB00020)
)

// ── Typography ────────────────────────────────────────────
val AppTypography = androidx.compose.material3.Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 48.sp,
        letterSpacing = (-1).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        letterSpacing = 0.15.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        letterSpacing = 0.1.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        letterSpacing = 0.5.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 0.5.sp
    )
)

// ── Theme ─────────────────────────────────────────────────
@Composable
fun GroceryManagerTheme(
    darkTheme: Boolean = true, // default to dark
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}