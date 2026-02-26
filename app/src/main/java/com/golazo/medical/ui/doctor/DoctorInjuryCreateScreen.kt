package com.golazo.medical.ui.doctor

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.golazo.medical.data.model.InjuryCase
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.components.rememberSpeechRecognizer
import com.golazo.medical.ui.theme.*

@Composable
fun DoctorInjuryCreateScreen(
    onBack: () -> Unit,
    onCreated: () -> Unit,
    viewModel: DoctorViewModel = hiltViewModel()
) {
    val players by viewModel.players.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadPlayers() }

    var selectedPlayerId by remember { mutableStateOf("") }
    var bodyArea by remember { mutableStateOf("") }
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
    var expanded by remember { mutableStateOf(false) }

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
        GolazoTopBar(title = "Create Injury", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Player Selector
            Surface(shape = RoundedCornerShape(20.dp), color = CardWhite, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                Text("Select Player", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    val selectedPlayer = players.find { it.user?.id == selectedPlayerId }
                    OutlinedTextField(
                        value = selectedPlayer?.let { "${it.profile?.firstName} ${it.profile?.lastName}" } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Player", fontSize = 12.sp) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        players.forEach { pw ->
                            DropdownMenuItem(
                                text = { Text("${pw.profile?.firstName} ${pw.profile?.lastName} (${pw.profile?.club})", fontSize = 12.sp) },
                                onClick = {
                                    selectedPlayerId = pw.user?.id ?: ""
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
            }

            Spacer(Modifier.height(12.dp))

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

            Surface(shape = RoundedCornerShape(20.dp), color = CardWhite, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                Text("Mechanism", fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
                                "Voice", 
                                tint = if (speechRecognizer.isListening) SeveritySevere else UefaBlue
                            )
                        }
                    }
                )
            }
            }

            Spacer(Modifier.height(12.dp))

            Surface(shape = RoundedCornerShape(20.dp), color = CardWhite, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                Text("Severity", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("minor" to "Minor", "moderate" to "Moderate", "severe" to "Severe").forEach { (v, l) ->
                        val color = when (v) { "minor" -> SeverityMinor; "moderate" -> SeverityModerate; else -> SeveritySevere }
                        FilterChip(
                            selected = severity == v,
                            onClick = { severity = v },
                            label = { Text(l, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = color, selectedLabelColor = White)
                        )
                    }
                }
            }
            }

            Spacer(Modifier.height(12.dp))

            Surface(shape = RoundedCornerShape(20.dp), color = CardWhite, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                Text("Details", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                GolazoTextField(value = injuryCategory, onValueChange = { injuryCategory = it }, label = "Category")
                Spacer(Modifier.height(8.dp))
                GolazoTextField(value = injuryType, onValueChange = { injuryType = it }, label = "Type")
                Spacer(Modifier.height(8.dp))
                GolazoTextField(value = injuryDate, onValueChange = { injuryDate = it }, label = "Date (YYYY-MM-DD)")
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isReinjury, onCheckedChange = { isReinjury = it }, colors = CheckboxDefaults.colors(checkedColor = UefaBlue))
                    Text("Re-injury", fontSize = 12.sp)
                }
            }
            }

            Spacer(Modifier.height(16.dp))

            GolazoButton(
                text = "Create Injury",
                onClick = {
                    viewModel.createInjury(
                        InjuryCase(
                            userId = selectedPlayerId,
                            bodyArea = bodyArea,
                            mechanism = mechanism,
                            severity = severity,
                            injuryCategory = injuryCategory.ifBlank { null },
                            injuryType = injuryType.ifBlank { null },
                            isReinjury = isReinjury,
                            injuryDate = injuryDate.ifBlank { null },
                            createdBy = "doctor"
                        ),
                        onSuccess = onCreated
                    )
                },
                enabled = selectedPlayerId.isNotBlank() && bodyArea.isNotBlank() && mechanism.isNotBlank()
            )

            Spacer(Modifier.height(80.dp))
        }
    }
}
