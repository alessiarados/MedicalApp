package com.golazo.medical.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.golazo.medical.data.model.InjuryNoteCreateRequest
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*

@Composable
fun InjuryDetailScreen(
    injuryId: String,
    onBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val injury by viewModel.currentInjury.collectAsStateWithLifecycle()
    val notes by viewModel.injuryNotes.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var showAddNote by remember { mutableStateOf(false) }
    var noteIntensity by remember { mutableIntStateOf(5) }
    var noteSoap by remember { mutableStateOf("") }
    var noteRtp by remember { mutableStateOf("") }

    LaunchedEffect(injuryId) { viewModel.loadInjuryDetail(injuryId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        GolazoTopBar(title = "Injury Details", onBack = onBack)

        if (isLoading && injury == null) {
            LoadingScreen()
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                injury?.let { inj ->
                    // Injury Info Card with Body Map
                    item {
                        val severityColor = when (inj.severity) {
                            "minor" -> SeverityMinor
                            "moderate" -> SeverityModerate
                            else -> SeveritySevere
                        }
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = CardWhite,
                            shadowElevation = 4.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Header with body area and severity
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(inj.bodyArea, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = severityColor.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            inj.severity.replaceFirstChar { it.uppercase() },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = severityColor,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                                
                                // Details
                                DetailRow("Mechanism", inj.mechanism)
                                inj.injuryCategory?.let { DetailRow("Category", it) }
                                inj.injuryType?.let { DetailRow("Type", it) }
                                DetailRow("Status", inj.status.replaceFirstChar { it.uppercase() })
                                DetailRow("Re-injury", if (inj.isReinjury) "Yes" else "No")
                                inj.injuryDate?.let { DetailRow("Date", it) }
                            }
                        }
                    }
                    
                    // Injury Location with Body Map
                    item {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = CardWhite,
                            shadowElevation = 4.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocationOn, null, tint = UefaBlue, modifier = Modifier.size(20.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text("Injury Location", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text("Front", fontSize = 12.sp, color = UefaBlue)
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(inj.bodyArea, fontSize = 12.sp, color = TextSecondary)
                                
                                // Body area chip
                                Spacer(Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = UefaBlue.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        inj.bodyArea,
                                        fontSize = 11.sp,
                                        color = UefaBlue,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                                
                                // Body Map
                                Spacer(Modifier.height(12.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(280.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Parse body areas from comma-separated string
                                    val bodyAreas = inj.bodyArea.split(",").map { it.trim() }.toSet()
                                    BodyMapWithHighlights(
                                        selectedAreas = bodyAreas,
                                        isFrontView = true,
                                        modifier = Modifier
                                            .fillMaxWidth(0.6f)
                                            .height(260.dp)
                                    )
                                }
                                
                                // Severity slider
                                Spacer(Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Severity", fontSize = 12.sp, color = TextSecondary)
                                    Text(
                                        inj.severity.replaceFirstChar { it.uppercase() },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = when (inj.severity) {
                                            "minor" -> SeverityMinor
                                            "moderate" -> SeverityModerate
                                            else -> SeveritySevere
                                        }
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                // Gradient severity bar
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(
                                                    Color(0xFF4CAF50),
                                                    Color(0xFFCDDC39),
                                                    Color(0xFFFFEB3B),
                                                    Color(0xFFFF9800),
                                                    Color(0xFFF44336)
                                                )
                                            ),
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                )
                                // Severity indicator
                                val severityPosition = when (inj.severity) {
                                    "minor" -> 0.15f
                                    "moderate" -> 0.5f
                                    else -> 0.85f
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(severityPosition)
                                            .wrapContentWidth(Alignment.End)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = CardWhite,
                                            shadowElevation = 2.dp,
                                            modifier = Modifier.size(16.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(3.dp)
                                                    .background(
                                                        color = when (inj.severity) {
                                                            "minor" -> SeverityMinor
                                                            "moderate" -> SeverityModerate
                                                            else -> SeveritySevere
                                                        },
                                                        shape = RoundedCornerShape(50)
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // RTP Progress
                    item {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = CardWhite,
                            shadowElevation = 4.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Return to Play Progress", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(12.dp))
                                val rtpStages = listOf("not_started", "in_rehab", "light_training", "full_training", "cleared")
                                val currentIndex = rtpStages.indexOf(inj.rtpStatus)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    rtpStages.forEachIndexed { index, stage ->
                                        val isActive = index <= currentIndex
                                        val color = if (isActive) UefaBlue else CardBorder
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Surface(
                                                shape = RoundedCornerShape(50),
                                                color = color,
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    if (isActive) {
                                                        Icon(Icons.Default.Check, null, tint = White, modifier = Modifier.size(14.dp))
                                                    }
                                                }
                                            }
                                            Spacer(Modifier.height(4.dp))
                                            Text(
                                                when (stage) {
                                                    "not_started" -> "Start"
                                                    "in_rehab" -> "Rehab"
                                                    "light_training" -> "Light"
                                                    "full_training" -> "Full"
                                                    "cleared" -> "Clear"
                                                    else -> stage
                                                },
                                                fontSize = 8.sp,
                                                color = if (isActive) UefaBlue else TextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Notes Timeline
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Notes Timeline", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            TextButton(onClick = { showAddNote = !showAddNote }) {
                                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Add Note", fontSize = 12.sp)
                            }
                        }
                    }

                    // Add Note Form
                    if (showAddNote) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = CardWhite,
                                shadowElevation = 4.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("New Note", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(8.dp))
                                    Text("Pain Intensity: $noteIntensity/10", fontSize = 12.sp)
                                    Slider(
                                        value = noteIntensity.toFloat(),
                                        onValueChange = { noteIntensity = it.toInt() },
                                        valueRange = 1f..10f,
                                        steps = 8,
                                        colors = SliderDefaults.colors(thumbColor = UefaBlue, activeTrackColor = UefaBlue)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    GolazoTextField(
                                        value = noteSoap,
                                        onValueChange = { noteSoap = it },
                                        label = "SOAP Notes",
                                        singleLine = false,
                                        maxLines = 5
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text("RTP Status Update (optional)", fontSize = 12.sp, color = TextSecondary)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        listOf("in_rehab", "light_training", "full_training", "cleared").forEach { status ->
                                            FilterChip(
                                                selected = noteRtp == status,
                                                onClick = { noteRtp = if (noteRtp == status) "" else status },
                                                label = {
                                                    Text(
                                                        when (status) {
                                                            "in_rehab" -> "Rehab"
                                                            "light_training" -> "Light"
                                                            "full_training" -> "Full"
                                                            "cleared" -> "Cleared"
                                                            else -> status
                                                        },
                                                        fontSize = 9.sp
                                                    )
                                                },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = UefaBlue,
                                                    selectedLabelColor = White
                                                )
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(12.dp))
                                    GolazoButton(
                                        text = "Save Note",
                                        onClick = {
                                            viewModel.addInjuryNote(
                                                injuryId,
                                                InjuryNoteCreateRequest(
                                                    authorId = viewModel.sessionManager.userId,
                                                    intensity = noteIntensity,
                                                    soapNotes = noteSoap,
                                                    rtpStatus = noteRtp.ifBlank { null }
                                                )
                                            ) {
                                                showAddNote = false
                                                noteSoap = ""
                                                noteRtp = ""
                                                noteIntensity = 5
                                            }
                                        },
                                        enabled = noteSoap.isNotBlank()
                                    )
                                }
                            }
                        }
                    }

                    // Existing Notes
                    items(notes) { note ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = CardWhite,
                            shadowElevation = 4.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Intensity: ${note.intensity}/10", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    note.rtpStatus?.let { RtpBadge(it) }
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(note.soapNotes, fontSize = 12.sp, color = TextSecondary)
                                Spacer(Modifier.height(4.dp))
                                Text(note.createdAt ?: "", fontSize = 10.sp, color = TextSecondary.copy(alpha = 0.7f))
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Text(label, fontSize = 11.sp, color = TextSecondary, modifier = Modifier.width(100.dp))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
