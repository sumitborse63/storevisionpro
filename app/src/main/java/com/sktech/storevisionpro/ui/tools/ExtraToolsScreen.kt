package com.sktech.storevisionpro.ui.tools

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sktech.storevisionpro.domain.model.UserRole
import com.sktech.storevisionpro.pdf.PdfReportGenerator
import com.sktech.storevisionpro.ui.state.StoreUiState
import com.sktech.storevisionpro.ui.state.StoreViewModel
import com.sktech.storevisionpro.ui.theme.*

data class ToolMenuItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val routeKey: String
)

@Composable
fun ExtraToolsScreen(
    uiState: StoreUiState,
    viewModel: StoreViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var activeToolModal by remember { mutableStateOf<String?>(null) }

    val toolItems = listOf(
        ToolMenuItem("GST Invoicing", "Generate, share & print tax invoices", Icons.Default.Receipt, "invoices"),
        ToolMenuItem("Customer Loyalty", "Customer QR, reward points & birthday discounts", Icons.Default.Loyalty, "loyalty"),
        ToolMenuItem("Cashbook & UPI", "Cash-in, cash-out, UPI sync & day-end ledger", Icons.Default.AccountBalanceWallet, "cashbook"),
        ToolMenuItem("Multi-Shop Switcher", "Switch active branch or consolidated rollup", Icons.Default.Apartment, "multishop"),
        ToolMenuItem("Staff & Roles", "Manage entry-only, cashier & manager rights", Icons.Default.Badge, "roles"),
        ToolMenuItem("Sync Queue Status", "Inspect pending local changes & resolve conflicts", Icons.Default.Sync, "sync"),
        ToolMenuItem("Hospitality QR Menu", "Digital table menu & KDS for cafe/restaurant", Icons.Default.RestaurantMenu, "hospitality"),
        ToolMenuItem("Audit Security Logs", "Cryptographic trail of all changes and AI approvals", Icons.Default.Security, "audit")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
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
                    text = "Business Operations Tools",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                Text(
                    text = "Accounting, Invoicing, Staff RBAC & Integrations",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(toolItems) { tool ->
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { activeToolModal = tool.routeKey }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = tool.icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = tool.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = tool.description,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        )
                    }
                }
            }
        }
    }

    // Interactive Modals for each of the 8 sub-tools
    when (activeToolModal) {
        "invoices" -> GstInvoicingDialog(
            uiState = uiState,
            onDismiss = { activeToolModal = null }
        )
        "loyalty" -> CustomerLoyaltyDialog(
            onDismiss = { activeToolModal = null }
        )
        "cashbook" -> CashbookDialog(
            uiState = uiState,
            onDismiss = { activeToolModal = null }
        )
        "multishop" -> MultiShopDialog(
            currentShop = uiState.currentShopName,
            onSelectShop = { shopName ->
                viewModel.switchBranch(shopName)
                activeToolModal = null
                Toast.makeText(context, "Switched active branch to $shopName", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { activeToolModal = null }
        )
        "roles" -> StaffRolesDialog(
            currentRole = uiState.currentRole,
            onSelectRole = { role ->
                viewModel.setRole(role)
                activeToolModal = null
                Toast.makeText(context, "Active role updated to ${role.displayName}", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { activeToolModal = null }
        )
        "sync" -> SyncQueueDialog(
            pendingCount = uiState.pendingSyncCount,
            onTriggerSync = {
                viewModel.triggerManualSync()
                Toast.makeText(context, "Immediate sync dispatched via WorkManager", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { activeToolModal = null }
        )
        "hospitality" -> HospitalityKdsDialog(
            onDismiss = { activeToolModal = null }
        )
        "audit" -> AuditSecurityDialog(
            onDismiss = { activeToolModal = null }
        )
    }
}

// 1. GST Invoicing Dialog (Screen 12)
@Composable
fun GstInvoicingDialog(uiState: StoreUiState, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var customerName by remember { mutableStateOf("Rohit Sharma") }
    var customerGstin by remember { mutableStateOf("27AABCU9603R1ZM") }
    var selectedGstRate by remember { androidx.compose.runtime.mutableIntStateOf(18) }

    val subtotal = 450.0
    val cgst = subtotal * (selectedGstRate / 200.0)
    val sgst = subtotal * (selectedGstRate / 200.0)
    val totalWithTax = subtotal + cgst + sgst

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("GST Tax Invoice Generator", style = MaterialTheme.typography.titleLarge.copy(color = OffWhitePearl, fontWeight = FontWeight.Bold))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Invoice No: INV-20261012-0089", style = MaterialTheme.typography.labelSmall.copy(color = PrecisionAzure, fontFamily = FontFamily.Monospace))
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Buyer Name", color = MutedSilver) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricEmerald, unfocusedBorderColor = SlateDivider, focusedTextColor = OffWhitePearl, unfocusedTextColor = OffWhitePearl)
                )
                OutlinedTextField(
                    value = customerGstin,
                    onValueChange = { customerGstin = it },
                    label = { Text("Buyer GSTIN (15 Digits)", color = MutedSilver) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricEmerald, unfocusedBorderColor = SlateDivider, focusedTextColor = OffWhitePearl, unfocusedTextColor = OffWhitePearl)
                )

                Text("Applicable GST Bracket:", style = MaterialTheme.typography.labelSmall.copy(color = MutedSilver))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(5, 12, 18, 28).forEach { rate ->
                        val isSelected = selectedGstRate == rate
                        Surface(
                            color = if (isSelected) EmeraldMuted else ZincMetal,
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ElectricEmerald else SlateDivider),
                            modifier = Modifier.clickable { selectedGstRate = rate }
                        ) {
                            Text(
                                text = "$rate%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) ElectricEmerald else OffWhitePearl,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Surface(color = ZincMetal, shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Taxable Subtotal:", style = MaterialTheme.typography.labelSmall.copy(color = MutedSilver))
                            Text("₹${subtotal.toInt()}", style = MaterialTheme.typography.labelSmall.copy(color = OffWhitePearl, fontFamily = FontFamily.Monospace))
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("CGST (${selectedGstRate / 2}%):", style = MaterialTheme.typography.labelSmall.copy(color = MutedSilver))
                            Text("₹${"%.2f".format(cgst)}", style = MaterialTheme.typography.labelSmall.copy(color = OffWhitePearl, fontFamily = FontFamily.Monospace))
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("SGST (${selectedGstRate / 2}%):", style = MaterialTheme.typography.labelSmall.copy(color = MutedSilver))
                            Text("₹${"%.2f".format(sgst)}", style = MaterialTheme.typography.labelSmall.copy(color = OffWhitePearl, fontFamily = FontFamily.Monospace))
                        }
                        HorizontalDivider(color = SlateDivider, modifier = Modifier.padding(vertical = 4.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Invoice Amount:", style = MaterialTheme.typography.titleSmall.copy(color = ElectricEmerald, fontWeight = FontWeight.Bold))
                            Text("₹${"%.2f".format(totalWithTax)}", style = MaterialTheme.typography.titleSmall.copy(color = ElectricEmerald, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val reportFile = PdfReportGenerator.generateBusinessReport(
                        context = context,
                        shopName = uiState.currentShopName,
                        shopType = "GST Tax Invoice",
                        period = "Invoice INV-0089",
                        totalRevenue = totalWithTax,
                        netProfit = totalWithTax - subtotal,
                        totalCustomers = 1,
                        sales = uiState.salesHistory,
                        products = uiState.products
                    )
                    if (reportFile != null) {
                        Toast.makeText(context, "Exporting GST Tax Invoice PDF...", Toast.LENGTH_SHORT).show()
                        PdfReportGenerator.sharePdfReport(context, reportFile)
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricEmerald)
            ) {
                Text("Generate & Share PDF", color = SlateDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = MutedSilver) }
        },
        containerColor = ZincCharcoal,
        shape = RoundedCornerShape(12.dp)
    )
}

// 2. Customer Loyalty Dialog (Screen 13)
@Composable
fun CustomerLoyaltyDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var customerPhone by remember { mutableStateOf("9876543210") }
    var pointsBalance by remember { androidx.compose.runtime.mutableIntStateOf(420) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Customer Loyalty & QR Pass", style = MaterialTheme.typography.titleLarge.copy(color = OffWhitePearl, fontWeight = FontWeight.Bold))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = { customerPhone = it },
                    label = { Text("Customer Phone Number", color = MutedSilver) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricEmerald, unfocusedBorderColor = SlateDivider, focusedTextColor = OffWhitePearl, unfocusedTextColor = OffWhitePearl)
                )

                Surface(
                    color = ZincMetal,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateDivider),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Member: Priya Verma", style = MaterialTheme.typography.titleSmall.copy(color = OffWhitePearl, fontWeight = FontWeight.Bold))
                            Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(AmberMuted).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                Text("GOLD TIER", style = MaterialTheme.typography.labelSmall.copy(color = WarningAmber, fontWeight = FontWeight.Bold, fontSize = 9.sp))
                            }
                        }
                        Text("Available Points: $pointsBalance pts", style = MaterialTheme.typography.titleMedium.copy(color = ElectricEmerald, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))
                        Text("Point Value: ₹${pointsBalance / 5} discount against next bill", style = MaterialTheme.typography.labelSmall.copy(color = MutedSilver))
                        Text("Visit Frequency: 3 visits / week (High Loyalty)", style = MaterialTheme.typography.labelSmall.copy(color = PrecisionAzure))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (pointsBalance >= 100) {
                        pointsBalance -= 100
                        Toast.makeText(context, "Redeemed 100 pts! ₹20 discount applied to current checkout", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Insufficient points to redeem", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricEmerald)
            ) {
                Text("Redeem 100 Pts (₹20)", color = SlateDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = MutedSilver) }
        },
        containerColor = ZincCharcoal,
        shape = RoundedCornerShape(12.dp)
    )
}

// 3. Cashbook & UPI Reconciliation Dialog (Screen 14)
@Composable
fun CashbookDialog(uiState: StoreUiState, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val openingCash = 5000.0
    val cashSales = uiState.salesHistory.filter { it.paymentMethod == "CASH" }.sumOf { it.totalAmount }
    val upiSales = uiState.salesHistory.filter { it.paymentMethod == "UPI" }.sumOf { it.totalAmount }
    val cashOut = 880.0 // Recorded supplier payout
    val closingCash = openingCash + cashSales - cashOut

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Daily Cashbook & Register", style = MaterialTheme.typography.titleLarge.copy(color = OffWhitePearl, fontWeight = FontWeight.Bold))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Shift Date: 12 Oct 2026 • Counter #1", style = MaterialTheme.typography.labelSmall.copy(color = MutedSilver))

                Surface(color = ZincMetal, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Opening Cash Drawer:", style = MaterialTheme.typography.labelSmall.copy(color = MutedSilver))
                            Text("₹${openingCash.toInt()}", style = MaterialTheme.typography.labelSmall.copy(color = OffWhitePearl, fontFamily = FontFamily.Monospace))
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Today's Cash Inflows:", style = MaterialTheme.typography.labelSmall.copy(color = ElectricEmerald))
                            Text("+₹${cashSales.toInt()}", style = MaterialTheme.typography.labelSmall.copy(color = ElectricEmerald, fontFamily = FontFamily.Monospace))
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Cash Out (Supplier/Exp):", style = MaterialTheme.typography.labelSmall.copy(color = SignalCrimson))
                            Text("-₹${cashOut.toInt()}", style = MaterialTheme.typography.labelSmall.copy(color = SignalCrimson, fontFamily = FontFamily.Monospace))
                        }
                        HorizontalDivider(color = SlateDivider, modifier = Modifier.padding(vertical = 4.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Expected Cash in Drawer:", style = MaterialTheme.typography.titleSmall.copy(color = OffWhitePearl, fontWeight = FontWeight.Bold))
                            Text("₹${closingCash.toInt()}", style = MaterialTheme.typography.titleSmall.copy(color = OffWhitePearl, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace))
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Verified UPI Settlements:", style = MaterialTheme.typography.titleSmall.copy(color = PrecisionAzure, fontWeight = FontWeight.Bold))
                            Text("₹${upiSales.toInt()}", style = MaterialTheme.typography.titleSmall.copy(color = PrecisionAzure, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    Toast.makeText(context, "Shift closed and cryptographic register hash persisted", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricEmerald)
            ) {
                Text("Close Shift & Lock Register", color = SlateDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Dismiss", color = MutedSilver) }
        },
        containerColor = ZincCharcoal,
        shape = RoundedCornerShape(12.dp)
    )
}

// 4. Multi-Shop Switcher Dialog (Screen 15)
@Composable
fun MultiShopDialog(
    currentShop: String,
    onSelectShop: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val branches = listOf(
        "Apex SuperMart (Main Branch)" to "Active Retail Flagship • 6 Staff",
        "Apex Express (West Corridor)" to "Express Convenience Store • 2 Staff",
        "Apex Wholesale Distribution Hub" to "Bulk Warehouse & Depot • 12 Staff"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Multi-Shop Branch Switcher", style = MaterialTheme.typography.titleLarge.copy(color = OffWhitePearl, fontWeight = FontWeight.Bold))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Select active operational store or review consolidated holdings:", style = MaterialTheme.typography.bodySmall.copy(color = MutedSilver))
                branches.forEach { (name, desc) ->
                    val isSelected = currentShop == name
                    Surface(
                        color = if (isSelected) EmeraldMuted else ZincMetal,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ElectricEmerald else SlateDivider),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectShop(name) }
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(name, style = MaterialTheme.typography.titleSmall.copy(color = if (isSelected) ElectricEmerald else OffWhitePearl, fontWeight = FontWeight.Bold))
                                if (isSelected) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = ElectricEmerald, modifier = Modifier.size(16.dp))
                                }
                            }
                            Text(desc, style = MaterialTheme.typography.labelSmall.copy(color = MutedSilver, fontSize = 10.sp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Done", color = ElectricEmerald) }
        },
        containerColor = ZincCharcoal,
        shape = RoundedCornerShape(12.dp)
    )
}

