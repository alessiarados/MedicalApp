package com.golazo.medical.ui.doctor

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        GolazoTopBar(title = "Training Sessions")

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Create Form Toggle
            item {
                if (!showCreateForm) {
                    GolazoButton(text = "Log New Session", onClick = { showCreateForm = true })
                }
            }

            // Create Form
            if (showCreateForm) {
                item {
                    GolazoCard {
                        Text("New Training Session", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))

                        GolazoTextField(value = title, onValueChange = { title = it }, label = "Session Title")
                        Spacer(Modifier.height(8.dp))

                        GolazoTextField(
                            value = date,
                            onValueChange = { date = it },
                            label = "Date (YYYY-MM-DD or 'last Tuesday')"
                        )
                        Spacer(Modifier.height(8.dp))

                        Text("Type", fontSize = 12.sp, color = TextSecondary)
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

                        Text("Time of Day", fontSize = 12.sp, color = TextSecondary)
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
                        Spacer(Modifier.height(12.dp))

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
            }

            // Sessions List
            if (isLoading) {
                item { LoadingScreen() }
            } else if (sessions.isEmpty() && !showCreateForm) {
                item {
                    EmptyState(
                        icon = Icons.Default.FitnessCenter,
                        title = "No training sessions",
                        subtitle = "Log your first session above"
                    )
                }
            } else {
                items(sessions) { session ->
                    GolazoCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(session.title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("${session.date} • ${session.timeOfDay}", fontSize = 11.sp, color = TextSecondary)
                                Spacer(Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = UefaBlueVeryLight
                                    ) {
                                        Text(
                                            session.type.replaceFirstChar { it.uppercase() },
                                            fontSize = 10.sp,
                                            color = UefaBlue,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text("${session.duration} min", fontSize = 10.sp, color = TextSecondary)
                                    Text("${session.attendees} players", fontSize = 10.sp, color = TextSecondary)
                                }
                                session.pitch?.let { Text("Pitch: $it", fontSize = 10.sp, color = TextSecondary) }
                                session.distance?.let { Text("Distance: ${it}m", fontSize = 10.sp, color = TextSecondary) }
                                session.notes?.let {
                                    Spacer(Modifier.height(4.dp))
                                    Text(it, fontSize = 10.sp, color = TextSecondary, maxLines = 2)
                                }
                            }
                            IconButton(onClick = { viewModel.deleteTrainingSession(session.id) }) {
                                Icon(Icons.Default.Delete, "Delete", tint = SeveritySevere, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}
