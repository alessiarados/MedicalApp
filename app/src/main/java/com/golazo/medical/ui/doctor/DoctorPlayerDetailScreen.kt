package com.golazo.medical.ui.doctor

import androidx.compose.foundation.background
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
            .background(MaterialTheme.colorScheme.background)
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
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = UefaBlue,
                            shadowElevation = 4.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    d.profile?.let { p ->
                                        ProfileAvatar(imageUrl = p.imageUrl, name = "${p.firstName} ${p.lastName}", size = 56, fallbackColor = White)
                                        Spacer(Modifier.width(16.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("${p.firstName} ${p.lastName}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = White)
                                            Text("${p.club} • ${p.position}", fontSize = 12.sp, color = White.copy(alpha = 0.8f))
                                            Text("${p.nationality} • DOB: ${p.dob}", fontSize = 11.sp, color = White.copy(alpha = 0.7f))
                                            Spacer(Modifier.height(8.dp))
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                StatusBadge(p.status, p.status == "active")
                                                PcmeStatusBadge(p.pcmeStatus)
                                            }
                                        }
                                    }
                                }
                                if (d.profile?.status == "inactive" || d.profile?.status == "pending_consent") {
                                    Spacer(Modifier.height(12.dp))
                                    GolazoOutlinedButton(
                                        text = "Invite Player",
                                        onClick = { showInviteDialog = true }
                                    )
                                }
                            }
                        }
                    }

                    // Injuries
                    item {
                        Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocalHospital, null, tint = SeveritySevere, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Injuries (${d.injuries.size})", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                }
                                Spacer(Modifier.height(12.dp))
                                if (d.injuries.isEmpty()) {
                                    Text("No injuries recorded", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                } else {
                                    d.injuries.take(5).forEach { injury ->
                                        val sevColor = when (injury.severity) { "minor" -> SeverityMinor; "moderate" -> SeverityModerate; else -> SeveritySevere }
                                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Surface(shape = CircleShape, color = sevColor.copy(alpha = 0.12f), modifier = Modifier.size(32.dp)) {
                                                Icon(Icons.Default.Warning, null, tint = sevColor, modifier = Modifier.padding(6.dp))
                                            }
                                            Spacer(Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(injury.bodyArea, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                                Text(injury.mechanism, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                            }
                                            Surface(shape = RoundedCornerShape(8.dp), color = sevColor.copy(alpha = 0.12f)) {
                                                Text(injury.severity.replaceFirstChar { it.uppercase() }, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = sevColor, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // PCME History
                    item {
                        Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.MedicalServices, null, tint = UefaBlue, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("PCME History (${d.pcmeEntries.size})", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                }
                                Spacer(Modifier.height(12.dp))
                                if (d.pcmeEntries.isEmpty()) {
                                    Text("No PCME records", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                } else {
                                    d.pcmeEntries.take(3).forEach { entry ->
                                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Surface(shape = CircleShape, color = UefaBlueVeryLight, modifier = Modifier.size(32.dp)) {
                                                Icon(Icons.Default.Assignment, null, tint = UefaBlue, modifier = Modifier.padding(6.dp))
                                            }
                                            Spacer(Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("PCME Record", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                                Text(entry.recordedAt.take(10), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Text("Blood: ${entry.bloodType}", fontSize = 11.sp, color = UefaBlue, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Training Sessions
                    item {
                        Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.FitnessCenter, null, tint = SeverityModerate, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Training (${d.trainingSessions.size})", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                }
                                Spacer(Modifier.height(12.dp))
                                if (d.trainingSessions.isEmpty()) {
                                    Text("No training sessions", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                } else {
                                    d.trainingSessions.take(3).forEach { session ->
                                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Surface(shape = CircleShape, color = SeverityModerate.copy(alpha = 0.12f), modifier = Modifier.size(32.dp)) {
                                                Icon(Icons.Default.DirectionsRun, null, tint = SeverityModerate, modifier = Modifier.padding(6.dp))
                                            }
                                            Spacer(Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(session.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                                Text("${session.date} • ${session.timeOfDay}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Surface(shape = RoundedCornerShape(8.dp), color = UefaBlueVeryLight) {
                                                Text("${session.duration} min", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = UefaBlue, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                            }
                                        }
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
