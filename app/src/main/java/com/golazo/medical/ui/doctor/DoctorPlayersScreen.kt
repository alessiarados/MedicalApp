package com.golazo.medical.ui.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*

@Composable
fun DoctorPlayersScreen(
    onPlayerClick: (String) -> Unit,
    viewModel: DoctorViewModel = hiltViewModel()
) {
    val players by viewModel.players.collectAsStateWithLifecycle()
    val injuries by viewModel.injuries.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    LaunchedEffect(Unit) {
        viewModel.loadPlayers()
        viewModel.loadInjuries()
    }

    val openInjuries = injuries.filter { it.status == "open" }
    val injuredPlayerIds = openInjuries.map { it.userId }.toSet()
    val activePlayers = players.filter { it.profile?.status == "active" }
    val needingPcme = players.filter { it.profile?.pcmeStatus in listOf("missing", "late", "expected") }

    val searchFiltered = if (searchQuery.isBlank()) players else players.filter {
        val name = "${it.profile?.firstName} ${it.profile?.lastName}".lowercase()
        name.contains(searchQuery.lowercase())
    }

    val filtered = when (selectedFilter) {
        "Active" -> searchFiltered.filter { it.profile?.status == "active" }
        "Injured" -> searchFiltered.filter { it.user?.id in injuredPlayerIds }
        "PCME Due" -> searchFiltered.filter { it.profile?.pcmeStatus in listOf("missing", "late", "expected") }
        else -> searchFiltered
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                                    "Squad Management",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.5).sp
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "${players.size} registered players",
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
                                    Icons.Default.Groups,
                                    null,
                                    tint = Color.White,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            HeroMiniStat("Total", "${players.size}", Icons.Default.People)
                            HeroMiniStat("Active", "${activePlayers.size}", Icons.Default.CheckCircle)
                            HeroMiniStat("Injured", "${injuredPlayerIds.size}", Icons.Default.LocalHospital)
                            HeroMiniStat("PCME Due", "${needingPcme.size}", Icons.Default.Assignment)
                        }
                    }
                }
            }
        }

        // ── Search Bar ──
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                GolazoTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = "Search players...",
                    modifier = Modifier.padding(4.dp),
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                )
            }
            Spacer(Modifier.height(12.dp))
        }

        // ── Filter Chips ──
        item {
            val filters = listOf(
                FilterOption("All", players.size, UefaBlue),
                FilterOption("Active", activePlayers.size, SeverityMinor),
                FilterOption("Injured", injuredPlayerIds.size, SeveritySevere),
                FilterOption("PCME Due", needingPcme.size, SeverityModerate)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { filter ->
                    val isSelected = selectedFilter == filter.label
                    Surface(
                        onClick = { selectedFilter = filter.label },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) filter.color else filter.color.copy(alpha = 0.08f),
                        shadowElevation = if (isSelected) 2.dp else 0.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                filter.label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) Color.White else filter.color
                            )
                            Spacer(Modifier.width(6.dp))
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) Color.White.copy(alpha = 0.25f) else filter.color.copy(alpha = 0.15f),
                                modifier = Modifier.size(20.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Text(
                                        "${filter.count}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else filter.color
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        // ── Player List ──
        if (isLoading) {
            item { LoadingScreen() }
        } else if (filtered.isEmpty()) {
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    EmptyState(icon = Icons.Default.People, title = "No players found")
                }
            }
        } else {
            items(filtered) { pw ->
                val profile = pw.profile ?: return@items
                val isInjured = pw.user?.id in injuredPlayerIds
                val playerInjuries = openInjuries.filter { it.userId == pw.user?.id }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { pw.user?.id?.let { onPlayerClick(it) } },
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Avatar with injury indicator
                            Box {
                                ProfileAvatar(
                                    imageUrl = profile.imageUrl,
                                    name = "${profile.firstName} ${profile.lastName}",
                                    size = 48,
                                    fallbackColor = if (isInjured) SeveritySevere else UefaBlue
                                )
                                if (isInjured) {
                                    Surface(
                                        shape = CircleShape,
                                        color = SeveritySevere,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .align(Alignment.BottomEnd)
                                    ) {
                                        Icon(
                                            Icons.Default.Warning,
                                            null,
                                            tint = Color.White,
                                            modifier = Modifier.padding(2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${profile.firstName} ${profile.lastName}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.2).sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = UefaBlue.copy(alpha = 0.1f)
                                    ) {
                                        Text(
                                            profile.position,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = UefaBlue,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        "${profile.club} • ${profile.nationality}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Icon(
                                Icons.Default.ChevronRight,
                                null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Status row
                        Spacer(Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        Spacer(Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                StatusBadge(profile.status, profile.status == "active")
                                PcmeStatusBadge(profile.pcmeStatus)
                            }

                            if (playerInjuries.isNotEmpty()) {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    playerInjuries.take(3).forEach { injury ->
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = SeveritySevere.copy(alpha = 0.08f)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.LocalHospital,
                                                    null,
                                                    tint = SeveritySevere,
                                                    modifier = Modifier.size(10.dp)
                                                )
                                                Spacer(Modifier.width(3.dp))
                                                Text(
                                                    injury.bodyArea.split(",").first().trim(),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = SeveritySevere
                                                )
                                            }
                                        }
                                    }
                                    if (playerInjuries.size > 3) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = SeveritySevere.copy(alpha = 0.08f)
                                        ) {
                                            Text(
                                                "+${playerInjuries.size - 3}",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SeveritySevere,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

private data class FilterOption(val label: String, val count: Int, val color: Color)

@Composable
private fun HeroMiniStat(label: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.15f), modifier = Modifier.size(32.dp)) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.padding(6.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(label, fontSize = 9.sp, color = Color.White.copy(alpha = 0.7f))
    }
}
