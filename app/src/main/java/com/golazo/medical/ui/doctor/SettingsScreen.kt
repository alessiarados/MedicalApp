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
import androidx.hilt.navigation.compose.hiltViewModel
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*

@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    viewModel: DoctorViewModel = hiltViewModel()
) {
    val user = viewModel.sessionManager.currentUser.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        GolazoTopBar(title = "Settings")

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Account Info
            item {
                GolazoCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        InitialsAvatar(user?.email ?: "Doctor", UefaBlue, 56)
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("Doctor Account", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(user?.email ?: "", fontSize = 12.sp, color = TextSecondary)
                            Text("Role: ${user?.role?.replaceFirstChar { it.uppercase() }}", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }

            // Settings Sections
            item {
                SectionHeader("Account")
                SettingsItem(Icons.Default.Person, "Profile Information", "Manage your account details")
                SettingsItem(Icons.Default.Lock, "Security", "Password, 2FA settings")
                SettingsItem(Icons.Default.Notifications, "Notifications", "Configure alert preferences")
            }

            item {
                SectionHeader("Preferences")
                SettingsItem(Icons.Default.Language, "Language", "English")
                SettingsItem(Icons.Default.DarkMode, "Appearance", "Light mode")
                SettingsItem(Icons.Default.DataUsage, "Data & Storage", "Manage cached data")
            }

            item {
                SectionHeader("Support")
                SettingsItem(Icons.Default.Help, "Help Center", "FAQs and documentation")
                SettingsItem(Icons.Default.BugReport, "Report a Bug", "Send feedback to developers")
                SettingsItem(Icons.Default.Info, "About", "Golazo Medical v1.0")
            }

            item {
                SectionHeader("Legal")
                SettingsItem(Icons.Default.Description, "Terms of Service", "View terms")
                SettingsItem(Icons.Default.PrivacyTip, "Privacy Policy", "View privacy policy")
                SettingsItem(Icons.Default.Cookie, "Cookie Policy", "Manage cookies")
            }

            item {
                Spacer(Modifier.height(8.dp))
                GolazoButton(
                    text = "Sign Out",
                    onClick = onLogout,
                    containerColor = SeveritySevere
                )
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun SettingsItem(icon: ImageVector, title: String, subtitle: String) {
    GolazoCard(modifier = Modifier.padding(vertical = 2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = UefaBlueVeryLight,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = UefaBlue, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text(subtitle, fontSize = 10.sp, color = TextSecondary)
            }
            Icon(Icons.Default.ChevronRight, null, tint = TextSecondary, modifier = Modifier.size(18.dp))
        }
    }
}
