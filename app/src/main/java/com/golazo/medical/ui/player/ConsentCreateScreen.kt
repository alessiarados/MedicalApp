package com.golazo.medical.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.golazo.medical.data.model.ConsentCreateRequest
import com.golazo.medical.data.model.ConsentScope
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*

data class ScopeSelection(
    val scope: String,
    val enabled: Boolean = false,
    val accessLevel: String = "read",
    val duration: String = "permanent",
    val expiresDays: String = ""
)

private val availableScopes = listOf(
    "Medical History", "Injury Records", "PCME Data",
    "Training Load", "Wellbeing Data", "Personal Information"
)

@Composable
fun ConsentCreateScreen(
    onBack: () -> Unit,
    onCreated: (String?) -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val isNonUefa = viewModel.sessionManager.isNonUefa

    val allGranteeTypes = listOf(
        "national_team_doctor" to "National Team Doctor",
        "parent_guardian" to "Parent/Guardian",
        "external_recipient" to "External Recipient"
    )
    val nonUefaTypes = listOf(
        "national_team_doctor" to "Club",
        "external_recipient" to "Other Users"
    )
    val granteeTypes = if (isNonUefa) nonUefaTypes else allGranteeTypes

    var granteeType by remember { mutableStateOf("") }
    var granteeName by remember { mutableStateOf("") }
    var granteeOrg by remember { mutableStateOf("") }
    var recipientEmail by remember { mutableStateOf("") }

    val scopeSelections = remember {
        mutableStateListOf(*availableScopes.map { ScopeSelection(it) }.toTypedArray())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        GolazoTopBar(title = "New Consent Grant", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Grantee Type
            GolazoCard {
                Text("Grantee Type", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                granteeTypes.forEach { (value, label) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = granteeType == value,
                            onClick = { granteeType = value },
                            colors = RadioButtonDefaults.colors(selectedColor = UefaBlue)
                        )
                        Text(label, fontSize = 12.sp)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Grantee Details
            GolazoCard {
                Text("Grantee Details", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                GolazoTextField(value = granteeName, onValueChange = { granteeName = it }, label = "Grantee Name")
                Spacer(Modifier.height(8.dp))
                GolazoTextField(value = granteeOrg, onValueChange = { granteeOrg = it }, label = "Organization (optional)")
                Spacer(Modifier.height(8.dp))
                GolazoTextField(
                    value = recipientEmail,
                    onValueChange = { recipientEmail = it },
                    label = "Recipient Email (optional, for invitation)",
                    keyboardType = KeyboardType.Email
                )
            }

            Spacer(Modifier.height(12.dp))

            // Scopes
            GolazoCard {
                Text("Data Scopes", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                scopeSelections.forEachIndexed { index, sel ->
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = sel.enabled,
                                onCheckedChange = { scopeSelections[index] = sel.copy(enabled = it) },
                                colors = CheckboxDefaults.colors(checkedColor = UefaBlue)
                            )
                            Text(sel.scope, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        }
                        if (sel.enabled) {
                            Row(
                                modifier = Modifier.padding(start = 40.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("read" to "Read", "edit" to "Edit").forEach { (v, l) ->
                                    FilterChip(
                                        selected = sel.accessLevel == v,
                                        onClick = { scopeSelections[index] = sel.copy(accessLevel = v) },
                                        label = { Text(l, fontSize = 10.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = UefaBlue,
                                            selectedLabelColor = White
                                        )
                                    )
                                }
                                listOf("permanent" to "Permanent", "time_bound" to "Time-bound").forEach { (v, l) ->
                                    FilterChip(
                                        selected = sel.duration == v,
                                        onClick = { scopeSelections[index] = sel.copy(duration = v) },
                                        label = { Text(l, fontSize = 10.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = UefaBlue,
                                            selectedLabelColor = White
                                        )
                                    )
                                }
                            }
                            if (sel.duration == "time_bound") {
                                GolazoTextField(
                                    value = sel.expiresDays,
                                    onValueChange = { scopeSelections[index] = sel.copy(expiresDays = it) },
                                    label = "Days until expiry",
                                    keyboardType = KeyboardType.Number,
                                    modifier = Modifier.padding(start = 40.dp, top = 4.dp)
                                )
                            }
                        }
                        if (index < scopeSelections.lastIndex) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            GolazoButton(
                text = "Create Consent Grant",
                onClick = {
                    val scopes = scopeSelections.filter { it.enabled }.map {
                        ConsentScope(
                            scope = it.scope,
                            accessLevel = it.accessLevel,
                            duration = it.duration,
                            expiresDays = it.expiresDays.toIntOrNull()
                        )
                    }
                    viewModel.createConsent(
                        ConsentCreateRequest(
                            userId = viewModel.sessionManager.userId,
                            granteeType = granteeType,
                            granteeName = granteeName,
                            granteeOrg = granteeOrg.ifBlank { null },
                            scopes = scopes,
                            recipientEmail = recipientEmail.ifBlank { null }
                        )
                    ) { resp ->
                        onCreated(resp.invitation?.token)
                    }
                },
                enabled = granteeType.isNotBlank() && granteeName.isNotBlank() && scopeSelections.any { it.enabled }
            )

            Spacer(Modifier.height(80.dp))
        }
    }
}
