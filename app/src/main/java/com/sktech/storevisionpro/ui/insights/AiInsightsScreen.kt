package com.sktech.storevisionpro.ui.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sktech.storevisionpro.domain.model.AiInsightItem
import com.sktech.storevisionpro.domain.model.SuggestionStatus
import com.sktech.storevisionpro.ui.components.DoubleBezelCard
import com.sktech.storevisionpro.ui.state.StoreUiState
import com.sktech.storevisionpro.ui.state.StoreViewModel
import com.sktech.storevisionpro.ui.theme.*

@Composable
fun AiInsightsScreen(
    uiState: StoreUiState,
    viewModel: StoreViewModel,
    onNavigateToHealthCheck: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Headline
        item {
            Column {
                Text(
                    text = "AI Business Insights",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                Text(
                    text = "Predictive Dynamic Pricing, Reorder Forecasting & Combos",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        // Business Health Check Feature Card
        item {
            DoubleBezelCard(
                outerCornerRadius = 16.dp,
                innerCornerRadius = 12.dp,
                shellColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                onClick = onNavigateToHealthCheck
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${uiState.healthScore}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Business Health Check",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "FAIR",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WarningAmber
                                    )
                                )
                            }
                        }
                        Text(
                            text = "Gross margin is 4.8% below benchmark. Tap to view full diagnostic audit.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open Health Check",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Actionable Proposals (Owner Approval Required)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                Text(
                    text = "${uiState.insights.count { it.status == SuggestionStatus.PENDING }} Pending",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = WarningAmber
                    )
                )
            }
        }

        // List of Suggestion Cards with strict HITL controls
        items(uiState.insights, key = { it.id }) { item ->
            AiSuggestionActionCard(
                insight = item,
                onAccept = { viewModel.acceptSuggestion(item.id) },
                onReject = { viewModel.rejectSuggestion(item.id) }
            )
        }
    }
}

@Composable
fun AiSuggestionActionCard(
    insight: AiInsightItem,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    DoubleBezelCard(
        modifier = Modifier.fillMaxWidth(),
        outerCornerRadius = 14.dp,
        innerCornerRadius = 10.dp,
        innerPadding = 14.dp
    ) {
        // Provenance Tag
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = insight.sourceProvenance,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = PrecisionAzure
                    )
                )
            }

            val statusBadge = when (insight.status) {
                SuggestionStatus.PENDING -> "PENDING APPROVAL" to WarningAmber
                SuggestionStatus.ACCEPTED -> "APPROVED" to MaterialTheme.colorScheme.primary
                SuggestionStatus.EDITED -> "EDITED" to PrecisionAzure
                SuggestionStatus.REJECTED -> "REJECTED" to SignalCrimson
            }

            Text(
                text = statusBadge.first,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = statusBadge.second
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Title & Explanation
        Text(
            text = insight.title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = insight.explanation,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Proposed Action Box
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "Proposed Action:",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Text(
                    text = insight.proposedActionText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                if (insight.estimatedFinancialImpact != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Expected Financial Impact: ${insight.estimatedFinancialImpact}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Human In The Loop Controls
        if (insight.status == SuggestionStatus.PENDING) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Accept",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }

                OutlinedButton(
                    onClick = onReject,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SignalCrimson),
                    shape = RoundedCornerShape(8.dp),
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
        } else if (insight.status == SuggestionStatus.ACCEPTED) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Approved by Owner. Action committed to store catalog and logged.",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary)
                )
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = null,
                    tint = SignalCrimson,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Rejected by Owner. No changes applied.",
                    style = MaterialTheme.typography.labelSmall.copy(color = SignalCrimson)
                )
            }
        }
    }
}
