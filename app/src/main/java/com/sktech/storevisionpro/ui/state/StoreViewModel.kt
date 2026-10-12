package com.sktech.storevisionpro.ui.state

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sktech.storevisionpro.data.local.StoreVisionDbHelper
import com.sktech.storevisionpro.data.sync.SyncManager
import com.sktech.storevisionpro.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StoreUiState(
    val currentShopName: String = "Apex SuperMart (Main Branch)",
    val shopType: ShopType = ShopType.GROCERY,
    val currentRole: UserRole = UserRole.OWNER,
    val currentLanguage: AppLanguage = AppLanguage.EN,
    val syncState: SyncState = SyncState.SYNCED,
    val pendingSyncCount: Int = 0,
    val isAuthenticated: Boolean = true,

    // POS State
    val currentBillItems: List<BillItem> = emptyList(),
    val customerCount: Int = 1,
    val visitedNoPurchaseCount: Int = 14,
    val selectedSaleTimestamp: Long = System.currentTimeMillis(),
    val isBarcodeScannerOpen: Boolean = false,

    // Catalog & Inventory (SSOT backed by SQLite)
    val products: List<ProductItem> = listOf(
        ProductItem("P-101", "Amul Gold Milk 500ml", "Dairy", 34.0, 31.0, 42.0, 10.0, "8901262010053", "Packet", true, 2, "B-2610A"),
        ProductItem("P-102", "Britannia Good Day 100g", "Biscuits", 30.0, 24.5, 68.0, 15.0, "8901063012117", "Packet", true, 120, "B-8831"),
        ProductItem("P-103", "Tata Salt Iodized 1kg", "Groceries", 28.0, 22.0, 14.0, 20.0, "8901058852331", "Packet", false, null, "B-9102"),
        ProductItem("P-104", "Fortune Sunflower Oil 1L", "Oils", 145.0, 128.0, 8.0, 10.0, "8906007281023", "Bottle", true, 180, "B-5012"),
        ProductItem("P-105", "Cadbury Dairy Milk Silk", "Confectionery", 175.0, 148.0, 0.0, 5.0, "7622201441221", "Bar", true, 90, "B-4411"),
        ProductItem("P-106", "Aashirvaad Shudh Chakki Atta 5kg", "Staples", 265.0, 235.0, 24.0, 8.0, "8901030381028", "Bag", true, 60, "B-1099")
    ),

    // Completed Sales History
    val salesHistory: List<SaleRecord> = listOf(
        SaleRecord(
            id = "S-1001",
            saleNumber = "S-20261012-042",
            timestamp = System.currentTimeMillis() - 25 * 60 * 1000,
            items = listOf(
                BillItem(ProductItem("P-101", "Amul Gold Milk 500ml", "Dairy", 34.0, 31.0, 42.0), 2.0),
                BillItem(ProductItem("P-102", "Britannia Good Day 100g", "Biscuits", 30.0, 24.5, 68.0), 1.0)
            ),
            subtotal = 98.0,
            taxAmount = 0.0,
            discountAmount = 0.0,
            totalAmount = 98.0,
            customerCount = 1,
            paymentMethod = "UPI"
        ),
        SaleRecord(
            id = "S-1000",
            saleNumber = "S-20261012-041",
            timestamp = System.currentTimeMillis() - 75 * 60 * 1000,
            items = listOf(
                BillItem(ProductItem("P-106", "Aashirvaad Shudh Chakki Atta 5kg", "Staples", 265.0, 235.0, 24.0), 1.0),
                BillItem(ProductItem("P-104", "Fortune Sunflower Oil 1L", "Oils", 145.0, 128.0, 8.0), 2.0)
            ),
            subtotal = 555.0,
            taxAmount = 0.0,
            discountAmount = 20.0,
            totalAmount = 535.0,
            customerCount = 1,
            paymentMethod = "CASH"
        )
    ),

    // AI Suggestions (Human in the loop)
    val insights: List<AiInsightItem> = listOf(
        AiInsightItem(
            id = "INS-01",
            type = "NEAR_EXPIRY_BUNDLE",
            title = "Near-Expiry Action: Amul Gold Milk (2 Days Left)",
            explanation = "Batch B-2610A (18 units) expires in 48 hours. Historical sales velocity indicates only 8 units will sell at full price before expiration.",
            sourceProvenance = "Rule-Based FEFO Engine [Local Batch DB]",
            status = SuggestionStatus.PENDING,
            proposedActionText = "Create 'Buy 2 Milk + Get Good Day Biscuit at 50% Off' Clearance Combo",
            estimatedFinancialImpact = "Recovers ₹490 capital, preventing ₹612 inventory write-off"
        ),
        AiInsightItem(
            id = "INS-02",
            type = "DYNAMIC_PRICING",
            title = "Dynamic Pricing: Evening Dairy Surge Detected",
            explanation = "Local footfall in residential corridor peaks between 18:00 and 20:30. Demand elasticity allows a ₹2 margin increase on premium curd and paneer.",
            sourceProvenance = "Verified Market Feed [Retail Index 12 Oct]",
            status = SuggestionStatus.PENDING,
            proposedActionText = "Increase Paneer 200g price by ₹2.00 during 18:00-21:00 peak hours",
            estimatedFinancialImpact = "+₹180 additional net margin per evening"
        ),
        AiInsightItem(
            id = "INS-03",
            type = "REORDER_FORECAST",
            title = "Stock-Out Warning: Fortune Sunflower Oil",
            explanation = "Stock is down to 8 bottles. At current weekly run rate (4.2 bottles/day), stock-out will occur in 1.9 days before next Tuesday's delivery.",
            sourceProvenance = "Predictive Stock Velocity Model",
            status = SuggestionStatus.PENDING,
            proposedActionText = "Generate Supplier Purchase Order for 24 Bottles to Sharma Distributors",
            estimatedFinancialImpact = "Avoids estimated ₹1,160 in lost gross sales"
        )
    ),

    // Chatbot Stream
    val chatMessages: List<ChatMessage> = listOf(
        ChatMessage(
            id = "M-01",
            isUser = false,
            text = "Hello! I am StoreVision Assistant. You can ask for stock levels, today's profit, or dictate voice logs for stock and expenses."
        )
    ),
    val isChatSheetOpen: Boolean = false,
    val isVoiceListening: Boolean = false,

    // Analytics Metrics
    val todayRevenue: Double = 14250.0,
    val todayProfit: Double = 3410.0,
    val todayCustomers: Int = 84,
    val topSellingProduct: String = "Amul Gold Milk 500ml (28 units)",
    val healthScore: Int = 74,
    val businessHealthReport: com.sktech.storevisionpro.domain.insights.BusinessHealthReport? = null,

    // App Theme State
    val isDarkTheme: Boolean = true,
    val lastCompletedSale: SaleRecord? = null
)

