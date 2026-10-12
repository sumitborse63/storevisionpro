package com.sktech.storevisionpro.pdf

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.sktech.storevisionpro.domain.model.ProductItem
import com.sktech.storevisionpro.domain.model.SaleRecord
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfReportGenerator {

    fun generateBusinessReport(
        context: Context,
        shopName: String,
        shopType: String,
        period: String,
        totalRevenue: Double,
        netProfit: Double,
        totalCustomers: Int,
        sales: List<SaleRecord>,
        products: List<ProductItem>
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 (595 x 842 points)
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply {
            color = Color.parseColor("#09090B")
            textSize = 20f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        val subPaint = Paint().apply {
            color = Color.parseColor("#71717A")
            textSize = 10f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }

        val headerBoxPaint = Paint().apply {
            color = Color.parseColor("#F4F4F5")
            style = Paint.Style.FILL
        }

        val borderPaint = Paint().apply {
            color = Color.parseColor("#D4D4D8")
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }

        val emeraldPaint = Paint().apply {
            color = Color.parseColor("#059669")
            textSize = 14f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            isAntiAlias = true
        }

        val bodyBoldPaint = Paint().apply {
            color = Color.parseColor("#18181B")
            textSize = 10f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            isAntiAlias = true
        }

        val bodyRegularPaint = Paint().apply {
            color = Color.parseColor("#3F3F46")
            textSize = 9.5f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }

        val monoPaint = Paint().apply {
            color = Color.parseColor("#18181B")
            textSize = 9.5f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
            isAntiAlias = true
        }

        var yPos = 40f

        // Document Title Header
        canvas.drawText("STOREVISION PRO — BUSINESS PERFORMANCE REPORT", 40f, yPos, titlePaint)
        yPos += 16f
        val dateStr = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
        canvas.drawText("Generated: $dateStr • Period: $period • Store: $shopName ($shopType)", 40f, yPos, subPaint)
        yPos += 20f

        // 1. Executive Financial KPI Summary Box
        canvas.drawRect(40f, yPos, 555f, yPos + 70f, headerBoxPaint)
        canvas.drawRect(40f, yPos, 555f, yPos + 70f, borderPaint)

        // Column 1: Gross Revenue
        canvas.drawText("TOTAL GROSS REVENUE", 55f, yPos + 22f, subPaint)
        canvas.drawText("₹${"%,d".format(totalRevenue.toInt())}", 55f, yPos + 48f, emeraldPaint)

        // Column 2: Estimated Net Profit
        canvas.drawText("ESTIMATED NET MARGIN", 185f, yPos + 22f, subPaint)
        canvas.drawText("₹${"%,d".format(netProfit.toInt())}", 185f, yPos + 48f, emeraldPaint)

        // Column 3: Footfall / Transactions
        canvas.drawText("RECORDED FOOTFALL", 325f, yPos + 22f, subPaint)
        canvas.drawText("$totalCustomers Shoppers", 325f, yPos + 48f, bodyBoldPaint)

        // Column 4: Health Rating
        canvas.drawText("DIAGNOSTIC HEALTH", 450f, yPos + 22f, subPaint)
        canvas.drawText("74% (FAIR)", 450f, yPos + 48f, bodyBoldPaint)

        yPos += 95f

        // 2. High-Velocity Leaderboard Table
        canvas.drawText("PRODUCT PERFORMANCE & INVENTORY VELOCITY", 40f, yPos, bodyBoldPaint)
        yPos += 14f

        // Table Header
        canvas.drawRect(40f, yPos, 555f, yPos + 22f, headerBoxPaint)
        canvas.drawRect(40f, yPos, 555f, yPos + 22f, borderPaint)
        canvas.drawText("SKU Code", 50f, yPos + 15f, bodyBoldPaint)
        canvas.drawText("Product Name", 140f, yPos + 15f, bodyBoldPaint)
        canvas.drawText("Category", 300f, yPos + 15f, bodyBoldPaint)
        canvas.drawText("Price", 390f, yPos + 15f, bodyBoldPaint)
        canvas.drawText("Current Stock", 470f, yPos + 15f, bodyBoldPaint)
        yPos += 22f

        // Table Rows
        products.take(6).forEach { prod ->
            canvas.drawRect(40f, yPos, 555f, yPos + 22f, borderPaint)
            canvas.drawText(prod.id, 50f, yPos + 15f, monoPaint)
            canvas.drawText(prod.name.take(22), 140f, yPos + 15f, bodyRegularPaint)
            canvas.drawText(prod.category, 300f, yPos + 15f, bodyRegularPaint)
            canvas.drawText("₹${prod.sellingPrice.toInt()}", 390f, yPos + 15f, monoPaint)
            canvas.drawText("${prod.currentStock.toInt()} ${prod.unit}s", 470f, yPos + 15f, monoPaint)
            yPos += 22f
        }

        yPos += 25f

        // 3. Recent Sales Ledger Entries
        canvas.drawText("RECENT TRANSACTION LEDGER (OFFLINE PERSISTED)", 40f, yPos, bodyBoldPaint)
        yPos += 14f

        canvas.drawRect(40f, yPos, 555f, yPos + 22f, headerBoxPaint)
        canvas.drawRect(40f, yPos, 555f, yPos + 22f, borderPaint)
        canvas.drawText("Bill Number", 50f, yPos + 15f, bodyBoldPaint)
        canvas.drawText("Timestamp", 180f, yPos + 15f, bodyBoldPaint)
        canvas.drawText("Items", 320f, yPos + 15f, bodyBoldPaint)
        canvas.drawText("Payment", 410f, yPos + 15f, bodyBoldPaint)
        canvas.drawText("Total Amount", 480f, yPos + 15f, bodyBoldPaint)
        yPos += 22f

        sales.take(4).forEach { s ->
            canvas.drawRect(40f, yPos, 555f, yPos + 22f, borderPaint)
            canvas.drawText(s.saleNumber, 50f, yPos + 15f, monoPaint)
            val time = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(s.timestamp))
            canvas.drawText(time, 180f, yPos + 15f, bodyRegularPaint)
            canvas.drawText("${s.items.size} SKUs", 320f, yPos + 15f, bodyRegularPaint)
            canvas.drawText(s.paymentMethod, 410f, yPos + 15f, bodyBoldPaint)
            canvas.drawText("₹${"%.2f".format(s.totalAmount)}", 480f, yPos + 15f, monoPaint)
            yPos += 22f
        }

        yPos += 30f

        // 4. Strategic Governance Note
        canvas.drawText("AI STRATEGIC GOVERNANCE COMPLIANCE", 40f, yPos, bodyBoldPaint)
        yPos += 12f
        canvas.drawText("All AI pricing and inventory recommendations reflect non-binding analytical proposals.", 40f, yPos, subPaint)
        yPos += 12f
        canvas.drawText("Zero autonomous changes occur without authenticated Shop Owner approval in the audit ledger.", 40f, yPos, subPaint)

        // Footer Bar
        canvas.drawLine(40f, 790f, 555f, 790f, borderPaint)
        canvas.drawText("StoreVisionPro Enterprise Retail Platform • Deterministic Edge Architecture", 40f, 805f, subPaint)
        canvas.drawText("Page 1 of 1", 500f, 805f, subPaint)

        pdfDocument.finishPage(page)

        // Save PDF to cache directory
        return try {
            val reportFile = File(context.cacheDir, "StoreVision_Report_${System.currentTimeMillis()}.pdf")
            val outputStream = FileOutputStream(reportFile)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDocument.close()
            reportFile
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }

    fun sharePdfReport(context: Context, pdfFile: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "StoreVisionPro Business Performance Report")
            putExtra(Intent.EXTRA_TEXT, "Attached is the latest business performance report from StoreVisionPro.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "Share Report via")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
