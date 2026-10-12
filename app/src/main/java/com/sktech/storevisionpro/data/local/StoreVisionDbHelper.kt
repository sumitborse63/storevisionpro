package com.sktech.storevisionpro.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.gson.Gson
import com.sktech.storevisionpro.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import java.util.UUID

class StoreVisionDbHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    private val gson = Gson()
    private val _dbChangeEvents = MutableSharedFlow<String>(extraBufferCapacity = 64)
    val dbChangeEvents: SharedFlow<String> = _dbChangeEvents.asSharedFlow()

    companion object {
        const val DATABASE_NAME = "storevisionpro_local.db"
        const val DATABASE_VERSION = 1

        @Volatile
        private var INSTANCE: StoreVisionDbHelper? = null

        fun getInstance(context: Context): StoreVisionDbHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: StoreVisionDbHelper(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE products (
                productId TEXT PRIMARY KEY,
                shopId TEXT NOT NULL,
                name TEXT NOT NULL,
                category TEXT NOT NULL,
                sellingPrice REAL NOT NULL,
                costPrice REAL NOT NULL,
                stockQty REAL NOT NULL,
                lowStockThreshold REAL NOT NULL DEFAULT 5.0,
                barcode TEXT,
                unit TEXT NOT NULL DEFAULT 'Piece',
                hasExpiry INTEGER NOT NULL DEFAULT 0,
                daysUntilExpiry INTEGER,
                batchNo TEXT,
                isArchived INTEGER NOT NULL DEFAULT 0,
                updatedAt INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX idx_products_barcode ON products(barcode)")
        db.execSQL("CREATE INDEX idx_products_shop ON products(shopId)")

        db.execSQL(
            """
            CREATE TABLE product_batches (
                batchId TEXT PRIMARY KEY,
                productId TEXT NOT NULL,
                shopId TEXT NOT NULL,
                batchNo TEXT NOT NULL,
                quantity REAL NOT NULL,
                costPrice REAL NOT NULL,
                receiptDate INTEGER NOT NULL,
                expiryDate INTEGER NOT NULL,
                status TEXT NOT NULL DEFAULT 'ACTIVE'
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE sales (
                saleId TEXT PRIMARY KEY,
                shopId TEXT NOT NULL,
                saleNumber TEXT NOT NULL,
                timestamp INTEGER NOT NULL,
                customerCount INTEGER NOT NULL DEFAULT 1,
                subtotal REAL NOT NULL,
                taxAmount REAL NOT NULL DEFAULT 0,
                discountAmount REAL NOT NULL DEFAULT 0,
                totalAmount REAL NOT NULL,
                paymentMethod TEXT NOT NULL,
                isSynced INTEGER NOT NULL DEFAULT 1
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX idx_sales_timestamp ON sales(timestamp)")

        db.execSQL(
            """
            CREATE TABLE sale_items (
                saleItemId TEXT PRIMARY KEY,
                saleId TEXT NOT NULL,
                productId TEXT NOT NULL,
                productName TEXT NOT NULL,
                batchId TEXT,
                quantity REAL NOT NULL,
                unitPrice REAL NOT NULL,
                totalPrice REAL NOT NULL,
                costPrice REAL NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE cashbook (
                entryId TEXT PRIMARY KEY,
                shopId TEXT NOT NULL,
                type TEXT NOT NULL,
                amount REAL NOT NULL,
                source TEXT NOT NULL,
                referenceId TEXT,
                timestamp INTEGER NOT NULL,
                notes TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE sync_queue (
                changeId TEXT PRIMARY KEY,
                shopId TEXT NOT NULL,
                collection TEXT NOT NULL,
                documentId TEXT NOT NULL,
                operation TEXT NOT NULL,
                payloadJson TEXT NOT NULL,
                status TEXT NOT NULL DEFAULT 'PENDING',
                retryCount INTEGER NOT NULL DEFAULT 0,
                createdAt INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX idx_sync_status ON sync_queue(status)")

        db.execSQL(
            """
            CREATE TABLE audit_logs (
                logId TEXT PRIMARY KEY,
                shopId TEXT NOT NULL,
                userId TEXT NOT NULL,
                action TEXT NOT NULL,
                affectedRecordId TEXT NOT NULL,
                detailsJson TEXT NOT NULL,
                timestamp INTEGER NOT NULL
            )
            """.trimIndent()
        )

        // Prepopulate baseline seed catalog
        seedDefaultCatalog(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Upgrade migration logic when version bumps
    }

    private fun seedDefaultCatalog(db: SQLiteDatabase) {
        val initialProducts = listOf(
            ProductItem("P-101", "Amul Gold Milk 500ml", "Dairy", 34.0, 31.0, 42.0, 10.0, "8901262010053", "Packet", true, 2, "B-2610A"),
            ProductItem("P-102", "Britannia Good Day 100g", "Biscuits", 30.0, 24.5, 68.0, 15.0, "8901063012117", "Packet", true, 120, "B-8831"),
            ProductItem("P-103", "Tata Salt Iodized 1kg", "Groceries", 28.0, 22.0, 14.0, 20.0, "8901058852331", "Packet", false, null, "B-9102"),
            ProductItem("P-104", "Fortune Sunflower Oil 1L", "Oils", 145.0, 128.0, 8.0, 10.0, "8906007281023", "Bottle", true, 180, "B-5012"),
            ProductItem("P-105", "Cadbury Dairy Milk Silk", "Confectionery", 175.0, 148.0, 0.0, 5.0, "7622201441221", "Bar", true, 90, "B-4411"),
            ProductItem("P-106", "Aashirvaad Shudh Chakki Atta 5kg", "Staples", 265.0, 235.0, 24.0, 8.0, "8901030381028", "Bag", true, 60, "B-1099")
        )

        initialProducts.forEach { prod ->
            val values = ContentValues().apply {
                put("productId", prod.id)
                put("shopId", "SHOP-MAIN-01")
                put("name", prod.name)
                put("category", prod.category)
                put("sellingPrice", prod.sellingPrice)
                put("costPrice", prod.costPrice)
                put("stockQty", prod.currentStock)
                put("lowStockThreshold", prod.lowStockThreshold)
                put("barcode", prod.barcode)
                put("unit", prod.unit)
                put("hasExpiry", if (prod.hasExpiry) 1 else 0)
                put("daysUntilExpiry", prod.daysUntilExpiry)
                put("batchNo", prod.batchNo)
                put("isArchived", 0)
                put("updatedAt", System.currentTimeMillis())
            }
            db.insert("products", null, values)
        }
    }

    // --- High-Performance Thread-Safe Operations ---

    suspend fun getAllProducts(shopId: String): List<ProductItem> = withContext(Dispatchers.IO) {
        val list = mutableListOf<ProductItem>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM products WHERE shopId = ? AND isArchived = 0 ORDER BY name ASC",
            arrayOf(shopId)
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    ProductItem(
                        id = it.getString(it.getColumnIndexOrThrow("productId")),
                        name = it.getString(it.getColumnIndexOrThrow("name")),
                        category = it.getString(it.getColumnIndexOrThrow("category")),
                        sellingPrice = it.getDouble(it.getColumnIndexOrThrow("sellingPrice")),
                        costPrice = it.getDouble(it.getColumnIndexOrThrow("costPrice")),
                        currentStock = it.getDouble(it.getColumnIndexOrThrow("stockQty")),
                        lowStockThreshold = it.getDouble(it.getColumnIndexOrThrow("lowStockThreshold")),
                        barcode = it.getString(it.getColumnIndexOrThrow("barcode")),
                        unit = it.getString(it.getColumnIndexOrThrow("unit")),
                        hasExpiry = it.getInt(it.getColumnIndexOrThrow("hasExpiry")) == 1,
                        daysUntilExpiry = if (it.isNull(it.getColumnIndexOrThrow("daysUntilExpiry"))) null else it.getInt(it.getColumnIndexOrThrow("daysUntilExpiry")),
                        batchNo = it.getString(it.getColumnIndexOrThrow("batchNo"))
                    )
                )
            }
        }
        list
    }

    suspend fun getProductByBarcode(shopId: String, barcode: String): ProductItem? = withContext(Dispatchers.IO) {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM products WHERE shopId = ? AND barcode = ? AND isArchived = 0 LIMIT 1",
            arrayOf(shopId, barcode)
        )
        cursor.use {
            if (it.moveToFirst()) {
                ProductItem(
                    id = it.getString(it.getColumnIndexOrThrow("productId")),
                    name = it.getString(it.getColumnIndexOrThrow("name")),
                    category = it.getString(it.getColumnIndexOrThrow("category")),
                    sellingPrice = it.getDouble(it.getColumnIndexOrThrow("sellingPrice")),
                    costPrice = it.getDouble(it.getColumnIndexOrThrow("costPrice")),
                    currentStock = it.getDouble(it.getColumnIndexOrThrow("stockQty")),
                    lowStockThreshold = it.getDouble(it.getColumnIndexOrThrow("lowStockThreshold")),
                    barcode = it.getString(it.getColumnIndexOrThrow("barcode")),
                    unit = it.getString(it.getColumnIndexOrThrow("unit")),
                    hasExpiry = it.getInt(it.getColumnIndexOrThrow("hasExpiry")) == 1,
                    daysUntilExpiry = if (it.isNull(it.getColumnIndexOrThrow("daysUntilExpiry"))) null else it.getInt(it.getColumnIndexOrThrow("daysUntilExpiry")),
                    batchNo = it.getString(it.getColumnIndexOrThrow("batchNo"))
                )
            } else null
        }
    }

    suspend fun recordSaleAtomic(
        sale: SaleRecord,
        shopId: String
    ): Boolean = withContext(Dispatchers.IO) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            // 1. Insert Sale Record
            val saleValues = ContentValues().apply {
                put("saleId", sale.id)
                put("shopId", shopId)
                put("saleNumber", sale.saleNumber)
                put("timestamp", sale.timestamp)
                put("customerCount", sale.customerCount)
                put("subtotal", sale.subtotal)
                put("taxAmount", sale.taxAmount)
                put("discountAmount", sale.discountAmount)
                put("totalAmount", sale.totalAmount)
                put("paymentMethod", sale.paymentMethod)
                put("isSynced", 0)
            }
            db.insertOrThrow("sales", null, saleValues)

            // 2. Insert Sale Items & Deduct Stock
            sale.items.forEach { item ->
                val itemValues = ContentValues().apply {
                    put("saleItemId", "SI-${UUID.randomUUID()}")
                    put("saleId", sale.id)
                    put("productId", item.product.id)
                    put("productName", item.product.name)
                    put("batchId", item.product.batchNo)
                    put("quantity", item.quantity)
                    put("unitPrice", item.product.sellingPrice)
                    put("totalPrice", item.lineTotal)
                    put("costPrice", item.product.costPrice)
                }
                db.insertOrThrow("sale_items", null, itemValues)

                // Decrement product inventory
                db.execSQL(
                    "UPDATE products SET stockQty = MAX(0, stockQty - ?), updatedAt = ? WHERE productId = ?",
                    arrayOf<Any>(item.quantity, System.currentTimeMillis(), item.product.id)
                )
            }

            // 3. Insert Cashbook Journal Entry
            val cashbookValues = ContentValues().apply {
                put("entryId", "CB-${UUID.randomUUID()}")
                put("shopId", shopId)
                put("type", if (sale.paymentMethod == "UPI") "UPI_SETTLEMENT" else "CASH_IN")
                put("amount", sale.totalAmount)
                put("source", "SALE")
                put("referenceId", sale.id)
                put("timestamp", sale.timestamp)
                put("notes", "Sale ${sale.saleNumber} (${sale.paymentMethod})")
            }
            db.insertOrThrow("cashbook", null, cashbookValues)

            // 4. Enqueue into sync_queue
            val syncValues = ContentValues().apply {
                put("changeId", UUID.randomUUID().toString())
                put("shopId", shopId)
                put("collection", "sales")
                put("documentId", sale.id)
                put("operation", "CREATE")
                put("payloadJson", gson.toJson(sale))
                put("status", "PENDING")
                put("retryCount", 0)
                put("createdAt", System.currentTimeMillis())
            }
            db.insertOrThrow("sync_queue", null, syncValues)

            db.setTransactionSuccessful()
            _dbChangeEvents.tryEmit("SALE_RECORDED")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.endTransaction()
        }
    }

    suspend fun getPendingSyncCount(): Int = withContext(Dispatchers.IO) {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM sync_queue WHERE status = 'PENDING'", null)
        cursor.use {
            if (it.moveToFirst()) it.getInt(0) else 0
        }
    }

    suspend fun markSyncQueueItemsProcessed(limit: Int = 20): Int = withContext(Dispatchers.IO) {
        val db = writableDatabase
        db.delete("sync_queue", "status = 'PENDING'", null)
    }

    suspend fun insertOrUpdateProduct(product: ProductItem, shopId: String) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("productId", product.id)
            put("shopId", shopId)
            put("name", product.name)
            put("category", product.category)
            put("sellingPrice", product.sellingPrice)
            put("costPrice", product.costPrice)
            put("stockQty", product.currentStock)
            put("lowStockThreshold", product.lowStockThreshold)
            put("barcode", product.barcode)
            put("unit", product.unit)
            put("hasExpiry", if (product.hasExpiry) 1 else 0)
            put("daysUntilExpiry", product.daysUntilExpiry)
            put("batchNo", product.batchNo)
            put("isArchived", 0)
            put("updatedAt", System.currentTimeMillis())
        }
        db.insertWithOnConflict("products", null, values, SQLiteDatabase.CONFLICT_REPLACE)
        _dbChangeEvents.tryEmit("PRODUCT_UPDATED")
    }
}
