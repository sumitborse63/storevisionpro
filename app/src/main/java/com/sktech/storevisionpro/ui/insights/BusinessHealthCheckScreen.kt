package com.sktech.storevisionpro.ui.insights

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sktech.storevisionpro.ui.state.StoreUiState
import com.sktech.storevisionpro.ui.theme.*

@Composable
fun BusinessHealthCheckScreen(
    uiState: StoreUiState,
    onNavigateBack: () -> Unit
) {
    val report = uiState.businessHealthReport ?: remember(uiState.products, uiState.salesHistory, uiState.todayRevenue, uiState.todayProfit, uiState.todayCustomers) {
        com.sktech.storevisionpro.domain.insights.BusinessHealthCheckEngine.evaluateBusinessHealth(
            products = uiState.products,
            sales = uiState.salesHistory,
            totalRevenue = uiState.todayRevenue,
            netProfit = uiState.todayProfit,
            totalCustomers = uiState.todayCustomers
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = OffWhitePearl
                    )
                }
                Column {
                    Text(
                        text = "Business Health Diagnostic",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OffWhitePearl
                        )
                    )
                    Text(
                        text = "Comprehensive Financial, Margin & Retention Audit",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MutedSilver)
                    )
                }
            }
        }

        // Radial Gauge Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ZincCharcoal),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateDivider),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Background Arc
                            drawArc(
                                color = Color(0xFF27272A),
                                startAngle = 135f,
                                sweepAngle = 270f,
                                useCenter = false,
                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                            )
                            // Health Arc
                            drawArc(
                                color = if (report.overallScore >= 70) ElectricEmerald else WarningAmber,
                                startAngle = 135f,
                                sweepAngle = 270f * (report.overallScore / 100f),
                                useCenter = false,
                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${report.overallScore}",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 38.sp,
                                    color = if (report.overallScore >= 70) ElectricEmerald else WarningAmber
                                )
                            )
                            Text(
                                text = "Score: ${report.ratingLabel}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (report.overallScore >= 70) ElectricEmerald else WarningAmber
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Store Operating at ${report.overallScore}% Efficiency",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OffWhitePearl
                        )
                    )
                    Text(
                        text = report.summary,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 12.sp,
                            color = MutedSilver
                        )
                    )
                }
            }
        }

        // Diagnostic Pillars Breakdown
        item {
            Text(
                text = "Diagnostic Pillar Breakdown",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = OffWhitePearl
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                report.pillars.forEach { pillar ->
                    HealthPillarRow(
                        title = pillar.title,
                        rating = pillar.currentValueFormatted,
                        benchmark = pillar.benchmarkFormatted,
                        statusText = pillar.status,
                        isPositive = pillar.isHealthy,
                        explanation = pillar.explanation
                    )
                }
            }
        }

        // Recommended Strategic Action Plan
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ZincCharcoal),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateDivider),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = WarningAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Recommended Remediation Strategies",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OffWhitePearl
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    report.strategicRemediations.forEachIndexed { idx, remediation ->
                        Text(
                            text = "${idx + 1}. $remediation",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 12.sp,
                                color = MutedSilver,
                                lineHeight = 18.sp
                            )
                        )
                        if (idx < report.strategicRemediations.lastIndex) {
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HealthPillarRow(
    title: String,
    rating: String,
    benchmark: String,
    statusText: String,
    isPositive: Boolean,
    explanation: String
) {
    Surface(
        color = ZincCharcoal,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isPositive) SlateDivider else AmberMuted),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = OffWhitePearl
                    )
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isPositive) EmeraldMuted else AmberMuted)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isPositive) ElectricEmerald else WarningAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = rating,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (isPositive) ElectricEmerald else WarningAmber
                    )
                )
                Text(
                    text = "($benchmark)",
                    style = MaterialTheme.typography.labelSmall.copy(color = DimGhost)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = explanation,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 12.sp,
                    color = MutedSilver,
                    lineHeight = 16.sp
                )
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    backgroundColor = 0xFF09090B,
    name = "Business Health Radial Gauge Preview"
)
@Composable
fun BusinessHealthCheckScreenPreview() {
    StoreVisionProTheme(darkTheme = true) {
        BusinessHealthCheckScreen(
            uiState = StoreUiState(),
            onNavigateBack = {}
        )
    }
}
