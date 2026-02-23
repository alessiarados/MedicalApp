package com.golazo.medical.ui.auth

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*

@Composable
fun OnboardingScreen(
    userId: String,
    onComplete: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.onboardingState.collectAsStateWithLifecycle()

    LaunchedEffect(state.completed) {
        if (state.completed) onComplete()
    }

    val positions = listOf("Goalkeeper", "Defender", "Midfielder", "Forward")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .statusBarsPadding()
    ) {
        GolazoTopBar(title = "Player Profile Setup")

        // Progress indicator
        LinearProgressIndicator(
            progress = { (state.currentStep + 1) / 3f },
            modifier = Modifier.fillMaxWidth(),
            color = UefaBlue,
            trackColor = UefaBlueVeryLight,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                "Step ${state.currentStep + 1} of 3",
                fontSize = 12.sp,
                color = TextSecondary
            )
            Spacer(Modifier.height(8.dp))

            when (state.currentStep) {
                0 -> {
                    Text("Personal Information", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    GolazoTextField(
                        value = state.firstName,
                        onValueChange = { viewModel.updateOnboardingField("firstName", it) },
                        label = "First Name"
                    )
                    Spacer(Modifier.height(12.dp))
                    GolazoTextField(
                        value = state.lastName,
                        onValueChange = { viewModel.updateOnboardingField("lastName", it) },
                        label = "Last Name"
                    )
                    Spacer(Modifier.height(12.dp))
                    GolazoTextField(
                        value = state.nationality,
                        onValueChange = { viewModel.updateOnboardingField("nationality", it) },
                        label = "Nationality"
                    )
                }
                1 -> {
                    Text("Football Details", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    GolazoTextField(
                        value = state.club,
                        onValueChange = { viewModel.updateOnboardingField("club", it) },
                        label = "Club"
                    )
                    Spacer(Modifier.height(12.dp))
                    GolazoTextField(
                        value = state.dob,
                        onValueChange = { viewModel.updateOnboardingField("dob", it) },
                        label = "Date of Birth (YYYY-MM-DD)"
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("Position", fontSize = 12.sp, color = TextSecondary)
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        positions.forEach { pos ->
                            FilterChip(
                                selected = state.position == pos,
                                onClick = { viewModel.updateOnboardingField("position", pos) },
                                label = { Text(pos, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = UefaBlue,
                                    selectedLabelColor = White
                                )
                            )
                        }
                    }
                }
                2 -> {
                    Text("Contact Information", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    GolazoTextField(
                        value = state.phoneNumber,
                        onValueChange = { viewModel.updateOnboardingField("phoneNumber", it) },
                        label = "Phone Number",
                        keyboardType = KeyboardType.Phone
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Used for 2FA verification on future logins",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            if (state.error != null) {
                Text(state.error!!, color = SeveritySevere, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.currentStep > 0) {
                    GolazoOutlinedButton(
                        text = "Back",
                        onClick = viewModel::prevOnboardingStep,
                        modifier = Modifier.weight(1f)
                    )
                }
                GolazoButton(
                    text = if (state.currentStep == 2) "Complete" else "Next",
                    onClick = {
                        if (state.currentStep == 2) viewModel.completeOnboarding(userId)
                        else viewModel.nextOnboardingStep()
                    },
                    isLoading = state.isLoading,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
