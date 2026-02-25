package com.golazo.medical.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: (userId: String, isNewUser: Boolean, requires2FA: Boolean) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.loginState.collectAsStateWithLifecycle()

    LaunchedEffect(state.navigateTo) {
        state.navigateTo?.let { nav ->
            onLoginSuccess(nav.userId, nav.isNewUser, nav.requires2FA)
            viewModel.clearNavigation()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

        // Logo area
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = UefaBlue,
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("G", color = White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("UEFA Medical Analyst", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = UefaBlue)
        Text("UEFA Medical Platform", fontSize = 12.sp, color = TextSecondary)

        Spacer(Modifier.height(32.dp))

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = CardWhite,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
            Text(
                if (state.isRegistration) "Create Account" else "Sign In",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))

            GolazoTextField(
                value = state.email,
                onValueChange = viewModel::updateEmail,
                label = "Email (@uefa.com)",
                keyboardType = KeyboardType.Email,
                leadingIcon = { Icon(Icons.Default.Email, null, tint = TextSecondary) }
            )
            Spacer(Modifier.height(12.dp))

            GolazoTextField(
                value = state.password,
                onValueChange = viewModel::updatePassword,
                label = "Password",
                isPassword = true,
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = TextSecondary) }
            )
            Spacer(Modifier.height(12.dp))

            // Role selector
            Text("Role", fontSize = 12.sp, color = TextSecondary)
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("player" to "Player", "doctor" to "Doctor").forEach { (value, label) ->
                    FilterChip(
                        selected = state.role == value,
                        onClick = { viewModel.updateRole(value) },
                        label = { Text(label, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = UefaBlue,
                            selectedLabelColor = White
                        )
                    )
                }
            }

            if (state.isRegistration && state.role == "player") {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = state.nonUefa,
                        onCheckedChange = viewModel::updateNonUefa,
                        colors = CheckboxDefaults.colors(checkedColor = UefaBlue)
                    )
                    Text("Non-UEFA player", fontSize = 12.sp, color = TextSecondary)
                }
            }

            Spacer(Modifier.height(16.dp))

            if (state.error != null) {
                Text(state.error!!, color = SeveritySevere, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
            }

            GolazoButton(
                text = if (state.isRegistration) "Register" else "Sign In",
                onClick = viewModel::login,
                isLoading = state.isLoading
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = if (state.isRegistration) "Already have an account? Sign In" else "Don't have an account? Register",
                fontSize = 12.sp,
                color = UefaBlue,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.toggleRegistration() }
                    .padding(8.dp)
            )
        }
        }
    }
}
