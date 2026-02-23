package com.golazo.medical.ui.player

import androidx.compose.foundation.background
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*

@Composable
fun ConsentListScreen(
    onCreateConsent: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val consents by viewModel.consents.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadConsents() }

    Scaffold(
        topBar = { GolazoTopBar(title = "Consent Management") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateConsent,
                containerColor = UefaBlue,
                contentColor = White
            ) {
                Icon(Icons.Default.Add, "New Grant")
            }
        },
        containerColor = BackgroundGray
    ) { padding ->
        if (isLoading) {
            LoadingScreen(modifier = Modifier.padding(padding))
        } else if (consents.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Security,
                title = "No consent grants",
                subtitle = "Tap + to create a new consent grant",
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp,
                    top = padding.calculateTopPadding() + 8.dp,
                    bottom = padding.calculateBottomPadding() + 80.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(consents) { grant ->
                    GolazoCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(grant.granteeName, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                grant.granteeOrg?.let {
                                    Text(it, fontSize = 11.sp, color = TextSecondary)
                                }
                                Spacer(Modifier.height(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = UefaBlueVeryLight
                                ) {
                                    Text(
                                        grant.granteeType.replace("_", " ").replaceFirstChar { it.uppercase() },
                                        fontSize = 10.sp,
                                        color = UefaBlue,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            IconButton(onClick = { viewModel.deleteConsent(grant.id) }) {
                                Icon(Icons.Default.Delete, "Revoke", tint = SeveritySevere, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Scopes:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        grant.scopes.forEach { scope ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 1.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, null, tint = SeverityMinor, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(scope.scope, fontSize = 11.sp, modifier = Modifier.weight(1f))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (scope.accessLevel == "edit") SeverityModerate.copy(alpha = 0.15f) else UefaBlueVeryLight
                                ) {
                                    Text(
                                        scope.accessLevel,
                                        fontSize = 9.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                    )
                                }
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    if (scope.duration == "permanent") "∞" else "${scope.expiresDays}d",
                                    fontSize = 9.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
