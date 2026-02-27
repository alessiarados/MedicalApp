package com.golazo.medical.ui.doctor

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*

@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    viewModel: DoctorViewModel = hiltViewModel(),
    preferencesManager: com.golazo.medical.data.PreferencesManager = hiltViewModel<DoctorViewModel>().preferencesManager
) {
    val user = viewModel.sessionManager.currentUser.value
    val context = LocalContext.current
    
    var showProfileDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showAppearanceDialog by remember { mutableStateOf(false) }
    var showDataDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp)
    ) {
        // Header
        item {
            Column {
                Text("Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text("Manage your account", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                SettingsGroupClickable("Account", listOf(
                    SettingsItem(Icons.Default.Person, "Profile Information", "Manage your account details") { showProfileDialog = true },
                    SettingsItem(Icons.Default.Notifications, "Notifications", "Configure alert preferences") { showNotificationsDialog = true }
                ))
                Spacer(Modifier.height(12.dp))
            }

            item {
                SettingsGroupClickable("Preferences", listOf(
                    SettingsItem(Icons.Default.DarkMode, "Appearance", "${preferencesManager.doctorTheme} mode") { showAppearanceDialog = true },
                    SettingsItem(Icons.Default.DataUsage, "Data & Storage", "Manage cached data") { showDataDialog = true }
                ))
                Spacer(Modifier.height(12.dp))
            }

            item {
                SettingsGroupClickable("Legal", listOf(
                    SettingsItem(Icons.Default.Cookie, "Cookie Policy", "Manage cookies") {
                        Toast.makeText(context, "Cookie settings managed", Toast.LENGTH_SHORT).show()
                    },
                    SettingsItem(Icons.Default.Info, "About", "UEFA Medical Analyst v1.0") { showAboutDialog = true }
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
    
    // Dialogs
    if (showProfileDialog) {
        ProfileDialog(
            email = user?.email ?: "",
            onDismiss = { showProfileDialog = false }
        )
    }
    
    if (showNotificationsDialog) {
        NotificationsDialog(
            preferencesManager = preferencesManager,
            onDismiss = { showNotificationsDialog = false }
        )
    }
    
    if (showLanguageDialog) {
        LanguageDialog(
            preferencesManager = preferencesManager,
            onDismiss = { showLanguageDialog = false }
        )
    }
    
    if (showAppearanceDialog) {
        AppearanceDialog(
            preferencesManager = preferencesManager,
            onDismiss = { showAppearanceDialog = false }
        )
    }
    
    if (showDataDialog) {
        DataStorageDialog(
            context = context,
            onDismiss = { showDataDialog = false }
        )
    }
    
    if (showAboutDialog) {
        AboutDialog(onDismiss = { showAboutDialog = false })
    }
}

private data class SettingsItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit
)

@Composable
private fun SettingsGroupClickable(title: String, items: List<SettingsItem>) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { item.onClick() }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(item.icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                        Text(item.subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                }
                if (index < items.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(start = 48.dp), color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
private fun ProfileDialog(email: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Profile Information", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
        text = {
            Column {
                Text("Email: $email", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(8.dp))
                Text("Role: Doctor", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(8.dp))
                Text("Account Status: Active", fontSize = 14.sp, color = SeverityMinor)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = MaterialTheme.colorScheme.primary) }
        }
    )
}

@Composable
private fun NotificationsDialog(
    preferencesManager: com.golazo.medical.data.PreferencesManager,
    onDismiss: () -> Unit
) {
    var pushEnabled by remember { mutableStateOf(preferencesManager.pushNotificationsEnabled) }
    var emailEnabled by remember { mutableStateOf(preferencesManager.emailNotificationsEnabled) }
    var injuryAlerts by remember { mutableStateOf(preferencesManager.injuryAlertsEnabled) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Notifications", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Push Notifications", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    Switch(checked = pushEnabled, onCheckedChange = { pushEnabled = it })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Email Notifications", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    Switch(checked = emailEnabled, onCheckedChange = { emailEnabled = it })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Injury Alerts", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    Switch(checked = injuryAlerts, onCheckedChange = { injuryAlerts = it })
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                preferencesManager.pushNotificationsEnabled = pushEnabled
                preferencesManager.emailNotificationsEnabled = emailEnabled
                preferencesManager.injuryAlertsEnabled = injuryAlerts
                onDismiss()
            }) { Text("Save", color = MaterialTheme.colorScheme.primary) }
        }
    )
}

@Composable
private fun LanguageDialog(
    preferencesManager: com.golazo.medical.data.PreferencesManager,
    onDismiss: () -> Unit
) {
    var selectedLanguage by remember { mutableStateOf(preferencesManager.language) }
    val languages = listOf("English", "Spanish", "French", "German", "Italian", "Portuguese")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Language", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
        text = {
            Column {
                languages.forEach { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedLanguage = language }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedLanguage == language,
                            onClick = { selectedLanguage = language }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(language, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                preferencesManager.language = selectedLanguage
                onDismiss()
            }) { Text("Save", color = MaterialTheme.colorScheme.primary) }
        }
    )
}

@Composable
private fun AppearanceDialog(
    preferencesManager: com.golazo.medical.data.PreferencesManager,
    onDismiss: () -> Unit
) {
    var selectedTheme by remember { mutableStateOf(preferencesManager.doctorTheme) }
    val themes = listOf("Light", "Dark", "System Default")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Appearance", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
        text = {
            Column {
                themes.forEach { theme ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedTheme = theme }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedTheme == theme,
                            onClick = { selectedTheme = theme }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(theme, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                preferencesManager.doctorTheme = selectedTheme
                onDismiss()
            }) { Text("Save", color = MaterialTheme.colorScheme.primary) }
        }
    )
}

@Composable
private fun DataStorageDialog(context: android.content.Context, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Data & Storage", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
        text = {
            Column {
                Text("Cached Data: 12.5 MB", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(8.dp))
                Text("Downloaded Files: 3.2 MB", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(16.dp))
                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "Cache cleared", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Clear Cache", color = MaterialTheme.colorScheme.primary)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = MaterialTheme.colorScheme.primary) }
        }
    )
}

@Composable
private fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("About", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
        text = {
            Column {
                Text("UEFA Medical Analyst", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(8.dp))
                Text("Version 1.0.0", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(4.dp))
                Text("Build 2026.02.27", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(16.dp))
                Text("© 2026 UEFA. All rights reserved.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                Text("Developed for UEFA Medical Staff to manage player health and injuries.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = MaterialTheme.colorScheme.primary) }
        }
    )
}
