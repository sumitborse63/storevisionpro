package com.sktech.storevisionpro.ui.analytics

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
fun AnalyticsDashboardScreen(
    uiState: StoreUiState,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedPeriod by remember { mutableStateOf("Weekly") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                            text = "Analytics & Intel",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Text(
                            text = "Performance & Footfall Analysis",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
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
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "PDF Export",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
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

        // Period Segment Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Daily", "Weekly", "Monthly", "Yearly").forEach { period ->
                    val selected = selectedPeriod == period
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selected) MaterialTheme.colorScheme.surface else Color.Transparent)
                            .clickable { selectedPeriod = period }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = period,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }

        // Section: Income Trend Chart (Double-Bezel Curve)
        item {
            DoubleBezelCard(
                outerCornerRadius = 16.dp,
                innerCornerRadius = 12.dp,
                innerPadding = 14.dp
            ) {
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
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "Daily Aggregated Trend",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

                    Text(
                        text = "₹14,250 Peak",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Canvas Line Graph
                val lineColor = MaterialTheme.colorScheme.primary
                val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)

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
                        color = gridColor,
                        start = Offset(0f, size.height * 0.5f),
                        end = Offset(size.width, size.height * 0.5f),
                        strokeWidth = 1f
                    )

                    // Draw trend curve
                    drawPath(
                        path = path,
                        color = lineColor,
                        style = Stroke(width = 3.dp.toPx())
                    )

                    // Draw Point dots
                    points.forEachIndexed { index, fraction ->
                        val x = index * stepX
                        val y = size.height * (1f - fraction)
                        drawCircle(
                            color = lineColor,
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }

        // Section: Hourly Customer Footfall Heatmap
        item {
            DoubleBezelCard(
                outerCornerRadius = 16.dp,
                innerCornerRadius = 12.dp,
                innerPadding = 14.dp
            ) {
                Text(
                    text = "Peak Traffic & Quiet Hours Heatmap",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "Hourly visitor density to optimize staff break schedules",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
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

                    val activeColor = MaterialTheme.colorScheme.primary
                    val idleColor = MaterialTheme.colorScheme.surfaceVariant

                    densities.forEachIndexed { _, density ->
                        val blockColor = when {
                            density >= 0.8f -> activeColor
                            density >= 0.5f -> activeColor.copy(alpha = 0.6f)
                            density >= 0.2f -> activeColor.copy(alpha = 0.3f)
                            else -> idleColor
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(26.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(blockColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "6 AM", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp))
                    Text(text = "12 PM", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp))
                    Text(text = "6 PM (Peak)", style = MaterialTheme.typography.labelSmall.copy(color = WarningAmber, fontSize = 9.sp, fontWeight = FontWeight.Bold))
                    Text(text = "10 PM", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp))
                }
            }
        }

        // Section: Leaderboard
        item {
            Text(
                text = "Product Velocity Leaderboard",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
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
    DoubleBezelCard(
        modifier = Modifier.fillMaxWidth(),
        outerCornerRadius = 12.dp,
        innerCornerRadius = 9.dp,
        innerPadding = 10.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                        .background(if (isTop) MaterialTheme.colorScheme.primaryContainer else CrimsonMuted),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = rank,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isTop) MaterialTheme.colorScheme.primary else SignalCrimson,
                            fontSize = 10.sp
                        )
                    )
                }

                Column {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = units,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isTop) MaterialTheme.colorScheme.onSurfaceVariant else SignalCrimson
                        )
                    )
                }
            }

            Text(
                text = revenue,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (isTop) MaterialTheme.colorScheme.primary else WarningAmber
                )
            )
        }
    }
}
