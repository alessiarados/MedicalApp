package com.golazo.medical.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun SessionsScreen(
    onBack: () -> Unit,
    onSessionClick: (String) -> Unit
) {
    val categories = SimulatedData.sessionsLibrary.map { it.category }.distinct()
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val filtered = if (selectedCategory != null) {
        SimulatedData.sessionsLibrary.filter { it.category == selectedCategory }
    } else {
        SimulatedData.sessionsLibrary
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        GolazoTopBar(title = "Wellbeing Sessions", onBack = onBack)

        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Category Filter
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("All", fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = UefaBlue, selectedLabelColor = White)
                    )
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = if (selectedCategory == cat) null else cat },
                            label = { Text(cat, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = UefaBlue, selectedLabelColor = White)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            items(filtered) { session ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSessionClick(session.id) },
                    shape = RoundedCornerShape(20.dp),
                    color = CardWhite,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = UefaBlueVeryLight
                            ) {
                                Text(
                                    session.category,
                                    fontSize = 9.sp,
                                    color = UefaBlue,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(session.title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(2.dp))
                            Text(session.description, fontSize = 11.sp, color = TextSecondary, maxLines = 2)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.PlayCircle, null, tint = UefaBlue, modifier = Modifier.size(32.dp))
                            Text("${session.duration} min", fontSize = 10.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}
