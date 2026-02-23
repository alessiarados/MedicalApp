package com.golazo.medical.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val UefaBlue = Color(0xFF1A4B8C)
val UefaBlueDark = Color(0xFF0D2B52)
val UefaBlueLight = Color(0xFF3A6BAC)
val UefaBlueVeryLight = Color(0xFFE8EFF8)
val White = Color(0xFFFFFFFF)
val CardBorder = Color(0xFFE0E0E0)
val BackgroundGray = Color(0xFFF5F5F5)
val TextPrimary = Color(0xFF1A1A1A)
val TextSecondary = Color(0xFF6B7280)
val SeverityMinor = Color(0xFF4CAF50)
val SeverityModerate = Color(0xFFFFC107)
val SeveritySevere = Color(0xFFF44336)
val StatusOpen = Color(0xFF2196F3)
val StatusClosed = Color(0xFF9E9E9E)
val StressCalm = Color(0xFF4CAF50)
val StressNormal = Color(0xFFFFC107)
val StressModerate = Color(0xFFFF9800)
val StressElevated = Color(0xFFF44336)

private val GolazoColorScheme = lightColorScheme(
    primary = UefaBlue,
    onPrimary = White,
    primaryContainer = UefaBlueVeryLight,
    onPrimaryContainer = UefaBlueDark,
    secondary = UefaBlueLight,
    onSecondary = White,
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
    displayLarge = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary),
    displayMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary),
    displaySmall = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary),
    headlineLarge = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary),
    headlineMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary),
    headlineSmall = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary),
    titleLarge = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary),
    titleMedium = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary),
    titleSmall = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary),
    bodyLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, color = TextPrimary),
    bodyMedium = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal, color = TextPrimary),
    bodySmall = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Normal, color = TextSecondary),
    labelLarge = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary),
    labelMedium = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextSecondary),
    labelSmall = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Medium, color = TextSecondary),
)

@Composable
fun GolazoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GolazoColorScheme,
        typography = GolazoTypography,
        content = content
    )
}