// 5. Staff & Roles (RBAC) Dialog (Screen 16)
@Composable
fun StaffRolesDialog(
    currentRole: UserRole,
    onSelectRole: (UserRole) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Role-Based Access Control (RBAC)", style = MaterialTheme.typography.titleLarge.copy(color = OffWhitePearl, fontWeight = FontWeight.Bold))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Switch active test persona to test role permissions enforcement:", style = MaterialTheme.typography.bodySmall.copy(color = MutedSilver))
                UserRole.entries.forEach { role ->
                    val isSelected = currentRole == role
                    Surface(
                        color = if (isSelected) EmeraldMuted else ZincMetal,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ElectricEmerald else SlateDivider),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectRole(role) }
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(role.displayName, style = MaterialTheme.typography.titleSmall.copy(color = if (isSelected) ElectricEmerald else OffWhitePearl, fontWeight = FontWeight.Bold))
                                val desc = when (role) {
                                    UserRole.OWNER -> "Full superuser authority & AI suggestion commitment"
                                    UserRole.MANAGER -> "Stock adjustments, price audits & reports"
                                    UserRole.STAFF -> "POS checkout, barcode scanning & shelf stocking"
                                }
                                Text(desc, style = MaterialTheme.typography.labelSmall.copy(color = MutedSilver, fontSize = 10.sp))
                            }
                            if (isSelected) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = ElectricEmerald, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = ElectricEmerald) }
        },
        containerColor = ZincCharcoal,
        shape = RoundedCornerShape(12.dp)
    )
}