class StoreViewModel(application: Application) : AndroidViewModel(application) {
    private val dbHelper = StoreVisionDbHelper.getInstance(application)
    private val _uiState = MutableStateFlow(StoreUiState())
    val uiState: StateFlow<StoreUiState> = _uiState.asStateFlow()

    init {
        // Load initial catalog from local SQLite SSOT
        viewModelScope.launch {
            val localProducts = dbHelper.getAllProducts("SHOP-MAIN-01")
            if (localProducts.isNotEmpty()) {
                _uiState.update { it.copy(products = localProducts) }
            }
            val pendingCount = dbHelper.getPendingSyncCount()
            _uiState.update { it.copy(pendingSyncCount = pendingCount) }

            // Evaluate offline heuristic rule engine & business health diagnostic
            val current = _uiState.value
            refreshInsightsAndHealth(
                products = current.products,
                sales = current.salesHistory,
                revenue = current.todayRevenue,
                profit = current.todayProfit,
                customers = current.todayCustomers
            )

            // Schedule WorkManager periodic synchronization
            SyncManager.schedulePeriodicSync(getApplication())
        }
    }

    private fun refreshInsightsAndHealth(
        products: List<ProductItem>,
        sales: List<SaleRecord>,
        revenue: Double,
        profit: Double,
        customers: Int
    ) {
        val footfallDistribution = listOf(
            0.05f, 0.02f, 0.01f, 0.01f, 0.02f, 0.05f,
            0.15f, 0.35f, 0.65f, 0.85f, 0.70f, 0.60f,
            0.55f, 0.40f, 0.15f, 0.20f, 0.45f, 0.75f,
            0.95f, 0.90f, 0.80f, 0.50f, 0.30f, 0.10f
        )
        val offlineInsights = com.sktech.storevisionpro.domain.insights.OfflineRuleEngine.generateAllOfflineRules(products, footfallDistribution)
        val report = com.sktech.storevisionpro.domain.insights.BusinessHealthCheckEngine.evaluateBusinessHealth(
            products = products,
            sales = sales,
            totalRevenue = revenue,
            netProfit = profit,
            totalCustomers = customers
        )

        _uiState.update { state ->
            val updatedInsights = offlineInsights.map { newIns ->
                val existing = state.insights.find { it.id == newIns.id }
                if (existing != null) newIns.copy(status = existing.status) else newIns
            }
            state.copy(
                insights = updatedInsights,
                healthScore = report.overallScore,
                businessHealthReport = report
            )
        }
    }

