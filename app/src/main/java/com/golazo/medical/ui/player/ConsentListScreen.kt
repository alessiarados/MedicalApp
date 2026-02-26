package com.golazo.medical.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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

    var selectedFilterIndex by rememberSaveable { mutableIntStateOf(0) }

    val filteredConsents = remember(consents, selectedFilterIndex) {
        when (selectedFilterIndex) {
            1 -> consents.filter {
                val type = it.granteeType.lowercase()
                type.contains("doctor")
            }
            2 -> consents.filter {
                val type = it.granteeType.lowercase()
                type.contains("parent")
            }
            3 -> consents.filter {
                val type = it.granteeType.lowercase()
                !(type.contains("doctor") || type.contains("parent"))
            }
            else -> consents
        }
    }

    LaunchedEffect(Unit) { viewModel.loadConsents() }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateConsent,
                containerColor = UefaBlue,
                contentColor = White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, "New Grant")
            }
        },
        containerColor = BackgroundGray
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .statusBarsPadding(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp)
        ) {
            // Header
            item {
                Column {
                    Text("Consent", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("Manage who can access your data", fontSize = 12.sp, color = TextSecondary)
                }
                Spacer(Modifier.height(16.dp))
            }

            item {
                val labels = listOf("All", "Team Doctor", "Parent", "External")
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    itemsIndexed(labels) { index, label ->
                        FilterChip(
                            selected = selectedFilterIndex == index,
                            onClick = { selectedFilterIndex = index },
                            label = { Text(label, fontSize = 12.sp) },
                            leadingIcon =
                                if (selectedFilterIndex == index) {
                                    { Icon(Icons.Default.Check, contentDescription = null) }
                                } else {
                                    null
                                }
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // Summary card
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = UefaBlue,
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(shape = CircleShape, color = White.copy(alpha = 0.2f), modifier = Modifier.size(48.dp)) {
                            Icon(Icons.Default.Security, null, tint = White, modifier = Modifier.padding(12.dp))
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("${filteredConsents.size} Active Grant${if (filteredConsents.size != 1) "s" else ""}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = White)
                            Text("Your data sharing permissions", fontSize = 12.sp, color = White.copy(alpha = 0.7f))
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            if (isLoading) {
                item { LoadingScreen() }
            } else if (filteredConsents.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Default.Security,
                        title = "No consent grants",
                        subtitle = "Tap + to create a new consent grant"
                    )
                }
            } else {
                items(filteredConsents) { grant ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = CardWhite,
                        shadowElevation = 4.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                    Surface(shape = CircleShape, color = UefaBlueVeryLight, modifier = Modifier.size(40.dp)) {
                                        Icon(Icons.Default.Person, null, tint = UefaBlue, modifier = Modifier.padding(8.dp))
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(grant.granteeName, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        grant.granteeOrg?.let {
                                            Text(it, fontSize = 11.sp, color = TextSecondary)
                                        }
                                    }
                                }
                                IconButton(onClick = { viewModel.deleteConsent(grant.id) }) {
                                    Icon(Icons.Default.Delete, "Revoke", tint = SeveritySevere, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = UefaBlueVeryLight
                            ) {
                                Text(
                                    grant.granteeType.replace("_", " ").replaceFirstChar { it.uppercase() },
                                    fontSize = 10.sp,
                                    color = UefaBlue,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            grant.scopes.forEach { scope ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, null, tint = SeverityMinor, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text(scope.scope, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (scope.accessLevel == "edit") SeverityModerate.copy(alpha = 0.12f) else UefaBlueVeryLight
                                    ) {
                                        Text(
                                            scope.accessLevel,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (scope.accessLevel == "edit") SeverityModerate else UefaBlue,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        if (scope.duration == "permanent") "∞" else "${scope.expiresDays}d",
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}
