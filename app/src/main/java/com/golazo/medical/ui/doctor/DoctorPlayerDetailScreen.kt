package com.golazo.medical.ui.doctor

import androidx.compose.foundation.background
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
fun DoctorPlayerDetailScreen(
    userId: String,
    onBack: () -> Unit,
    viewModel: DoctorViewModel = hiltViewModel()
) {
    val detail by viewModel.playerDetail.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var showInviteDialog by remember { mutableStateOf(false) }
    var inviteEmail by remember { mutableStateOf("") }
    var invitePhone by remember { mutableStateOf("") }

    LaunchedEffect(userId) { viewModel.loadPlayerDetail(userId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        GolazoTopBar(title = "Player Detail", onBack = onBack)

        if (isLoading && detail == null) {
            LoadingScreen()
        } else {
            detail?.let { d ->
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Profile Card
                    item {
                        GolazoCard {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                d.profile?.let { p ->
                                    InitialsAvatar("${p.firstName} ${p.lastName}", UefaBlue, 56)
                                    Spacer(Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${p.firstName} ${p.lastName}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        Text("${p.club} • ${p.position}", fontSize = 12.sp, color = TextSecondary)
                                        Text("${p.nationality} • DOB: ${p.dob}", fontSize = 11.sp, color = TextSecondary)
                                        Spacer(Modifier.height(4.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            StatusBadge(p.status, p.status == "active")
                                            PcmeStatusBadge(p.pcmeStatus)
                                        }
                                    }
                                }
                            }

                            // Invite button for inactive players
                            if (d.profile?.status == "inactive" || d.profile?.status == "pending_consent") {
                                Spacer(Modifier.height(12.dp))
                                GolazoOutlinedButton(
                                    text = "Invite Player",
                                    onClick = { showInviteDialog = true }
                                )
                            }
                        }
                    }

                    // Injuries
                    item {
                        SectionHeader("Injuries (${d.injuries.size})")
                    }
                    if (d.injuries.isEmpty()) {
                        item {
                            GolazoCard { Text("No injuries recorded", fontSize = 12.sp, color = TextSecondary) }
                        }
                    } else {
                        items(d.injuries.take(5)) { injury ->
                            GolazoCard {
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

                    // PCME History
                    item {
                        SectionHeader("PCME History (${d.pcmeEntries.size})")
                    }
                    if (d.pcmeEntries.isEmpty()) {
                        item {
                            GolazoCard { Text("No PCME records", fontSize = 12.sp, color = TextSecondary) }
                        }
                    } else {
                        items(d.pcmeEntries.take(3)) { entry ->
                            GolazoCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("PCME Record", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        Text(entry.recordedAt, fontSize = 10.sp, color = TextSecondary)
                                    }
                                    Text("Blood: ${entry.bloodType}", fontSize = 11.sp, color = UefaBlue)
                                }
                            }
                        }
                    }

                    // Training Sessions
                    item {
                        SectionHeader("Training Sessions (${d.trainingSessions.size})")
                    }
                    if (d.trainingSessions.isEmpty()) {
                        item {
                            GolazoCard { Text("No training sessions", fontSize = 12.sp, color = TextSecondary) }
                        }
                    } else {
                        items(d.trainingSessions.take(3)) { session ->
                            GolazoCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(session.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        Text("${session.date} • ${session.timeOfDay}", fontSize = 10.sp, color = TextSecondary)
                                    }
                                    Text("${session.duration} min", fontSize = 11.sp, color = UefaBlue)
                                }
                            }
                        }
                    }

                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }

    // Invite Dialog
    if (showInviteDialog) {
        AlertDialog(
            onDismissRequest = { showInviteDialog = false },
            title = { Text("Invite Player", fontSize = 16.sp) },
            text = {
                Column {
                    GolazoTextField(value = inviteEmail, onValueChange = { inviteEmail = it }, label = "Email")
                    Spacer(Modifier.height(8.dp))
                    GolazoTextField(value = invitePhone, onValueChange = { invitePhone = it }, label = "Phone Number")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.invitePlayer(userId, inviteEmail, invitePhone) {
                        showInviteDialog = false
                        viewModel.loadPlayerDetail(userId)
                    }
                }) { Text("Send Invite") }
            },
            dismissButton = {
                TextButton(onClick = { showInviteDialog = false }) { Text("Cancel") }
            }
        )
    }
}
