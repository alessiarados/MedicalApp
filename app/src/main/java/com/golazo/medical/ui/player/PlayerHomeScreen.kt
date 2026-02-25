package com.golazo.medical.ui.player

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.golazo.medical.data.SimulatedData
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*

@Composable
fun PlayerHomeScreen(
    onSignOut: () -> Unit = {},
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    var showProfileMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadProfile() }

    val firstName = profile?.firstName ?: "Alex"
    val lastName = profile?.lastName ?: "Thompson"
    val position = profile?.position ?: "MF"
    val location = profile?.location ?: "Barcelona, Spain"
    val club = profile?.club ?: ""
    val nationality = profile?.nationality ?: ""
    val dob = profile?.dob ?: ""
    val imageUrl = profile?.imageUrl
    val email = viewModel.sessionManager.currentUser.value?.email ?: ""

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // ── Hero Player Card ──
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                color = UefaBlue,
                shadowElevation = 12.dp
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(UefaBlueDark, UefaBlue, UefaBlueLight.copy(alpha = 0.8f))
                            )
                        )
                        .padding(22.dp)
                ) {
                    // Status badge top-left
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = SeverityMinor,
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            "Active",
                            color = White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                    // Avatar top-right (clickable for profile menu)
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .align(Alignment.TopEnd)
                            .clickable { showProfileMenu = true }
                    ) {
                        ProfileAvatar(
                            imageUrl = imageUrl,
                            name = "$firstName $lastName",
                            size = 44,
                            fallbackColor = White
                        )
                    }
                    // Player info
                    Column(modifier = Modifier.padding(top = 40.dp)) {
                        Text(firstName, color = White.copy(alpha = 0.85f), fontSize = 15.sp, letterSpacing = 0.5.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                lastName,
                                color = White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(Modifier.width(10.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = White.copy(alpha = 0.18f)
                            ) {
                                Text(
                                    position.take(2).uppercase(),
                                    color = White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = White.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(location, color = White.copy(alpha = 0.6f), fontSize = 12.sp, letterSpacing = 0.2.sp)
                        }
                    }
                }
            }
        }

        // ── Recovery + Injuries Row ──
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Recovery Card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    color = CardWhite,
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Refresh, null, tint = UefaBlue, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Recovery", fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.2.sp)
                        }
                        Spacer(Modifier.height(12.dp))
                        // Circular progress
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                            val recoveryScore = 78
                            Canvas(modifier = Modifier.size(80.dp)) {
                                val strokeWidth = 8.dp.toPx()
                                drawArc(
                                    color = BackgroundGray,
                                    startAngle = -90f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                                    size = Size(size.width - strokeWidth, size.height - strokeWidth)
                                )
                                drawArc(
                                    color = Color(0xFFFFC107),
                                    startAngle = -90f,
                                    sweepAngle = 360f * recoveryScore / 100f,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                                    size = Size(size.width - strokeWidth, size.height - strokeWidth)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("78", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Good", fontSize = 10.sp, color = TextSecondary)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            MiniStat("Sleep", "7.5h")
                            MiniStat("Fatigue", "Low")
                            MiniStat("Load", "Med")
                        }
                    }
                }

                // Injuries Card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    color = CardWhite,
                    shadowElevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalHospital, null, tint = SeveritySevere, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Injuries", fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.2.sp)
                            }
                            Icon(Icons.Default.ChevronRight, null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("2", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(Modifier.width(6.dp))
                            Text("active", fontSize = 13.sp, color = TextSecondary, modifier = Modifier.padding(bottom = 6.dp))
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total", fontSize = 10.sp, color = TextSecondary)
                                Text("7 injuries", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Column {
                                Text("Days Out", fontSize = 10.sp, color = TextSecondary)
                                Text("208 days", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        // Severity bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(3f)
                                    .fillMaxHeight()
                                    .background(SeverityMinor)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(2f)
                                    .fillMaxHeight()
                                    .background(SeverityModerate)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(2f)
                                    .fillMaxHeight()
                                    .background(SeveritySevere)
                            )
                        }
                    }
                }
            }
        }

        // ── This Week Calendar ──
        item {
            Spacer(Modifier.height(12.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                color = CardWhite,
                shadowElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("This Week", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.2.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            LegendDot(Color(0xFF1A4B8C), "Training")
                            LegendDot(Color(0xFF9C27B0), "Match")
                            LegendDot(Color(0xFFFF9800), "Travel")
                            LegendDot(Color(0xFF4CAF50), "Rest")
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val days = listOf(
                            Triple("Mon", "23", "training"),
                            Triple("Tue", "24", "training"),
                            Triple("Wed", "25", "training"),
                            Triple("Thu", "26", "training"),
                            Triple("Fri", "27", "travel"),
                            Triple("Sat", "28", "match"),
                            Triple("Sun", "1", "rest")
                        )
                        days.forEachIndexed { index, (day, date, type) ->
                            WeekDayCell(
                                day = day,
                                date = date,
                                type = type,
                                isToday = index == 0
                            )
                        }
                    }
                }
            }
        }

        // ── Wellbeing Suggestion ──
        item {
            Spacer(Modifier.height(12.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { },
                shape = RoundedCornerShape(20.dp),
                color = CardWhite,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.SelfImprovement, null, tint = UefaBlue, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Wellbeing", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.2.sp)
                            Spacer(Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = UefaBlueVeryLight
                            ) {
                                Text(
                                    "Suggested",
                                    fontSize = 9.sp,
                                    color = UefaBlue,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            "Breathing exercises & mental preparation sessions",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                }
            }
        }

        // ── Season Stats + vs League Row ──
        item {
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Season Stats
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    color = CardWhite,
                    shadowElevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EmojiEvents, null, tint = AccentGold, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Season", fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.2.sp)
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatBox("24", "Apps")
                            StatBox("32", "Hrs")
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatBox("8", "Goals")
                            StatBox("12", "Assists")
                        }
                    }
                }

                // vs League
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    color = CardWhite,
                    shadowElevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.BarChart, null, tint = UefaBlue, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("vs League", fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.2.sp)
                        }
                        Spacer(Modifier.height(12.dp))
                        // Mini bar chart
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            BarGroup("Avg", 0.5f, 0.65f)
                            BarGroup("Injury", 0.3f, 0.2f)
                            BarGroup("Load", 0.7f, 0.6f)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LegendDot(UefaBlue, "You")
                            Spacer(Modifier.width(12.dp))
                            LegendDot(TextSecondary.copy(alpha = 0.4f), "Avg")
                        }
                    }
                }
            }
        }

        // ── Upcoming Load ──
        item {
            Spacer(Modifier.height(12.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                color = CardWhite,
                shadowElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FitnessCenter, null, tint = UefaBlue, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Upcoming Load", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.2.sp)
                    }
                    Spacer(Modifier.height(12.dp))

                    UpcomingLoadItem(
                        icon = Icons.Default.SportsSoccer,
                        iconColor = UefaBlue,
                        title = "Tactical Session",
                        subtitle = "Today, 10:00",
                        intensity = "Medium",
                        intensityColor = SeverityModerate
                    )
                    Spacer(Modifier.height(10.dp))
                    UpcomingLoadItem(
                        icon = Icons.Default.FitnessCenter,
                        iconColor = SeverityMinor,
                        title = "Gym + Recovery",
                        subtitle = "Tomorrow, 09:00",
                        intensity = "Light",
                        intensityColor = SeverityMinor
                    )
                    Spacer(Modifier.height(10.dp))
                    UpcomingLoadItem(
                        icon = Icons.Default.Flight,
                        iconColor = Color(0xFFFF9800),
                        title = "Away Match Travel",
                        subtitle = "Thu, 14:00",
                        intensity = "Madrid",
                        intensityColor = UefaBlue
                    )
                }
            }
        }
    }

    // Profile Sheet
    if (showProfileMenu) {
        ProfileSheet(
            imageUrl = imageUrl,
            firstName = firstName,
            lastName = lastName,
            position = position,
            club = club,
            nationality = nationality,
            location = location,
            dob = dob,
            email = email,
            onDismiss = { showProfileMenu = false },
            onSignOut = {
                showProfileMenu = false
                viewModel.sessionManager.clear()
                onSignOut()
            },
            onDeleteAccount = {
                showProfileMenu = false
                showDeleteDialog = true
            }
        )
    }

    // Delete Account Confirmation
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Warning, null, tint = SeveritySevere) },
            title = { Text("Delete Account", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "This action is permanent. All your medical data, injury records, and consent grants will be permanently deleted.",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.sessionManager.clear()
                    onSignOut()
                }) {
                    Text("Delete", color = SeveritySevere, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ── Profile Sheet ──

@Composable
private fun ProfileSheet(
    imageUrl: String?,
    firstName: String,
    lastName: String,
    position: String,
    club: String,
    nationality: String,
    location: String,
    dob: String,
    email: String,
    onDismiss: () -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                indication = null,
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            ) { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .clickable(
                    indication = null,
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                ) { /* consume clicks */ }
        ) {
            // Blue header with avatar
            Surface(
                color = UefaBlue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(top = 12.dp, bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Close button row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, "Close", tint = White)
                        }
                    }

                    // Avatar circle
                    Box(contentAlignment = Alignment.Center) {
                        ProfileAvatar(
                            imageUrl = imageUrl,
                            name = "$firstName $lastName",
                            size = 88,
                            fallbackColor = White
                        )
                        // Online indicator
                        Surface(
                            shape = CircleShape,
                            color = SeverityMinor,
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.BottomEnd),
                            shadowElevation = 2.dp
                        ) {
                            Icon(Icons.Default.Person, null, tint = White, modifier = Modifier.padding(3.dp))
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text(
                        "$firstName $lastName",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(shape = RoundedCornerShape(8.dp), color = White.copy(alpha = 0.2f)) {
                            Text(position, fontSize = 11.sp, color = White, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }
                        Surface(shape = RoundedCornerShape(8.dp), color = SeverityMinor) {
                            Text("Active", fontSize = 11.sp, color = White, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                        }
                    }
                }
            }

            // Content cards
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundGray)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Player Details Card
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = CardWhite,
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Player Details", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = UefaBlue)
                        Spacer(Modifier.height(16.dp))

                        ProfileDetailRow(Icons.Default.SportsSoccer, "Club", club.ifBlank { "Not set" })
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = BackgroundGray)
                        ProfileDetailRow(Icons.Default.Flag, "Nationality", nationality.ifBlank { "Not set" })
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = BackgroundGray)
                        ProfileDetailRow(Icons.Default.LocationOn, "Location", location.ifBlank { "Not set" })
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = BackgroundGray)
                        ProfileDetailRow(Icons.Default.CalendarMonth, "Date of Birth", dob.ifBlank { "Not set" })
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = BackgroundGray)
                        ProfileDetailRow(Icons.Default.Email, "Email", email.ifBlank { "Not set" })
                    }
                }

                // Account Actions Card
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = CardWhite,
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(
                            "Account",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = UefaBlue,
                            modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
                        )

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSignOut() }
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            color = White
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(shape = RoundedCornerShape(10.dp), color = UefaBlueVeryLight, modifier = Modifier.size(36.dp)) {
                                    Icon(Icons.Default.Logout, null, tint = UefaBlue, modifier = Modifier.padding(8.dp))
                                }
                                Spacer(Modifier.width(14.dp))
                                Text("Sign Out", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = BackgroundGray)

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onDeleteAccount() }
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            color = White
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(shape = RoundedCornerShape(10.dp), color = SeveritySevere.copy(alpha = 0.1f), modifier = Modifier.size(36.dp)) {
                                    Icon(Icons.Default.DeleteForever, null, tint = SeveritySevere, modifier = Modifier.padding(8.dp))
                                }
                                Spacer(Modifier.width(14.dp))
                                Text("Delete Account", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = SeveritySevere)
                            }
                        }

                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileDetailRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = RoundedCornerShape(10.dp), color = UefaBlueVeryLight, modifier = Modifier.size(36.dp)) {
            Icon(icon, null, tint = UefaBlue, modifier = Modifier.padding(8.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column {
            Text(label, fontSize = 11.sp, color = TextSecondary)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ── Helper Composables ──

@Composable
private fun MiniStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 9.sp, color = TextSecondary)
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}

@Composable
private fun StatBox(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(label, fontSize = 10.sp, color = TextSecondary)
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(3.dp))
        Text(label, fontSize = 8.sp, color = TextSecondary)
    }
}

