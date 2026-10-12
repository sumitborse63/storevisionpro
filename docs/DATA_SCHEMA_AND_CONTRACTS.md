# StoreVisionPro — Data Schema & API Contracts Specification

## 1. Cloud Firestore Data Schema (24 Collections)

All Firestore collections enforce strict multi-tenant isolation scoped by `shopId`. The document structure is designed to minimize read/write amplification while supporting atomic queries.

```
/shops/{shopId}
/users/{userId}
/products/{productId}
/productBatches/{batchId}
/sales/{saleId}
/saleItems/{saleItemId}
/expenses/{expenseId}
/suppliers/{supplierId}
/supplierPayments/{paymentId}
/customers/{customerId}
/invoices/{invoiceId}
/cashbook/{entryId}
/campaigns/{campaignId}
/aiSuggestions/{suggestionId}
/voiceLogs/{logId}
/auditLogs/{logId}
/hospitalityMenus/{menuItemId}
/hospitalityOrders/{orderId}
/marketSources/{sourceId}
/priceObservations/{observationId}
/trendRecords/{trendId}
/brandingProfiles/{brandId}
/logoVersions/{versionId}
/footfallVisits/{visitId}
```

### 1.1 Core Entities Schema Definition (JSON Schema Representation)

#### Collection: `shops`
```json
{
  "shopId": "string (UUID)",
  "ownerId": "string (User UID)",
  "shopName": "string",
  "shopType": "string [GROCERY, APPAREL, ELECTRONICS, COSMETICS, CAFE_RESTAURANT, GENERAL]",
  "address": "string",
  "phone": "string",
  "gstNumber": "string?",
  "currency": "string (default: INR)",
  "instagramId": "string?",
  "socialIds": {
    "whatsapp": "string?",
    "facebook": "string?"
  },
  "logoUrl": "string?",
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

#### Collection: `users`
```json
{
  "userId": "string (Firebase Auth UID)",
  "name": "string",
  "email": "string?",
  "phone": "string",
  "shopIds": ["string"],
  "activeShopId": "string",
  "role": "string [OWNER, MANAGER, STAFF]",
  "language": "string [en, hi, mr, gu, ta, te, bn, kn]",
  "biometricEnabled": "boolean",
  "createdAt": "timestamp"
}
```

#### Collection: `products`
```json
{
  "productId": "string (UUID)",
  "shopId": "string (UUID)",
  "name": "string",
  "category": "string",
  "price": "number (Selling Price)",
  "costPrice": "number (Purchase Cost)",
  "stockQty": "number",
  "lowStockThreshold": "number (default: 5)",
  "barcode": "string? (Indexed)",
  "supplierId": "string?",
  "imageUrl": "string?",
  "unit": "string [PIECE, KG, GRAM, LITER, PACKET]",
  "hasExpiry": "boolean",
  "isArchived": "boolean (Soft Delete)",
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

#### Collection: `productBatches`
```json
{
  "batchId": "string (UUID)",
  "productId": "string (UUID)",
  "shopId": "string (UUID)",
  "batchNo": "string",
  "quantity": "number",
  "costPrice": "number",
  "receiptDate": "timestamp",
  "expiryDate": "timestamp",
  "supplierId": "string?",
  "status": "string [ACTIVE, DEPLETED, EXPIRED]"
}
```

#### Collection: `sales`
```json
{
  "saleId": "string (UUID)",
  "shopId": "string (UUID)",
  "saleNumber": "string (Daily Sequence: S-YYYYMMDD-001)",
  "timestamp": "timestamp",
  "customerCount": "number (default: 1)",
  "customerId": "string?",
  "subtotal": "number",
  "taxAmount": "number",
  "discountAmount": "number",
  "totalAmount": "number",
  "paymentMethod": "string [CASH, UPI, CARD, SPLIT]",
  "status": "string [COMPLETED, REFUNDED, VOID]",
  "createdBy": "string (User UID)",
  "offlineSyncToken": "string (UUID)"
}
```

#### Collection: `saleItems`
```json
{
  "saleItemId": "string (UUID)",
  "saleId": "string (UUID)",
  "productId": "string (UUID)",
  "productName": "string",
  "batchId": "string?",
  "quantity": "number",
  "unitPrice": "number",
  "totalPrice": "number",
  "costPrice": "number"
}
```

#### Collection: `expenses`
```json
{
  "expenseId": "string (UUID)",
  "shopId": "string (UUID)",
  "category": "string [RENT, ELECTRICITY, SALARY, STOCK_PURCHASE, MAINTENANCE, OTHER]",
  "amount": "number",
  "date": "timestamp",
  "description": "string",
  "paymentMethod": "string [CASH, UPI, BANK_TRANSFER]",
  "recordedBy": "string (User UID)",
  "voiceLogId": "string?"
}
```

#### Collection: `cashbook`
```json
{
  "entryId": "string (UUID)",
  "shopId": "string (UUID)",
  "type": "string [CASH_IN, CASH_OUT, UPI_SETTLEMENT]",
  "amount": "number",
  "source": "string [SALE, EXPENSE, CASH_DEPOSIT, CASH_WITHDRAWAL, SUPPLIER_PAYMENT]",
  "referenceId": "string? (saleId or expenseId)",
  "timestamp": "timestamp",
  "notes": "string?",
  "closingBalanceSnapshot": "number?"
}
```

#### Collection: `aiSuggestions`
```json
{
  "suggestionId": "string (UUID)",
  "shopId": "string (UUID)",
  "type": "string [DYNAMIC_PRICING, REORDER_FORECAST, COMBO_BUNDLE, NEAR_EXPIRY_OFFER, HEALTH_CHECK_STRATEGY, MARKETING_POSTER]",
  "title": "string",
  "explanation": "string",
  "dataSource": "string [RULE_BASED_OFFLINE, MARKET_FEED_ONLINE, AI_HEALTH_CHECK]",
  "confidenceScore": "number (0.0 to 1.0)",
  "proposedPayload": "map (JSON containing specific mutation values)",
  "status": "string [PENDING, ACCEPTED, EDITED, REJECTED]",
  "decidedBy": "string? (User UID)",
  "decidedAt": "timestamp?",
  "rejectionReason": "string?",
  "createdAt": "timestamp"
}
```

#### Collection: `auditLogs`
```json
{
  "logId": "string (UUID)",
  "shopId": "string (UUID)",
  "userId": "string (User UID)",
  "userRole": "string",
  "action": "string [CREATE_SALE, UPDATE_STOCK, APPROVE_AI_SUGGESTION, REJECT_AI_SUGGESTION, CHANGE_USER_ROLE, MANUAL_PRICE_OVERRIDE]",
  "affectedRecordId": "string",
  "affectedCollection": "string",
  "previousValueJson": "string?",
  "newValueJson": "string?",
  "ipAddressOrDeviceId": "string",
  "timestamp": "timestamp"
}
```

---

## 2. Android Room Database Schema (Local SQLite SSOT)

The local SQLite database mirrors the critical operational collections with full transactional support, foreign key constraints, and reactive Kotlin `Flow` queries.

### 2.1 Room Entities (Kotlin Implementation)

```kotlin
package com.sktech.storevisionpro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import androidx.room.ForeignKey

@Entity(
    tableName = "products",
    indices = [
        Index(value = ["shopId", "barcode"], unique = false),
        Index(value = ["shopId", "name"])
    ]
)
data class ProductEntity(
    @PrimaryKey val productId: String,
    val shopId: String,
    val name: String,
    val category: String,
    val price: Double,
    val costPrice: Double,
    val stockQty: Double,
    val lowStockThreshold: Double = 5.0,
    val barcode: String?,
    val supplierId: String?,
    val imageUrl: String?,
    val unit: String = "PIECE",
    val hasExpiry: Boolean = false,
    val isArchived: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "product_batches",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["productId"]), Index(value = ["expiryDate"])]
)
data class ProductBatchEntity(
    @PrimaryKey val batchId: String,
    val productId: String,
    val shopId: String,
    val batchNo: String,
    val quantity: Double,
    val costPrice: Double,
    val receiptDate: Long,
    val expiryDate: Long,
    val status: String = "ACTIVE"
)

@Entity(
    tableName = "sales",
    indices = [Index(value = ["shopId", "timestamp"])]
)
data class SaleEntity(
    @PrimaryKey val saleId: String,
    val shopId: String,
    val saleNumber: String,
    val timestamp: Long,
    val customerCount: Int = 1,
    val customerId: String?,
    val subtotal: Double,
    val taxAmount: Double,
    val discountAmount: Double,
    val totalAmount: Double,
    val paymentMethod: String,
    val status: String = "COMPLETED",
    val createdBy: String,
    val offlineSyncToken: String
)

@Entity(
    tableName = "sale_items",
    foreignKeys = [
        ForeignKey(
            entity = SaleEntity::class,
            parentColumns = ["saleId"],
            childColumns = ["saleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["saleId"]), Index(value = ["productId"])]
)
data class SaleItemEntity(
    @PrimaryKey val saleItemId: String,
    val saleId: String,
    val productId: String,
    val productName: String,
    val batchId: String?,
    val quantity: Double,
    val unitPrice: Double,
    val totalPrice: Double,
    val costPrice: Double
)

@Entity(
    tableName = "cashbook",
    indices = [Index(value = ["shopId", "timestamp"])]
)
data class CashbookEntryEntity(
    @PrimaryKey val entryId: String,
    val shopId: String,
    val type: String, // CASH_IN, CASH_OUT, UPI_SETTLEMENT
    val amount: Double,
    val source: String,
    val referenceId: String?,
    val timestamp: Long,
    val notes: String?
)

@Entity(
    tableName = "sync_queue",
    indices = [Index(value = ["status", "createdAt"])]
)
data class SyncQueueEntity(
    @PrimaryKey val changeId: String,
    val shopId: String,
    val collection: String,
    val documentId: String,
    val operation: String, // CREATE, UPDATE, DELETE
    val payloadJson: String,
    val status: String = "PENDING", // PENDING, SYNCING, SYNCED, FAILED, CONFLICT
    val retryCount: Int = 0,
    val errorMessage: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
```

### 2.2 Room Data Access Objects (DAOs)

```kotlin
package com.sktech.storevisionpro.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.sktech.storevisionpro.data.local.entity.*

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE shopId = :shopId AND isArchived = 0 ORDER BY name ASC")
    fun observeProductsByShop(shopId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE shopId = :shopId AND barcode = :barcode AND isArchived = 0 LIMIT 1")
    suspend fun getProductByBarcode(shopId: String, barcode: String): ProductEntity?

    @Query("SELECT * FROM products WHERE shopId = :shopId AND stockQty <= lowStockThreshold AND isArchived = 0")
    fun observeLowStockProducts(shopId: String): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProduct(product: ProductEntity)

    @Query("UPDATE products SET stockQty = stockQty - :soldQty, updatedAt = :timestamp WHERE productId = :productId")
    suspend fun decrementStock(productId: String, soldQty: Double, timestamp: Long = System.currentTimeMillis())
}

@Dao
interface SaleDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSale(sale: SaleEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSaleItems(items: List<SaleItemEntity>)

    @Query("SELECT * FROM sales WHERE shopId = :shopId AND timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getSalesBetweenTimestamps(shopId: String, startTime: Long, endTime: Long): Flow<List<SaleEntity>>

    @Transaction
    suspend fun recordCompleteSaleTransaction(
        sale: SaleEntity,
        items: List<SaleItemEntity>,
        cashbookEntry: CashbookEntryEntity,
        syncEntries: List<SyncQueueEntity>,
        productDecrements: List<Pair<String, Double>>
    ) {
        insertSale(sale)
        insertSaleItems(items)
        // Handled via injected DAOs in repository transaction runner
    }
}

@Dao
interface SyncQueueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueueChange(change: SyncQueueEntity)

    @Query("SELECT * FROM sync_queue WHERE status = 'PENDING' OR status = 'FAILED' ORDER BY createdAt ASC LIMIT 50")
    suspend fun getPendingChanges(): List<SyncQueueEntity>

    @Query("UPDATE sync_queue SET status = :newStatus, retryCount = retryCount + 1, errorMessage = :error WHERE changeId = :changeId")
    suspend fun updateStatus(changeId: String, newStatus: String, error: String? = null)

    @Query("DELETE FROM sync_queue WHERE changeId = :changeId")
    suspend fun markSyncedAndRemove(changeId: String)
}
```

---

## 3. AI Orchestration Gateway API Contracts (OpenAPI 3.1)

The backend AI service exposes structured JSON endpoints consumed by the Android client via Retrofit / OkHttp.

### 3.1 Endpoint: Natural Language Assistant Query
- **Path:** `POST /api/v1/ai/assistant/query`
- **Headers:** `Authorization: Bearer <Firebase_ID_Token>`, `X-Shop-Id: <UUID>`
- **Request Payload:**
```json
{
  "query": "How many Oreo biscuit packets are left, and what were today's total sales?",
  "shopId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "userId": "firebase_auth_user_123",
  "language": "en"
}
```
- **Response Payload:**
```json
{
  "queryId": "7b13467f-4428-4448-8422-fc37207dc32a",
  "responseText": "You currently have 18 Oreo biscuit packets in stock. Today's total sales stand at ₹14,250 across 42 transactions.",
  "dataPoints": [
    { "entity": "product", "name": "Oreo Biscuit", "stock": 18, "unit": "PACKET" },
    { "entity": "sales", "date": "2026-10-12", "totalAmount": 14250.0, "billCount": 42 }
  ],
  "suggestedActions": []
}
```

### 3.2 Endpoint: Voice Log Structured Parsing
- **Path:** `POST /api/v1/ai/voice-log/parse`
- **Request Payload:**
```json
{
  "transcript": "Received 50 bottles of mustard oil from Sharma Distributors for 6500 rupees cash today",
  "shopId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "language": "en"
}
```
- **Response Payload (Strict Extraction Schema):**
```json
{
  "parseStatus": "SUCCESS",
  "intent": "INVENTORY_RECEIPT_AND_EXPENSE",
  "proposedRecord": {
    "productName": "Mustard Oil",
    "quantity": 50.0,
    "unit": "BOTTLE",
    "totalCost": 6500.0,
    "unitCost": 130.0,
    "supplierName": "Sharma Distributors",
    "paymentMethod": "CASH",
    "category": "STOCK_PURCHASE"
  },
  "requiresConfirmation": true,
  "confidenceScore": 0.94
}
```

### 3.3 Endpoint: Business Health Check Diagnostic
- **Path:** `POST /api/v1/ai/analytics/health-check`
- **Request Payload:**
```json
{
  "shopId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "periodDays": 30,
  "metrics": {
    "totalRevenue": 420000.0,
    "totalExpenses": 348000.0,
    "grossMarginPercent": 17.14,
    "averageDailyCustomers": 88,
    "repeatCustomerRatePercent": 31.5,
    "deadStockValue": 32000.0
  }
}
```
- **Response Payload:**
```json
{
  "healthScore": 72,
  "rating": "FAIR",
  "diagnosticFindings": [
    {
      "area": "MARGIN",
      "status": "ATTENTION_REQUIRED",
      "observation": "Gross margin of 17.14% is 4.8% below typical grocery benchmarks (22%).",
      "actionableStrategy": "Review purchase pricing on top 5 FMCG categories and bundle slow-moving high-margin spices."
    },
    {
      "area": "DEAD_STOCK",
      "status": "CRITICAL",
      "observation": "₹32,000 capital is locked in inventory without movement in the last 21 days.",
      "actionableStrategy": "Launch a weekend clearance bundle pairing dead stock with best-selling wheat flour."
    }
  ],
  "proposedOffers": [
    {
      "offerType": "BUNDLE_DISCOUNT",
      "leadProduct": "Aashirvaad Atta 5kg",
      "bundleProduct": "Saffola Gold Oil 1L",
      "discountAmount": 25.0,
      "expectedMarginImpact": "+4.2%"
    }
  ]
}
```

### 3.4 Endpoint: Verified Market Price Comparison
- **Path:** `GET /api/v1/market/price-comparison?barcode=8901030381028&shopType=GROCERY`
- **Response Payload:**
```json
{
  "barcode": "8901030381028",
  "productName": "Red Label Tea 500g",
  "skuMatched": "HUL-RL-500G-CARTON",
  "sources": [
    {
      "sourceName": "National Retail Catalog Index",
      "retrievalTimestamp": "2026-10-12T14:30:00Z",
      "observedPrice": 260.0,
      "mrp": 275.0,
      "sourceUrl": "https://data.retailindex.in/sku/8901030381028",
      "verificationStatus": "VERIFIED_ACTIVE"
    }
  ],
  "marketReferencePrice": 260.0,
  "sufficientData": true
}
```
