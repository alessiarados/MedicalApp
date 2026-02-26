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
fun InjuryHistoryScreen(
    onBack: () -> Unit,
    onInjuryClick: (String) -> Unit,
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val sorted = injuries.sortedByDescending { it.createdAt }
                items(sorted) { injury ->
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
                        color = CardWhite,
                        shadowElevation = 4.dp
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = sevColor.copy(alpha = 0.12f), modifier = Modifier.size(40.dp)) {
                                Icon(Icons.Default.History, null, tint = sevColor, modifier = Modifier.padding(8.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(injury.bodyArea, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(injury.mechanism, fontSize = 11.sp, color = TextSecondary, maxLines = 1)
                                Spacer(Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Surface(shape = RoundedCornerShape(8.dp), color = sevColor.copy(alpha = 0.12f)) {
                                        Text(
                                            injury.severity.replaceFirstChar { it.uppercase() },
                                            fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = sevColor,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Text(
                                        injury.injuryDate ?: injury.createdAt ?: "",
                                        fontSize = 10.sp, color = TextSecondary
                                    )
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
