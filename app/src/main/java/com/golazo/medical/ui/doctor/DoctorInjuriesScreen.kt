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
fun DoctorInjuriesScreen(
    onInjuryClick: (String) -> Unit,
    onCreateInjury: () -> Unit,
    viewModel: DoctorViewModel = hiltViewModel()
) {
    val injuries by viewModel.injuries.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var filterStatus by remember { mutableStateOf<String?>(null) }
    var filterSeverity by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { viewModel.loadInjuries() }

    val filtered = injuries.filter { inj ->
        (filterStatus == null || inj.status == filterStatus) &&
        (filterSeverity == null || inj.severity == filterSeverity)
    }

    Scaffold(
        topBar = { GolazoTopBar(title = "All Injuries") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateInjury,
                containerColor = UefaBlue,
                contentColor = White
            ) {
                Icon(Icons.Default.Add, "New Injury")
            }
        },
        containerColor = BackgroundGray
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = filterStatus == null,
                    onClick = { filterStatus = null },
                    label = { Text("All", fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = UefaBlue, selectedLabelColor = White)
                )
                FilterChip(
                    selected = filterStatus == "open",
                    onClick = { filterStatus = if (filterStatus == "open") null else "open" },
                    label = { Text("Open", fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = StatusOpen, selectedLabelColor = White)
                )
                FilterChip(
                    selected = filterStatus == "closed",
                    onClick = { filterStatus = if (filterStatus == "closed") null else "closed" },
                    label = { Text("Closed", fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = StatusClosed, selectedLabelColor = White)
                )
                listOf("minor", "moderate", "severe").forEach { sev ->
                    val color = when (sev) {
                        "minor" -> SeverityMinor
                        "moderate" -> SeverityModerate
                        else -> SeveritySevere
                    }
                    FilterChip(
                        selected = filterSeverity == sev,
                        onClick = { filterSeverity = if (filterSeverity == sev) null else sev },
                        label = { Text(sev.replaceFirstChar { it.uppercase() }, fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = color, selectedLabelColor = White)
                    )
                }
            }

            if (isLoading) {
                LoadingScreen()
            } else if (filtered.isEmpty()) {
                EmptyState(icon = Icons.Default.HealthAndSafety, title = "No injuries found")
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtered) { injury ->
                        GolazoCard(
                            modifier = Modifier.clickable { onInjuryClick(injury.id) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(injury.bodyArea, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(injury.mechanism, fontSize = 11.sp, color = TextSecondary, maxLines = 1)
                                    Spacer(Modifier.height(4.dp))
                                    Text("Created by: ${injury.createdBy}", fontSize = 10.sp, color = TextSecondary)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    SeverityBadge(injury.severity)
                                    Spacer(Modifier.height(4.dp))
                                    StatusBadge(injury.status)
                                    Spacer(Modifier.height(4.dp))
                                    RtpBadge(injury.rtpStatus)
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
