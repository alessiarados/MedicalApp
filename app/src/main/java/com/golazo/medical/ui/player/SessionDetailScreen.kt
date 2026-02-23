package com.golazo.medical.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.golazo.medical.data.SimulatedData
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*

@Composable
fun SessionDetailScreen(
    sessionId: String,
    onBack: () -> Unit
) {
    val session = SimulatedData.sessionsLibrary.find { it.id == sessionId }
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    // Simulate playback
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (progress < 1f) {
                kotlinx.coroutines.delay(100)
                progress = (progress + 0.001f).coerceAtMost(1f)
            }
            isPlaying = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        GolazoTopBar(title = session?.title ?: "Session", onBack = onBack)

        session?.let { s ->
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Audio Player
                item {
                    GolazoCard {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = UefaBlueVeryLight
                            ) {
                                Text(
                                    s.category,
                                    fontSize = 10.sp,
                                    color = UefaBlue,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(s.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("${s.duration} minutes", fontSize = 12.sp, color = TextSecondary)
                            Spacer(Modifier.height(16.dp))

                            // Player controls
                            IconButton(
                                onClick = { isPlaying = !isPlaying },
                                modifier = Modifier.size(64.dp)
                            ) {
                                Icon(
                                    if (isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                    "Play/Pause",
                                    tint = UefaBlue,
                                    modifier = Modifier.size(64.dp)
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth().height(4.dp),
                                color = UefaBlue,
                                trackColor = BackgroundGray,
                            )
                            Spacer(Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${(progress * s.duration).toInt()}:00", fontSize = 10.sp, color = TextSecondary)
                                Text("${s.duration}:00", fontSize = 10.sp, color = TextSecondary)
                            }
                        }
                    }
                }

                // Description
                item {
                    GolazoCard {
                        Text("About", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(s.description, fontSize = 12.sp, color = TextSecondary)
                    }
                }

                // Step-by-step guidance
                item {
                    GolazoCard {
                        Text("Guidance Steps", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                    }
                }

                itemsIndexed(s.steps) { index, step ->
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = UefaBlue,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("${index + 1}", fontSize = 11.sp, color = White, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(step, fontSize = 12.sp, modifier = Modifier.weight(1f))
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        } ?: EmptyState(icon = Icons.Default.ErrorOutline, title = "Session not found")
    }
}
