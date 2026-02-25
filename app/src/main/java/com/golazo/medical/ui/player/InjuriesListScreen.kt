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
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateInjury,
                containerColor = UefaBlue,
                contentColor = White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, "New Injury")
            }
        },
        containerColor = BackgroundGray
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .statusBarsPadding(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Injuries", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text("Track and manage your injuries", fontSize = 12.sp, color = TextSecondary)
                    }
                    IconButton(onClick = onHistoryClick) {
                        Surface(shape = CircleShape, color = UefaBlueVeryLight, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.Default.History, "History", tint = UefaBlue, modifier = Modifier.padding(8.dp))
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // Summary card
            item {
                val activeCount = injuries.count { it.status == "open" }
                val resolvedCount = injuries.count { it.status == "resolved" }
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = UefaBlue,
                    shadowElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text("$activeCount", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = White)
                            Text("Active", fontSize = 11.sp, color = White.copy(alpha = 0.7f))
                        }
                        Box(modifier = Modifier.width(1.dp).height(40.dp).background(White.copy(alpha = 0.3f)))
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text("$resolvedCount", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = White)
                            Text("Resolved", fontSize = 11.sp, color = White.copy(alpha = 0.7f))
                        }
                        Box(modifier = Modifier.width(1.dp).height(40.dp).background(White.copy(alpha = 0.3f)))
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text("${injuries.size}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = White)
                            Text("Total", fontSize = 11.sp, color = White.copy(alpha = 0.7f))
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            if (isLoading) {
                item { LoadingScreen() }
            } else if (injuries.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Default.HealthAndSafety,
                        title = "No injuries recorded",
                        subtitle = "Tap + to report a new injury"
                    )
                }
            } else {
                items(injuries) { injury ->
                    val severityColor = when (injury.severity) {
                        "minor" -> SeverityMinor
                        "moderate" -> SeverityModerate
                        else -> SeveritySevere
                    }
                    val severityIcon = when (injury.severity) {
                        "minor" -> Icons.Default.CheckCircle
                        "moderate" -> Icons.Default.Warning
                        else -> Icons.Default.Error
                    }
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onInjuryClick(injury.id) },
                        shape = RoundedCornerShape(20.dp),
                        color = CardWhite,
                        shadowElevation = 4.dp
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                            Surface(
                                shape = CircleShape,
                                color = severityColor.copy(alpha = 0.12f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Icon(severityIcon, null, tint = severityColor, modifier = Modifier.padding(10.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(injury.bodyArea, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (injury.status == "open") StatusOpen.copy(alpha = 0.12f) else StatusClosed.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            injury.status.replaceFirstChar { it.uppercase() },
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (injury.status == "open") StatusOpen else StatusClosed,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.height(2.dp))
                                Text(injury.mechanism, fontSize = 11.sp, color = TextSecondary, maxLines = 2)
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = severityColor.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            injury.severity.replaceFirstChar { it.uppercase() },
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = severityColor,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Text(injury.injuryDate ?: "", fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                            Icon(Icons.Default.ChevronRight, null, tint = TextSecondary, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}