@Composable
private fun WeekDayCell(day: String, date: String, type: String, isToday: Boolean) {
    val bgColor = if (isToday) UefaBlue else Color.Transparent
    val textColor = if (isToday) White else TextPrimary
    val dayColor = if (isToday) White.copy(alpha = 0.7f) else TextSecondary
    val dotColor = when (type) {
        "training" -> UefaBlue
        "match" -> Color(0xFF9C27B0)
        "travel" -> Color(0xFFFF9800)
        "rest" -> SeverityMinor
        else -> TextSecondary
    }
    val typeIcon = when (type) {
        "training" -> Icons.Default.SportsSoccer
        "match" -> Icons.Default.Stadium
        "travel" -> Icons.Default.Flight
        "rest" -> Icons.Default.SelfImprovement
        else -> Icons.Default.Circle
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Text(day, fontSize = 9.sp, color = dayColor, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        Text(date, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(Modifier.height(4.dp))
        Icon(
            typeIcon,
            contentDescription = null,
            tint = if (isToday) White else dotColor,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun BarGroup(label: String, youFraction: Float, avgFraction: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.height(60.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(12.dp)
                    .fillMaxHeight(youFraction)
                    .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                    .background(UefaBlue)
            )
            Box(
                modifier = Modifier
                    .width(12.dp)
                    .fillMaxHeight(avgFraction)
                    .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                    .background(TextSecondary.copy(alpha = 0.3f))
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 8.sp, color = TextSecondary, textAlign = TextAlign.Center)
    }
}

@Composable
private fun UpcomingLoadItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    intensity: String,
    intensityColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = iconColor.copy(alpha = 0.12f),
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.padding(10.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, fontSize = 11.sp, color = TextSecondary)
        }
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = intensityColor.copy(alpha = 0.12f)
        ) {
            Text(
                intensity,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = intensityColor,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}
