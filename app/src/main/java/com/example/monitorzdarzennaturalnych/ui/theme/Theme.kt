package com.example.monitorzdarzennaturalnych.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

// ─── Paleta kolorów – ciemny granat (premium dark navy) ───

// Podstawowe kolory
val NavyDark = Color(0xFF0A0E21)         // Główne tło – głęboki granat
val NavySurface = Color(0xFF111636)       // Surface – nieco jaśniejszy granat
val NavySurfaceVariant = Color(0xFF1A1F44) // Surface Variant – karty, chipy
val NavyElevated = Color(0xFF1E2450)      // Elementy wyżej – floating components

// Akcenty
val AccentBlue = Color(0xFF6C9FFF)        // Primary – nowoczesny, żywy niebieski
val AccentBlueBright = Color(0xFF8BB8FF)  // Primary jasniejszy
val AccentTeal = Color(0xFF5DE5D5)        // Secondary – turkusowy akcent
val AccentTealDark = Color(0xFF2ABFAB)    // Secondary ciemniejszy

// Kolory zagrożeń (markery)
val DangerRed = Color(0xFFFF6B6B)         // Zagrożenie
val WarningOrange = Color(0xFFFFAD5C)     // Ostrzeżenie
val IceBlue = Color(0xFF6EC6FF)           // Lód
val WeatherGreen = Color(0xFF6BDD9A)      // Zjawiska pogodowe

// Tertiary
val TertiaryPurple = Color(0xFFB388FF)    // Akcent trzeci
val TertiaryPurpleDark = Color(0xFF7C4DFF)

// Powierzchnia z przezroczystością (do overlay)
val SurfaceOverlay = Color(0xCC111636)    // ~80% opacity
val ChipSelected = Color(0xFF253270)      // Wybrany chip
val ChipDefault = Color(0xFF1A1F44)       // Domyślny chip

// Tekst
val TextPrimary = Color(0xFFE8ECF4)       // Jasny tekst
val TextSecondary = Color(0xFF8B92A8)     // Drugorzędny tekst
val TextTertiary = Color(0xFF5A6178)      // Trzeciorzędny tekst

// Ciemny motyw premium (Material 3 Dark Navy)
private val DarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = NavyDark,
    primaryContainer = Color(0xFF1A3A6A),
    onPrimaryContainer = AccentBlueBright,

    secondary = AccentTeal,
    onSecondary = NavyDark,
    secondaryContainer = Color(0xFF0E3D38),
    onSecondaryContainer = AccentTeal,

    tertiary = TertiaryPurple,
    onTertiary = NavyDark,
    tertiaryContainer = Color(0xFF2D1F5E),
    onTertiaryContainer = TertiaryPurple,

    background = NavyDark,
    onBackground = TextPrimary,

    surface = NavySurface,
    onSurface = TextPrimary,
    surfaceVariant = NavySurfaceVariant,
    onSurfaceVariant = TextSecondary,

    outline = Color(0xFF2E3558),
    outlineVariant = Color(0xFF1E2350),

    error = DangerRed,
    onError = NavyDark,
    errorContainer = Color(0xFF4A1C1C),
    onErrorContainer = DangerRed,

    inverseSurface = TextPrimary,
    inverseOnSurface = NavyDark,
    inversePrimary = Color(0xFF2962FF),

    scrim = Color(0xFF000000),
    surfaceTint = AccentBlue
)

// Jasny motyw (zachowany jako fallback)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1A56DB),
    secondary = Color(0xFF0EA5A0),
    background = Color(0xFFF8F9FC),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onBackground = Color(0xFF1A1C2E),
    onSurface = Color(0xFF1A1C2E)
)

// ─── Typografia Material 3 ───
val MonitorTypography = Typography(
    // Display
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),

    // Headline
    headlineLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),

    // Title
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // Body
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),

    // Label
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

@Composable
fun MonitorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Ustawienie koloru gornego paska w telefonie
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MonitorTypography,
        content = content
    )
}