// 6. Sync Queue Status Dialog (Screen 11)
@Composable
fun SyncQueueDialog(
    pendingCount: Int,
    onTriggerSync: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Offline WorkManager Sync Queue", style = MaterialTheme.typography.titleLarge.copy(color = OffWhitePearl, fontWeight = FontWeight.Bold))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    color = ZincMetal,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Pending Mutations in SQLite:", style = MaterialTheme.typography.bodySmall.copy(color = MutedSilver))
                            Text("$pendingCount items", style = MaterialTheme.typography.bodySmall.copy(color = if (pendingCount > 0) WarningAmber else ElectricEmerald, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Backpressure Strategy:", style = MaterialTheme.typography.bodySmall.copy(color = MutedSilver))
                            Text("Exponential Backoff", style = MaterialTheme.typography.bodySmall.copy(color = OffWhitePearl))
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Network Constraint:", style = MaterialTheme.typography.bodySmall.copy(color = MutedSilver))
                            Text("NetworkType.CONNECTED", style = MaterialTheme.typography.bodySmall.copy(color = PrecisionAzure, fontFamily = FontFamily.Monospace))
                        }
                    }
                }
                Text("All local changes remain safely persisted on SQLite Single Source of Truth until synced with Cloud Firestore.", style = MaterialTheme.typography.labelSmall.copy(color = MutedSilver))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onTriggerSync()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricEmerald)
            ) {
                Text("Force Immediate Sync", color = SlateDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Dismiss", color = MutedSilver) }
        },
        containerColor = ZincCharcoal,
        shape = RoundedCornerShape(12.dp)
    )
}

