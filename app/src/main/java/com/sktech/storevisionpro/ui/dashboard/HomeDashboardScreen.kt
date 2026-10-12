package com.sktech.storevisionpro.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sktech.storevisionpro.domain.model.SaleRecord
import com.sktech.storevisionpro.ui.state.StoreUiState
import com.sktech.storevisionpro.ui.theme.*

@Composable
fun HomeDashboardScreen(
    uiState: StoreUiState,
    onNavigateToSalesEntry: () -> Unit,
    onNavigateToProducts: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToInsights: () -> Unit,
    onOpenVoiceAssistant: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section: Top Headline & Live Clock Status
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Store Command Center",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OffWhitePearl
                        )
                    )
                    Text(
                        text = "Operational Overview • Active Shifts",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MutedSilver)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ZincMetal)
                        .border(1.dp, SlateDivider, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "LIVE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ElectricEmerald,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }
        }

        // Section: 4-Stat Hero Bento Grid (Asymmetric layout per stitch-design-taste)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Bento Card 1: Today's Revenue
                    BentoStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Today's Revenue",
                        value = "₹${"%,d".format(uiState.todayRevenue.toInt())}",
                        subtitle = "+14.8% vs Yesterday",
                        valueColor = ElectricEmerald,
                        icon = Icons.AutoMirrored.Filled.TrendingUp
                    )

                    // Bento Card 2: Customers & Footfall
                    BentoStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Footfall / Bills",
                        value = "${uiState.todayCustomers} Customers",
                        subtitle = "${uiState.visitedNoPurchaseCount} Visited (No Buy)",
                        valueColor = OffWhitePearl,
                        icon = Icons.Default.People
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Bento Card 3: Top Moving Item
                    BentoStatCard(
                        modifier = Modifier.weight(1.2f),
                        title = "Top Bestseller Today",
                        value = uiState.topSellingProduct,
                        subtitle = "High Velocity FMCG",
                        valueColor = PrecisionAzure,
                        icon = Icons.Default.Stars
                    )

                    // Bento Card 4: Net Margin
                    BentoStatCard(
                        modifier = Modifier.weight(0.8f),
                        title = "Net Margin",
                        value = "₹${"%,d".format(uiState.todayProfit.toInt())}",
                        subtitle = "23.9% Health",
                        valueColor = WarningAmber,
                        icon = Icons.Default.PieChart
                    )
                }
            }
        }

        // Section: Quick Action Row
        item {
            Text(
                text = "Quick Shift Actions",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = OffWhitePearl
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionButton(
                    modifier = Modifier.weight(1f),
                    title = "New Sale",
                    icon = Icons.Default.AddShoppingCart,
                    accent = true,
                    onClick = onNavigateToSalesEntry
                )
                QuickActionButton(
                    modifier = Modifier.weight(1f),
                    title = "Add Product",
                    icon = Icons.Default.Inventory2,
                    accent = false,
                    onClick = onNavigateToProducts
                )
                QuickActionButton(
                    modifier = Modifier.weight(1f),
                    title = "Voice Log",
                    icon = Icons.Default.Mic,
                    accent = false,
                    onClick = onOpenVoiceAssistant
                )
            }
        }

        // Section: Dynamic Priority Alert Card (Near Expiry / Pre-peak warning)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ZincMetal),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberMuted),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToInsights() }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AmberMuted),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = WarningAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Near-Expiry Restock Action Required",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = OffWhitePearl
                            )
                        )
                        Text(
                            text = "Amul Milk Batch B-2610A expires in 48 hours. Tap to review AI clearance combo.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 12.sp,
                                color = MutedSilver
                            )
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Review",
                        tint = MutedSilver
                    )
                }
            }
        }

        // Section: Live Recent Transactions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = OffWhitePearl
                    )
                )
                Text(
                    text = "View Analytics",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ElectricEmerald,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.clickable { onNavigateToAnalytics() }
                )
            }
        }

        items(uiState.salesHistory.take(4).size) { index ->
            val sale = uiState.salesHistory[index]
            RecentSaleRowItem(sale = sale)
        }
    }
}

@Composable
fun BentoStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    valueColor: androidx.compose.ui.graphics.Color,
    icon: ImageVector
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = ZincCharcoal),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateDivider)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MutedSilver,
                        fontSize = 11.sp
                    )
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MutedSilver,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = valueColor
                ),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = DimGhost,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
fun QuickActionButton(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    accent: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (accent) ElectricEmerald else ZincMetal,
            contentColor = OffWhitePearl
        ),
        shape = RoundedCornerShape(10.dp),
        border = if (accent) null else androidx.compose.foundation.BorderStroke(1.dp, SlateDivider),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (accent) OffWhitePearl else ElectricEmerald
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            )
        }
    }
}

@Composable
fun RecentSaleRowItem(sale: SaleRecord) {
    Surface(
        color = ZincCharcoal,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateDivider),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = sale.saleNumber,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = OffWhitePearl
                        )
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(ZincMetal)
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = sale.paymentMethod,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                color = PrecisionAzure,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                Text(
                    text = "${sale.items.size} Items • ${sale.customerCount} Customer",
                    style = MaterialTheme.typography.labelSmall.copy(color = MutedSilver)
                )
            }

            Text(
                text = "₹${"%.2f".format(sale.totalAmount)}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = ElectricEmerald
                )
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    backgroundColor = 0xFF09090B,
    name = "Command Center Bento Preview"
)
@Composable
fun HomeDashboardScreenPreview() {
    StoreVisionProTheme(darkTheme = true) {
        HomeDashboardScreen(
            uiState = StoreUiState(),
            onNavigateToSalesEntry = {},
            onNavigateToProducts = {},
            onNavigateToAnalytics = {},
            onNavigateToInsights = {},
            onOpenVoiceAssistant = {}
        )
    }
}

