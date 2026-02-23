package com.golazo.medical.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HealthAndSafety
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
fun InjuryHistoryScreen(
    onBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val injuries by viewModel.injuries.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadInjuries() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        GolazoTopBar(title = "Injury History", onBack = onBack)

        if (injuries.isEmpty()) {
            EmptyState(
                icon = Icons.Default.HealthAndSafety,
                title = "No injury history"
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val sorted = injuries.sortedByDescending { it.createdAt }
                items(sorted) { injury ->
                    GolazoCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(injury.bodyArea, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(injury.mechanism, fontSize = 11.sp, color = TextSecondary, maxLines = 1)
                            }
                            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                                SeverityBadge(injury.severity)
                                Spacer(Modifier.height(4.dp))
                                StatusBadge(injury.status)
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            RtpBadge(injury.rtpStatus)
                            Text(injury.injuryDate ?: injury.createdAt ?: "", fontSize = 10.sp, color = TextSecondary)
                        }
                    }
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}
