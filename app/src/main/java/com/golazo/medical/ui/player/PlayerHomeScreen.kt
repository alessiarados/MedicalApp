package com.golazo.medical.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.golazo.medical.data.SimulatedData
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*

@Composable
fun PlayerHomeScreen(
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadProfile() }

    val playerName = profile?.let { "${it.firstName} ${it.lastName}" } ?: "Player"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Welcome Header
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
                    Text("Welcome back,", fontSize = 12.sp, color = White.copy(alpha = 0.8f))
                    Text(playerName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = White)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        profile?.club ?: "",
                        fontSize = 12.sp,
                        color = White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Performance Metrics
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                SectionHeader("Performance Metrics")
            }
        }
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(SimulatedData.performanceMetrics) { metric ->
                    GolazoCard(modifier = Modifier.width(140.dp)) {
                        Text(metric.label, fontSize = 10.sp, color = TextSecondary)
                        Spacer(Modifier.height(4.dp))
                        Text(metric.value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = UefaBlue)
                        Spacer(Modifier.height(2.dp))
                        Text(
                            metric.trend,
                            fontSize = 10.sp,
                            color = if (metric.trendUp) SeverityMinor else SeveritySevere
                        )
                    }
                }
            }
        }

        // Recovery Tracking
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                SectionHeader("Recovery Tracking")
                GolazoCard {
                    SimulatedData.recoveryData.forEach { data ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(data.label, fontSize = 12.sp, modifier = Modifier.weight(1f))
                            Text(
                                "${data.value}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = when {
                                    data.value >= 80 -> SeverityMinor
                                    data.value >= 60 -> SeverityModerate
                                    else -> SeveritySevere
                                }
                            )
                        }
                        if (data != SimulatedData.recoveryData.last()) {
                            LinearProgressIndicator(
                                progress = { data.value / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp),
                                color = when {
                                    data.value >= 80 -> SeverityMinor
                                    data.value >= 60 -> SeverityModerate
                                    else -> SeveritySevere
                                },
                                trackColor = BackgroundGray,
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        // Weekly Schedule
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                SectionHeader("Weekly Schedule")
                GolazoCard {
                    SimulatedData.weeklySchedule.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = UefaBlueVeryLight,
                                modifier = Modifier.width(40.dp)
                            ) {
                                Text(
                                    item.day,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UefaBlue,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(item.activity, fontSize = 12.sp, modifier = Modifier.weight(1f))
                            Text(item.time, fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }

        // Load Management
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                SectionHeader("Load Management")
                GolazoCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Metric", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                        Text("This Week", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                        Text("Last Week", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    }
                    Spacer(Modifier.height(8.dp))
                    SimulatedData.loadManagement.forEach { stat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stat.label, fontSize = 11.sp, modifier = Modifier.weight(1f))
                            Text(
                                "${stat.thisWeek}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = UefaBlue,
                                modifier = Modifier.width(60.dp)
                            )
                            Text(
                                "${stat.lastWeek}",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                modifier = Modifier.width(60.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
