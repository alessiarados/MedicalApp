package com.golazo.medical.ui.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.loadPlayers() }

    val filtered = if (searchQuery.isBlank()) players else players.filter {
        val name = "${it.profile?.firstName} ${it.profile?.lastName}".lowercase()
        name.contains(searchQuery.lowercase())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        GolazoTopBar(title = "Players")

        // Search
        GolazoTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = "Search players...",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            leadingIcon = { Icon(Icons.Default.Search, null, tint = TextSecondary) }
        )

        if (isLoading) {
            LoadingScreen()
        } else if (filtered.isEmpty()) {
            EmptyState(icon = Icons.Default.People, title = "No players found")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filtered) { pw ->
                    val profile = pw.profile ?: return@items
                    GolazoCard(
                        modifier = Modifier.clickable { pw.user?.id?.let { onPlayerClick(it) } }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            InitialsAvatar("${profile.firstName} ${profile.lastName}", UefaBlue, 44)
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${profile.firstName} ${profile.lastName}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "${profile.club} • ${profile.position}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    profile.nationality,
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                StatusBadge(profile.status, profile.status == "active")
                                Spacer(Modifier.height(4.dp))
                                PcmeStatusBadge(profile.pcmeStatus)
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}
