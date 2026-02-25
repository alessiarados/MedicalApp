package com.golazo.medical.ui.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.golazo.medical.data.model.*
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*

@Composable
fun DoctorPcmeFormScreen(
    userId: String,
    onBack: () -> Unit,
    onCreated: () -> Unit,
    viewModel: DoctorViewModel = hiltViewModel()
) {
    var bloodType by remember { mutableStateOf("unknown") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var ecgStatus by remember { mutableStateOf("") }
    var echoStatus by remember { mutableStateOf("") }
    var scatScore by remember { mutableStateOf("") }
    var asthma by remember { mutableStateOf("") }
    var hepatitisB by remember { mutableStateOf("") }
    var tetanusStatus by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }
    var signature by remember { mutableStateOf("") }

    // Import prescriptions
    var showImportDialog by remember { mutableStateOf(false) }
    val prescriptions = remember { mutableStateListOf<Prescription>() }
    var newPrescName by remember { mutableStateOf("") }
    var newPrescDosage by remember { mutableStateOf("") }
    var newPrescFreq by remember { mutableStateOf("") }
    var newPrescBy by remember { mutableStateOf("") }

    val bloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "unknown")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        GolazoTopBar(title = "New PCME", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // General
            Surface(shape = RoundedCornerShape(20.dp), color = CardWhite, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                Text("General Information", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Blood Type", fontSize = 12.sp, color = TextSecondary)
                Spacer(Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    bloodTypes.forEach { bt ->
                        FilterChip(
                            selected = bloodType == bt,
                            onClick = { bloodType = bt },
                            label = { Text(bt, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = UefaBlue,
                                selectedLabelColor = White
                            )
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GolazoTextField(value = height, onValueChange = { height = it }, label = "Height (cm)", modifier = Modifier.weight(1f), keyboardType = KeyboardType.Number)
                    GolazoTextField(value = weight, onValueChange = { weight = it }, label = "Weight (kg)", modifier = Modifier.weight(1f), keyboardType = KeyboardType.Number)
                }
            }
            }

            Spacer(Modifier.height(12.dp))

            // Cardiac
            Surface(shape = RoundedCornerShape(20.dp), color = CardWhite, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                Text("Cardiac", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                GolazoTextField(value = ecgStatus, onValueChange = { ecgStatus = it }, label = "ECG Status")
                Spacer(Modifier.height(8.dp))
                GolazoTextField(value = echoStatus, onValueChange = { echoStatus = it }, label = "Echo Status")
            }
            }

            Spacer(Modifier.height(12.dp))

            // Concussion
            Surface(shape = RoundedCornerShape(20.dp), color = CardWhite, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                Text("Concussion (SCAT)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                GolazoTextField(value = scatScore, onValueChange = { scatScore = it }, label = "SCAT Score", keyboardType = KeyboardType.Number)
            }
            }

            Spacer(Modifier.height(12.dp))

            // Medical
            Surface(shape = RoundedCornerShape(20.dp), color = CardWhite, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                Text("Medical History", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                GolazoTextField(value = asthma, onValueChange = { asthma = it }, label = "Asthma Status")
                Spacer(Modifier.height(8.dp))
                GolazoTextField(value = hepatitisB, onValueChange = { hepatitisB = it }, label = "Hepatitis B Status")
                Spacer(Modifier.height(8.dp))
                GolazoTextField(value = tetanusStatus, onValueChange = { tetanusStatus = it }, label = "Tetanus Status")
                Spacer(Modifier.height(8.dp))
                GolazoTextField(value = allergies, onValueChange = { allergies = it }, label = "Allergies", singleLine = false, maxLines = 3)
            }
            }

            Spacer(Modifier.height(12.dp))

            // Prescriptions (Doctor-only import feature)
            Surface(shape = RoundedCornerShape(20.dp), color = CardWhite, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Prescriptions", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { showImportDialog = true }) {
                        Text("+ Add", fontSize = 12.sp)
                    }
                }
                if (prescriptions.isEmpty()) {
                    Text("No prescriptions added", fontSize = 11.sp, color = TextSecondary)
                } else {
                    prescriptions.forEach { p ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(p.name, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Text("${p.dosage} - ${p.frequency} (by ${p.prescribedBy})", fontSize = 10.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }
            }

            Spacer(Modifier.height(12.dp))

            // Notes
            Surface(shape = RoundedCornerShape(20.dp), color = CardWhite, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                Text("Notes", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                GolazoTextField(value = notes, onValueChange = { notes = it }, label = "Additional Notes", singleLine = false, maxLines = 4)
            }
            }

            Spacer(Modifier.height(12.dp))

            // Terms & Signature
            Surface(shape = RoundedCornerShape(20.dp), color = CardWhite, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                Text("Terms & Signature", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = termsAccepted,
                        onCheckedChange = { termsAccepted = it },
                        colors = CheckboxDefaults.colors(checkedColor = UefaBlue)
                    )
                    Text("I accept the terms and conditions for this PCME record", fontSize = 11.sp)
                }
                Spacer(Modifier.height(8.dp))
                GolazoTextField(value = signature, onValueChange = { signature = it }, label = "Doctor Signature (Full Name)")
            }
            }

            Spacer(Modifier.height(16.dp))

            GolazoButton(
                text = "Create PCME Record",
                onClick = {
                    viewModel.createPcmeEntry(
                        PcmeEntry(
                            userId = userId,
                            recordedBy = viewModel.sessionManager.userId,
                            recordedAt = java.time.Instant.now().toString(),
                            bloodType = bloodType,
                            height = height.ifBlank { null },
                            weight = weight.ifBlank { null },
                            ecgStatus = ecgStatus.ifBlank { null },
                            echoStatus = echoStatus.ifBlank { null },
                            scatScore = scatScore.toIntOrNull(),
                            asthma = asthma.ifBlank { null },
                            hepatitisB = hepatitisB.ifBlank { null },
                            tetanusStatus = tetanusStatus.ifBlank { null },
                            allergies = allergies.ifBlank { null },
                            prescriptions = prescriptions.toList(),
                            notes = notes.ifBlank { null },
                            termsAccepted = termsAccepted,
                            signatureData = signature.ifBlank { null }
                        ),
                        onSuccess = onCreated
                    )
                },
                enabled = termsAccepted && signature.isNotBlank()
            )

            Spacer(Modifier.height(80.dp))
        }
    }

    // Add Prescription Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Add Prescription", fontSize = 14.sp) },
            text = {
                Column {
                    GolazoTextField(value = newPrescName, onValueChange = { newPrescName = it }, label = "Medication Name")
                    Spacer(Modifier.height(8.dp))
                    GolazoTextField(value = newPrescDosage, onValueChange = { newPrescDosage = it }, label = "Dosage")
                    Spacer(Modifier.height(8.dp))
                    GolazoTextField(value = newPrescFreq, onValueChange = { newPrescFreq = it }, label = "Frequency")
                    Spacer(Modifier.height(8.dp))
                    GolazoTextField(value = newPrescBy, onValueChange = { newPrescBy = it }, label = "Prescribed By")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newPrescName.isNotBlank()) {
                        prescriptions.add(Prescription(newPrescName, newPrescDosage, newPrescFreq, newPrescBy))
                        newPrescName = ""; newPrescDosage = ""; newPrescFreq = ""; newPrescBy = ""
                        showImportDialog = false
                    }
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) { Text("Cancel") }
            }
        )
    }
}
