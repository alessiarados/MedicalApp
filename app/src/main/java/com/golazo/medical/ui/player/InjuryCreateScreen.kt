package com.golazo.medical.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.golazo.medical.data.model.InjuryCase
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*

@Composable
fun InjuryCreateScreen(
    onBack: () -> Unit,
    onCreated: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    var bodyArea by remember { mutableStateOf("") }
    var mechanism by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf("minor") }
    var injuryCategory by remember { mutableStateOf("") }
    var injuryType by remember { mutableStateOf("") }
    var isReinjury by remember { mutableStateOf(false) }
    var injuryDate by remember { mutableStateOf("") }

    val bodyAreas = listOf(
        "Head", "Neck", "Left Shoulder", "Right Shoulder",
        "Left Elbow", "Right Elbow", "Left Wrist", "Right Wrist",
        "Chest", "Upper Back", "Lower Back", "Abdomen",
        "Left Hip", "Right Hip", "Left Knee", "Right Knee",
        "Left Ankle", "Right Ankle", "Left Hamstring", "Right Hamstring",
        "Left Calf", "Right Calf", "Left Foot", "Right Foot"
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
            // Body Area
            Surface(shape = RoundedCornerShape(20.dp), color = CardWhite, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                Text("Body Area", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    bodyAreas.forEach { area ->
                        FilterChip(
                            selected = bodyArea == area,
                            onClick = { bodyArea = area },
                            label = { Text(area, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = UefaBlue,
                                selectedLabelColor = White
                            )
                        )
                    }
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
                        IconButton(onClick = { /* Voice input placeholder */ }) {
                            Icon(Icons.Default.Mic, "Voice input", tint = UefaBlue)
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
                            bodyArea = bodyArea,
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
                enabled = bodyArea.isNotBlank() && mechanism.isNotBlank()
            )

            Spacer(Modifier.height(80.dp))
        }
    }
}
