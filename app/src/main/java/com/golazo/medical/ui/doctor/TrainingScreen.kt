package com.golazo.medical.ui.doctor

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.golazo.medical.data.model.TrainingCreateRequest
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*

@Composable
fun TrainingScreen(
    viewModel: DoctorViewModel = hiltViewModel()
) {
    val sessions by viewModel.trainingSessions.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var showCreateForm by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }

    // Form state
    var date by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("practice") }
    var title by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var attendees by remember { mutableStateOf("") }
    var timeOfDay by remember { mutableStateOf("morning") }
    var pitch by remember { mutableStateOf("") }
    var distance by remember { mutableStateOf("") }
    var formNotes by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.loadTrainingSessions() }

    val sessionTypes = listOf("practice", "weights", "film", "conditioning", "recovery")
    val timesOfDay = listOf("morning", "afternoon", "evening")

    val totalDuration = sessions.sumOf { it.duration }
    val avgAttendees = if (sessions.isNotEmpty()) sessions.sumOf { it.attendees } / sessions.size else 0

    val filtered = when (selectedFilter) {
        "All" -> sessions
        else -> sessions.filter { it.type.equals(selectedFilter, ignoreCase = true) }
    }

    val typeColor = { t: String ->
        when (t.lowercase()) {
            "practice" -> UefaBlue
            "weights" -> Color(0xFFFF9800)
            "conditioning" -> SeveritySevere
            "recovery" -> SeverityMinor
            "film" -> Color(0xFF9C27B0)
            else -> TextSecondary
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // ── Hero Header ──
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
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "Training Log",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.5).sp
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Track and manage sessions",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 13.sp,
                                    letterSpacing = 0.3.sp
                                )
                            }
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.15f),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.Default.FitnessCenter,
                                    null,
                                    tint = Color.White,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TrainingHeroStat("Sessions", "${sessions.size}", Icons.Default.Event)
                            TrainingHeroStat("Total Min", "$totalDuration", Icons.Default.Timer)
                            TrainingHeroStat("Avg Players", "$avgAttendees", Icons.Default.People)
                            TrainingHeroStat("Types", "${sessions.map { it.type }.distinct().size}", Icons.Default.Category)
                        }

                        if (!showCreateForm) {
                            Spacer(Modifier.height(16.dp))
                            Surface(
                                onClick = { showCreateForm = true },
                                shape = RoundedCornerShape(14.dp),
                                color = Color.White.copy(alpha = 0.18f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Log New Session", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── Create Form ──
        if (showCreateForm) {
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = CardWhite,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = UefaBlue.copy(alpha = 0.12f), modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Edit, null, tint = UefaBlue, modifier = Modifier.padding(6.dp))
                            }
                            Spacer(Modifier.width(10.dp))
                            Text("New Training Session", fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp)
                        }
                        Spacer(Modifier.height(16.dp))

                        GolazoTextField(value = title, onValueChange = { title = it }, label = "Session Title")
                        Spacer(Modifier.height(8.dp))

                        GolazoTextField(
                            value = date,
                            onValueChange = { date = it },
                            label = "Date (YYYY-MM-DD or 'last Tuesday')"
                        )
                        Spacer(Modifier.height(8.dp))

                        Text("Type", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            sessionTypes.forEach { t ->
                                FilterChip(
                                    selected = type == t,
                                    onClick = { type = t },
                                    label = { Text(t.replaceFirstChar { it.uppercase() }, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = UefaBlue,
                                        selectedLabelColor = White
                                    )
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            GolazoTextField(
                                value = duration,
                                onValueChange = { duration = it },
                                label = "Duration (min)",
                                keyboardType = KeyboardType.Number,
                                modifier = Modifier.weight(1f)
                            )
                            GolazoTextField(
                                value = attendees,
                                onValueChange = { attendees = it },
                                label = "Attendees",
                                keyboardType = KeyboardType.Number,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(Modifier.height(8.dp))

                        Text("Time of Day", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            timesOfDay.forEach { t ->
                                FilterChip(
                                    selected = timeOfDay == t,
                                    onClick = { timeOfDay = t },
                                    label = { Text(t.replaceFirstChar { it.uppercase() }, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = UefaBlue,
                                        selectedLabelColor = White
                                    )
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            GolazoTextField(value = pitch, onValueChange = { pitch = it }, label = "Pitch", modifier = Modifier.weight(1f))
                            GolazoTextField(value = distance, onValueChange = { distance = it }, label = "Distance (m)", keyboardType = KeyboardType.Number, modifier = Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(8.dp))

                        GolazoTextField(value = formNotes, onValueChange = { formNotes = it }, label = "Notes", singleLine = false, maxLines = 3)
                        Spacer(Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            GolazoOutlinedButton(
                                text = "Cancel",
                                onClick = { showCreateForm = false },
                                modifier = Modifier.weight(1f)
                            )
                            GolazoButton(
                                text = "Save",
                                onClick = {
                                    viewModel.createTrainingSession(
                                        TrainingCreateRequest(
                                            date = date,
                                            type = type,
                                            title = title,
                                            duration = duration.toIntOrNull() ?: 0,
                                            attendees = attendees.toIntOrNull() ?: 0,
                                            notes = formNotes.ifBlank { null },
                                            timeOfDay = timeOfDay,
                                            pitch = pitch.ifBlank { null },
                                            distance = distance.toIntOrNull()
                                        )
                                    ) {
                                        showCreateForm = false
                                        title = ""; date = ""; duration = ""; attendees = ""
                                        pitch = ""; distance = ""; formNotes = ""
                                    }
                                },
                                enabled = title.isNotBlank() && date.isNotBlank(),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }

        // ── Weekly Summary ──
        item {
            val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            // Simulated weekly load data based on sessions
            val weeklyLoad = remember(sessions) {
                if (sessions.isEmpty()) {
                    listOf(0, 0, 0, 0, 0, 0, 0)
                } else {
                    listOf(75, 90, 60, 85, 45, 30, 0).mapIndexed { i, base ->
                        if (i < sessions.size) (base + sessions[i % sessions.size].duration % 30) else base
                    }
                }
            }
            val maxLoad = (weeklyLoad.maxOrNull() ?: 1).coerceAtLeast(1)

            // Type breakdown
            val typeBreakdown = remember(sessions) {
                sessions.groupBy { it.type }
                    .mapValues { it.value.sumOf { s -> s.duration } }
                    .entries
                    .sortedByDescending { it.value }
            }
            val totalTypeDuration = typeBreakdown.sumOf { it.value }.coerceAtLeast(1)

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = CardWhite,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Section header
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = UefaBlue.copy(alpha = 0.1f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Icon(Icons.Default.BarChart, null, tint = UefaBlue, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Weekly Summary", fontSize = 15.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.2).sp)
                            Text("Training load overview", fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    // Bar chart
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        days.forEachIndexed { idx, day ->
                            val load = weeklyLoad[idx]
                            val fraction = load.toFloat() / maxLoad
                            val animatedFraction by animateFloatAsState(
                                targetValue = fraction,
                                animationSpec = tween(durationMillis = 600, delayMillis = idx * 80),
                                label = "bar_$idx"
                            )
                            val barColor = when {
                                load >= 85 -> SeveritySevere
                                load >= 60 -> SeverityModerate
                                load > 0 -> SeverityMinor
                                else -> BackgroundGray
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                if (load > 0) {
                                    Text(
                                        "$load",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = barColor
                                    )
                                    Spacer(Modifier.height(4.dp))
                                }
                                Box(
                                    modifier = Modifier
                                        .width(24.dp)
                                        .height((90 * animatedFraction).coerceAtLeast(4f).dp)
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(barColor, barColor.copy(alpha = 0.5f))
                                            ),
                                            shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                        )
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(day, fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    // Load legend
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WeeklyLegendDot(SeverityMinor, "Low")
                        Spacer(Modifier.width(14.dp))
                        WeeklyLegendDot(SeverityModerate, "Medium")
                        Spacer(Modifier.width(14.dp))
                        WeeklyLegendDot(SeveritySevere, "High")
                    }

                    // Type breakdown
                    if (typeBreakdown.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = BackgroundGray)
                        Spacer(Modifier.height(14.dp))

                        Text("Type Breakdown", fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.2).sp)
                        Spacer(Modifier.height(10.dp))

                        typeBreakdown.forEach { (typeName, mins) ->
                            val pct = (mins.toFloat() / totalTypeDuration)
                            val tColor = when (typeName.lowercase()) {
                                "practice" -> UefaBlue
                                "weights" -> Color(0xFFFF9800)
                                "conditioning" -> SeveritySevere
                                "recovery" -> SeverityMinor
                                "film" -> Color(0xFF9C27B0)
                                else -> TextSecondary
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = tColor,
                                    modifier = Modifier.size(8.dp)
                                ) {}
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    typeName.replaceFirstChar { it.uppercase() },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.width(90.dp)
                                )
                                Box(modifier = Modifier.weight(1f).height(8.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = BackgroundGray,
                                        modifier = Modifier.fillMaxSize()
                                    ) {}
                                    val animPct by animateFloatAsState(
                                        targetValue = pct,
                                        animationSpec = tween(800),
                                        label = "type_$typeName"
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = tColor,
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(animPct)
                                    ) {}
                                }
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    "${mins}m",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = tColor
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        // ── Filter Chips ──
        item {
            val allTypes = listOf("All") + sessions.map { it.type.replaceFirstChar { c -> c.uppercase() } }.distinct()
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(allTypes) { filterType ->
                    val isSelected = selectedFilter.equals(filterType, ignoreCase = true)
                    val chipColor = if (filterType == "All") UefaBlue else typeColor(filterType)
                    Surface(
                        onClick = { selectedFilter = filterType },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) chipColor else chipColor.copy(alpha = 0.08f),
                        shadowElevation = if (isSelected) 2.dp else 0.dp
                    ) {
                        Text(
                            filterType,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) Color.White else chipColor,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        // ── Sessions List ──
        if (isLoading) {
            item { LoadingScreen() }
        } else if (filtered.isEmpty() && !showCreateForm) {
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    EmptyState(
                        icon = Icons.Default.FitnessCenter,
                        title = "No training sessions",
                        subtitle = "Log your first session above"
                    )
                }
            }
        } else {
            items(filtered) { session ->
                val sessionColor = typeColor(session.type)
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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Type icon
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = sessionColor.copy(alpha = 0.12f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                val typeIcon = when (session.type.lowercase()) {
                                    "practice" -> Icons.Default.SportsSoccer
                                    "weights" -> Icons.Default.FitnessCenter
                                    "conditioning" -> Icons.Default.DirectionsRun
                                    "recovery" -> Icons.Default.SelfImprovement
                                    "film" -> Icons.Default.Videocam
                                    else -> Icons.Default.Event
                                }
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Icon(typeIcon, null, tint = sessionColor, modifier = Modifier.size(22.dp))
                                }
                            }

                            Spacer(Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    session.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.2).sp
                                )
                                Spacer(Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = sessionColor.copy(alpha = 0.1f)
                                    ) {
                                        Text(
                                            session.type.replaceFirstChar { it.uppercase() },
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = sessionColor,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        "${session.date} • ${session.timeOfDay.replaceFirstChar { it.uppercase() }}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            IconButton(
                                onClick = { viewModel.deleteTrainingSession(session.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, "Delete", tint = SeveritySevere.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(Modifier.height(12.dp))
                        HorizontalDivider(color = BackgroundGray)
                        Spacer(Modifier.height(10.dp))

                        // Stats row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SessionStatChip(Icons.Default.Timer, "${session.duration} min")
                            SessionStatChip(Icons.Default.People, "${session.attendees} players")
                            session.pitch?.let { SessionStatChip(Icons.Default.Place, it) }
                            session.distance?.let { SessionStatChip(Icons.Default.Straighten, "${it}m") }
                        }

                        session.notes?.let {
                            Spacer(Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BackgroundGray.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    it,
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    maxLines = 2,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun TrainingHeroStat(label: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(32.dp)) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.padding(6.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(label, fontSize = 9.sp, color = Color.White.copy(alpha = 0.8f))
    }
}

@Composable
private fun SessionStatChip(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = TextSecondary, modifier = Modifier.size(13.dp))
        Spacer(Modifier.width(4.dp))
        Text(text, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun WeeklyLegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = CircleShape, color = color, modifier = Modifier.size(8.dp)) {}
        Spacer(Modifier.width(4.dp))
        Text(label, fontSize = 10.sp, color = TextSecondary)
    }
}
