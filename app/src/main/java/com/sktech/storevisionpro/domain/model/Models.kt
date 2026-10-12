package com.sktech.storevisionpro.domain.model

enum class ShopType(val displayName: String, val categoryHint: String) {
    GROCERY("Grocery & Supermarket", "FMCG, Food, Daily Essentials"),
    APPAREL("Clothing & Fashion", "Garments, Textiles, Footwear"),
    ELECTRONICS("Electronics & Mobile", "Smartphones, Accessories, Hardware"),
    COSMETICS("Cosmetics & Beauty", "Skincare, Personal Care, Fragrances"),
    CAFE_RESTAURANT("Cafe & Restaurant", "Hospitality, Food & Beverage, QR Menu"),
    GENERAL("General Store & Retail", "Household, Stationery, Miscellaneous")
}

enum class UserRole(val displayName: String) {
    OWNER("Shop Owner (Full Admin)"),
    MANAGER("Store Manager (Catalog & Operations)"),
    STAFF("Cashier / Staff (Entry Only)")
}

enum class AppLanguage(val code: String, val nativeName: String, val englishName: String) {
    EN("en", "English", "English"),
    HI("hi", "हिन्दी", "Hindi"),
    MR("mr", "मराठी", "Marathi"),
    GU("gu", "ગુજરાતી", "Gujarati"),
    TA("ta", "தமிழ்", "Tamil"),
    TE("te", "తెలుగు", "Telugu"),
    BN("bn", "বাংলা", "Bengali"),
    KN("kn", "ಕನ್ನಡ", "Kannada")
}

enum class SyncState {
    SYNCED,
    SYNCING,
    OFFLINE_LOCAL,
    CONFLICT
}

enum class SuggestionStatus {
    PENDING,
    ACCEPTED,
    EDITED,
    REJECTED
}

data class ProductItem(
    val id: String,
    val name: String,
    val category: String,
    val sellingPrice: Double,
    val costPrice: Double,
    val currentStock: Double,
    val lowStockThreshold: Double = 5.0,
    val barcode: String? = null,
    val unit: String = "Piece",
    val hasExpiry: Boolean = false,
    val daysUntilExpiry: Int? = null,
    val batchNo: String? = null
)

data class BillItem(
    val product: ProductItem,
    val quantity: Double,
    val lineTotal: Double = product.sellingPrice * quantity
)

data class SaleRecord(
    val id: String,
    val saleNumber: String,
    val timestamp: Long,
    val items: List<BillItem>,
    val subtotal: Double,
    val taxAmount: Double,
    val discountAmount: Double,
    val totalAmount: Double,
    val customerCount: Int = 1,
    val paymentMethod: String = "CASH",
    val isSynced: Boolean = true
)

data class AiInsightItem(
    val id: String,
    val type: String,
    val title: String,
    val explanation: String,
    val sourceProvenance: String,
    val status: SuggestionStatus = SuggestionStatus.PENDING,
    val proposedActionText: String,
    val estimatedFinancialImpact: String? = null
)

data class ChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val pendingConfirmation: PendingConfirmationData? = null
)

data class PendingConfirmationData(
    val actionType: String,
    val title: String,
    val entitySummary: String,
    val proposedDetails: Map<String, String>,
    var isConfirmed: Boolean = false,
    var isRejected: Boolean = false
)