    // POS Barcode & Scanner Actions
    fun setBarcodeScannerOpen(open: Boolean) {
        _uiState.update { it.copy(isBarcodeScannerOpen = open) }
    }

    fun handleScannedBarcode(barcode: String): Boolean {
        val state = _uiState.value
        val matchedProduct = state.products.find { it.barcode == barcode }
        return if (matchedProduct != null) {
            addProductToBill(matchedProduct)
            true
        } else {
            false
        }
    }

    // POS Bill Actions
    fun addProductToBill(product: ProductItem) {
        _uiState.update { state ->
            val existingIndex = state.currentBillItems.indexOfFirst { it.product.id == product.id }
            val updatedItems = if (existingIndex >= 0) {
                state.currentBillItems.mapIndexed { idx, item ->
                    if (idx == existingIndex) item.copy(quantity = item.quantity + 1.0) else item
                }
            } else {
                state.currentBillItems + BillItem(product, 1.0)
            }
            state.copy(currentBillItems = updatedItems)
        }
    }

    fun removeProductFromBill(productId: String) {
        _uiState.update { state ->
            val updated = state.currentBillItems.mapNotNull { item ->
                if (item.product.id == productId) {
                    if (item.quantity > 1.0) item.copy(quantity = item.quantity - 1.0) else null
                } else item
            }
            state.copy(currentBillItems = updated)
        }
    }

    fun incrementCustomerCount() {
        _uiState.update { it.copy(customerCount = it.customerCount + 1) }
    }

    fun decrementCustomerCount() {
        _uiState.update { if (it.customerCount > 1) it.copy(customerCount = it.customerCount - 1) else it }
    }

    fun recordVisitedNoPurchase() {
        _uiState.update { it.copy(visitedNoPurchaseCount = it.visitedNoPurchaseCount + 1) }
    }

    fun completeCurrentSale(paymentMethod: String = "CASH") {
        val state = _uiState.value
        if (state.currentBillItems.isEmpty()) return

        val subtotal = state.currentBillItems.sumOf { it.lineTotal }
        val newSale = SaleRecord(
            id = "S-${System.currentTimeMillis()}",
            saleNumber = "S-20261012-${state.salesHistory.size + 43}",
            timestamp = state.selectedSaleTimestamp,
            items = state.currentBillItems,
            subtotal = subtotal,
            taxAmount = 0.0,
            discountAmount = 0.0,
            totalAmount = subtotal,
            customerCount = state.customerCount,
            paymentMethod = paymentMethod
        )

        // Atomic transaction commit to SQLite SSOT
        viewModelScope.launch {
            dbHelper.recordSaleAtomic(newSale, "SHOP-MAIN-01")
            SyncManager.triggerImmediateSync(getApplication())
            val pendingCount = dbHelper.getPendingSyncCount()

            // Update in-memory state
            val updatedProducts = state.products.map { prod ->
                val soldItem = state.currentBillItems.find { it.product.id == prod.id }
                if (soldItem != null) {
                    prod.copy(currentStock = maxOf(0.0, prod.currentStock - soldItem.quantity))
                } else prod
            }

            _uiState.update {
                it.copy(
                    salesHistory = listOf(newSale) + it.salesHistory,
                    products = updatedProducts,
                    currentBillItems = emptyList(),
                    customerCount = 1,
                    todayRevenue = it.todayRevenue + subtotal,
                    todayCustomers = it.todayCustomers + state.customerCount,
                    pendingSyncCount = pendingCount,
                    lastCompletedSale = newSale
                )
            }

            val updated = _uiState.value
            refreshInsightsAndHealth(
                products = updated.products,
                sales = updated.salesHistory,
                revenue = updated.todayRevenue,
                profit = updated.todayProfit,
                customers = updated.todayCustomers
            )
        }
    }

