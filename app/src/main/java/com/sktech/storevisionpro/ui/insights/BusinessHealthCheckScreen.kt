package com.sktech.storevisionpro.ui.insights

import android.widget.Toast
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
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sktech.storevisionpro.pdf.PdfReportGenerator
import com.sktech.storevisionpro.ui.components.DoubleBezelCard
import com.sktech.storevisionpro.ui.state.StoreUiState
import com.sktech.storevisionpro.ui.theme.*

@Composable
fun BusinessHealthCheckScreen(
    uiState: StoreUiState,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val report = uiState.businessHealthReport ?: remember(
        uiState.products,
        uiState.salesHistory,
        uiState.todayRevenue,
        uiState.todayProfit,
        uiState.todayCustomers
    ) {
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
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Column {
                        Text(
                            text = "Business Health Diagnostic",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Text(
                            text = "Comprehensive Financial & Margin Audit",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                // Export PDF Button
                Button(
                    onClick = {
                        val pdf = PdfReportGenerator.generateBusinessReport(
                            context = context,
                            shopName = uiState.currentShopName,
                            shopType = uiState.shopType.displayName,
                            period = "Diagnostic Report",
                            totalRevenue = uiState.todayRevenue,
                            netProfit = uiState.todayProfit,
                            totalCustomers = uiState.todayCustomers,
                            sales = uiState.salesHistory,
                            products = uiState.products
                        )
                        if (pdf != null) {
                            PdfReportGenerator.sharePdfReport(context, pdf)
                        } else {
                            Toast.makeText(context, "Failed to compile PDF report", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = "Export PDF",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Export PDF",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }

        // Radial Gauge Double-Bezel Card
        item {
            DoubleBezelCard(
                outerCornerRadius = 18.dp,
                innerCornerRadius = 14.dp,
                innerPadding = 18.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val arcBgColor = MaterialTheme.colorScheme.surfaceVariant
                    val arcHighlightColor = if (report.overallScore >= 70) MaterialTheme.colorScheme.primary else WarningAmber

                    Box(
                        modifier = Modifier.size(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Background Arc
                            drawArc(
                                color = arcBgColor,
                                startAngle = 135f,
                                sweepAngle = 270f,
                                useCenter = false,
                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                            )
                            // Health Arc
                            drawArc(
                                color = arcHighlightColor,
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
                                    color = arcHighlightColor
                                )
                            )
                            Text(
                                text = "Score: ${report.ratingLabel}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = arcHighlightColor
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Store Operating at ${report.overallScore}% Efficiency",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = report.summary,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

        // Diagnostic Pillars Breakdown Header
        item {
            Text(
                text = "Diagnostic Pillar Breakdown",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Spacer(modifier = Modifier.height(4.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
            DoubleBezelCard(
                outerCornerRadius = 16.dp,
                innerCornerRadius = 12.dp,
                shellColor = AmberMuted.copy(alpha = 0.35f),
                borderColor = WarningAmber.copy(alpha = 0.4f)
            ) {
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
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                report.strategicRemediations.forEachIndexed { idx, remediation ->
                    Text(
                        text = "${idx + 1}. $remediation",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
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

@Composable
fun HealthPillarRow(
    title: String,
    rating: String,
    benchmark: String,
    statusText: String,
    isPositive: Boolean,
    explanation: String
) {
    DoubleBezelCard(
        modifier = Modifier.fillMaxWidth(),
        outerCornerRadius = 12.dp,
        innerCornerRadius = 9.dp,
        innerPadding = 12.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isPositive) MaterialTheme.colorScheme.primaryContainer else AmberMuted)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isPositive) MaterialTheme.colorScheme.primary else WarningAmber,
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
                    color = if (isPositive) MaterialTheme.colorScheme.primary else WarningAmber
                )
            )
            Text(
                text = "($benchmark)",
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = explanation,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        )
    }
}
