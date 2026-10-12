package com.sktech.storevisionpro.ui.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.sktech.storevisionpro.pdf.PdfReportGenerator
import com.sktech.storevisionpro.ui.state.StoreUiState
import com.sktech.storevisionpro.ui.theme.*

@Composable
fun AnalyticsDashboardScreen(
    uiState: StoreUiState,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedPeriod by remember { mutableStateOf("Weekly") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Analytics & Business Intel",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OffWhitePearl
                        )
                    )
                    Text(
                        text = "Performance Diagnostics & Footfall Analysis",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MutedSilver)
                    )
                }

                // PDF Export Button
                Button(
                    onClick = {
                        val reportFile = PdfReportGenerator.generateBusinessReport(
                            context = context,
                            shopName = uiState.currentShopName,
                            shopType = uiState.shopType.displayName,
                            period = selectedPeriod,
                            totalRevenue = uiState.todayRevenue,
                            netProfit = uiState.todayProfit,
                            totalCustomers = uiState.todayCustomers,
                            sales = uiState.salesHistory,
                            products = uiState.products
                        )
                        if (reportFile != null) {
                            Toast.makeText(context, "Report PDF Generated Successfully", Toast.LENGTH_SHORT).show()
                            PdfReportGenerator.sharePdfReport(context, reportFile)
                        } else {
                            Toast.makeText(context, "Failed to compile PDF report", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZincMetal),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateDivider),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "PDF Export",
                        tint = ElectricEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Export PDF",
                        style = MaterialTheme.typography.labelSmall.copy(color = OffWhitePearl)
                    )
                }
            }
        }

        // Period Segment Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ZincCharcoal)
                    .border(1.dp, SlateDivider, RoundedCornerShape(8.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Daily", "Weekly", "Monthly", "Yearly").forEach { period ->
                    val selected = selectedPeriod == period
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (selected) ZincMetal else Color.Transparent)
                            .clickable { selectedPeriod = period }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = period,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) ElectricEmerald else MutedSilver
                            )
                        )
                    }
                }
            }
        }

        // Section: Income Trend Chart (Custom Canvas Curve)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ZincCharcoal),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateDivider),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Revenue & Margin Curve",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OffWhitePearl
                                )
                            )
                            Text(
                                text = "Daily Aggregated Trend",
                                style = MaterialTheme.typography.labelSmall.copy(color = MutedSilver)
                            )
                        }

                        Text(
                            text = "₹14,250 Peak",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = ElectricEmerald
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Canvas Line Graph
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    ) {
                        val points = listOf(0.35f, 0.42f, 0.38f, 0.65f, 0.58f, 0.90f, 0.82f)
                        val stepX = size.width / (points.size - 1)
                        val path = Path()

                        points.forEachIndexed { index, fraction ->
                            val x = index * stepX
                            val y = size.height * (1f - fraction)
                            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }

                        // Draw Grid lines
                        drawLine(
                            color = Color(0xFF27272A),
                            start = Offset(0f, size.height * 0.5f),
                            end = Offset(size.width, size.height * 0.5f),
                            strokeWidth = 1f
                        )

                        // Draw trend curve
                        drawPath(
                            path = path,
                            color = ElectricEmerald,
                            style = Stroke(width = 3.dp.toPx())
                        )

                        // Draw Point dots
                        points.forEachIndexed { index, fraction ->
                            val x = index * stepX
                            val y = size.height * (1f - fraction)
                            drawCircle(
                                color = ElectricEmerald,
                                radius = 4.dp.toPx(),
                                center = Offset(x, y)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                            Text(
                                text = day,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = DimGhost,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Section: Hourly Customer Footfall Heatmap (Custom 24-hr Canvas)
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
                        Text(
                            text = "Peak Traffic & Quiet Hours Heatmap",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OffWhitePearl
                            )
                        )
                    }
                    Text(
                        text = "Hourly visitor density to optimize staff break schedules",
                        style = MaterialTheme.typography.labelSmall.copy(color = MutedSilver)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 24 Hour blocks grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        val densities = listOf(
                            0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, // 00-05 Night
                            0.1f, 0.2f, 0.5f, 0.8f, 0.7f, 0.4f, // 06-11 Morning
                            0.2f, 0.1f, 0.1f, 0.3f, 0.6f, 0.9f, // 12-17 Afternoon (Quiet 13-15)
                            1.0f, 0.9f, 0.8f, 0.4f, 0.1f, 0.0f  // 18-23 Evening Peak (18-20)
                        )

                        densities.forEachIndexed { hour, density ->
                            val blockColor = when {
                                density >= 0.8f -> ElectricEmerald
                                density >= 0.5f -> ElectricEmerald.copy(alpha = 0.6f)
                                density >= 0.2f -> ElectricEmerald.copy(alpha = 0.3f)
                                density > 0.0f -> ZincMetal
                                else -> Color(0xFF131316)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(26.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(blockColor)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "6 AM", style = MaterialTheme.typography.labelSmall.copy(color = DimGhost, fontSize = 9.sp))
                        Text(text = "12 PM", style = MaterialTheme.typography.labelSmall.copy(color = DimGhost, fontSize = 9.sp))
                        Text(text = "6 PM (Peak)", style = MaterialTheme.typography.labelSmall.copy(color = WarningAmber, fontSize = 9.sp, fontWeight = FontWeight.Bold))
                        Text(text = "10 PM", style = MaterialTheme.typography.labelSmall.copy(color = DimGhost, fontSize = 9.sp))
                    }
                }
            }
        }

        // Section: Leaderboard (Top 10 Bestsellers vs Dead Stock)
        item {
            Text(
                text = "Product Velocity Leaderboard",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = OffWhitePearl
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                LeaderboardRow(
                    rank = "#1",
                    name = "Amul Gold Milk 500ml",
                    units = "42 units sold",
                    revenue = "₹1,428.00",
                    isTop = true
                )
                LeaderboardRow(
                    rank = "#2",
                    name = "Britannia Good Day 100g",
                    units = "26 units sold",
                    revenue = "₹780.00",
                    isTop = true
                )
                LeaderboardRow(
                    rank = "#3",
                    name = "Fortune Sunflower Oil 1L",
                    units = "12 bottles sold",
                    revenue = "₹1,740.00",
                    isTop = true
                )
                LeaderboardRow(
                    rank = "DEAD",
                    name = "Cadbury Dairy Milk Silk",
                    units = "0 sales in 18 days",
                    revenue = "₹1,184 locked",
                    isTop = false
                )
            }
        }
    }
}

@Composable
fun LeaderboardRow(
    rank: String,
    name: String,
    units: String,
    revenue: String,
    isTop: Boolean
) {
    Surface(
        color = ZincCharcoal,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isTop) SlateDivider else AmberMuted),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (isTop) EmeraldMuted else CrimsonMuted),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = rank,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isTop) ElectricEmerald else SignalCrimson,
                            fontSize = 10.sp
                        )
                    )
                }

                Column {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OffWhitePearl
                        )
                    )
                    Text(
                        text = units,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isTop) MutedSilver else SignalCrimson
                        )
                    )
                }
            }

            Text(
                text = revenue,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (isTop) OffWhitePearl else WarningAmber
                )
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    backgroundColor = 0xFF09090B,
    name = "Analytics & Footfall Curve Preview"
)
@Composable
fun AnalyticsDashboardScreenPreview() {
    StoreVisionProTheme(darkTheme = true) {
        AnalyticsDashboardScreen(
            uiState = StoreUiState(),
            onNavigateBack = {}
        )
    }
}