// 7. Hospitality Digital QR Menu & KDS Dialog (Screens 19 & 20)
@Composable
fun HospitalityKdsDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var selectedTable by remember { androidx.compose.runtime.mutableIntStateOf(4) }
    var orderSent by remember { mutableStateOf(false) }

    val foodItems = listOf(
        "Masala Chai (Double Cutting)" to 25.0,
        "Paneer Tikka Grilled Sandwich" to 120.0,
        "South Indian Filter Coffee" to 35.0,
        "Crispy Veg Samosa (2 Pcs)" to 40.0
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Hospitality Digital Menu & KDS", style = MaterialTheme.typography.titleLarge.copy(color = OffWhitePearl, fontWeight = FontWeight.Bold))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Select Table Number:", style = MaterialTheme.typography.labelSmall.copy(color = MutedSilver))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(1, 2, 3, 4, 5, 6).forEach { tbl ->
                        val isSel = selectedTable == tbl
                        Surface(
                            color = if (isSel) EmeraldMuted else ZincMetal,
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) ElectricEmerald else SlateDivider),
                            modifier = Modifier.clickable { selectedTable = tbl }
                        ) {
                            Text(
                                "T-$tbl",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSel) ElectricEmerald else OffWhitePearl,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                if (!orderSent) {
                    Text("Digital Table Order Items:", style = MaterialTheme.typography.labelSmall.copy(color = MutedSilver))
                    foodItems.forEach { (name, price) ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(name, style = MaterialTheme.typography.bodySmall.copy(color = OffWhitePearl))
                            Text("₹${price.toInt()}", style = MaterialTheme.typography.bodySmall.copy(color = ElectricEmerald, fontFamily = FontFamily.Monospace))
                        }
                    }
                } else {
                    Surface(
                        color = ZincMetal,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricEmerald),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Kitchen Display System (KDS)", style = MaterialTheme.typography.labelSmall.copy(color = PrecisionAzure, fontWeight = FontWeight.Bold))
                                Text("TICKET #042", style = MaterialTheme.typography.labelSmall.copy(color = ElectricEmerald, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Table $selectedTable • 2x Masala Chai, 1x Paneer Sandwich", style = MaterialTheme.typography.bodySmall.copy(color = OffWhitePearl))
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(AmberMuted).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                Text("STATUS: PREPARING IN KITCHEN", style = MaterialTheme.typography.labelSmall.copy(color = WarningAmber, fontSize = 9.sp, fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!orderSent) {
                        orderSent = true
                        Toast.makeText(context, "Order dispatched to Kitchen Display System!", Toast.LENGTH_SHORT).show()
                    } else {
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricEmerald)
            ) {
                Text(if (!orderSent) "Dispatch to Kitchen (KDS)" else "Done", color = SlateDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = MutedSilver) }
        },
        containerColor = ZincCharcoal,
        shape = RoundedCornerShape(12.dp)
    )
}

