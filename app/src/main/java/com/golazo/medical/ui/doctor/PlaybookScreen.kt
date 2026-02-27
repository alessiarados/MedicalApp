package com.golazo.medical.ui.doctor

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*

// Data classes for Playbook
data class Play(
    val id: String,
    val name: String,
    val formation: String,
    val category: String,
    val riskLevel: String,
    val successRate: Int,
    val goals: Int,
    val chances: Int,
    val injuryRate: Float,
    val description: String,
    val keyPlayers: List<String>,
    val videoClips: List<VideoClip> = emptyList(),
    val isFavorite: Boolean = false,
    val totalInjuries: Int = 0,
    val executions: Int = 0
)

data class VideoClip(
    val title: String,
    val opponent: String,
    val half: String,
    val date: String,
    val duration: String,
    val note: String
)

// Sample data
private fun getSamplePlays(): List<Play> = listOf(
    Play(
        id = "1",
        name = "Near Post Corner",
        formation = "4-3-3",
        category = "Set Pieces",
        riskLevel = "Low",
        successRate = 34,
        goals = 8,
        chances = 24,
        injuryRate = 3.0f,
        description = "Inswinging corner aimed at the near post. First runner attacks the near post while second runner peels away to the back post. Third runner makes a late run to the penalty spot.",
        keyPlayers = listOf("LW", "ST", "CB"),
        videoClips = listOf(
            VideoClip("Corner Goal vs Arsenal", "Arsenal", "2nd Half", "2024-10-20", "0:15", "Silva header")
        ),
        isFavorite = true,
        totalInjuries = 3,
        executions = 80
    ),
    Play(
        id = "2",
        name = "Counter Attack - Long Ball",
        formation = "4-3-3",
        category = "Transition",
        riskLevel = "Medium",
        successRate = 28,
        goals = 12,
        chances = 43,
        injuryRate = 5.1f,
        description = "Quick transition from defensive third. GK or CB plays long diagonal to wide forward making run in behind. Striker and opposite winger make supporting runs.",
        keyPlayers = listOf("GK", "CB", "LW", "ST"),
        videoClips = listOf(
            VideoClip("Counter Attack Goal", "Chelsea", "1st Half", "2024-09-28", "0:22", "")
        ),
        isFavorite = true,
        totalInjuries = 8,
        executions = 157
    ),
    Play(
        id = "3",
        name = "High Press - 4-3-3",
        formation = "4-3-3",
        category = "Defensive",
        riskLevel = "High",
        successRate = 42,
        goals = 6,
        chances = 38,
        injuryRate = 12.4f,
        description = "Aggressive pressing from the front three. Triggers: GK pass to CB, or CB receiving with back to goal. Front three cuts passing lanes while midfield steps up to squeeze space.",
        keyPlayers = listOf("ST", "LW", "RW", "CM"),
        totalInjuries = 11,
        executions = 89
    ),
    Play(
        id = "4",
        name = "Build-up Play - Central",
        formation = "4-2-3-1",
        category = "Attacking",
        riskLevel = "Low",
        successRate = 72,
        goals = 4,
        chances = 52,
        injuryRate = 2.1f,
        description = "Patient build-up through the center. Double pivot receives from CBs, CAM drops to create overload. Full-backs push high to stretch defense wide.",
        keyPlayers = listOf("CDM", "CM", "CAM"),
        videoClips = listOf(
            VideoClip("Build-up Goal vs Liverpool", "Liverpool", "2nd Half", "2024-10-05", "0:28", "15 passes before goal")
        ),
        isFavorite = true,
        totalInjuries = 4,
        executions = 190
    ),
    Play(
        id = "5",
        name = "Free Kick Routine - Wall",
        formation = "4-3-3",
        category = "Set Pieces",
        riskLevel = "Low",
        successRate = 18,
        goals = 5,
        chances = 28,
        injuryRate = 2.2f,
        description = "Direct free kick near the box. Decoy runner goes over the ball while shooter curls around the wall. Second option: short pass and cross.",
        keyPlayers = listOf("CM", "CAM", "ST"),
        totalInjuries = 1,
        executions = 45
    ),
    Play(
        id = "6",
        name = "Overlap & Cross",
        formation = "4-3-3",
        category = "Attacking",
        riskLevel = "Medium",
        successRate = 31,
        goals = 7,
        chances = 61,
        injuryRate = 4.5f,
        description = "Winger holds width while full-back overlaps on the outside. Cross aimed at back post with striker attacking near post and CAM arriving late at the edge of the box.",
        keyPlayers = listOf("RB", "RW", "ST", "CAM"),
        totalInjuries = 9,
        executions = 198
    ),
    Play(
        id = "7",
        name = "Low Block Defense",
        formation = "5-3-2",
        category = "Defensive",
        riskLevel = "Low",
        successRate = 78,
        goals = 0,
        chances = 12,
        injuryRate = 3.6f,
        description = "Compact defensive shape with two banks of 5 and 3. Strikers drop to midfield line when defending. Focus on blocking central passing lanes and forcing wide.",
        keyPlayers = listOf("CB", "CDM", "LWB", "RWB"),
        totalInjuries = 2,
        executions = 56
    ),
    Play(
        id = "8",
        name = "Quick Throw-in",
        formation = "4-4-2",
        category = "Transition",
        riskLevel = "Medium",
        successRate = 45,
        goals = 2,
        chances = 18,
        injuryRate = 4.5f,
        description = "Fast throw-in to catch opposition out of shape. Receiver lays off first-time to midfielder who plays quick vertical pass. Option for overlapping full-back.",
        keyPlayers = listOf("LB", "RB", "CM", "LW"),
        totalInjuries = 4,
        executions = 89
    ),
    Play(
        id = "9",
        name = "Penalty Box Crash",
        formation = "4-3-3",
        category = "Set Pieces",
        riskLevel = "High",
        successRate = 22,
        goals = 4,
        chances = 18,
        injuryRate = 14.7f,
        description = "Corner kick with multiple runners attacking different zones. Near post, back post, penalty spot, and edge of box all covered. Designed to create chaos.",
        keyPlayers = listOf("CB", "ST", "CM"),
        totalInjuries = 5,
        executions = 34
    ),
    Play(
        id = "10",
        name = "Gegenpressing",
        formation = "4-3-3",
        category = "Transition",
        riskLevel = "High",
        successRate = 38,
        goals = 9,
        chances = 32,
        injuryRate = 12.4f,
        description = "Immediate counter-press after losing possession. Nearest 3-4 players swarm the ball within 5 seconds. If not recovered, drop into shape.",
        keyPlayers = listOf("ST", "LW", "RW", "CM"),
        videoClips = listOf(
            VideoClip("Gegenpressing Goal", "Man City", "1st Half", "2024-10-12", "0:12", "Won ball and scored in 4 seconds")
        ),
        isFavorite = true,
        totalInjuries = 22,
        executions = 177
    )
)

