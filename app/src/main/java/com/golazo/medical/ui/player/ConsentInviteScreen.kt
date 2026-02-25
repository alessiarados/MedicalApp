package com.golazo.medical.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.golazo.medical.data.model.InvitationToken
import com.golazo.medical.data.repository.GolazoRepository
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InviteViewModel @Inject constructor(
    private val repository: GolazoRepository
) : ViewModel() {
    private val _invitation = MutableStateFlow<InvitationToken?>(null)
    val invitation = _invitation.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadInvitation(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getInvitation(token).onSuccess {
                _invitation.value = it.invitation
            }
            _isLoading.value = false
        }
    }
}

@Composable
fun ConsentInviteScreen(
    token: String,
    onBack: () -> Unit,
    viewModel: InviteViewModel = hiltViewModel()
) {
    val invitation by viewModel.invitation.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(token) { viewModel.loadInvitation(token) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        GolazoTopBar(title = "Consent Invitation", onBack = onBack)

        if (isLoading) {
            LoadingScreen()
        } else {
            invitation?.let { inv ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(32.dp))
                    Icon(Icons.Default.MarkEmailRead, null, tint = UefaBlue, modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Invitation Sent", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = UefaBlue)
                    Spacer(Modifier.height(24.dp))

                    Surface(shape = RoundedCornerShape(20.dp), color = CardWhite, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                        DetailRow("Recipient", inv.recipientName)
                        DetailRow("Email", inv.recipientEmail)
                        inv.granteeOrg?.let { DetailRow("Organization", it) }
                        DetailRow("Expires", inv.expiresAt)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "An invitation link has been generated. Share it with the recipient to grant them access.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                        Surface(
                            color = BackgroundGray,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Token: ${inv.token.take(16)}...",
                                fontSize = 10.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    GolazoButton(text = "Done", onClick = onBack, modifier = Modifier.width(200.dp))
                }
            } ?: EmptyState(
                icon = Icons.Default.ErrorOutline,
                title = "Invitation not found"
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(label, fontSize = 11.sp, color = TextSecondary, modifier = Modifier.width(100.dp))
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}
