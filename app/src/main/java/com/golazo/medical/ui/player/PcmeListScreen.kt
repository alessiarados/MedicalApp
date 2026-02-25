package com.golazo.medical.ui.player

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
fun PcmeListScreen(
    onEntryClick: (String) -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val entries by viewModel.pcmeEntries.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadPcmeEntries() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .statusBarsPadding(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp)
    ) {
        // Header
        item {
            Column {
                Text("PCME Records", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Pre-competition medical examinations", fontSize = 12.sp, color = TextSecondary)
            }
            Spacer(Modifier.height(16.dp))
        }

        // Status card
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = UefaBlue,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(shape = CircleShape, color = White.copy(alpha = 0.2f), modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Default.MedicalServices, null, tint = White, modifier = Modifier.padding(12.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("${entries.size} Record${if (entries.size != 1) "s" else ""}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = White)
                        Text(
                            if (entries.isNotEmpty()) "Last exam: ${entries.first().recordedAt.take(10)}" else "No records yet",
                            fontSize = 12.sp,
                            color = White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        if (isLoading) {
            item { LoadingScreen() }
        } else if (entries.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Default.MedicalServices,
                    title = "No PCME records",
                    subtitle = "Your pre-competition medical examinations will appear here"
                )
            }
        } else {
            items(entries) { entry ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEntryClick(entry.id) },
                    shape = RoundedCornerShape(20.dp),
                    color = CardWhite,
                    shadowElevation = 4.dp
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = UefaBlueVeryLight, modifier = Modifier.size(44.dp)) {
                            Icon(Icons.Default.Assignment, null, tint = UefaBlue, modifier = Modifier.padding(10.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("PCME Record", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(entry.recordedAt.take(10), fontSize = 11.sp, color = TextSecondary)
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                PcmeMiniStat("Blood", entry.bloodType)
                                entry.height?.let { PcmeMiniStat("Ht", "${it}cm") }
                                entry.weight?.let { PcmeMiniStat("Wt", "${it}kg") }
                                entry.scatScore?.let { PcmeMiniStat("SCAT", "$it") }
                            }
                        }
                        Icon(Icons.Default.ChevronRight, null, tint = TextSecondary)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun PcmeMiniStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = UefaBlue)
        Text(label, fontSize = 9.sp, color = TextSecondary)
    }
}