@Composable
fun PlaybookScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedPlay by remember { mutableStateOf<Play?>(null) }
    
    val plays = remember { getSamplePlays() }
    val filters = listOf("All", "Set Pieces", "Attacking", "Defensive")
    
    val filteredPlays = plays.filter { play ->
        selectedFilter == "All" || play.category == selectedFilter || 
        (selectedFilter == "Attacking" && play.category == "Transition")
    }

    if (selectedPlay != null) {
        PlayDetailScreen(play = selectedPlay!!, onBack = { selectedPlay = null })
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Hero Header
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = UefaBlue,
                    shadowElevation = 12.dp
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(UefaBlueDark, UefaBlue, UefaBlueLight.copy(alpha = 0.8f))
                                )
                            )
                            .padding(22.dp)
                    ) {
                        Column {
                            Text(
                                "Playbook",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Analyze tactics, track injury correlations, optimize strategy.",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 13.sp,
                                letterSpacing = 0.3.sp
                            )
                        }
                    }
                }
            }

            // Tabs
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        listOf("Plays", "Analytics").forEachIndexed { index, tab ->
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (selectedTab == index) UefaBlue else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedTab = index }
                            ) {
                                Text(
                                    tab,
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    textAlign = TextAlign.Center,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            if (selectedTab == 0) {
                // Filters for Plays tab
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filters) { filter ->
                            FilterChip(
                                selected = selectedFilter == filter,
                                onClick = { selectedFilter = filter },
                                label = { 
                                    Text(
                                        filter, 
                                        fontSize = 12.sp,
                                        color = if (selectedFilter == filter) White else MaterialTheme.colorScheme.onSurface
                                    ) 
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = UefaBlue,
                                    selectedLabelColor = White,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }

                // Play Cards
                items(filteredPlays) { play ->
                    PlayCard(play = play, onClick = { selectedPlay = play })
                }
            } else {
                // Analytics Tab Content
                item {
                    AnalyticsContent(plays = plays)
                }
            }
        }
    }
}

