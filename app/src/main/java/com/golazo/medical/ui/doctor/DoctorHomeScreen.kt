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
fun DoctorHomeScreen(
    onViewPlayers: () -> Unit,
    onViewInjuries: () -> Unit,
    onViewPcme: () -> Unit,
    onViewTraining: () -> Unit,
    viewModel: DoctorViewModel = hiltViewModel()
) {
    val players by viewModel.players.collectAsStateWithLifecycle()
    val injuries by viewModel.injuries.collectAsStateWithLifecycle()
    val pcmeEntries by viewModel.pcmeEntries.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadPlayers()
        viewModel.loadInjuries()
        viewModel.loadPcmeEntries()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Header
        item {
            Surface(
                color = UefaBlue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(20.dp)
                ) {
                    Text("Doctor Dashboard", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = White)
                    Text("Medical Staff Overview", fontSize = 12.sp, color = White.copy(alpha = 0.8f))
                }
            }
        }

        // Stats Overview
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard("Players", "${players.size}", Icons.Default.People, Modifier.weight(1f))
                StatCard("Injuries", "${injuries.size}", Icons.Default.LocalHospital, Modifier.weight(1f))
                StatCard("PCMEs", "${pcmeEntries.size}", Icons.Default.MedicalServices, Modifier.weight(1f))
            }
        }

        // Open Injuries
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                SectionHeader("Open Injuries")
                val openInjuries = injuries.filter { it.status == "open" }.take(3)
                if (openInjuries.isEmpty()) {
                    GolazoCard {
                        Text("No open injuries", fontSize = 12.sp, color = TextSecondary)
                    }
                } else {
                    openInjuries.forEach { injury ->
                        GolazoCard(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(injury.bodyArea, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(injury.mechanism, fontSize = 10.sp, color = TextSecondary, maxLines = 1)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    SeverityBadge(injury.severity)
                                    Spacer(Modifier.height(4.dp))
                                    RtpBadge(injury.rtpStatus)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Actions
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                SectionHeader("Quick Actions")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickAction("View Players", Icons.Default.People, onViewPlayers, Modifier.weight(1f))
                    QuickAction("Create PCME", Icons.Default.MedicalServices, onViewPcme, Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickAction("View Injuries", Icons.Default.LocalHospital, onViewInjuries, Modifier.weight(1f))
                    QuickAction("Log Training", Icons.Default.FitnessCenter, onViewTraining, Modifier.weight(1f))
                }
            }
        }

        // Upcoming PCMEs
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                SectionHeader("Players Needing PCME")
                val needingPcme = players.filter {
                    it.profile?.pcmeStatus in listOf("missing", "late", "expected")
                }.take(3)
                if (needingPcme.isEmpty()) {
                    GolazoCard {
                        Text("All players up to date", fontSize = 12.sp, color = TextSecondary)
                    }
                } else {
                    needingPcme.forEach { pw ->
                        pw.profile?.let { p ->
                            GolazoCard(modifier = Modifier.padding(vertical = 4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    InitialsAvatar("${p.firstName} ${p.lastName}", UefaBlue, 36)
                                    Spacer(Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${p.firstName} ${p.lastName}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        Text("${p.club} • ${p.position}", fontSize = 10.sp, color = TextSecondary)
                                    }
                                    PcmeStatusBadge(p.pcmeStatus)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    GolazoCard(modifier = modifier) {
        Icon(icon, null, tint = UefaBlue, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(8.dp))
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = UefaBlue)
        Text(label, fontSize = 10.sp, color = TextSecondary)
    }
}

@Composable
private fun QuickAction(label: String, icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = UefaBlueVeryLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = UefaBlue, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(8.dp))
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = UefaBlue)
        }
    }
}