    fun addNewProduct(product: ProductItem) {
        viewModelScope.launch {
            dbHelper.insertOrUpdateProduct(product, "SHOP-MAIN-01")
            _uiState.update { it.copy(products = listOf(product) + it.products) }
            val updated = _uiState.value
            refreshInsightsAndHealth(
                products = updated.products,
                sales = updated.salesHistory,
                revenue = updated.todayRevenue,
                profit = updated.todayProfit,
                customers = updated.todayCustomers
            )
        }
    }

    // AI Suggestions Human In The Loop Actions
    fun acceptSuggestion(suggestionId: String) {
        _uiState.update { state ->
            val updated = state.insights.map {
                if (it.id == suggestionId) it.copy(status = SuggestionStatus.ACCEPTED) else it
            }
            state.copy(insights = updated)
        }
    }

    fun rejectSuggestion(suggestionId: String) {
        _uiState.update { state ->
            val updated = state.insights.map {
                if (it.id == suggestionId) it.copy(status = SuggestionStatus.REJECTED) else it
            }
            state.copy(insights = updated)
        }
    }

    // Assistant & Voice Log
    fun setChatSheetOpen(open: Boolean) {
        _uiState.update { it.copy(isChatSheetOpen = open) }
    }

    fun sendUserChatMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(id = "M-${System.currentTimeMillis()}", isUser = true, text = text)
        _uiState.update { it.copy(chatMessages = it.chatMessages + userMsg) }

