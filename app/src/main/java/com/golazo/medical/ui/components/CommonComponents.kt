package com.golazo.medical.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.golazo.medical.ui.theme.*

@Composable
fun GolazoCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(0.5.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            content = content
        )
    }
}

@Composable
fun GolazoTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(
        color = UefaBlueDark,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 6.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = White)
                }
            } else {
                Spacer(Modifier.width(12.dp))
            }
            Text(
                text = title,
                color = White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp,
                modifier = Modifier.weight(1f)
            )
            actions()
        }
    }
}

@Composable
fun GolazoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    forceLightTheme: Boolean = false
) {
    val colors = if (forceLightTheme) {
        OutlinedTextFieldDefaults.colors(
            focusedBorderColor = UefaBlue,
            unfocusedBorderColor = CardBorder,
            focusedLabelColor = UefaBlue,
            unfocusedLabelColor = TextSecondary,
            unfocusedContainerColor = CardWhite,
            focusedContainerColor = White,
            cursorColor = UefaBlue,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
        )
    } else {
        OutlinedTextFieldDefaults.colors(
            focusedBorderColor = UefaBlue,
            unfocusedBorderColor = CardBorder,
            focusedLabelColor = UefaBlue,
            unfocusedLabelColor = TextPrimary,
            unfocusedContainerColor = CardWhite,
            focusedContainerColor = White,
            cursorColor = UefaBlue,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedPlaceholderColor = TextSecondary,
            unfocusedPlaceholderColor = TextSecondary,
        )
    }
    
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        maxLines = maxLines,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(14.dp),
        colors = colors,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        enabled = enabled,
        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
    )
}

@Composable
fun GolazoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    containerColor: Color = UefaBlue,
    contentColor: Color = White
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.4f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp, pressedElevation = 1.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
        } else {
            Text(text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.3.sp, color = contentColor)
        }
    }
}

@Composable
fun GolazoOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.5.dp, UefaBlue),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = UefaBlue)
    ) {
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.3.sp)
    }
}

@Composable
fun SeverityBadge(severity: String) {
    val (color, label) = when (severity.lowercase()) {
        "minor" -> SeverityMinor to "Minor"
        "moderate" -> SeverityModerate to "Moderate"
        "severe" -> SeveritySevere to "Severe"
        else -> TextSecondary to severity
    }
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun StatusBadge(status: String, isOpen: Boolean = status.lowercase() == "open") {
    val color = if (isOpen) StatusOpen else StatusClosed
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text = status.replaceFirstChar { it.uppercase() },
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun RtpBadge(rtpStatus: String) {
    val label = when (rtpStatus) {
        "not_started" -> "Not Started"
        "in_rehab" -> "In Rehab"
        "light_training" -> "Light Training"
        "full_training" -> "Full Training"
        "cleared" -> "Cleared"
        else -> rtpStatus
    }
    val color = when (rtpStatus) {
        "cleared" -> SeverityMinor
        "full_training" -> Color(0xFF66BB6A)
        "light_training" -> SeverityModerate
        "in_rehab" -> StressModerate
        else -> TextSecondary
    }
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun PcmeStatusBadge(status: String) {
    val (color, label) = when (status.lowercase()) {
        "entered" -> SeverityMinor to "Entered"
        "expected" -> SeverityModerate to "Expected"
        "late" -> SeveritySevere to "Late"
        "missing" -> TextSecondary to "Missing"
        else -> TextSecondary to status
    }
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        letterSpacing = 0.2.sp,
        modifier = modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String = "",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = UefaBlueVeryLight,
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon, null,
                    modifier = Modifier.size(36.dp),
                    tint = UefaBlue.copy(alpha = 0.5f)
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        if (subtitle.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            Text(subtitle, fontSize = 13.sp, color = TextSecondary, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = UefaBlue)
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message, fontSize = 14.sp, color = SeveritySevere, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        GolazoButton("Retry", onClick = onRetry, modifier = Modifier.width(120.dp))
    }
}

@Composable
fun InitialsAvatar(
    name: String,
    color: Color = UefaBlue,
    size: Int = 40
) {
    val initials = name.split(" ").take(2).map { it.firstOrNull()?.uppercase() ?: "" }.joinToString("")
    Surface(
        modifier = Modifier.size(size.dp),
        shape = CircleShape,
        color = color.copy(alpha = 0.12f),
        shadowElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = initials,
                color = color,
                fontSize = (size / 3).sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun ProfileAvatar(
    imageUrl: String?,
    name: String,
    size: Int = 40,
    fallbackColor: Color = UefaBlue
) {
    if (!imageUrl.isNullOrBlank()) {
        val fullUrl = if (imageUrl.startsWith("http")) imageUrl
            else "http://10.0.2.2:3000$imageUrl"
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(fullUrl)
                .crossfade(true)
                .build(),
            contentDescription = name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape)
        )
    } else {
        InitialsAvatar(name = name, color = fallbackColor, size = size)
    }
}
