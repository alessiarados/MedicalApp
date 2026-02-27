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
fun DoctorInjuriesScreen(
    onInjuryClick: (String) -> Unit,
    onCreateInjury: () -> Unit,
    onBack: () -> Unit,
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
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .statusBarsPadding(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp)
        ) {
            // Header with back button
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground)
                    }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("All Injuries", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Text("${injuries.size} total injuries", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            // Filters
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = filterStatus == null && filterSeverity == null,
                        onClick = { filterStatus = null; filterSeverity = null },
                        label = { Text("All", fontSize = 10.sp, color = if (filterStatus == null && filterSeverity == null) White else MaterialTheme.colorScheme.onSurface) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = UefaBlue,
                            selectedLabelColor = White,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    FilterChip(
                        selected = filterStatus == "open",
                        onClick = { filterStatus = if (filterStatus == "open") null else "open" },
                        label = { Text("Open", fontSize = 10.sp, color = if (filterStatus == "open") White else MaterialTheme.colorScheme.onSurface) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StatusOpen,
                            selectedLabelColor = White,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    FilterChip(
                        selected = filterStatus == "closed",
                        onClick = { filterStatus = if (filterStatus == "closed") null else "closed" },
                        label = { Text("Closed", fontSize = 10.sp, color = if (filterStatus == "closed") White else MaterialTheme.colorScheme.onSurface) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StatusClosed,
                            selectedLabelColor = White,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        )
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
                            label = { Text(sev.replaceFirstChar { it.uppercase() }, fontSize = 10.sp, color = if (filterSeverity == sev) White else MaterialTheme.colorScheme.onSurface) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = color,
                                selectedLabelColor = White,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            if (isLoading) {
                item { LoadingScreen() }
            } else if (filtered.isEmpty()) {
                item { EmptyState(icon = Icons.Default.HealthAndSafety, title = "No injuries found") }
            } else {
                items(filtered) { injury ->
                    val sevColor = when (injury.severity) {
                        "minor" -> SeverityMinor
                        "moderate" -> SeverityModerate
                        else -> SeveritySevere
                    }
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onInjuryClick(injury.id) },
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 4.dp
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                            Surface(shape = CircleShape, color = sevColor.copy(alpha = 0.12f), modifier = Modifier.size(40.dp)) {
                                Icon(Icons.Default.Warning, null, tint = sevColor, modifier = Modifier.padding(8.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(injury.bodyArea, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text(injury.mechanism, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                Spacer(Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(shape = RoundedCornerShape(8.dp), color = sevColor.copy(alpha = 0.12f)) {
                                        Text(injury.severity.replaceFirstChar { it.uppercase() }, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = sevColor, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                    }
                                    Surface(shape = RoundedCornerShape(8.dp), color = if (injury.status == "open") StatusOpen.copy(alpha = 0.12f) else StatusClosed.copy(alpha = 0.12f)) {
                                        Text(injury.status.replaceFirstChar { it.uppercase() }, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = if (injury.status == "open") StatusOpen else StatusClosed, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                    }
                                }
                                Text("Created by: ${injury.createdBy}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}
