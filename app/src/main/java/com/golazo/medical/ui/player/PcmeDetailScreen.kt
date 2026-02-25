package com.golazo.medical.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*

@Composable
fun PcmeDetailScreen(
    entryId: String,
    onBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val entry by viewModel.currentPcme.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(entryId) { viewModel.loadPcmeDetail(entryId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        GolazoTopBar(title = "PCME Detail", onBack = onBack)

        if (isLoading && entry == null) {
            LoadingScreen()
        } else {
            entry?.let { pcme ->
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // General
                    item {
                        PcmeSection("General Information", Icons.Default.Person) {
                            PcmeRow("Blood Type", pcme.bloodType)
                            pcme.height?.let { PcmeRow("Height", "${it} cm") }
                            pcme.weight?.let { PcmeRow("Weight", "${it} kg") }
                            PcmeRow("Recorded", pcme.recordedAt.take(10))
                        }
                    }

                    // Cardiac
                    item {
                        PcmeSection("Cardiac", Icons.Default.Favorite) {
                            pcme.ecgStatus?.let { PcmeRow("ECG Status", it) }
                            pcme.echoStatus?.let { PcmeRow("Echo Status", it) }
                        }
                    }

                    // Concussion
                    item {
                        PcmeSection("Concussion (SCAT)", Icons.Default.Psychology) {
                            pcme.scatScore?.let { PcmeRow("SCAT Score", "$it") }
                            pcme.scatDate?.let { PcmeRow("SCAT Date", it) }
                        }
                    }

                    // Vaccinations
                    if (pcme.vaccinePassport.isNotEmpty()) {
                        item {
                            PcmeSection("Vaccine Passport", Icons.Default.Vaccines) {
                                pcme.vaccinePassport.forEach { v ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp)
                                    ) {
                                        Text(v.vaccine, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                        Text(v.date, fontSize = 11.sp, color = TextSecondary)
                                    }
                                }
                            }
                        }
                    }

                    // Medical Conditions
                    if (pcme.medicalConditions.isNotEmpty()) {
                        item {
                            PcmeSection("Medical Conditions", Icons.Default.MedicalInformation) {
                                pcme.medicalConditions.forEach { c ->
                                    Text(c.condition, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    Text(c.notes, fontSize = 11.sp, color = TextSecondary)
                                    Spacer(Modifier.height(4.dp))
                                }
                            }
                        }
                    }

                    // Medications
                    if (pcme.currentMedications.isNotEmpty()) {
                        item {
                            PcmeSection("Current Medications", Icons.Default.Medication) {
                                pcme.currentMedications.forEach { m ->
                                    PcmeRow(m.medication, "${m.dosage} - ${m.frequency}")
                                }
                            }
                        }
                    }

                    // Prescriptions
                    if (pcme.prescriptions.isNotEmpty()) {
                        item {
                            PcmeSection("Prescriptions", Icons.Default.Receipt) {
                                pcme.prescriptions.forEach { p ->
                                    Text(p.name, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    Text("${p.dosage} - ${p.frequency} (by ${p.prescribedBy})", fontSize = 11.sp, color = TextSecondary)
                                    Spacer(Modifier.height(4.dp))
                                }
                            }
                        }
                    }

                    // Allergies
                    pcme.allergies?.let {
                        item {
                            PcmeSection("Allergies", Icons.Default.Warning) {
                                Text(it, fontSize = 12.sp)
                            }
                        }
                    }

                    // Other
                    item {
                        PcmeSection("Other", Icons.Default.MoreHoriz) {
                            pcme.asthma?.let { PcmeRow("Asthma", it) }
                            pcme.hepatitisB?.let { PcmeRow("Hepatitis B", it) }
                            pcme.tetanusStatus?.let { PcmeRow("Tetanus", it) }
                            pcme.lastVaccineDate?.let { PcmeRow("Last Vaccine", it) }
                        }
                    }

                    // Signature
                    if (pcme.termsAccepted) {
                        item {
                            PcmeSection("Signature", Icons.Default.Draw) {
                                PcmeRow("Terms Accepted", pcme.termsAcceptedAt?.take(10) ?: "Yes")
                                pcme.signatureData?.let {
                                    Text("Signature: ${it.take(20)}...", fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                        }
                    }

                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun PcmeSection(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = CardWhite,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = UefaBlue, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun PcmeRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Text(label, fontSize = 11.sp, color = TextSecondary, modifier = Modifier.weight(1f))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
