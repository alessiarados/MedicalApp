package com.golazo.medical.ui.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*

@Composable
fun DoctorPcmeListScreen(
    onEntryClick: (String) -> Unit,
    onCreatePcme: (String) -> Unit,
    viewModel: DoctorViewModel = hiltViewModel()
) {
    val entries by viewModel.pcmeEntries.collectAsStateWithLifecycle()
    val players by viewModel.players.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var showPlayerPicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadPcmeEntries()
        viewModel.loadPlayers()
    }

    Scaffold(
        topBar = { GolazoTopBar(title = "PCME Records") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showPlayerPicker = true },
                containerColor = UefaBlue,
                contentColor = White
            ) {
                Icon(Icons.Default.Add, "New PCME")
            }
        },
        containerColor = BackgroundGray
    ) { padding ->
        if (isLoading) {
            LoadingScreen(modifier = Modifier.padding(padding))
        } else if (entries.isEmpty()) {
            EmptyState(
                icon = Icons.Default.MedicalServices,
                title = "No PCME records",
                subtitle = "Tap + to create a new PCME",
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp,
                    top = padding.calculateTopPadding() + 8.dp,
                    bottom = padding.calculateBottomPadding() + 80.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(entries) { entry ->
                    GolazoCard(
                        modifier = Modifier.clickable { onEntryClick(entry.id) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("PCME Record", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Patient: ${entry.userId.take(8)}...", fontSize = 10.sp, color = TextSecondary)
                                Text(entry.recordedAt, fontSize = 10.sp, color = TextSecondary)
                            }
                            Icon(Icons.Default.ChevronRight, null, tint = TextSecondary)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column {
                                Text("Blood Type", fontSize = 10.sp, color = TextSecondary)
                                Text(entry.bloodType, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            entry.height?.let {
                                Column {
                                    Text("Height", fontSize = 10.sp, color = TextSecondary)
                                    Text(it, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            entry.weight?.let {
                                Column {
                                    Text("Weight", fontSize = 10.sp, color = TextSecondary)
                                    Text(it, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showPlayerPicker) {
        AlertDialog(
            onDismissRequest = { showPlayerPicker = false },
            title = { Text("Select Player for PCME", fontSize = 14.sp) },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(players) { pw ->
                        TextButton(
                            onClick = {
                                showPlayerPicker = false
                                pw.user?.id?.let { onCreatePcme(it) }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "${pw.profile?.firstName} ${pw.profile?.lastName} (${pw.profile?.club})",
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPlayerPicker = false }) { Text("Cancel") }
            }
        )
    }
}
