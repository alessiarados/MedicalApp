package com.golazo.medical.ui.player

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.golazo.medical.data.SimulatedData
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*
import java.util.Calendar

@Composable
fun WellbeingScreen(
    onFindHelp: () -> Unit,
    onSessions: () -> Unit,
    onBreathing: () -> Unit
) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

    var bioSignals by remember { mutableStateOf(SimulatedData.generateBioSignals(hour)) }

    // Simulate bio signal updates every 2 seconds
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(2000)
            bioSignals = SimulatedData.generateBioSignals(hour)
        }
    }

    val recommendation = SimulatedData.getRecommendation(dayOfWeek, hour)

    val stressColor = when (bioSignals.stressLevel) {
        "Calm" -> StressCalm
        "Normal" -> StressNormal
        "Moderate" -> StressModerate
        "Elevated" -> StressElevated
        else -> StressNormal
    }

    // Heartbeat animation
    val heartScale by rememberInfiniteTransition(label = "heart").animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween((60000 / bioSignals.heartRate) / 2, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartbeat"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Blue Header Banner
        item {
            Surface(
                color = UefaBlue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(20.dp)
                ) {
                    Column {
                        Text("Wellbeing", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = White)
                        Text("Hello, take care of yourself today", fontSize = 12.sp, color = White.copy(alpha = 0.8f))
                    }
                    IconButton(
                        onClick = onFindHelp,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Shield, "Find Help", tint = White)
                    }
                }
            }
        }

        // Your Status Right Now
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                SectionHeader("Your Status Right Now")
                GolazoCard {
                    // Heart Rate with pulsing icon
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Favorite,
                            null,
                            tint = SeveritySevere,
                            modifier = Modifier.size((20 * heartScale).dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("${bioSignals.heartRate}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = SeveritySevere)
                        Text(" bpm", fontSize = 12.sp, color = TextSecondary)
                    }

                    // ECG Trace
                    Spacer(Modifier.height(8.dp))
                    EcgTrace(modifier = Modifier.fillMaxWidth().height(40.dp))

                    Spacer(Modifier.height(12.dp))

                    // Stress Level
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Stress Level", fontSize = 12.sp, color = TextSecondary)
                        Text(bioSignals.stressLevel, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = stressColor)
                    }
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { bioSignals.stressValue / 100f },
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = stressColor,
                        trackColor = BackgroundGray,
                    )

                    Spacer(Modifier.height(12.dp))

                    // HRV & Coherence
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("HRV", fontSize = 10.sp, color = TextSecondary)
                            Text("${bioSignals.hrv} ms", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(
                                when {
                                    bioSignals.hrv > 60 -> "Excellent"
                                    bioSignals.hrv > 45 -> "Good"
                                    else -> "Low"
                                },
                                fontSize = 10.sp,
                                color = when {
                                    bioSignals.hrv > 60 -> StressCalm
                                    bioSignals.hrv > 45 -> StressNormal
                                    else -> StressElevated
                                }
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Coherence", fontSize = 10.sp, color = TextSecondary)
                            Text("${bioSignals.coherence}%", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(
                                when {
                                    bioSignals.coherence > 75 -> "High"
                                    bioSignals.coherence > 50 -> "Medium"
                                    else -> "Low"
                                },
                                fontSize = 10.sp,
                                color = when {
                                    bioSignals.coherence > 75 -> StressCalm
                                    bioSignals.coherence > 50 -> StressNormal
                                    else -> StressElevated
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Nudge message
                    val nudge = when {
                        bioSignals.stressValue > 60 -> "Your stress is elevated. A breathing exercise could help right now."
                        bioSignals.hrv < 40 -> "Your HRV is low. Consider a recovery session."
                        bioSignals.coherence < 50 -> "Your coherence could be better. Try a quick focus session."
                        else -> "You're in a good state. Keep it up!"
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = UefaBlueVeryLight
                    ) {
                        Text(nudge, fontSize = 11.sp, color = UefaBlue, modifier = Modifier.padding(12.dp))
                    }

                    Spacer(Modifier.height(12.dp))
                    GolazoButton(text = "Start Breathing Exercise", onClick = onBreathing)
                }
            }
        }

        // Weekly Trends
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                SectionHeader("Weekly Trends")
                GolazoCard {
                    SparklineRow("Heart Rate", SimulatedData.weeklyHrData, SeveritySevere, "bpm")
                    Spacer(Modifier.height(12.dp))
                    SparklineRow("Stress", SimulatedData.weeklyStressData, StressModerate, "%")
                    Spacer(Modifier.height(12.dp))
                    SparklineRow("HRV", SimulatedData.weeklyHrvData, UefaBlue, "ms")

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(8.dp))

                    Text("Before vs After (Last Session)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    SimulatedData.beforeAfterData.forEach { ba ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(ba.metric, fontSize = 11.sp, color = TextSecondary)
                            Text(ba.before, fontSize = 11.sp)
                            Text("→", fontSize = 11.sp, color = TextSecondary)
                            Text(ba.after, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = if (ba.improved) StressCalm else StressElevated)
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Your last session improved coherence by 37% and reduced stress significantly.",
                        fontSize = 10.sp,
                        color = UefaBlue
                    )
                }
            }
        }

        // Recommended for You
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                SectionHeader("Recommended for You")
                GolazoCard {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = UefaBlueVeryLight
                    ) {
                        Text(
                            recommendation.category,
                            fontSize = 10.sp,
                            color = UefaBlue,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(recommendation.title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(recommendation.description, fontSize = 11.sp, color = TextSecondary)
                    Spacer(Modifier.height(8.dp))
                    GolazoOutlinedButton(text = "View Sessions", onClick = onSessions)
                }
            }
        }

        // This Week Stats
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                SectionHeader("This Week")
                GolazoCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem("Sessions", "3")
                        StatItem("Total Time", "28 min")
                        StatItem("Coherence", "+12%")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = UefaBlue)
        Text(label, fontSize = 10.sp, color = TextSecondary)
    }
}

