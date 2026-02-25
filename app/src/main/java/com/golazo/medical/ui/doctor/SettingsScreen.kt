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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .statusBarsPadding(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp)
    ) {
        // Header
        item {
            Column {
                Text("Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Manage your account", fontSize = 12.sp, color = TextSecondary)
            }
            Spacer(Modifier.height(16.dp))
        }

            // Account Info
            item {
                Surface(shape = RoundedCornerShape(16.dp), color = UefaBlue, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        InitialsAvatar(user?.email ?: "Doctor", White, 56)
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("Doctor Account", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = White)
                            Text(user?.email ?: "", fontSize = 12.sp, color = White.copy(alpha = 0.8f))
                            Text("Role: ${user?.role?.replaceFirstChar { it.uppercase() }}", fontSize = 11.sp, color = White.copy(alpha = 0.7f))
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // Settings Sections
            item {
                SettingsGroup("Account", listOf(
                    Triple(Icons.Default.Person, "Profile Information", "Manage your account details"),
                    Triple(Icons.Default.Lock, "Security", "Password, 2FA settings"),
                    Triple(Icons.Default.Notifications, "Notifications", "Configure alert preferences")
                ))
                Spacer(Modifier.height(12.dp))
            }

            item {
                SettingsGroup("Preferences", listOf(
                    Triple(Icons.Default.Language, "Language", "English"),
                    Triple(Icons.Default.DarkMode, "Appearance", "Light mode"),
                    Triple(Icons.Default.DataUsage, "Data & Storage", "Manage cached data")
                ))
                Spacer(Modifier.height(12.dp))
            }

            item {
                SettingsGroup("Support", listOf(
                    Triple(Icons.Default.Help, "Help Center", "FAQs and documentation"),
                    Triple(Icons.Default.BugReport, "Report a Bug", "Send feedback to developers"),
                    Triple(Icons.Default.Info, "About", "UEFA Medical Analyst v1.0")
                ))
                Spacer(Modifier.height(12.dp))
            }

            item {
                SettingsGroup("Legal", listOf(
                    Triple(Icons.Default.Description, "Terms of Service", "View terms"),
                    Triple(Icons.Default.PrivacyTip, "Privacy Policy", "View privacy policy"),
                    Triple(Icons.Default.Cookie, "Cookie Policy", "Manage cookies")
                ))
                Spacer(Modifier.height(16.dp))
            }

            item {
                GolazoButton(
                    text = "Sign Out",
                    onClick = onLogout,
                    containerColor = SeveritySevere
                )
            }
        }
}

@Composable
private fun SettingsGroup(title: String, items: List<Triple<ImageVector, String, String>>) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = CardWhite,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
            Spacer(Modifier.height(12.dp))
            items.forEachIndexed { index, (icon, itemTitle, subtitle) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = UefaBlueVeryLight,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(icon, null, tint = UefaBlue, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(itemTitle, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text(subtitle, fontSize = 10.sp, color = TextSecondary)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                }
                if (index < items.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(start = 48.dp))
                }
            }
        }
    }
}