@Composable
private fun AnalyticsContent(plays: List<Play>) {
    val totalPlays = plays.size
    val totalGoals = plays.sumOf { it.goals }
    val totalChances = plays.sumOf { it.chances }
    val totalInjuries = plays.sumOf { it.totalInjuries }
    
    val lowRisk = plays.count { it.riskLevel == "Low" }
    val mediumRisk = plays.count { it.riskLevel == "Medium" }
    val highRisk = plays.count { it.riskLevel == "High" }
    
    val highestRiskPlays = plays.sortedByDescending { it.injuryRate }.take(5)
    val safestPlays = plays.sortedBy { it.injuryRate }.take(5)
    
    // Injuries by position (simulated data based on key players)
    val positionInjuries = mapOf(
        "CM" to 15, "ST" to 14, "LW" to 12, "RW" to 11, "CB" to 5, "RB" to 4
    )
    
    // Injuries by body part (simulated)
    val bodyPartInjuries = listOf(
        "Hamstring" to 19, "Ankle" to 15, "Knee" to 10,
        "Groin" to 9, "Calf" to 9, "Shoulder" to 4
    )

    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Season Overview
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Season Overview", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OverviewStatItem(Icons.Default.Flag, "$totalPlays", "Plays", UefaBlue)
                    OverviewStatItem(Icons.Default.SportsSoccer, "$totalGoals", "Goals", UefaBlue)
                    OverviewStatItem(Icons.Default.TrendingUp, "$totalChances", "Chances", SeverityMinor)
                    OverviewStatItem(Icons.Default.Warning, "$totalInjuries", "Injuries", SeveritySevere)
                }
            }
        }
        
        // Risk Distribution
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Risk Distribution", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(12.dp))
                
                // Progress bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                ) {
                    val total = (lowRisk + mediumRisk + highRisk).coerceAtLeast(1)
                    Box(modifier = Modifier.weight(lowRisk.toFloat().coerceAtLeast(0.1f)).fillMaxHeight().background(SeverityMinor))
                    Box(modifier = Modifier.weight(mediumRisk.toFloat().coerceAtLeast(0.1f)).fillMaxHeight().background(SeverityModerate))
                    Box(modifier = Modifier.weight(highRisk.toFloat().coerceAtLeast(0.1f)).fillMaxHeight().background(SeveritySevere))
                }
                
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    RiskLegendItem(SeverityMinor, "Low ($lowRisk)")
                    RiskLegendItem(SeverityModerate, "Medium ($mediumRisk)")
                    RiskLegendItem(SeveritySevere, "High ($highRisk)")
                }
            }
        }
        
        // Highest Risk & Safest Plays
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Highest Risk
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Highest Risk", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SeveritySevere)
                    Spacer(Modifier.height(8.dp))
                    highestRiskPlays.forEach { play ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                play.name.take(15) + if (play.name.length > 15) "..." else "",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = SeveritySevere.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    "${play.injuryRate}%",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SeveritySevere,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            // Safest Plays
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Safest Plays", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SeverityMinor)
                    Spacer(Modifier.height(8.dp))
                    safestPlays.forEach { play ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                play.name.take(15) + if (play.name.length > 15) "..." else "",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = SeverityMinor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    "${play.injuryRate}%",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SeverityMinor,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Injuries by Position
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Injuries by Position", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(12.dp))
                val maxInjuries = positionInjuries.values.maxOrNull() ?: 1
                positionInjuries.forEach { (position, count) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(position, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.width(32.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(16.dp)
                                .padding(horizontal = 8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxSize()
                            ) {}
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = UefaBlue,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(count.toFloat() / maxInjuries)
                            ) {}
                        }
                        Text("$count", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.width(24.dp), textAlign = TextAlign.End)
                    }
                }
            }
        }
        
        // Pitch Injury Heat Map
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Pitch Injury Heat Map", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(12.dp))
                
                // Football pitch
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF2E7D32),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Pitch lines
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val lineColor = Color.White.copy(alpha = 0.6f)
                            val strokeWidth = 2f
                            
                            // Center line
                            drawLine(lineColor, Offset(w/2, 0f), Offset(w/2, h), strokeWidth)
                            // Center circle
                            drawCircle(lineColor, radius = 40f, center = Offset(w/2, h/2), style = Stroke(strokeWidth))
                            // Left penalty area
                            drawRect(lineColor, topLeft = Offset(0f, h*0.25f), size = Size(w*0.15f, h*0.5f), style = Stroke(strokeWidth))
                            // Right penalty area
                            drawRect(lineColor, topLeft = Offset(w*0.85f, h*0.25f), size = Size(w*0.15f, h*0.5f), style = Stroke(strokeWidth))
                            // Border
                            drawRect(lineColor, topLeft = Offset(0f, 0f), size = Size(w, h), style = Stroke(strokeWidth))
                        }
                        
                        // Heat map dots
                        Box(modifier = Modifier.fillMaxSize()) {
                            // High injury zones (red dots)
                            HeatMapDot(0.3f, 0.5f, SeveritySevere, 20.dp)
                            HeatMapDot(0.7f, 0.5f, SeveritySevere, 18.dp)
                            // Medium zones (orange)
                            HeatMapDot(0.5f, 0.3f, SeverityModerate, 16.dp)
                            HeatMapDot(0.5f, 0.7f, SeverityModerate, 16.dp)
                            // Low zones (green)
                            HeatMapDot(0.15f, 0.5f, SeverityMinor, 14.dp)
                            HeatMapDot(0.85f, 0.5f, SeverityMinor, 14.dp)
                            HeatMapDot(0.5f, 0.5f, UefaBlue, 12.dp)
                        }
                    }
                }
            }
        }
        
        // Injuries by Body Part
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Injuries by Body Part", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(12.dp))
                
                // First row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    bodyPartInjuries.take(3).forEach { (part, count) ->
                        BodyPartStatItem(count, part)
                    }
                }
                Spacer(Modifier.height(12.dp))
                // Second row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    bodyPartInjuries.drop(3).forEach { (part, count) ->
                        BodyPartStatItem(count, part)
                    }
                }
            }
        }
        
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun OverviewStatItem(icon: ImageVector, value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = CircleShape,
            color = color.copy(alpha = 0.12f),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun RiskLegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).background(color, CircleShape))
        Spacer(Modifier.width(4.dp))
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun BoxScope.HeatMapDot(xFraction: Float, yFraction: Float, color: Color, size: Dp) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart)
    ) {
        Box(
            modifier = Modifier
                .offset(
                    x = (xFraction * 300).dp - size / 2,
                    y = (yFraction * 180).dp - size / 2
                )
                .size(size)
                .background(color.copy(alpha = 0.7f), CircleShape)
        )
    }
}

