package com.golazo.medical.ui.doctor

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*

@Composable
fun DoctorHomeScreen(
    onViewPlayers: () -> Unit,
    onViewInjuries: () -> Unit,
    onViewPcme: () -> Unit,
    onViewTraining: () -> Unit,
    onNavigateToInjury: (String) -> Unit = {},
    onNavigateToPcme: (String) -> Unit = {},
    viewModel: DoctorViewModel = hiltViewModel()
) {
    val players by viewModel.players.collectAsStateWithLifecycle()
    val injuries by viewModel.injuries.collectAsStateWithLifecycle()
    val pcmeEntries by viewModel.pcmeEntries.collectAsStateWithLifecycle()
    val trainingSessions by viewModel.trainingSessions.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadCount.collectAsStateWithLifecycle()
    
    var showNotifications by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadPlayers()
        viewModel.loadInjuries()
        viewModel.loadPcmeEntries()
        viewModel.loadTrainingSessions()
        viewModel.loadNotifications()
    }

    val totalPlayers = players.size.coerceAtLeast(1)
    val openInjuries = injuries.filter { it.status == "open" }
    val injuredPlayerIds = openInjuries.map { it.userId }.toSet()
    val availablePlayers = totalPlayers - injuredPlayerIds.size
    val availabilityPercent = (availablePlayers.toFloat() / totalPlayers * 100).toInt()

    val doctorName = viewModel.sessionManager.currentUser.value?.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() } ?: "Doctor"

    // Notifications Dialog
    if (showNotifications) {
        NotificationsDialog(
            notifications = notifications,
            onDismiss = { showNotifications = false },
            onMarkAsRead = { id -> viewModel.markNotificationAsRead(id) },
            onMarkAllAsRead = { viewModel.markAllNotificationsAsRead() },
            onNavigateToInjury = onNavigateToInjury,
            onNavigateToPcme = onNavigateToPcme
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // ── Hero Card ──
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
                    // Role badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = AccentGold,
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            "Medical Staff",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                    
                    // Notification button
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clickable { showNotifications = true }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = AccentGold,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        if (unreadCount > 0) {
                            Surface(
                                shape = CircleShape,
                                color = SeveritySevere,
                                modifier = Modifier
                                    .size(18.dp)
                                    .align(Alignment.TopEnd)
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Text(
                                        if (unreadCount > 9) "9+" else "$unreadCount",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp)
                    ) {
                        Text(
                            "Dr. $doctorName",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "UEFA Medical Analyst",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 13.sp,
                            letterSpacing = 0.3.sp
                        )
                        Spacer(Modifier.height(16.dp))

                        // Quick stats row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            HeroStat("Squad", "$totalPlayers", Icons.Default.People)
                            HeroStat("Injured", "${injuredPlayerIds.size}", Icons.Default.LocalHospital)
                            HeroStat("PCMEs", "${pcmeEntries.size}", Icons.Default.MedicalServices)
                            HeroStat("Sessions", "${trainingSessions.size}", Icons.Default.FitnessCenter)
                        }
                    }
                }
            }
        }

        // ── Squad Availability ──
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = UefaBlue.copy(alpha = 0.12f), modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Groups, null, tint = UefaBlue, modifier = Modifier.padding(6.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Text("Squad Availability", fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Donut chart
                        Box(
                            modifier = Modifier.size(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(110.dp)) {
                                val strokeWidth = 14.dp.toPx()
                                val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                                val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

                                // Background arc
                                drawArc(
                                    color = BackgroundGray,
                                    startAngle = 0f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    topLeft = topLeft,
                                    size = arcSize,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                                // Available arc (green)
                                val availableSweep = (availablePlayers.toFloat() / totalPlayers) * 360f
                                drawArc(
                                    color = SeverityMinor,
                                    startAngle = -90f,
                                    sweepAngle = availableSweep,
                                    useCenter = false,
                                    topLeft = topLeft,
                                    size = arcSize,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                                // Injured arc (red)
                                drawArc(
                                    color = SeveritySevere,
                                    startAngle = -90f + availableSweep,
                                    sweepAngle = 360f - availableSweep,
                                    useCenter = false,
                                    topLeft = topLeft,
                                    size = arcSize,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$availabilityPercent%", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text("Available", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Spacer(Modifier.width(20.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AvailabilityRow(SeverityMinor, "Available", availablePlayers, totalPlayers)
                            AvailabilityRow(SeveritySevere, "Injured", injuredPlayerIds.size, totalPlayers)

                            val needingPcme = players.count { it.profile?.pcmeStatus in listOf("missing", "late", "expected") }
                            AvailabilityRow(SeverityModerate, "PCME Due", needingPcme, totalPlayers)
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        // ── Weekly Load Overview ──
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = Color(0xFFFF9800).copy(alpha = 0.12f), modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.BarChart, null, tint = Color(0xFFFF9800), modifier = Modifier.padding(6.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Text("Weekly Load", fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(Modifier.height(16.dp))

                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    val loads = listOf(78, 92, 65, 88, 45, 95, 30)
                    val maxLoad = 100

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        days.forEachIndexed { idx, day ->
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "${loads[idx]}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary
                                )
                                Spacer(Modifier.height(4.dp))
                                val barHeight = (loads[idx].toFloat() / maxLoad * 80).dp
                                val barColor = when {
                                    loads[idx] >= 90 -> SeveritySevere
                                    loads[idx] >= 70 -> SeverityModerate
                                    else -> SeverityMinor
                                }
                                Box(
                                    modifier = Modifier
                                        .width(20.dp)
                                        .height(barHeight)
                                        .background(
                                            barColor.copy(alpha = 0.8f),
                                            RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                        )
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(day, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        LoadLegendItem(SeverityMinor, "Low (<70)")
                        LoadLegendItem(SeverityModerate, "Medium (70-89)")
                        LoadLegendItem(SeveritySevere, "High (90+)")
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        // ── Return to Play ──
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = UefaBlue.copy(alpha = 0.12f), modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.DirectionsRun, null, tint = UefaBlue, modifier = Modifier.padding(6.dp))
                            }
                            Spacer(Modifier.width(10.dp))
                            Text("Return to Play", fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Surface(shape = RoundedCornerShape(12.dp), color = UefaBlue.copy(alpha = 0.08f)) {
                            Text(
                                "${openInjuries.size} active",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = UefaBlue,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    if (openInjuries.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SeverityMinor.copy(alpha = 0.08f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, null, tint = SeverityMinor, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(10.dp))
                                Text("All players fully available", fontSize = 13.sp, color = SeverityMinor, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    } else {
                        openInjuries.take(4).forEach { injury ->
                            val player = players.find { it.user?.id == injury.userId }
                            val playerName = player?.profile?.let { "${it.firstName} ${it.lastName}" } ?: "Unknown Player"
                            val playerPos = player?.profile?.position ?: ""
                            val sevColor = when (injury.severity) {
                                "minor" -> SeverityMinor
                                "moderate" -> SeverityModerate
                                else -> SeveritySevere
                            }
                            val rtpLabel = when (injury.rtpStatus) {
                                "cleared" -> "Cleared"
                                "in_progress" -> "In Progress"
                                "conditioning" -> "Conditioning"
                                else -> "Not Started"
                            }
                            val rtpProgress = when (injury.rtpStatus) {
                                "cleared" -> 1.0f
                                "conditioning" -> 0.75f
                                "in_progress" -> 0.5f
                                else -> 0.15f
                            }

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        ProfileAvatar(imageUrl = player?.profile?.imageUrl, name = playerName, size = 36, fallbackColor = sevColor)
                                        Spacer(Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(playerName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                            Text("$playerPos • ${injury.bodyArea}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Surface(shape = RoundedCornerShape(8.dp), color = sevColor.copy(alpha = 0.12f)) {
                                            Text(
                                                injury.severity.replaceFirstChar { it.uppercase() },
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = sevColor,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(10.dp))

                                    // RTP Progress bar
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("RTP:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                                        Spacer(Modifier.width(8.dp))
                                        Box(modifier = Modifier.weight(1f)) {
                                            LinearProgressIndicator(
                                                progress = { rtpProgress },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(6.dp),
                                                color = when {
                                                    rtpProgress >= 0.75f -> SeverityMinor
                                                    rtpProgress >= 0.5f -> SeverityModerate
                                                    else -> SeveritySevere
                                                },
                                                trackColor = MaterialTheme.colorScheme.outline,
                                                strokeCap = StrokeCap.Round
                                            )
                                        }
                                        Spacer(Modifier.width(8.dp))
                                        Text(rtpLabel, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }

                                    if (injury.estimatedReturnDate != null) {
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            "Est. return: ${injury.estimatedReturnDate}",
                                            fontSize = 10.sp,
                                            color = UefaBlue,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        // ── Quick Actions ──
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickAction("Players", Icons.Default.People, UefaBlue, onViewPlayers, Modifier.weight(1f))
                QuickAction("PCME", Icons.Default.MedicalServices, SeverityMinor, onViewPcme, Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickAction("Injuries", Icons.Default.LocalHospital, SeveritySevere, onViewInjuries, Modifier.weight(1f))
                QuickAction("Training", Icons.Default.FitnessCenter, Color(0xFFFF9800), onViewTraining, Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
        }

        // ── Players Needing PCME ──
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = SeverityModerate.copy(alpha = 0.12f), modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Assignment, null, tint = SeverityModerate, modifier = Modifier.padding(6.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Text("Players Needing PCME", fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(Modifier.height(16.dp))
                    val needingPcme = players.filter {
                        it.profile?.pcmeStatus in listOf("missing", "late", "expected")
                    }.take(4)
                    if (needingPcme.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SeverityMinor.copy(alpha = 0.08f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, null, tint = SeverityMinor, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(10.dp))
                                Text("All players up to date", fontSize = 13.sp, color = SeverityMinor, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    } else {
                        needingPcme.forEach { pw ->
                            pw.profile?.let { p ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ProfileAvatar(imageUrl = p.imageUrl, name = "${p.firstName} ${p.lastName}", size = 36, fallbackColor = UefaBlue)
                                    Spacer(Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${p.firstName} ${p.lastName}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                        Text("${p.club} • ${p.position}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    PcmeStatusBadge(p.pcmeStatus)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Helper Composables ──

@Composable
private fun HeroStat(label: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.15f), modifier = Modifier.size(36.dp)) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.padding(8.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
    }
}

@Composable
private fun AvailabilityRow(color: Color, label: String, count: Int, total: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Spacer(Modifier.width(8.dp))
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text("$count", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun LoadLegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color.copy(alpha = 0.8f), RoundedCornerShape(2.dp))
        )
        Spacer(Modifier.width(4.dp))
        Text(label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun QuickAction(label: String, icon: ImageVector, color: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Surface(shape = CircleShape, color = color.copy(alpha = 0.15f), modifier = Modifier.size(32.dp)) {
                Icon(icon, null, tint = color, modifier = Modifier.padding(6.dp))
            }
            Spacer(Modifier.width(10.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = color)
        }
    }
}

@Composable
fun NotificationsDialog(
    notifications: List<com.golazo.medical.data.model.AppNotification>,
    onDismiss: () -> Unit,
    onMarkAsRead: (String) -> Unit,
    onMarkAllAsRead: () -> Unit,
    onNavigateToInjury: (String) -> Unit = {},
    onNavigateToPcme: (String) -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FlashOn, null, tint = AccentGold, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Actions Needed", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                }
                if (notifications.any { !it.isRead }) {
                    TextButton(onClick = onMarkAllAsRead) {
                        Text("Mark all read", fontSize = 11.sp, color = UefaBlueLight)
                    }
                }
            }
        },
        text = {
            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CheckCircle, null, tint = SeverityMinor, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("No notifications", color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            } else {
                Column(
                    modifier = Modifier.heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "${notifications.filter { !it.isRead }.size} unread • ${notifications.size} items",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    notifications.take(10).forEach { notification ->
                        NotificationItem(
                            notification = notification,
                            onClick = {
                                onMarkAsRead(notification.id)
                                when (notification.type) {
                                    "injury_created" -> notification.relatedId?.let { onNavigateToInjury(it) }
                                    "pcme_created" -> notification.relatedId?.let { onNavigateToPcme(it) }
                                }
                                onDismiss()
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = UefaBlueLight)
            }
        }
    )
}

@Composable
private fun NotificationItem(
    notification: com.golazo.medical.data.model.AppNotification,
    onClick: () -> Unit
) {
    val isUrgent = notification.type == "injury_created"
    val initials = notification.playerName?.split(" ")?.mapNotNull { it.firstOrNull()?.uppercase() }?.take(2)?.joinToString("") ?: "?"
    val bgColor = when (notification.type) {
        "injury_created" -> SeveritySevere
        "pcme_created" -> SeverityMinor
        else -> UefaBlue
    }
    
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (notification.isRead) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
        border = if (!notification.isRead) BorderStroke(1.dp, bgColor.copy(alpha = 0.3f)) else null,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = bgColor.copy(alpha = 0.15f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(initials, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = bgColor)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        notification.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isUrgent) {
                        Spacer(Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = SeveritySevere
                        ) {
                            Text(
                                "Urgent",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    notification.message,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
