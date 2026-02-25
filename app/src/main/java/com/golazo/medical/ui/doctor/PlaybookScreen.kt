package com.golazo.medical.ui.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*

private data class PlaybookSectionData(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val items: List<String>
)

@Composable
fun PlaybookScreen() {
    val sections = listOf(
        PlaybookSectionData("Concussion Protocol", Icons.Default.Psychology, Color(0xFF9C27B0), listOf(
            "SCAT6 Assessment Tool",
            "Return to Play Protocol (Graduated)",
            "Baseline Testing Requirements",
            "Sideline Assessment Checklist"
        )),
        PlaybookSectionData("Cardiac Screening", Icons.Default.MonitorHeart, SeveritySevere, listOf(
            "ECG Interpretation Guidelines",
            "Echocardiography Standards",
            "Sudden Cardiac Arrest Protocol",
            "Pre-Participation Cardiac Screening"
        )),
        PlaybookSectionData("Injury Management", Icons.Default.LocalHospital, Color(0xFFFF9800), listOf(
            "RICE Protocol",
            "Muscle Injury Classification",
            "Ligament Injury Grading",
            "Return to Play Decision Matrix"
        )),
        PlaybookSectionData("Anti-Doping", Icons.Default.Science, SeverityMinor, listOf(
            "WADA Prohibited List",
            "Therapeutic Use Exemptions (TUE)",
            "Testing Procedures",
            "Medication Clearance Guide"
        )),
        PlaybookSectionData("Emergency Procedures", Icons.Default.Emergency, SeveritySevere, listOf(
            "Pitch-Side Emergency Action Plan",
            "AED Protocol",
            "Spinal Injury Management",
            "Heat Illness Protocol"
        )),
        PlaybookSectionData("Mental Health", Icons.Default.SelfImprovement, UefaBlue, listOf(
            "Player Wellbeing Assessment",
            "Referral Pathways",
            "Confidentiality Guidelines",
            "Crisis Intervention Protocol"
        ))
    )

    val totalProtocols = sections.sumOf { it.items.size }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // ── Hero Header ──
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
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
                                    "UEFA Medical Protocols & Reference",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 13.sp,
                                    letterSpacing = 0.3.sp
                                )
                            }
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.15f),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.Default.MenuBook,
                                    null,
                                    tint = Color.White,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            PlaybookHeroStat("Sections", "${sections.size}", Icons.Default.Folder)
                            PlaybookHeroStat("Protocols", "$totalProtocols", Icons.Default.Article)
                            PlaybookHeroStat("Updated", "2025", Icons.Default.Update)
                        }
                    }
                }
            }
        }

        // ── Sections ──
        sections.forEachIndexed { index, section ->
            item {
                PlaybookSection(section, index)
            }
        }
    }
}

@Composable
private fun PlaybookHeroStat(label: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.15f), modifier = Modifier.size(32.dp)) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.padding(6.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(label, fontSize = 9.sp, color = Color.White.copy(alpha = 0.7f))
    }
}

@Composable
private fun PlaybookSection(section: PlaybookSectionData, index: Int) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = CardWhite,
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = section.color.copy(alpha = 0.12f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(section.icon, null, tint = section.color, modifier = Modifier.size(22.dp))
                    }
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        section.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.2).sp
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "${section.items.size} protocols",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = if (expanded) section.color.copy(alpha = 0.12f) else BackgroundGray,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        "Toggle",
                        tint = if (expanded) section.color else TextSecondary,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }

            if (expanded) {
                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = BackgroundGray)
                Spacer(Modifier.height(10.dp))

                section.items.forEachIndexed { idx, item ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BackgroundGray.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = section.color.copy(alpha = 0.12f),
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Text(
                                        "${idx + 1}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = section.color
                                    )
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                item,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                Icons.Default.ChevronRight,
                                null,
                                tint = TextSecondary.copy(alpha = 0.5f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