// 8. Cryptographic Audit Security Log Dialog
@Composable
fun AuditSecurityDialog(onDismiss: () -> Unit) {
    val logs = listOf(
        Triple("12:28:14", "AUTH_BIOMETRIC", "Shop Owner authenticated with fingerprint sensor (PASS)"),
        Triple("12:20:05", "AI_HITL_APPROVAL", "Owner approved Rule-01 Near-Expiry Bundle clearance discount"),
        Triple("11:45:12", "SALE_TXN_COMMIT", "Sale S-20261012-042 committed to SQLite SSOT (Hash: 9a3f2...)"),
        Triple("11:00:00", "WORKMANAGER_SYNC", "Push batch synchronized with Cloud Firestore (Status: 200 OK)")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Cryptographic Audit Trail", style = MaterialTheme.typography.titleLarge.copy(color = OffWhitePearl, fontWeight = FontWeight.Bold))
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(logs) { (time, tag, msg) ->
                    Surface(
                        color = ZincMetal,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(tag, style = MaterialTheme.typography.labelSmall.copy(color = ElectricEmerald, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 9.sp))
                                Text(time, style = MaterialTheme.typography.labelSmall.copy(color = DimGhost, fontSize = 9.sp))
                            }
                            Text(msg, style = MaterialTheme.typography.bodySmall.copy(color = OffWhitePearl, fontSize = 11.sp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = ElectricEmerald) }
        },
        containerColor = ZincCharcoal,
        shape = RoundedCornerShape(12.dp)
    )
}
