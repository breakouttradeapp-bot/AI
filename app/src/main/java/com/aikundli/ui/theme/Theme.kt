package com.aikundli.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ─── Astrology color palette ───────────────────────────────────────────────
val CosmicPurple       = Color(0xFF6A0572)
val DeepIndigo         = Color(0xFF1A0533)
val MysticViolet       = Color(0xFF9C27B0)
val GoldenStar         = Color(0xFFFFD700)
val CelestialBlue      = Color(0xFF1565C0)
val NebulaPink         = Color(0xFFE91E8C)
val StarlightWhite     = Color(0xFFF8F0FF)
val DarkSpace          = Color(0xFF0A0015)
val CardBackground     = Color(0xFF1C0533)
val GlassWhite         = Color(0x22FFFFFF)
val GlassBorder        = Color(0x44FFFFFF)

private val DarkColorScheme = darkColorScheme(
    primary         = MysticViolet,
    onPrimary       = Color.White,
    primaryContainer = CosmicPurple,
    secondary       = GoldenStar,
    onSecondary     = DarkSpace,
    background      = DarkSpace,
    surface         = CardBackground,
    onBackground    = StarlightWhite,
    onSurface       = StarlightWhite,
    error           = Color(0xFFCF6679),
)

private val LightColorScheme = lightColorScheme(
    primary         = CosmicPurple,
    onPrimary       = Color.White,
    primaryContainer = Color(0xFFEDD9FF),
    secondary       = Color(0xFF7B5EA7),
    background      = Color(0xFFF5F0FF),
    surface         = Color.White,
    onBackground    = DeepIndigo,
    onSurface       = DeepIndigo,
)

@Composable
fun AIKundliTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = KundliTypography,
        content     = content
    )
}

val KundliTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize   = 32.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize   = 22.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize   = 18.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight  = 24.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp,
        lineHeight  = 20.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize   = 11.sp,
        letterSpacing = 0.5.sp
    )
)
