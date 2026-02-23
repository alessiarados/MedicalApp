package com.golazo.medical.ui.shared

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golazo.medical.data.SimulatedData
import com.golazo.medical.data.model.ChatMessage
import com.golazo.medical.data.model.ChatRequest
import com.golazo.medical.data.repository.GolazoRepository
import com.golazo.medical.ui.components.*
import com.golazo.medical.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntelligenceViewModel @Inject constructor(
    private val repository: GolazoRepository
) : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _highlightedNodes = MutableStateFlow<Set<String>>(emptySet())
    val highlightedNodes = _highlightedNodes.asStateFlow()
    private val _filterType = MutableStateFlow<String?>(null)
    val filterType = _filterType.asStateFlow()

    fun sendMessage(text: String) {
        val userMsg = ChatMessage("user", text)
        _messages.value = _messages.value + userMsg
        viewModelScope.launch {
            _isLoading.value = true
            repository.chat(
                ChatRequest(
                    message = text,
                    conversationHistory = _messages.value
                )
            ).onSuccess { resp ->
                val assistantMsg = ChatMessage("assistant", resp.response)
                _messages.value = _messages.value + assistantMsg
                // Process graph actions
                resp.graphActions.forEach { action ->
                    when {
                        action.startsWith("highlight:") -> {
                            val nodeId = action.removePrefix("highlight:")
                            _highlightedNodes.value = _highlightedNodes.value + nodeId
                        }
                        action.startsWith("filter:") -> {
                            _filterType.value = action.removePrefix("filter:")
                        }
                        action == "clear" -> {
                            _highlightedNodes.value = emptySet()
                            _filterType.value = null
                        }
                    }
                }
            }.onFailure {
                val errorMsg = ChatMessage("assistant", "Sorry, I couldn't process that request. Please try again.")
                _messages.value = _messages.value + errorMsg
            }
            _isLoading.value = false
        }
    }

    fun selectNode(nodeId: String) {
        _highlightedNodes.value = setOf(nodeId)
    }

    fun setFilter(type: String?) {
        _filterType.value = type
    }
}

@Composable
fun IntelligenceScreen(
    onBack: () -> Unit,
    viewModel: IntelligenceViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val highlightedNodes by viewModel.highlightedNodes.collectAsStateWithLifecycle()
    val filterType by viewModel.filterType.collectAsStateWithLifecycle()
    var chatInput by remember { mutableStateOf("") }
    var showChat by remember { mutableStateOf(false) }

    val nodeTypes = SimulatedData.graphNodes.map { it.type }.distinct()
    val filteredNodes = if (filterType != null) {
        SimulatedData.graphNodes.filter { it.type == filterType }
    } else {
        SimulatedData.graphNodes
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        GolazoTopBar(
            title = "Intelligence Platform",
            onBack = onBack,
            actions = {
                IconButton(onClick = { showChat = !showChat }) {
                    Icon(
                        if (showChat) Icons.Default.Map else Icons.Default.Chat,
                        "Toggle",
                        tint = White
                    )
                }
            }
        )

        // Node type filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FilterChip(
                selected = filterType == null,
                onClick = { viewModel.setFilter(null) },
                label = { Text("All", fontSize = 9.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = UefaBlue,
                    selectedLabelColor = White
                )
            )
            nodeTypes.take(4).forEach { type ->
                FilterChip(
                    selected = filterType == type,
                    onClick = { viewModel.setFilter(if (filterType == type) null else type) },
                    label = { Text(type.replaceFirstChar { it.uppercase() }, fontSize = 9.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = UefaBlue,
                        selectedLabelColor = White
                    )
                )
            }
        }

        if (showChat) {
            // Chat Panel
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    reverseLayout = true
                ) {
                    if (isLoading) {
                        item {
                            Row {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = UefaBlue)
                                Spacer(Modifier.width(8.dp))
                                Text("Thinking...", fontSize = 12.sp, color = TextSecondary)
                            }
                        }
                    }
                    items(messages.reversed()) { msg ->
                        val isUser = msg.role == "user"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isUser) UefaBlue else White,
                                shadowElevation = 1.dp,
                                modifier = Modifier.widthIn(max = 280.dp)
                            ) {
                                Text(
                                    msg.content,
                                    fontSize = 12.sp,
                                    color = if (isUser) White else TextPrimary,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }

                // Chat input
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 80.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GolazoTextField(
                        value = chatInput,
                        onValueChange = { chatInput = it },
                        label = "Ask about the graph...",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (chatInput.isNotBlank()) {
                                viewModel.sendMessage(chatInput)
                                chatInput = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Send, "Send", tint = UefaBlue)
                    }
                }
            }
        } else {
            // Graph Visualization
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(bottom = 64.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw edges
                    SimulatedData.graphEdges.forEach { edge ->
                        val fromNode = SimulatedData.graphNodes.find { it.id == edge.from }
                        val toNode = SimulatedData.graphNodes.find { it.id == edge.to }
                        if (fromNode != null && toNode != null) {
                            val bothVisible = filteredNodes.any { it.id == fromNode.id } && filteredNodes.any { it.id == toNode.id }
                            if (bothVisible) {
                                drawLine(
                                    color = Color.LightGray,
                                    start = Offset(fromNode.x * size.width, fromNode.y * size.height),
                                    end = Offset(toNode.x * size.width, toNode.y * size.height),
                                    strokeWidth = 1.5f
                                )
                            }
                        }
                    }

                    // Draw nodes
                    filteredNodes.forEach { node ->
                        val isHighlighted = highlightedNodes.contains(node.id)
                        val radius = if (isHighlighted) 22f else 16f
                        val color = Color(node.color)
                        drawCircle(
                            color = if (isHighlighted) color else color.copy(alpha = 0.7f),
                            radius = radius,
                            center = Offset(node.x * size.width, node.y * size.height)
                        )
                        if (isHighlighted) {
                            drawCircle(
                                color = color.copy(alpha = 0.3f),
                                radius = radius + 8f,
                                center = Offset(node.x * size.width, node.y * size.height)
                            )
                        }
                    }
                }

                // Node labels overlay
                filteredNodes.forEach { node ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Text(
                            node.label,
                            fontSize = 8.sp,
                            fontWeight = if (highlightedNodes.contains(node.id)) FontWeight.Bold else FontWeight.Normal,
                            color = TextPrimary,
                            modifier = Modifier
                                .offset(
                                    x = (node.x * 300).dp,
                                    y = (node.y * 500 + 18).dp
                                )
                                .clickable { viewModel.selectNode(node.id) }
                        )
                    }
                }

                // Legend
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .background(White.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text("Legend", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    nodeTypes.forEach { type ->
                        val node = SimulatedData.graphNodes.find { it.type == type }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Canvas(modifier = Modifier.size(8.dp)) {
                                drawCircle(Color(node?.color ?: 0xFF000000))
                            }
                            Spacer(Modifier.width(4.dp))
                            Text(type.replaceFirstChar { it.uppercase() }, fontSize = 8.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}