        viewModelScope.launch {
            val lower = text.lowercase()
            val replyMsg = when {
                lower.contains("oreo") || lower.contains("biscuit") -> {
                    ChatMessage(
                        id = "M-${System.currentTimeMillis() + 1}",
                        isUser = false,
                        text = "Britannia Good Day biscuits currently have 68 packets in stock (Shelf A3). Oreo is currently unlisted."
                    )
                }
                lower.contains("milk") -> {
                    ChatMessage(
                        id = "M-${System.currentTimeMillis() + 1}",
                        isUser = false,
                        text = "Amul Gold Milk has 42 packets remaining. Warning: 18 packets expire in 2 days (Batch B-2610A)."
                    )
                }
                lower.contains("profit") || lower.contains("revenue") || lower.contains("sales") -> {
                    ChatMessage(
                        id = "M-${System.currentTimeMillis() + 1}",
                        isUser = false,
                        text = "Today's total revenue is ₹${_uiState.value.todayRevenue.toInt()} across ${_uiState.value.todayCustomers} customers. Estimated net profit margin: 23.9% (₹${_uiState.value.todayProfit.toInt()})."
                    )
                }
                lower.contains("received") || lower.contains("order") || lower.contains("bought") -> {
                    ChatMessage(
                        id = "M-${System.currentTimeMillis() + 1}",
                        isUser = false,
                        text = "I interpreted your stock receipt update. Please review and confirm before I commit to inventory and cashbook:",
                        pendingConfirmation = PendingConfirmationData(
                            actionType = "STOCK_RECEIPT",
                            title = "Stock Receipt & Cash Out Confirmation",
                            entitySummary = "40 Units Tata Salt from Supplier Wholesale Hub",
                            proposedDetails = mapOf(
                                "Product" to "Tata Salt Iodized 1kg",
                                "Quantity" to "40 Units",
                                "Total Cost" to "₹880.00",
                                "Payment Method" to "CASH"
                            )
                        )
                    )
                }
                else -> {
                    ChatMessage(
                        id = "M-${System.currentTimeMillis() + 1}",
                        isUser = false,
                        text = "I can answer inventory queries, check financial metrics, run your Business Health Check, or process voice receipts. What would you like to review?"
                    )
                }
            }
            _uiState.update { it.copy(chatMessages = it.chatMessages + replyMsg) }
        }
    }

    fun confirmVoiceLogAction(confirmation: PendingConfirmationData) {
        confirmation.isConfirmed = true
        _uiState.update { state ->
            val updatedProds = state.products.map {
                if (it.name.contains("Tata Salt", ignoreCase = true)) {
                    it.copy(currentStock = it.currentStock + 40.0)
                } else it
            }
            val confirmMsg = ChatMessage(
                id = "M-${System.currentTimeMillis()}",
                isUser = false,
                text = "Confirmed and committed: +40 Tata Salt added to inventory. Recorded ₹880 cash-out in Cashbook."
            )
            state.copy(products = updatedProds, chatMessages = state.chatMessages + confirmMsg)
        }
    }

    fun rejectVoiceLogAction(confirmation: PendingConfirmationData) {
        confirmation.isRejected = true
        val rejectMsg = ChatMessage(
            id = "M-${System.currentTimeMillis()}",
            isUser = false,
            text = "Discarded proposed stock entry. No database changes were made."
        )
        _uiState.update { it.copy(chatMessages = it.chatMessages + rejectMsg) }
    }

    fun setLanguage(language: AppLanguage) {
        _uiState.update { it.copy(currentLanguage = language) }
    }

    fun setRole(role: UserRole) {
        _uiState.update { it.copy(currentRole = role) }
    }

    fun setShopType(shopType: ShopType) {
        _uiState.update { it.copy(shopType = shopType) }
    }

    fun switchBranch(branchName: String) {
        _uiState.update { it.copy(currentShopName = branchName) }
    }

    fun triggerManualSync() {
        viewModelScope.launch {
            SyncManager.triggerImmediateSync(getApplication())
            val pending = dbHelper.getPendingSyncCount()
            _uiState.update { it.copy(pendingSyncCount = pending, syncState = SyncState.SYNCED) }
        }
    }

    // Theme Management
    fun toggleTheme() {
        _uiState.update { it.copy(isDarkTheme = !it.isDarkTheme) }
    }

    fun setDarkTheme(isDark: Boolean) {
        _uiState.update { it.copy(isDarkTheme = isDark) }
    }

    fun clearLastCompletedSale() {
        _uiState.update { it.copy(lastCompletedSale = null) }
    }

    // Real Inventory Adjustments
    fun adjustStock(productId: String, delta: Double) {
        val state = _uiState.value
        val target = state.products.find { it.id == productId } ?: return
        val newStock = maxOf(0.0, target.currentStock + delta)
        val updated = target.copy(currentStock = newStock)
        viewModelScope.launch {
            dbHelper.insertOrUpdateProduct(updated, "SHOP-MAIN-01")
            val updatedList = state.products.map { if (it.id == productId) updated else it }
            _uiState.update { it.copy(products = updatedList) }
            val curr = _uiState.value
            refreshInsightsAndHealth(curr.products, curr.salesHistory, curr.todayRevenue, curr.todayProfit, curr.todayCustomers)
        }
    }

    // Real AI Clearance Bundle Execution
    fun applyClearanceBundle(nearExpiryProductId: String, pairedProductId: String, discountPercent: Double = 50.0) {
        val state = _uiState.value
        val updatedList = state.products.map { prod ->
            if (prod.id == pairedProductId) {
                prod.copy(sellingPrice = prod.sellingPrice * (1.0 - (discountPercent / 100.0)))
            } else prod
        }
        viewModelScope.launch {
            val updatedPaired = updatedList.find { it.id == pairedProductId }
            if (updatedPaired != null) {
                dbHelper.insertOrUpdateProduct(updatedPaired, "SHOP-MAIN-01")
            }
            _uiState.update { st ->
                val updatedInsights = st.insights.map {
                    if (it.type == "NEAR_EXPIRY_BUNDLE") it.copy(status = SuggestionStatus.ACCEPTED) else it
                }
                st.copy(products = updatedList, insights = updatedInsights)
            }
        }
    }
}
