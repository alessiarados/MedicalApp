package com.golazo.medical.ui.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.golazo.medical.R
import com.golazo.medical.data.model.InjuryCase
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.components.rememberSpeechRecognizer
import com.golazo.medical.ui.theme.*

@Composable
fun InjuryCreateScreen(
    onBack: () -> Unit,
    onCreated: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val isFemale = profile?.gender == "female"
    
    LaunchedEffect(Unit) { viewModel.loadProfile() }
    
    var selectedBodyAreas by remember { mutableStateOf(setOf<String>()) }
    var mechanism by remember { mutableStateOf("") }
    
    val speechRecognizer = rememberSpeechRecognizer(
        onResult = { text -> mechanism = if (mechanism.isEmpty()) text else "$mechanism $text" },
        onError = { }
    )
    var severity by remember { mutableStateOf("minor") }
    var injuryCategory by remember { mutableStateOf("") }
    var injuryType by remember { mutableStateOf("") }
    var isReinjury by remember { mutableStateOf(false) }
    var injuryDate by remember { mutableStateOf("") }
    var bodyViewFront by remember { mutableStateOf(true) }

    // Row 1: Head, Neck, Shoulder, Chest, Back
    // Row 2: Hip/Groin, Thigh (Front), Thigh (Back), Knee
    // Row 3: Lower Leg, Calf, Ankle, Foot
    val bodyAreas = listOf(
        listOf("Head", "Neck", "Shoulder", "Chest", "Back"),
        listOf("Hip/Groin", "Thigh (Front)", "Thigh (Back)", "Knee"),
        listOf("Lower Leg", "Calf", "Ankle", "Foot")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        GolazoTopBar(title = "Report Injury", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Injury Map
            Surface(shape = RoundedCornerShape(20.dp), color = CardWhite, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, null, tint = UefaBlue, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Injury Map", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Row {
                            FilterChip(
                                selected = bodyViewFront,
                                onClick = { bodyViewFront = true },
                                label = { Text("Front", fontSize = 11.sp, color = if (bodyViewFront) UefaBlue else TextSecondary) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = UefaBlue.copy(alpha = 0.15f),
                                    selectedLabelColor = UefaBlue
                                )
                            )
                            Spacer(Modifier.width(6.dp))
                            FilterChip(
                                selected = !bodyViewFront,
                                onClick = { bodyViewFront = false },
                                label = { Text("Back", fontSize = 11.sp, color = if (!bodyViewFront) UefaBlue else TextSecondary) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = UefaBlue.copy(alpha = 0.15f),
                                    selectedLabelColor = UefaBlue
                                )
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("Select body parts to highlight on the map.", fontSize = 11.sp, color = TextSecondary)
                    Spacer(Modifier.height(12.dp))

                    // Body area chips in rows
                    bodyAreas.forEach { row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            row.forEach { area ->
                                FilterChip(
                                    selected = selectedBodyAreas.contains(area),
                                    onClick = {
                                        selectedBodyAreas = if (selectedBodyAreas.contains(area)) {
                                            selectedBodyAreas - area
                                        } else {
                                            selectedBodyAreas + area
                                        }
                                    },
                                    label = { Text(area, fontSize = 11.sp, color = if (selectedBodyAreas.contains(area)) UefaBlue else TextSecondary) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = UefaBlue.copy(alpha = 0.15f),
                                        selectedLabelColor = UefaBlue
                                    )
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Selected count and Clear all - always show row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${selectedBodyAreas.size} selected",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        if (selectedBodyAreas.isNotEmpty()) {
                            TextButton(onClick = { selectedBodyAreas = emptySet() }) {
                                Text("Clear all", fontSize = 12.sp, color = UefaBlue)
                            }
                        }
                    }

                    // Selected areas labels as individual chips
                    if (selectedBodyAreas.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            selectedBodyAreas.forEach { area ->
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = UefaBlue.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        area,
                                        fontSize = 11.sp,
                                        color = UefaBlue,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Body map with highlight overlays
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        BodyMapWithHighlights(
                            selectedAreas = selectedBodyAreas,
                            isFrontView = bodyViewFront,
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(300.dp),
                            isFemale = isFemale
                        )
                    }

                }
            }

            Spacer(Modifier.height(12.dp))

            // Mechanism
            Surface(shape = RoundedCornerShape(20.dp), color = CardWhite, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                Text("How did it happen?", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                GolazoTextField(
                    value = mechanism,
                    onValueChange = { mechanism = it },
                    label = "Describe the mechanism",
                    singleLine = false,
                    maxLines = 4,
                    trailingIcon = {
                        IconButton(onClick = { 
                            if (speechRecognizer.isListening) {
                                speechRecognizer.stopListening()
                            } else {
                                speechRecognizer.startListening()
                            }
                        }) {
                            Icon(
                                Icons.Default.Mic, 
                                "Voice input", 
                                tint = if (speechRecognizer.isListening) SeveritySevere else UefaBlue
                            )
                        }
                    }
                )
            }
            }

            Spacer(Modifier.height(12.dp))

            // Severity
            Surface(shape = RoundedCornerShape(20.dp), color = CardWhite, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                Text("Severity", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("minor" to "Minor", "moderate" to "Moderate", "severe" to "Severe").forEach { (value, label) ->
                        val color = when (value) {
                            "minor" -> SeverityMinor
                            "moderate" -> SeverityModerate
                            else -> SeveritySevere
                        }
                        FilterChip(
                            selected = severity == value,
                            onClick = { severity = value },
                            label = { Text(label, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = color,
                                selectedLabelColor = White
                            )
                        )
                    }
                }
            }
            }

            Spacer(Modifier.height(12.dp))

            // Additional Details
            Surface(shape = RoundedCornerShape(20.dp), color = CardWhite, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                Text("Additional Details", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                GolazoTextField(
                    value = injuryCategory,
                    onValueChange = { injuryCategory = it },
                    label = "Injury Category (e.g., Muscle, Ligament)"
                )
                Spacer(Modifier.height(8.dp))
                GolazoTextField(
                    value = injuryType,
                    onValueChange = { injuryType = it },
                    label = "Injury Type (e.g., Strain, Tear)"
                )
                Spacer(Modifier.height(8.dp))
                GolazoTextField(
                    value = injuryDate,
                    onValueChange = { injuryDate = it },
                    label = "Injury Date (YYYY-MM-DD)"
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isReinjury,
                        onCheckedChange = { isReinjury = it },
                        colors = CheckboxDefaults.colors(checkedColor = UefaBlue)
                    )
                    Text("This is a re-injury", fontSize = 12.sp)
                }
            }
            }

            Spacer(Modifier.height(16.dp))

            GolazoButton(
                text = "Report Injury",
                onClick = {
                    viewModel.createInjury(
                        InjuryCase(
                            bodyArea = selectedBodyAreas.joinToString(", "),
                            mechanism = mechanism,
                            severity = severity,
                            injuryCategory = injuryCategory.ifBlank { null },
                            injuryType = injuryType.ifBlank { null },
                            isReinjury = isReinjury,
                            injuryDate = injuryDate.ifBlank { null },
                            createdBy = "player"
                        ),
                        onSuccess = onCreated
                    )
                },
                enabled = selectedBodyAreas.isNotEmpty() && mechanism.isNotBlank()
            )

            Spacer(Modifier.height(80.dp))
        }
    }
}
