package com.golazo.medical.ui.doctor

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
fun DoctorPcmeDetailScreen(
    entryId: String,
    onBack: () -> Unit,
    viewModel: DoctorViewModel = hiltViewModel()
) {
    val entry by viewModel.currentPcme.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(entryId) { viewModel.loadPcmeDetail(entryId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        GolazoTopBar(
            title = "PCME Detail",
            onBack = onBack,
            actions = {
                IconButton(onClick = { /* Edit mode placeholder */ }) {
                    Icon(Icons.Default.Edit, "Edit", tint = White)
                }
            }
        )

        if (isLoading && entry == null) {
            LoadingScreen()
        } else {
            entry?.let { pcme ->
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        DoctorPcmeSection("General Information", Icons.Default.Person) {
                            PcmeRow("Blood Type", pcme.bloodType)
                            pcme.height?.let { PcmeRow("Height", it) }
                            pcme.weight?.let { PcmeRow("Weight", it) }
                            PcmeRow("Recorded", pcme.recordedAt.take(10))
                            pcme.recordedBy?.let { PcmeRow("Recorded By", it.take(8) + "...") }
                        }
                    }

                    item {
                        DoctorPcmeSection("Cardiac", Icons.Default.MonitorHeart) {
                            pcme.ecgStatus?.let { PcmeRow("ECG Status", it) } ?: PcmeRow("ECG Status", "Not recorded")
                            pcme.echoStatus?.let { PcmeRow("Echo Status", it) } ?: PcmeRow("Echo Status", "Not recorded")
                        }
                    }

                    item {
                        DoctorPcmeSection("Concussion (SCAT)", Icons.Default.Psychology) {
                            pcme.scatScore?.let { PcmeRow("SCAT Score", "$it") } ?: PcmeRow("SCAT Score", "Not recorded")
                            pcme.scatDate?.let { PcmeRow("SCAT Date", it) }
                        }
                    }

                    if (pcme.vaccinePassport.isNotEmpty()) {
                        item {
                            DoctorPcmeSection("Vaccine Passport", Icons.Default.Vaccines) {
                                pcme.vaccinePassport.forEach { v ->
                                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                                        Text(v.vaccine, fontSize = 11.sp, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface)
                                        Text(v.date, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }

                    if (pcme.medicalConditions.isNotEmpty()) {
                        item {
                            DoctorPcmeSection("Medical Conditions", Icons.Default.LocalHospital) {
                                pcme.medicalConditions.forEach { c ->
                                    Text(c.condition, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                                    Text(c.notes, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(Modifier.height(4.dp))
                                }
                            }
                        }
                    }

                    if (pcme.currentMedications.isNotEmpty()) {
                        item {
                            DoctorPcmeSection("Current Medications", Icons.Default.Medication) {
                                pcme.currentMedications.forEach { m ->
                                    PcmeRow(m.medication, "${m.dosage} - ${m.frequency}")
                                }
                            }
                        }
                    }

                    if (pcme.prescriptions.isNotEmpty()) {
                        item {
                            Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Receipt, null, tint = UefaBlue, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text("Prescriptions", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                    TextButton(onClick = { /* Import prescriptions */ }) {
                                        Text("Import", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                                pcme.prescriptions.forEach { p ->
                                    Text(p.name, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                                    Text("${p.dosage} - ${p.frequency} (by ${p.prescribedBy})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(Modifier.height(4.dp))
                                }
                            }
                            }
                        }
                    }

                    pcme.allergies?.let {
                        item {
                            DoctorPcmeSection("Allergies", Icons.Default.Warning) {
                                Text(it, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    item {
                        DoctorPcmeSection("Other", Icons.Default.Info) {
                            pcme.asthma?.let { PcmeRow("Asthma", it) }
                            pcme.hepatitisB?.let { PcmeRow("Hepatitis B", it) }
                            pcme.tetanusStatus?.let { PcmeRow("Tetanus", it) }
                            pcme.lastVaccineDate?.let { PcmeRow("Last Vaccine", it) }
                        }
                    }

                    if (pcme.termsAccepted) {
                        item {
                            DoctorPcmeSection("Signature", Icons.Default.Draw) {
                                PcmeRow("Terms Accepted", pcme.termsAcceptedAt ?: "Yes")
                                pcme.signatureData?.let {
                                    Text("Signature: ${it.take(20)}...", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
private fun DoctorPcmeSection(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = UefaBlue, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
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
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
    }
}
