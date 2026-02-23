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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.golazo.medical.data.SimulatedData
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*

@Composable
fun FindHelpScreen(
    onBack: () -> Unit
) {
    var selectedPsychologist by remember { mutableStateOf<SimulatedData.Psychologist?>(null) }
    var selectedSessionType by remember { mutableStateOf("") }
    var selectedSlot by remember { mutableStateOf("") }
    var bookingConfirmed by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(UefaBlue)
                    .statusBarsPadding()
            ) {
                GolazoTopBar(title = "Find Help", onBack = onBack)
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    Text("Your Privacy Is Absolute", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = White)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Sessions are NOT routed through your club medical department",
                        fontSize = 11.sp,
                        color = White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Privacy Badges
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Icons.Default.Lock to "End-to-End\nEncrypted",
                    Icons.Default.VisibilityOff to "Not Visible\nto Club",
                    Icons.Default.VerifiedUser to "Licensed\nProfessionals"
                ).forEach { (icon, label) ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = UefaBlueVeryLight,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(icon, null, tint = UefaBlue, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.height(4.dp))
                            Text(label, fontSize = 9.sp, color = UefaBlue, textAlign = TextAlign.Center, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }

        // Your Sessions
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                SectionHeader("Your Sessions")
                GolazoCard {
                    if (SimulatedData.upcomingSessions.isEmpty()) {
                        Text("No upcoming sessions", fontSize = 12.sp, color = TextSecondary)
                    } else {
                        SimulatedData.upcomingSessions.forEach { session ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(session.psychologist, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    Text("${session.date}, ${session.time} • ${session.type}", fontSize = 10.sp, color = TextSecondary)
                                }
                                if (session.isToday) {
                                    Button(
                                        onClick = {},
                                        colors = ButtonDefaults.buttonColors(containerColor = SeverityMinor),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Join", fontSize = 10.sp)
                                    }
                                }
                            }
                            if (session != SimulatedData.upcomingSessions.last()) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                    }
                }
            }
        }

        // Psychologist Directory
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                SectionHeader("Available Professionals")
            }
        }

        items(SimulatedData.psychologists) { psych ->
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                GolazoCard(
                    modifier = Modifier.clickable {
                        selectedPsychologist = if (selectedPsychologist?.id == psych.id) null else psych
                        selectedSessionType = ""
                        selectedSlot = ""
                        bookingConfirmed = false
                    }
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        // Avatar
                        Box {
                            InitialsAvatar(psych.name, Color(psych.color), 44)
                            if (psych.available) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(SeverityMinor)
                                        .align(Alignment.BottomEnd)
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(psych.name, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(psych.title, fontSize = 10.sp, color = TextSecondary)
                            Spacer(Modifier.height(4.dp))
                            // Specialties
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                psych.specialties.forEach { spec ->
                                    Surface(shape = RoundedCornerShape(4.dp), color = UefaBlueVeryLight) {
                                        Text(spec, fontSize = 9.sp, color = UefaBlue, modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp))
                                    }
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${psych.languages.joinToString(", ")}", fontSize = 9.sp, color = TextSecondary)
                                Spacer(Modifier.width(8.dp))
                                repeat(psych.rating.toInt()) {
                                    Icon(Icons.Default.Star, null, tint = SeverityModerate, modifier = Modifier.size(10.dp))
                                }
                                Text(" ${psych.rating}", fontSize = 9.sp, color = TextSecondary)
                                Spacer(Modifier.width(8.dp))
                                Text("${psych.sessionCount} sessions", fontSize = 9.sp, color = TextSecondary)
                            }
                            Spacer(Modifier.height(2.dp))
                            Text("Next: ${psych.nextSlot}", fontSize = 10.sp, color = if (psych.available) SeverityMinor else TextSecondary)
                        }
                    }

                    // Booking panel
                    if (selectedPsychologist?.id == psych.id) {
                        Spacer(Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(12.dp))

                        if (bookingConfirmed) {
                            Surface(shape = RoundedCornerShape(8.dp), color = SeverityMinor.copy(alpha = 0.15f)) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, null, tint = SeverityMinor, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Booking confirmed! $selectedSessionType at $selectedSlot", fontSize = 11.sp, color = SeverityMinor)
                                }
                            }
                        } else {
                            Text("Session Type", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Text Chat" to Icons.Default.Chat, "Voice Call" to Icons.Default.Phone, "Video" to Icons.Default.Videocam).forEach { (type, icon) ->
                                    FilterChip(
                                        selected = selectedSessionType == type,
                                        onClick = { selectedSessionType = type },
                                        label = { Text(type, fontSize = 10.sp) },
                                        leadingIcon = { Icon(icon, null, modifier = Modifier.size(14.dp)) },
                                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = UefaBlue, selectedLabelColor = White)
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Available Slots", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                psych.availableSlots.forEach { slot ->
                                    FilterChip(
                                        selected = selectedSlot == slot,
                                        onClick = { selectedSlot = slot },
                                        label = { Text(slot, fontSize = 10.sp) },
                                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = UefaBlue, selectedLabelColor = White)
                                    )
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            GolazoButton(
                                text = "Confirm Booking",
                                onClick = { bookingConfirmed = true },
                                enabled = selectedSessionType.isNotBlank() && selectedSlot.isNotBlank()
                            )
                        }
                    }
                }
            }
        }

        // Privacy Reminder
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = UefaBlueVeryLight
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Shield, null, tint = UefaBlue, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "All sessions are confidential and protected. Your club and team staff cannot access any information from these sessions.",
                            fontSize = 10.sp,
                            color = UefaBlue
                        )
                    }
                }
            }
        }
    }
}
