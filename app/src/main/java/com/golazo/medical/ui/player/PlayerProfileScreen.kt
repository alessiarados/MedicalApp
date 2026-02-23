package com.golazo.medical.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*

@Composable
fun PlayerProfileScreen(
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadProfile() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        GolazoTopBar(title = "Profile")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            profile?.let { p ->
                InitialsAvatar("${p.firstName} ${p.lastName}", UefaBlue, 80)
                Spacer(Modifier.height(12.dp))
                Text("${p.firstName} ${p.lastName}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(p.position, fontSize = 12.sp, color = TextSecondary)

                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusBadge(p.status, p.status == "active")
                    PcmeStatusBadge(p.pcmeStatus)
                }

                Spacer(Modifier.height(24.dp))

                GolazoCard {
                    SectionHeader("Personal Information")
                    ProfileRow("First Name", p.firstName)
                    ProfileRow("Last Name", p.lastName)
                    ProfileRow("Nationality", p.nationality)
                    ProfileRow("Date of Birth", p.dob)
                    p.location?.let { ProfileRow("Location", it) }
                }

                Spacer(Modifier.height(12.dp))

                GolazoCard {
                    SectionHeader("Football Details")
                    ProfileRow("Club", p.club)
                    ProfileRow("Position", p.position)
                    ProfileRow("Status", p.status.replaceFirstChar { it.uppercase() })
                    ProfileRow("PCME Status", p.pcmeStatus.replaceFirstChar { it.uppercase() })
                }

                Spacer(Modifier.height(12.dp))

                GolazoCard {
                    SectionHeader("Account")
                    val user = viewModel.sessionManager.currentUser.value
                    user?.let {
                        ProfileRow("Email", it.email)
                        ProfileRow("Role", it.role.replaceFirstChar { c -> c.uppercase() })
                        ProfileRow("Non-UEFA", if (it.nonUefa) "Yes" else "No")
                        it.phoneNumber?.let { ph -> ProfileRow("Phone", ph) }
                    }
                }

                Spacer(Modifier.height(80.dp))
            } ?: LoadingScreen()
        }
    }
}

@Composable
private fun ProfileRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = TextSecondary)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
