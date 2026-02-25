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
fun DoctorPcmeHistoryScreen(
    userId: String,
    onBack: () -> Unit,
    onEntryClick: (String) -> Unit,
    viewModel: DoctorViewModel = hiltViewModel()
) {
    val entries by viewModel.pcmeEntries.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(userId) { viewModel.loadPlayerPcmeHistory(userId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        GolazoTopBar(title = "PCME History", onBack = onBack)

        if (isLoading) {
            LoadingScreen()
        } else if (entries.isEmpty()) {
            EmptyState(
                icon = Icons.Default.MedicalServices,
                title = "No PCME history for this player"
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(entries) { entry ->
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable { onEntryClick(entry.id) },
                        shape = RoundedCornerShape(20.dp),
                        color = CardWhite,
                        shadowElevation = 4.dp
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = UefaBlueVeryLight, modifier = Modifier.size(40.dp)) {
                                Icon(Icons.Default.Assignment, null, tint = UefaBlue, modifier = Modifier.padding(8.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("PCME Record", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(entry.recordedAt.take(10), fontSize = 10.sp, color = TextSecondary)
                                Spacer(Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Surface(shape = RoundedCornerShape(8.dp), color = UefaBlueVeryLight) {
                                        Text(entry.bloodType, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = UefaBlue, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                    }
                                    entry.height?.let { Text("$it cm", fontSize = 10.sp, color = TextSecondary) }
                                    entry.weight?.let { Text("$it kg", fontSize = 10.sp, color = TextSecondary) }
                                }
                            }
                            Icon(Icons.Default.ChevronRight, null, tint = TextSecondary)
                        }
                    }
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}
