package com.golazo.medical.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
fun InjuriesListScreen(
    onCreateInjury: () -> Unit,
    onInjuryClick: (String) -> Unit,
    onHistoryClick: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val injuries by viewModel.injuries.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadInjuries() }

    Scaffold(
        topBar = {
            GolazoTopBar(
                title = "Injuries",
                actions = {
                    IconButton(onClick = onHistoryClick) {
                        Icon(Icons.Default.History, "History", tint = White)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateInjury,
                containerColor = UefaBlue,
                contentColor = White
            ) {
                Icon(Icons.Default.Add, "New Injury")
            }
        },
        containerColor = BackgroundGray
    ) { padding ->
        if (isLoading) {
            LoadingScreen(modifier = Modifier.padding(padding))
        } else if (injuries.isEmpty()) {
            EmptyState(
                icon = Icons.Default.HealthAndSafety,
                title = "No injuries recorded",
                subtitle = "Tap + to report a new injury",
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp,
                    top = padding.calculateTopPadding() + 8.dp,
                    bottom = padding.calculateBottomPadding() + 80.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(injuries) { injury ->
                    GolazoCard(
                        modifier = Modifier.clickable { onInjuryClick(injury.id) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    injury.bodyArea,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    injury.mechanism,
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    maxLines = 2
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                SeverityBadge(injury.severity)
                                Spacer(Modifier.height(4.dp))
                                StatusBadge(injury.status)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RtpBadge(injury.rtpStatus)
                            Text(
                                injury.injuryDate ?: "",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
