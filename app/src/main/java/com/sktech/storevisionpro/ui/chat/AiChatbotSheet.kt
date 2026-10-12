package com.sktech.storevisionpro.ui.chat

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sktech.storevisionpro.domain.model.ChatMessage
import com.sktech.storevisionpro.domain.model.PendingConfirmationData
import com.sktech.storevisionpro.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatbotSheet(
    messages: List<ChatMessage>,
    onSendMessage: (String) -> Unit,
    onConfirmAction: (PendingConfirmationData) -> Unit,
    onRejectAction: (PendingConfirmationData) -> Unit,
    onDismiss: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        },
        modifier = Modifier.fillMaxHeight(0.88f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "StoreVision AI Assistant",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "AGENTIC AI",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                    Text(
                        text = "Scoped to active branch • Owner approval enforced",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), thickness = 1.dp)

            // Chat Message Stream
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatBubbleItem(
                        message = msg,
                        onConfirm = { onConfirmAction(it) },
                        onReject = { onRejectAction(it) }
                    )
                }
            }

            // Quick Prompt Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickPromptChip(text = "Check Milk Stock") {
                    onSendMessage("Check Milk Stock")
                }
                QuickPromptChip(text = "Today's Profit") {
                    onSendMessage("Today's Profit")
                }
                QuickPromptChip(text = "Log 40 Tata Salt") {
                    onSendMessage("Received 40 packets of Tata Salt for 880 rupees today")
                }
            }

            // Interactive Input Bar
            Surface(
                color = ZincMetal,
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateDivider),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    // Voice Mic Button
                    IconButton(
                        onClick = {
                            isRecording = !isRecording
                            if (isRecording) {
                                // Simulate speech-to-text input after brief moment
                                coroutineScope.launch {
                                    kotlinx.coroutines.delay(1800)
                                    isRecording = false
                                    onSendMessage("Received 40 units of Tata Salt from Wholesale Hub for ₹880 cash")
                                }
                            }
                        }
                    ) {
                        val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
                        val scale by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 1.25f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(600, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "scale"
                        )

                        Icon(
                            imageVector = if (isRecording) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Voice Log",
                            tint = if (isRecording) SignalCrimson else ElectricEmerald,
                            modifier = if (isRecording) Modifier.size(24.dp * scale) else Modifier.size(24.dp)
                        )
                    }

                    TextField(
                        value = if (isRecording) "Listening to speech..." else inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = "Ask stock, metrics, or speak voice log...",
                                style = MaterialTheme.typography.bodyMedium.copy(color = DimGhost)
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = ElectricEmerald,
                            focusedTextColor = OffWhitePearl,
                            unfocusedTextColor = OffWhitePearl
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        enabled = !isRecording
                    )

                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                onSendMessage(inputText)
                                inputText = ""
                            }
                        },
                        enabled = inputText.isNotBlank() && !isRecording
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (inputText.isNotBlank()) ElectricEmerald else DimGhost
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubbleItem(
    message: ChatMessage,
    onConfirm: (PendingConfirmationData) -> Unit,
    onReject: (PendingConfirmationData) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isUser) 16.dp else 4.dp,
                        bottomEnd = if (message.isUser) 4.dp else 16.dp
                    )
                )
                .background(if (message.isUser) ZincMetal else ZincCharcoal)
                .border(
                    width = 1.dp,
                    color = if (message.isUser) SlateDivider else EmeraldBorder.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = OffWhitePearl,
                    lineHeight = 19.sp
                )
            )
        }

        // Render Confirmation Card if structured action is proposed
        if (message.pendingConfirmation != null) {
            Spacer(modifier = Modifier.height(8.dp))
            StructuredConfirmationCard(
                data = message.pendingConfirmation,
                onConfirm = { onConfirm(message.pendingConfirmation) },
                onReject = { onReject(message.pendingConfirmation) }
            )
        }
    }
}

@Composable
fun StructuredConfirmationCard(
    data: PendingConfirmationData,
    onConfirm: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ZincMetal),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateDivider),
        modifier = Modifier
            .widthIn(max = 320.dp)
            .padding(top = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = WarningAmber,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = OffWhitePearl
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            data.proposedDetails.forEach { (key, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = key,
                        style = MaterialTheme.typography.labelSmall.copy(color = MutedSilver)
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = OffWhitePearl
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (!data.isConfirmed && !data.isRejected) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricEmerald),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Confirm & Log",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OffWhitePearl
                            )
                        )
                    }

                    OutlinedButton(
                        onClick = onReject,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SignalCrimson),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Reject",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SignalCrimson
                            )
                        )
                    }
                }
            } else if (data.isConfirmed) {
                Text(
                    text = "✓ Confirmed and committed by Owner",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ElectricEmerald,
                        fontWeight = FontWeight.Bold
                    )
                )
            } else {
                Text(
                    text = "✗ Discarded by Owner",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SignalCrimson,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun QuickPromptChip(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(ZincMetal)
            .border(1.dp, SlateDivider, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                color = MutedSilver
            )
        )
    }
}
