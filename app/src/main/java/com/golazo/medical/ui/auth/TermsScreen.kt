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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*

@Composable
fun TermsScreen(
    userId: String,
    onAccepted: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.termsState.collectAsStateWithLifecycle()

    LaunchedEffect(state.accepted) {
        if (state.accepted) onAccepted()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .statusBarsPadding()
    ) {
        GolazoTopBar(title = "Terms & Conditions")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            GolazoCard {
                Text("UEFA Medical Platform", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("Terms & Conditions", fontSize = 12.sp, color = TextSecondary)
                Spacer(Modifier.height(16.dp))

                val terms = listOf(
                    "1. Data Collection & Usage" to "Your medical data will be collected and stored securely for the purpose of medical care and performance monitoring.",
                    "2. Data Sharing" to "Your data may be shared with authorized medical personnel based on your consent grants. You control who has access.",
                    "3. Privacy" to "All data is encrypted and stored in compliance with GDPR and UEFA data protection policies.",
                    "4. Rights" to "You have the right to access, modify, and delete your personal data at any time.",
                    "5. Consent" to "By signing below, you agree to these terms and authorize the platform to process your medical data."
                )

                terms.forEach { (title, body) ->
                    Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text(body, fontSize = 11.sp, color = TextSecondary)
                    Spacer(Modifier.height(12.dp))
                }
            }

            Spacer(Modifier.height(16.dp))

            GolazoCard {
                Text("Signature", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("Type your full name as your digital signature", fontSize = 11.sp, color = TextSecondary)
                Spacer(Modifier.height(8.dp))
                GolazoTextField(
                    value = state.signature,
                    onValueChange = viewModel::updateSignature,
                    label = "Full Name Signature"
                )
            }

            Spacer(Modifier.height(16.dp))

            if (state.error != null) {
                Text(state.error!!, color = SeveritySevere, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
            }

            GolazoButton(
                text = "Accept & Continue",
                onClick = { viewModel.acceptTerms(userId) },
                isLoading = state.isLoading
            )
        }
    }
}