@Composable
private fun SparklineRow(label: String, data: List<Int>, color: Color, unit: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.width(60.dp)) {
            Text(label, fontSize = 10.sp, color = TextSecondary)
            Text("${data.last()} $unit", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.width(8.dp))
        SparklineChart(data = data, color = color, modifier = Modifier.weight(1f).height(30.dp))
        Spacer(Modifier.width(8.dp))
        val trend = data.last() - data.first()
        Text(
            if (trend > 0) "↑" else "↓",
            fontSize = 14.sp,
            color = if ((label == "Stress" && trend < 0) || (label != "Stress" && trend > 0)) StressCalm else StressElevated
        )
    }
}

@Composable
fun SparklineChart(data: List<Int>, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        if (data.size < 2) return@Canvas
        val maxVal = data.max().toFloat()
        val minVal = data.min().toFloat()
        val range = (maxVal - minVal).coerceAtLeast(1f)
        val stepX = size.width / (data.size - 1)

        val path = Path()
        data.forEachIndexed { i, v ->
            val x = i * stepX
            val y = size.height - ((v - minVal) / range) * size.height
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, color, style = Stroke(width = 2f))
    }
}

@Composable
fun EcgTrace(modifier: Modifier = Modifier) {
    val phase by rememberInfiniteTransition(label = "ecg").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing)
        ),
        label = "ecgPhase"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val mid = h / 2
        val path = Path()
        val offset = phase * w * 0.3f

        path.moveTo(0f, mid)
        for (x in 0..w.toInt() step 2) {
            val xf = x.toFloat()
            val normalizedX = ((xf + offset) % (w * 0.3f)) / (w * 0.3f)
            val y = when {
                normalizedX < 0.3f -> mid
                normalizedX < 0.35f -> mid - h * 0.15f
                normalizedX < 0.4f -> mid + h * 0.05f
                normalizedX < 0.45f -> mid - h * 0.4f
                normalizedX < 0.5f -> mid + h * 0.25f
                normalizedX < 0.55f -> mid - h * 0.1f
                normalizedX < 0.6f -> mid
                else -> mid
            }
            path.lineTo(xf, y)
        }
        drawPath(path, Color(0xFFF44336), style = Stroke(width = 2f))
    }
}
