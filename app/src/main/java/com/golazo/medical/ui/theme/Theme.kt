package com.golazo.medical.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.golazo.medical.R

// ── UEFA B2B Design System — Lato Font Family ──
val LatoFontFamily = FontFamily(
    Font(R.font.lato_light, FontWeight.Light),
    Font(R.font.lato_regular, FontWeight.Normal),
    Font(R.font.lato_regular, FontWeight.Medium),
    Font(R.font.lato_bold, FontWeight.SemiBold),
    Font(R.font.lato_bold, FontWeight.Bold),
)

// ── UEFA B2B Design System — Official Color Tokens ──
val UefaNavy = Color(0xFF031F38)          // Nav background — official
val UefaBlue = Color(0xFF1A3D7C)          // Primary blue
val UefaBlueDark = Color(0xFF031F38)      // Dark navy — aligned to UEFA nav
val UefaBlueLight = Color(0xFF3570B8)     // Lighter blue
val UefaBlueVeryLight = Color(0xFFEBF1FA) // Very light blue bg
val UefaPink = Color(0xFFDB1B77)          // UEFA accent pink — official
val UefaCyan = Color(0xFF00D9F7)          // UEFA secondary accent — official
val White = Color(0xFFFFFFFF)
val CardBorder = Color(0xFFDBE8ED)        // UEFA footer divider color
val BackgroundGray = Color(0xFFF0F2F5)
val TextPrimary = Color(0xFF151839)       // UEFA official body/heading color
val TextSecondary = Color(0xFF59707B)     // UEFA official page intro color
val SeverityMinor = Color(0xFF34C759)
val SeverityModerate = Color(0xFFFFBE0B)
val SeveritySevere = Color(0xFFEF4444)
val StatusOpen = Color(0xFF3B82F6)
val StatusClosed = Color(0xFF9CA3AF)
val StressCalm = Color(0xFF34C759)
val StressNormal = Color(0xFFFFBE0B)
val StressModerate = Color(0xFFF59E0B)
val StressElevated = Color(0xFFEF4444)
val AccentGold = Color(0xFFDB1B77)        // Remapped to UEFA pink for accent usage
val CardWhite = Color(0xFFFCFCFD)

private val GolazoColorScheme = lightColorScheme(
    primary = UefaBlue,
    onPrimary = White,
    primaryContainer = UefaBlueVeryLight,
    onPrimaryContainer = UefaBlueDark,
    secondary = UefaCyan,
    onSecondary = UefaNavy,
    tertiary = UefaPink,
    onTertiary = White,
    background = BackgroundGray,
    onBackground = TextPrimary,
    surface = White,
    onSurface = TextPrimary,
    surfaceVariant = BackgroundGray,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    error = SeveritySevere,
)

val GolazoTypography = Typography(
    displayLarge = TextStyle(fontFamily = LatoFontFamily, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary),
    displayMedium = TextStyle(fontFamily = LatoFontFamily, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary),
    displaySmall = TextStyle(fontFamily = LatoFontFamily, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary),
    headlineLarge = TextStyle(fontFamily = LatoFontFamily, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary),
    headlineMedium = TextStyle(fontFamily = LatoFontFamily, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary),
    headlineSmall = TextStyle(fontFamily = LatoFontFamily, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary),
    titleLarge = TextStyle(fontFamily = LatoFontFamily, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary),
    titleMedium = TextStyle(fontFamily = LatoFontFamily, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary),
    titleSmall = TextStyle(fontFamily = LatoFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary),
    bodyLarge = TextStyle(fontFamily = LatoFontFamily, fontSize = 14.sp, fontWeight = FontWeight.Normal, color = TextPrimary),
    bodyMedium = TextStyle(fontFamily = LatoFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Normal, color = TextPrimary),
    bodySmall = TextStyle(fontFamily = LatoFontFamily, fontSize = 10.sp, fontWeight = FontWeight.Normal, color = TextSecondary),
    labelLarge = TextStyle(fontFamily = LatoFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary),
    labelMedium = TextStyle(fontFamily = LatoFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextSecondary),
    labelSmall = TextStyle(fontFamily = LatoFontFamily, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = TextSecondary),
)

@Composable
fun GolazoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GolazoColorScheme,
        typography = GolazoTypography,
        content = content
    )
}
