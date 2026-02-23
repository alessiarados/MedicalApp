package com.golazo.medical.ui.player

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
fun PcmeListScreen(
    onEntryClick: (String) -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val entries by viewModel.pcmeEntries.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadPcmeEntries() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        GolazoTopBar(title = "PCME Records")

        if (isLoading) {
            LoadingScreen()
        } else if (entries.isEmpty()) {
            EmptyState(
                icon = Icons.Default.MedicalServices,
                title = "No PCME records",
                subtitle = "Your pre-competition medical examinations will appear here"
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
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
                                Text(entry.recordedAt, fontSize = 11.sp, color = TextSecondary)
                            }
                            Icon(Icons.Default.ChevronRight, null, tint = TextSecondary)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
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
                            entry.scatScore?.let {
                                Column {
                                    Text("SCAT", fontSize = 10.sp, color = TextSecondary)
                                    Text("$it", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}
