package com.golazo.medical.ui.doctor

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*

@Composable
fun PlaybookScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        GolazoTopBar(title = "Medical Playbook")

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                GolazoCard {
                    Text("UEFA Medical Protocols", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("Reference material for medical staff", fontSize = 12.sp, color = TextSecondary)
                }
            }

            val sections = listOf(
                Triple("Concussion Protocol", Icons.Default.Psychology, listOf(
                    "SCAT6 Assessment Tool",
                    "Return to Play Protocol (Graduated)",
                    "Baseline Testing Requirements",
                    "Sideline Assessment Checklist"
                )),
                Triple("Cardiac Screening", Icons.Default.MonitorHeart, listOf(
                    "ECG Interpretation Guidelines",
                    "Echocardiography Standards",
                    "Sudden Cardiac Arrest Protocol",
                    "Pre-Participation Cardiac Screening"
                )),
                Triple("Injury Management", Icons.Default.LocalHospital, listOf(
                    "RICE Protocol",
                    "Muscle Injury Classification",
                    "Ligament Injury Grading",
                    "Return to Play Decision Matrix"
                )),
                Triple("Anti-Doping", Icons.Default.Science, listOf(
                    "WADA Prohibited List",
                    "Therapeutic Use Exemptions (TUE)",
                    "Testing Procedures",
                    "Medication Clearance Guide"
                )),
                Triple("Emergency Procedures", Icons.Default.Emergency, listOf(
                    "Pitch-Side Emergency Action Plan",
                    "AED Protocol",
                    "Spinal Injury Management",
                    "Heat Illness Protocol"
                )),
                Triple("Mental Health", Icons.Default.SelfImprovement, listOf(
                    "Player Wellbeing Assessment",
                    "Referral Pathways",
                    "Confidentiality Guidelines",
                    "Crisis Intervention Protocol"
                ))
            )

            sections.forEach { (title, icon, items) ->
                item {
                    PlaybookSection(title, icon, items)
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun PlaybookSection(title: String, icon: ImageVector, items: List<String>) {
    var expanded by remember { mutableStateOf(false) }

    GolazoCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = UefaBlueVeryLight,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = UefaBlue, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    "Toggle",
                    tint = TextSecondary
                )
            }
        }
        if (expanded) {
            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))
            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Article, null, tint = UefaBlue, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(item, fontSize = 12.sp)
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Default.ChevronRight, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