@Composable
private fun BodyPartStatItem(count: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("$count", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun PlayCard(play: Play, onClick: () -> Unit) {
    val riskColor = when (play.riskLevel) {
        "Low" -> SeverityMinor
        "Medium" -> SeverityModerate
        else -> SeveritySevere
    }
    val categoryIcon = when (play.category) {
        "Set Pieces" -> Icons.Default.Flag
        "Transition" -> Icons.Default.FlashOn
        "Defensive" -> Icons.Default.Shield
        else -> Icons.Default.SportsSoccer
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = UefaBlue.copy(alpha = 0.12f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(categoryIcon, null, tint = UefaBlue, modifier = Modifier.size(22.dp))
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(play.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        if (play.isFavorite) {
                            Spacer(Modifier.width(6.dp))
                            Icon(Icons.Default.Star, null, tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                        }
                        if (play.videoClips.isNotEmpty()) {
                            Spacer(Modifier.width(4.dp))
                            Surface(shape = RoundedCornerShape(4.dp), color = UefaBlue.copy(alpha = 0.12f)) {
                                Row(modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PlayCircle, null, tint = UefaBlue, modifier = Modifier.size(10.dp))
                                    Text("${play.videoClips.size}", fontSize = 9.sp, color = UefaBlue)
                                }
                            }
                        }
                    }
                    Text("${play.formation} • ${play.category}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Surface(shape = RoundedCornerShape(8.dp), color = riskColor.copy(alpha = 0.12f)) {
                    Text(play.riskLevel, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = riskColor, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            Spacer(Modifier.height(12.dp))
            
            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PlayStatItem("${play.successRate}%", "Success", UefaBlue)
                PlayStatItem("${play.goals}", "Goals", MaterialTheme.colorScheme.onSurface)
                PlayStatItem("${play.chances}", "Chances", MaterialTheme.colorScheme.onSurface)
                PlayStatItem("${play.injuryRate}%", "Injury", if (play.injuryRate > 10) SeveritySevere else MaterialTheme.colorScheme.onSurface)
            }
            
            // High injury warning
            if (play.injuryRate > 10) {
                Spacer(Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SeveritySevere.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, null, tint = SeveritySevere, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("High injury risk: ${play.totalInjuries} injuries recorded", fontSize = 11.sp, color = SeveritySevere)
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayStatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun PlayDetailScreen(play: Play, onBack: () -> Unit) {
    val riskColor = when (play.riskLevel) {
        "Low" -> SeverityMinor
        "Medium" -> SeverityModerate
        else -> SeveritySevere
    }
    val categoryIcon = when (play.category) {
        "Set Pieces" -> Icons.Default.Flag
        "Transition" -> Icons.Default.FlashOn
        "Defensive" -> Icons.Default.Shield
        else -> Icons.Default.SportsSoccer
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.Close, "Close", tint = MaterialTheme.colorScheme.onSurface)
                        }
                        Spacer(Modifier.weight(1f))
                        Icon(
                            if (play.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            "Favorite",
                            tint = if (play.isFavorite) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Spacer(Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = UefaBlue.copy(alpha = 0.12f),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Icon(categoryIcon, null, tint = UefaBlue, modifier = Modifier.size(28.dp))
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(play.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Row {
                                Text(play.formation, fontSize = 12.sp, color = UefaBlue, fontWeight = FontWeight.Medium)
                                Spacer(Modifier.width(12.dp))
                                Text(play.category, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Surface(shape = RoundedCornerShape(8.dp), color = riskColor) {
                        Text("${play.riskLevel} Risk", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = White, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                    }
                }
            }
        }
        
        // Description
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Description", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SeverityMinor)
                    Spacer(Modifier.height(8.dp))
                    Text(play.description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 20.sp)
                }
            }
        }
        
        // Key Players
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Key Players", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        play.keyPlayers.forEach { player ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Text(player, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        }
        
        // Pitch Diagram
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Pitch Diagram", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(12.dp))
                    // Simplified pitch representation
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.fillMaxWidth().height(180.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize().border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        ) {
                            // Center circle
                            Surface(
                                shape = CircleShape,
                                color = Color.Transparent,
                                border = BorderStroke(2.dp, Color.White.copy(alpha = 0.5f)),
                                modifier = Modifier.size(60.dp)
                            ) {}
                            // Center line
                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color.White.copy(alpha = 0.5f),
                                thickness = 2.dp
                            )
                            Text("Tactical View", color = White.copy(alpha = 0.7f), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
        
        // Video Clips
        if (play.videoClips.isNotEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Video Clips", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.height(12.dp))
                        play.videoClips.forEach { clip ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                        Icon(Icons.Default.PlayCircle, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
                                    }
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(clip.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                    Text("vs ${clip.opponent} • ${clip.half} • ${clip.date}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("Duration: ${clip.duration}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            if (clip.note.isNotEmpty()) {
                                Text(clip.note, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                            }
                        }
                    }
                }
            }
        }
        
        // Performance Stats
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Performance Stats", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DetailStatItem("${play.successRate}%", "Success Rate")
                        DetailStatItem("${play.goals}", "Goals")
                        DetailStatItem("${play.chances}", "Chances")
                    }
                }
            }
        }
        
        // Injury Correlation (for all plays)
        item {
            val injuryColor = when {
                play.injuryRate > 10 -> SeveritySevere
                play.injuryRate > 4 -> SeverityModerate
                else -> SeverityMinor
            }
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, null, tint = injuryColor, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Injury Correlation", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = injuryColor.copy(alpha = 0.12f)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text("${play.injuryRate}%", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = injuryColor)
                                Text("Injury Rate", fontSize = 10.sp, color = injuryColor)
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${play.totalInjuries}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Total Injuries", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${play.executions}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Executions", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

