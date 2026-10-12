package com.sktech.storevisionpro.ui.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import com.sktech.storevisionpro.domain.model.BillItem
import com.sktech.storevisionpro.domain.model.ProductItem
import com.sktech.storevisionpro.ui.state.StoreUiState
import com.sktech.storevisionpro.ui.state.StoreViewModel
import com.sktech.storevisionpro.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SalesEntryScreen(
    uiState: StoreUiState,
    viewModel: StoreViewModel,
    onNavigateBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf("CASH") }
    var isScannerOpen by remember { mutableStateOf(false) }
    val timeFormatter = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val formattedTime = remember(uiState.selectedSaleTimestamp) {
        timeFormatter.format(Date(uiState.selectedSaleTimestamp))
    }

    val subtotal = remember(uiState.currentBillItems) {
        uiState.currentBillItems.sumOf { it.lineTotal }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark)
    ) {
        // Top Operational Header
        Surface(
            color = ZincCharcoal,
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateDivider),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
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
                                tint = OffWhitePearl
                            )
                        }
                        Text(
                            text = "Point of Sale (POS)",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = OffWhitePearl
                            )
                        )
                    }

                    // Editable Timestamp Chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ZincMetal)
                            .border(1.dp, SlateDivider, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = MutedSilver
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Customer Counter & "Visited, No Purchase" Footfall Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Customer Count:",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MutedSilver)
                        )
                        // Split Stepper [-] 1 [+]
                        Surface(
                            color = ZincMetal,
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SlateDivider)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable { viewModel.decrementCustomerCount() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "-", style = MaterialTheme.typography.titleMedium, color = OffWhitePearl)
                                }
                                Text(
                                    text = "${uiState.customerCount}",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = OffWhitePearl
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable { viewModel.incrementCustomerCount() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "+", style = MaterialTheme.typography.titleMedium, color = OffWhitePearl)
                                }
                            }
                        }
                    }

                    // Visited, No Purchase Button
                    OutlinedButton(
                        onClick = { viewModel.recordVisitedNoPurchase() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MutedSilver),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlateDivider),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonOff,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "No Purchase (${uiState.visitedNoPurchaseCount})",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                        )
                    }
                }
            }
        }

        // Quick Product Catalog Strip
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tap to Add Item:",
                    style = MaterialTheme.typography.labelSmall.copy(color = MutedSilver)
                )
                Surface(
                    color = ZincMetal,
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateDivider),
                    modifier = Modifier.clickable { isScannerOpen = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scan Barcode",
                            tint = ElectricEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Scan Barcode",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ElectricEmerald,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(uiState.products) { product ->
                    Surface(
                        color = ZincMetal,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlateDivider),
                        modifier = Modifier.clickable { viewModel.addProductToBill(product) }
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = OffWhitePearl
                                ),
                                maxLines = 1
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "₹${product.sellingPrice.toInt()}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricEmerald
                                    )
                                )
                                Text(
                                    text = "Stock: ${product.currentStock.toInt()}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (product.currentStock <= product.lowStockThreshold) SignalCrimson else DimGhost,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Bill Items Table
        Text(
            text = "Active Bill Items (${uiState.currentBillItems.size})",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = OffWhitePearl
            ),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (uiState.currentBillItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ZincCharcoal)
                            .border(1.dp, SlateDivider, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = DimGhost,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Bill is currently empty",
                                style = MaterialTheme.typography.bodyMedium.copy(color = MutedSilver)
                            )
                            Text(
                                text = "Tap any catalog item above or scan a barcode",
                                style = MaterialTheme.typography.labelSmall.copy(color = DimGhost)
                            )
                        }
                    }
                }
            } else {
                items(uiState.currentBillItems) { billItem ->
                    BillRowItem(
                        item = billItem,
                        onAdd = { viewModel.addProductToBill(billItem.product) },
                        onRemove = { viewModel.removeProductFromBill(billItem.product.id) }
                    )
                }
            }
        }

        // Bottom Pinned Checkout Summary Box
        Surface(
            color = ZincCharcoal,
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateDivider),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Payment Method Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("CASH", "UPI", "CARD").forEach { method ->
                        val selected = selectedPaymentMethod == method
                        Surface(
                            color = if (selected) EmeraldMuted else ZincMetal,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (selected) ElectricEmerald else SlateDivider
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedPaymentMethod = method }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = method,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) ElectricEmerald else MutedSilver
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Grand Total Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Grand Total Amount",
                            style = MaterialTheme.typography.labelSmall.copy(color = MutedSilver)
                        )
                        Text(
                            text = "${uiState.currentBillItems.size} Items • ${uiState.customerCount} Customer",
                            style = MaterialTheme.typography.labelSmall.copy(color = DimGhost)
                        )
                    }

                    Text(
                        text = "₹${"%.2f".format(subtotal)}",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = ElectricEmerald
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Checkout Button
                Button(
                    onClick = { viewModel.completeCurrentSale(selectedPaymentMethod) },
                    enabled = uiState.currentBillItems.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricEmerald,
                        disabledContainerColor = ZincMetal
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = OffWhitePearl
                        )
                        Text(
                            text = "Complete Sale & Print Bill",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OffWhitePearl
                            )
                        )
                    }
                }
            }
        }

        if (isScannerOpen) {
            BarcodeScannerSheet(
                onBarcodeScanned = { code ->
                    viewModel.handleScannedBarcode(code)
                    isScannerOpen = false
                },
                onDismiss = { isScannerOpen = false }
            )
        }
    }
}

@Composable
fun BillRowItem(
    item: BillItem,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        color = ZincMetal,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateDivider),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = OffWhitePearl
                    )
                )
                Text(
                    text = "@ ₹${item.product.sellingPrice} / ${item.product.unit}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = MutedSilver
                    )
                )
            }

            // Stepper [-] Qty [+]
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = ZincCharcoal,
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateDivider),
                    modifier = Modifier
                        .size(28.dp)
                        .clickable(onClick = onRemove)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "-", color = OffWhitePearl, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = "${item.quantity.toInt()}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = OffWhitePearl
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Surface(
                    color = ZincCharcoal,
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateDivider),
                    modifier = Modifier
                        .size(28.dp)
                        .clickable(onClick = onAdd)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "+", color = OffWhitePearl, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Line Total
            Text(
                text = "₹${"%.2f".format(item.lineTotal)}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = ElectricEmerald
                )
            )
        }
    }
}
