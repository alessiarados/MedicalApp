package com.golazo.medical.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
fun PinScreen(
    userId: String,
    onVerified: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.pinState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        viewModel.requestPin(userId)
    }

    LaunchedEffect(state.verified) {
        if (state.verified) onVerified()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .padding(24.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(80.dp))

        Text("Verify Your Identity", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = UefaBlue)
        Spacer(Modifier.height(8.dp))
        Text("Enter the 4-digit PIN sent to your phone", fontSize = 12.sp, color = TextSecondary, textAlign = TextAlign.Center)

        if (state.expectedPin.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text("(Dev PIN: ${state.expectedPin})", fontSize = 10.sp, color = TextSecondary)
        }

        Spacer(Modifier.height(32.dp))

        // PIN Input
        BasicTextField(
            value = state.pin,
            onValueChange = viewModel::updatePin,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            decorationBox = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.weight(1f))
                    repeat(4) { index ->
                        val char = state.pin.getOrNull(index)
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .border(
                                    2.dp,
                                    if (index == state.pin.length) UefaBlue else CardBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .background(White, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (char != null) "●" else "",
                                fontSize = 24.sp,
                                color = UefaBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(Modifier.weight(1f))
                }
            }
        )

        Spacer(Modifier.height(24.dp))

        if (state.error != null) {
            Text(state.error!!, color = SeveritySevere, fontSize = 12.sp)
            Spacer(Modifier.height(8.dp))
        }

        GolazoButton(
            text = "Verify",
            onClick = { viewModel.verifyPin(userId) },
            isLoading = state.isLoading,
            modifier = Modifier.width(200.dp)
        )
    }
}
