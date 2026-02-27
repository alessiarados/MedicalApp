package com.golazo.medical.ui.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
    onBack: () -> Unit,
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
        containerColor = MaterialTheme.colorScheme.background
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
                modifier = Modifier.statusBarsPadding(),
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp,
                    top = 8.dp,
                    bottom = padding.calculateBottomPadding() + 90.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground)
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("PCME Records", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                            Text("${entries.size} records", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
                items(entries) { entry ->
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable { onEntryClick(entry.id) },
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 4.dp
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(40.dp)) {
                                Icon(Icons.Default.Assignment, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(8.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("PCME Record", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text(entry.recordedAt.take(10), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                                        Text(entry.bloodType, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                    }
                                    entry.height?.let { Text("${it} cm", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                                    entry.weight?.let { Text("${it} kg", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                                }
                            }
                            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }

    if (showPlayerPicker) {
        AlertDialog(
            onDismissRequest = { showPlayerPicker = false },
            title = { Text("Select Player for PCME", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface) },
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
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPlayerPicker = false }) { Text("Cancel", color = MaterialTheme.colorScheme.primary) }
            }
        )
    }
}
