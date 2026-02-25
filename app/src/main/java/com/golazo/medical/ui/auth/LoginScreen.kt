package com.golazo.medical.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            UefaNavy,
            UefaBlueDark,
            UefaBlue.copy(alpha = 0.85f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(56.dp))

            // Logo
            Surface(
                shape = CircleShape,
                color = White.copy(alpha = 0.15f),
                modifier = Modifier.size(88.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            Text(
                "UEFA Medical",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = White,
                letterSpacing = 0.5.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Player Care & Medical Platform",
                fontSize = 13.sp,
                color = White.copy(alpha = 0.7f),
                letterSpacing = 0.3.sp
            )

            Spacer(Modifier.height(36.dp))

            // Form card
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = White,
                shadowElevation = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        if (state.isRegistration) "Create Account" else "Welcome Back",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = UefaNavy
                    )
                    Text(
                        if (state.isRegistration) "Register to get started" else "Sign in to continue",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(20.dp))

                    GolazoTextField(
                        value = state.email,
                        onValueChange = viewModel::updateEmail,
                        label = "Email (@uefa.com)",
                        keyboardType = KeyboardType.Email,
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = TextSecondary) }
                    )
                    Spacer(Modifier.height(14.dp))

                    GolazoTextField(
                        value = state.password,
                        onValueChange = viewModel::updatePassword,
                        label = "Password",
                        isPassword = true,
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = TextSecondary) }
                    )
                    Spacer(Modifier.height(16.dp))

                    // Role selector
                    Text("I am a", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = UefaNavy)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf("player" to "Player", "doctor" to "Doctor").forEach { (value, label) ->
                            FilterChip(
                                selected = state.role == value,
                                onClick = { viewModel.updateRole(value) },
                                label = {
                                    Text(
                                        label,
                                        fontSize = 13.sp,
                                        fontWeight = if (state.role == value) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (state.role == value) White else UefaNavy
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = UefaBlue,
                                    selectedLabelColor = White,
                                    containerColor = UefaBlueVeryLight,
                                    labelColor = UefaNavy
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = Color.Transparent,
                                    selectedBorderColor = Color.Transparent,
                                    enabled = true,
                                    selected = state.role == value
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
                            Text("Non-UEFA player", fontSize = 13.sp, color = TextSecondary)
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    if (state.error != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SeveritySevere.copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                state.error!!,
                                color = SeveritySevere,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    GolazoButton(
                        text = if (state.isRegistration) "Create Account" else "Sign In",
                        onClick = viewModel::login,
                        isLoading = state.isLoading
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Toggle registration / sign in
            Text(
                text = if (state.isRegistration) "Already have an account? Sign In" else "Don't have an account? Register",
                fontSize = 13.sp,
                color = White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { viewModel.toggleRegistration() }
                    .padding(12.dp)
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}
