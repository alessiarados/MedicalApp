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
import com.golazo.medical.data.model.PcmeEntry
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun PcmeListScreen(
    onEntryClick: (String) -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val entries by viewModel.pcmeEntries.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val sortedEntries = remember(entries) {
        entries.sortedByDescending { it.recordedAt }
    }

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
                        Text("${sortedEntries.size} Record${if (sortedEntries.size != 1) "s" else ""}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = White)
                        Text(
                            if (sortedEntries.isNotEmpty()) "Last exam: ${sortedEntries.first().recordedAt.take(10)}" else "No records yet",
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
        } else if (sortedEntries.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Default.MedicalServices,
                    title = "No PCME records",
                    subtitle = "Your pre-competition medical examinations will appear here"
                )
            }
        } else {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.History, null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("PCME History", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = UefaBlueVeryLight
                    ) {
                        Text(
                            "${sortedEntries.size} records",
                            fontSize = 11.sp,
                            color = UefaBlue,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            items(sortedEntries) { entry ->
                PcmeHistoryCard(entry = entry, onClick = { onEntryClick(entry.id) })
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

private enum class PcmeUiStatus { InProgress, Expired }

private fun pcmeUiStatus(entry: PcmeEntry, now: LocalDate = LocalDate.now()): PcmeUiStatus {
    val recorded = entry.recordedAt.take(10)
    val recordedDate = runCatching { LocalDate.parse(recorded) }.getOrNull()
    val daysOld = recordedDate?.let { ChronoUnit.DAYS.between(it, now) }

    if (!entry.termsAccepted) return PcmeUiStatus.InProgress
    if (daysOld != null && daysOld > 365) return PcmeUiStatus.Expired
    return PcmeUiStatus.InProgress
}

private fun pcmeCompletionPercent(entry: PcmeEntry): Int {
    val checks = listOf(
        entry.bloodType.isNotBlank() && entry.bloodType != "unknown",
        !entry.height.isNullOrBlank(),
        !entry.weight.isNullOrBlank(),
        entry.scatScore != null,
        entry.ecgStatus != null,
        entry.echoStatus != null,
        entry.termsAccepted
    )
    val total = checks.size
    val done = checks.count { it }
    return ((done.toFloat() / total.toFloat()) * 100f).toInt().coerceIn(0, 100)
}

@Composable
private fun PcmeHistoryCard(entry: PcmeEntry, onClick: () -> Unit) {
    val status = remember(entry) { pcmeUiStatus(entry) }
    val percent = remember(entry) { pcmeCompletionPercent(entry) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = CardWhite,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = UefaBlueVeryLight, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.EventNote, null, tint = UefaBlue, modifier = Modifier.padding(8.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(entry.recordedAt.take(10), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = when (status) {
                        PcmeUiStatus.InProgress -> SeverityModerate.copy(alpha = 0.14f)
                        PcmeUiStatus.Expired -> SeveritySevere.copy(alpha = 0.14f)
                    }
                ) {
                    Text(
                        when (status) {
                            PcmeUiStatus.InProgress -> "In Progress"
                            PcmeUiStatus.Expired -> "Expired"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = when (status) {
                            PcmeUiStatus.InProgress -> SeverityModerate
                            PcmeUiStatus.Expired -> SeveritySevere
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Progress", fontSize = 11.sp, color = TextSecondary)
                Text("$percent%", fontSize = 11.sp, color = TextSecondary)
            }
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { percent / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = SeverityModerate,
                trackColor = BackgroundGray
            )

            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PcmeMiniStat("Blood", entry.bloodType)
                entry.scatScore?.let { PcmeMiniStat("SCAT", "$it/100") }
            }

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!entry.asthma.isNullOrBlank()) {
                    Surface(shape = RoundedCornerShape(50), color = UefaBlueVeryLight) {
                        Text("Asthma", fontSize = 10.sp, color = UefaBlue, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                }
                if (!entry.allergies.isNullOrBlank()) {
                    Surface(shape = RoundedCornerShape(50), color = UefaBlueVeryLight) {
                        Text("Allergies", fontSize = 10.sp, color = UefaBlue, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                }
            }
        }
    }
}
