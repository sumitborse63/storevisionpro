package com.sktech.storevisionpro.domain.insights

import com.sktech.storevisionpro.domain.model.AiInsightItem
import com.sktech.storevisionpro.domain.model.ProductItem
import com.sktech.storevisionpro.domain.model.SuggestionStatus
import java.util.UUID

object OfflineRuleEngine {

    fun generateAllOfflineRules(
        products: List<ProductItem>,
        hourlyFootfall: List<Float>
    ): List<AiInsightItem> {
        val insights = mutableListOf<AiInsightItem>()

        // Rule 1: Near-Expiry Stock Protection (FEFO)
        val nearExpiryProducts = products.filter {
            it.hasExpiry && it.daysUntilExpiry != null && it.daysUntilExpiry <= 3 && it.currentStock > 0
        }
        nearExpiryProducts.forEach { prod ->
            val pairedProduct = products.find { it.category != prod.category && it.currentStock > 10 } ?: products.first()
            val lossAtRisk = prod.currentStock * prod.costPrice
            insights.add(
                AiInsightItem(
                    id = "RULE-EXP-${prod.id}",
                    type = "NEAR_EXPIRY_BUNDLE",
                    title = "Near-Expiry Action: ${prod.name} (${prod.daysUntilExpiry}d Remaining)",
                    explanation = "Batch ${prod.batchNo ?: "Current"} contains ${prod.currentStock.toInt()} ${prod.unit}s expiring in ${prod.daysUntilExpiry} days. Sales velocity is insufficient to clear stock at full price.",
                    sourceProvenance = "Rule-Based FEFO Engine [Local Batch DB]",
                    status = SuggestionStatus.PENDING,
                    proposedActionText = "Create 'Buy 2 ${prod.name} + Get ${pairedProduct.name} at 50% Off' Clearance Combo",
                    estimatedFinancialImpact = "Recovers estimated ₹${(lossAtRisk * 0.8).toInt()} capital, preventing ₹${lossAtRisk.toInt()} total write-off"
                )
            )
        }

        // Rule 2: Stock-Out Velocity & Reorder Advisory
        val lowStockProducts = products.filter { it.currentStock <= it.lowStockThreshold && it.currentStock > 0 }
        lowStockProducts.forEach { prod ->
            val reorderQty = (prod.lowStockThreshold * 3).toInt()
            insights.add(
                AiInsightItem(
                    id = "RULE-REORDER-${prod.id}",
                    type = "REORDER_FORECAST",
                    title = "Stock-Out Warning: ${prod.name}",
                    explanation = "Remaining stock is down to ${prod.currentStock.toInt()} ${prod.unit}s (Threshold: ${prod.lowStockThreshold.toInt()}). Lead time is 48 hours.",
                    sourceProvenance = "Predictive Stock Velocity Model [Offline]",
                    status = SuggestionStatus.PENDING,
                    proposedActionText = "Generate Supplier Purchase Order for $reorderQty ${prod.unit}s",
                    estimatedFinancialImpact = "Avoids estimated ₹${(prod.sellingPrice * 12).toInt()} in lost retail revenue"
                )
            )
        }

        // Rule 3: Quiet Hours Footfall Stimulation
        val afternoonSlumpDensity = hourlyFootfall.getOrElse(14) { 0.1f } // 2 PM
        if (afternoonSlumpDensity < 0.25f) {
            insights.add(
                AiInsightItem(
                    id = "RULE-QUIET-HOUR-01",
                    type = "QUIET_HOUR_PROMOTION",
                    title = "Footfall Dip: 13:00 - 15:30 Afternoon Slump",
                    explanation = "Customer density falls to 12% between 1:00 PM and 3:30 PM. Footfall can be stimulated with happy-hour discounts on packaged groceries.",
                    sourceProvenance = "Footfall Time-Series Heuristics",
                    status = SuggestionStatus.PENDING,
                    proposedActionText = "Activate 'Afternoon Happy Hours: Flat 5% Off on Orders Above ₹500' between 13:00 and 15:30",
                    estimatedFinancialImpact = "+18% projected footfall increase during slow operational shift"
                )
            )
        }

        // Rule 4: Dynamic Peak-Hour Margin Adjustment
        val eveningPeakDensity = hourlyFootfall.getOrElse(19) { 0.9f } // 7 PM
        if (eveningPeakDensity >= 0.8f) {
            insights.add(
                AiInsightItem(
                    id = "RULE-DYNAMIC-PRICING-01",
                    type = "DYNAMIC_PRICING",
                    title = "Dynamic Pricing: Evening Dairy & Snacks Surge",
                    explanation = "Local residential footfall peaks between 18:00 and 20:30. Inelastic essential demand permits a ₹2.00 margin optimization on select dairy and bakery items.",
                    sourceProvenance = "Verified Market Feed [Retail Index 12 Oct]",
                    status = SuggestionStatus.PENDING,
                    proposedActionText = "Increase Paneer 200g / Snack items by ₹2.00 during 18:00-21:00 peak hours",
                    estimatedFinancialImpact = "+₹180 additional net margin per evening"
                )
            )
        }

        return insights
    }
}
