package com.sktech.storevisionpro.marketing

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object PosterBitmapExporter {

    fun generatePosterBitmap(
        context: Context,
        shopName: String,
        promotionBadge: String,
        productName: String,
        templateName: String,
        instagramHandle: String
    ): File? {
        val width = 1080
        val height = 1080
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Background Fill (#09090B Cockpit Canvas)
        val bgPaint = Paint().apply {
            color = Color.parseColor("#09090B")
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // 2. Decorative Outer Border (#10B981 Electric Emerald)
        val borderPaint = Paint().apply {
            color = Color.parseColor("#10B981")
            style = Paint.Style.STROKE
            strokeWidth = 14f
            isAntiAlias = true
        }
        val innerMargin = 40f
        canvas.drawRoundRect(
            RectF(innerMargin, innerMargin, width - innerMargin, height - innerMargin),
            32f, 32f, borderPaint
        )

        // 3. Inner Card Surface (#18181B)
        val surfacePaint = Paint().apply {
            color = Color.parseColor("#18181B")
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(
            RectF(innerMargin + 20f, innerMargin + 20f, width - innerMargin - 20f, height - innerMargin - 20f),
            24f, 24f, surfacePaint
        )

        // 4. Shop Name Header
        val brandPaint = Paint().apply {
            color = Color.parseColor("#F4F4F5")
            textSize = 56f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(shopName.uppercase(), width / 2f, 180f, brandPaint)

        // Template Subtitle
        val subPaint = Paint().apply {
            color = Color.parseColor("#38BDF8")
            textSize = 28f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("★  ${templateName.uppercase()}  ★", width / 2f, 230f, subPaint)

        // Divider
        val dividerPaint = Paint().apply {
            color = Color.parseColor("#27272A")
            strokeWidth = 4f
        }
        canvas.drawLine(140f, 270f, width - 140f, 270f, dividerPaint)

        // 5. Huge Discount Badge Ribbon (#10B981)
        val badgeBgPaint = Paint().apply {
            color = Color.parseColor("#10B981")
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val badgeRect = RectF(120f, 340f, width - 120f, 480f)
        canvas.drawRoundRect(badgeRect, 28f, 28f, badgeBgPaint)

        val badgeTextPaint = Paint().apply {
            color = Color.parseColor("#09090B")
            textSize = 64f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(promotionBadge.uppercase(), width / 2f, 425f, badgeTextPaint)

        // 6. Featured Product Title
        val productPaint = Paint().apply {
            color = Color.parseColor("#FAFAFA")
            textSize = 48f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(productName, width / 2f, 590f, productPaint)

        // Subtext
        val disclaimerPaint = Paint().apply {
            color = Color.parseColor("#A1A1AA")
            textSize = 26f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("Fresh Stock Guaranteed • Valid In-Store & Delivery", width / 2f, 640f, disclaimerPaint)
        canvas.drawText("Offer Valid While Stocks Last • Terms & Conditions Apply", width / 2f, 680f, disclaimerPaint)

        // 7. High-Tech Corner Geometry Elements
        val techLinePaint = Paint().apply {
            color = Color.parseColor("#10B981")
            strokeWidth = 3f
        }
        canvas.drawLine(width / 2f - 100f, 740f, width / 2f + 100f, 740f, techLinePaint)

        // 8. Footer Brand Info & Handle
        val footerPaint = Paint().apply {
            color = Color.parseColor("#71717A")
            textSize = 24f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("VISIT US TODAY • INSTAGRAM: $instagramHandle", width / 2f, 880f, footerPaint)
        canvas.drawText("POWERED BY STOREVISION PRO ENTERPRISE", width / 2f, 920f, footerPaint)

        // Save Bitmap to PNG cache
        return try {
            val file = File(context.cacheDir, "StoreVision_Poster_${System.currentTimeMillis()}.png")
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun sharePosterImage(context: Context, imageFile: File, targetAppPackage: String? = null) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "Special deal at our store! Check out our latest promotion.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (targetAppPackage != null) {
                setPackage(targetAppPackage)
            }
        }

        try {
            if (targetAppPackage != null && intent.resolveActivity(context.packageManager) != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } else {
                val chooser = Intent.createChooser(intent, "Share Poster Image via")
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)
            }
        } catch (e: Exception) {
            // Fallback to standard chooser
            val chooser = Intent.createChooser(intent, "Share Poster Image via")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        }
    }
}
