package com.golazo.medical.ui.shared

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*

@Composable
fun SimulationsScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        GolazoTopBar(title = "Tactical Simulations", onBack = onBack)

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                GolazoCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.SportsFootball, null, tint = UefaBlue, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("Tactical Simulation Engine", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Run tactical scenarios and analyze outcomes",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            val simulations = listOf(
                Triple("Formation Analysis", Icons.Default.GridOn, "Compare formations and their effectiveness against different opponents"),
                Triple("Set Piece Simulator", Icons.Default.Flag, "Design and test set piece routines with player positioning"),
                Triple("Counter Attack Patterns", Icons.Default.Speed, "Analyze transition play and counter-attacking opportunities"),
                Triple("Defensive Shape", Icons.Default.Shield, "Test defensive formations and pressing triggers"),
                Triple("Player Matchups", Icons.Default.CompareArrows, "Simulate 1v1 matchups based on player attributes"),
                Triple("Injury Impact Analysis", Icons.Default.LocalHospital, "Model team performance impact when key players are injured")
            )

            simulations.forEach { (title, icon, description) ->
                item {
                    SimulationCard(title, icon, description)
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun SimulationCard(title: String, icon: ImageVector, description: String) {
    GolazoCard {
        Row(verticalAlignment = Alignment.Top) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = UefaBlueVeryLight,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = UefaBlue, modifier = Modifier.size(28.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(2.dp))
                Text(description, fontSize = 11.sp, color = TextSecondary)
                Spacer(Modifier.height(8.dp))
                GolazoOutlinedButton(
                    text = "Run Simulation",
                    onClick = { /* Placeholder */ }
                )
            }
        }
    }
}
