package com.golazo.medical.ui.player

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.golazo.medical.data.SimulatedData
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun BreathingScreen(
    onBack: () -> Unit
) {
    var selectedProtocol by remember { mutableStateOf(SimulatedData.breathingProtocols[0]) }
    var isActive by remember { mutableStateOf(false) }
    var currentPhase by remember { mutableStateOf("Ready") }
    var currentRound by remember { mutableIntStateOf(0) }
    var phaseTimer by remember { mutableIntStateOf(0) }
    var sessionComplete by remember { mutableStateOf(false) }

    // Breathing animation
    val breathScale by animateFloatAsState(
        targetValue = when (currentPhase) {
            "Inhale" -> 1.0f
            "Exhale" -> 0.4f
            else -> 0.7f
        },
        animationSpec = tween(
            durationMillis = when (currentPhase) {
                "Inhale" -> selectedProtocol.inhale * 1000
                "Exhale" -> selectedProtocol.exhale * 1000
                else -> 500
            },
            easing = EaseInOutCubic
        ),
        label = "breathScale"
    )

    // Simulated biofeedback
    var simHr by remember { mutableIntStateOf(72) }
    var simStress by remember { mutableIntStateOf(45) }
    var simHrv by remember { mutableIntStateOf(48) }
    var simCoherence by remember { mutableIntStateOf(55) }

    // Timer logic
    LaunchedEffect(isActive) {
        if (!isActive) return@LaunchedEffect
        sessionComplete = false
        currentRound = 1

        while (currentRound <= selectedProtocol.rounds && isActive) {
            // Inhale
            currentPhase = "Inhale"
            for (t in selectedProtocol.inhale downTo 1) {
                phaseTimer = t
                delay(1000)
                if (!isActive) return@LaunchedEffect
            }
            // Hold 1
            if (selectedProtocol.hold1 > 0) {
                currentPhase = "Hold"
                for (t in selectedProtocol.hold1 downTo 1) {
                    phaseTimer = t
                    delay(1000)
                    if (!isActive) return@LaunchedEffect
                }
            }
            // Exhale
            currentPhase = "Exhale"
            for (t in selectedProtocol.exhale downTo 1) {
                phaseTimer = t
                delay(1000)
                if (!isActive) return@LaunchedEffect
            }
            // Hold 2
            if (selectedProtocol.hold2 > 0) {
                currentPhase = "Hold"
                for (t in selectedProtocol.hold2 downTo 1) {
                    phaseTimer = t
                    delay(1000)
                    if (!isActive) return@LaunchedEffect
                }
            }
            currentRound++

            // Update simulated biofeedback
            simHr = (simHr - 1).coerceAtLeast(58)
            simStress = (simStress - 3).coerceAtLeast(10)
            simHrv = (simHrv + 2).coerceAtMost(75)
            simCoherence = (simCoherence + 4).coerceAtMost(95)
        }

        if (isActive) {
            isActive = false
            sessionComplete = true
            currentPhase = "Complete"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        GolazoTopBar(title = "Breathing Exercise", onBack = {
            isActive = false
            onBack()
        })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (sessionComplete) {
                // Session Summary
                Spacer(Modifier.height(32.dp))
                Icon(Icons.Default.CheckCircle, null, tint = SeverityMinor, modifier = Modifier.size(64.dp))
                Spacer(Modifier.height(16.dp))
                Text("Session Complete!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SeverityMinor)
                Spacer(Modifier.height(24.dp))

                GolazoCard {
                    Text("Session Summary", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    SummaryRow("Protocol", selectedProtocol.name)
                    SummaryRow("Rounds", "${selectedProtocol.rounds}")
                    SummaryRow("Heart Rate", "$simHr bpm")
                    SummaryRow("Stress", "$simStress%")
                    SummaryRow("HRV", "$simHrv ms")
                    SummaryRow("Coherence", "$simCoherence%")
                }

                Spacer(Modifier.height(16.dp))
                GolazoButton(text = "Done", onClick = onBack)
            } else {
                // Protocol selector
                if (!isActive) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SimulatedData.breathingProtocols.forEach { proto ->
                            FilterChip(
                                selected = selectedProtocol.id == proto.id,
                                onClick = { selectedProtocol = proto },
                                label = { Text(proto.name, fontSize = 10.sp) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = UefaBlue,
                                    selectedLabelColor = White
                                )
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(selectedProtocol.description, fontSize = 11.sp, color = TextSecondary, textAlign = TextAlign.Center)
                }

                Spacer(Modifier.height(24.dp))

                // Breathing Circle
                Box(
                    modifier = Modifier.size(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val radius = size.minDimension / 2 * breathScale
                        drawCircle(
                            color = UefaBlue.copy(alpha = 0.15f),
                            radius = size.minDimension / 2
                        )
                        drawCircle(
                            color = UefaBlue.copy(alpha = 0.3f),
                            radius = radius
                        )
                        drawCircle(
                            color = UefaBlue,
                            radius = radius * 0.7f
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            currentPhase,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                        if (isActive && currentPhase != "Complete") {
                            Text("$phaseTimer", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = White)
                        }
                    }
                }

                if (isActive) {
                    Spacer(Modifier.height(8.dp))
                    Text("Round $currentRound of ${selectedProtocol.rounds}", fontSize = 12.sp, color = TextSecondary)
                }

                Spacer(Modifier.height(24.dp))

                // Biofeedback (simulated)
                if (isActive) {
                    GolazoCard {
                        Text("Live Biofeedback", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            BioItem("HR", "$simHr", "bpm", SeveritySevere)
                            BioItem("Stress", "$simStress", "%", StressModerate)
                            BioItem("HRV", "$simHrv", "ms", UefaBlue)
                            BioItem("Coh.", "$simCoherence", "%", SeverityMinor)
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                if (!isActive && !sessionComplete) {
                    GolazoButton(
                        text = "Start",
                        onClick = {
                            isActive = true
                            simHr = 72; simStress = 45; simHrv = 48; simCoherence = 55
                        }
                    )
                } else if (isActive) {
                    GolazoOutlinedButton(
                        text = "Stop",
                        onClick = { isActive = false; currentPhase = "Ready" }
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun BioItem(label: String, value: String, unit: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
        Text(unit, fontSize = 9.sp, color = TextSecondary)
        Text(label, fontSize = 10.sp, color = TextSecondary)
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = TextSecondary)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
