package com.sktech.storevisionpro

import com.sktech.storevisionpro.domain.insights.OfflineRuleEngine
import com.sktech.storevisionpro.domain.model.ProductItem
import com.sktech.storevisionpro.domain.model.SuggestionStatus
import org.junit.Assert.*
import org.junit.Test

class OfflineRuleEngineTest {

    @Test
    fun generateAllOfflineRules_detectsNearExpiryProductAndSuggestsBundle() {
        val products = listOf(
            ProductItem(
                id = "P-101",
                name = "Amul Fresh Milk 500ml",
                category = "Dairy",
                sellingPrice = 34.0,
                costPrice = 30.0,
                currentStock = 18.0,
                lowStockThreshold = 10.0,
                hasExpiry = true,
                daysUntilExpiry = 2,
                batchNo = "B-2610A"
            ),
            ProductItem(
                id = "P-102",
                name = "Britannia Good Day",
                category = "Biscuits",
                sellingPrice = 30.0,
                costPrice = 24.0,
                currentStock = 50.0,
                lowStockThreshold = 15.0,
                hasExpiry = true,
                daysUntilExpiry = 90
            )
        )
        val hourlyFootfall = List(24) { 0.5f }

        val rules = OfflineRuleEngine.generateAllOfflineRules(products, hourlyFootfall)

        val expiryRule = rules.find { it.type == "NEAR_EXPIRY_BUNDLE" }
        assertNotNull("Near-expiry bundle rule should be generated for 2-day expiry product", expiryRule)
        assertEquals(SuggestionStatus.PENDING, expiryRule?.status)
        assertTrue(expiryRule!!.explanation.contains("Amul Fresh Milk 500ml") || expiryRule.title.contains("Amul Fresh Milk 500ml"))
        assertTrue(expiryRule.proposedActionText.contains("Clearance Combo"))
    }

    @Test
    fun generateAllOfflineRules_detectsLowStockAndGeneratesReorderAdvisory() {
        val products = listOf(
            ProductItem(
                id = "P-201",
                name = "Fortune Sunflower Oil 1L",
                category = "Oils",
                sellingPrice = 145.0,
                costPrice = 125.0,
                currentStock = 4.0, // Below threshold of 10.0
                lowStockThreshold = 10.0
            )
        )
        val hourlyFootfall = List(24) { 0.5f }

        val rules = OfflineRuleEngine.generateAllOfflineRules(products, hourlyFootfall)

        val reorderRule = rules.find { it.type == "REORDER_FORECAST" }
        assertNotNull("Reorder advisory rule should be generated for stock below threshold", reorderRule)
        assertTrue(reorderRule!!.title.contains("Fortune Sunflower Oil 1L"))
        assertTrue(reorderRule.proposedActionText.contains("Generate Supplier Purchase Order"))
    }

    @Test
    fun generateAllOfflineRules_detectsAfternoonSlumpAndStimulatesFootfall() {
        val products = listOf(
            ProductItem(id = "P-01", name = "Salt", category = "Staples", sellingPrice = 20.0, costPrice = 16.0, currentStock = 20.0, lowStockThreshold = 5.0)
        )
        // Set hour 14 (2 PM) footfall density low (0.10f < 0.25f)
        val hourlyFootfall = MutableList(24) { 0.5f }
        hourlyFootfall[14] = 0.10f

        val rules = OfflineRuleEngine.generateAllOfflineRules(products, hourlyFootfall)

        val slumpRule = rules.find { it.type == "QUIET_HOUR_PROMOTION" }
        assertNotNull("Quiet hours promotion should be proposed when 2 PM footfall dips below 25%", slumpRule)
        assertTrue(slumpRule!!.title.contains("13:00 - 15:30 Afternoon Slump"))
    }

    @Test
    fun generateAllOfflineRules_detectsEveningSurgeAndProposesDynamicPricing() {
        val products = listOf(
            ProductItem(id = "P-01", name = "Curd", category = "Dairy", sellingPrice = 30.0, costPrice = 24.0, currentStock = 20.0, lowStockThreshold = 5.0)
        )
        // Set hour 19 (7 PM) footfall density high (0.92f >= 0.8f)
        val hourlyFootfall = MutableList(24) { 0.5f }
        hourlyFootfall[19] = 0.92f

        val rules = OfflineRuleEngine.generateAllOfflineRules(products, hourlyFootfall)

        val surgeRule = rules.find { it.type == "DYNAMIC_PRICING" }
        assertNotNull("Dynamic pricing rule should trigger during evening surge", surgeRule)
        assertTrue(surgeRule!!.explanation.contains("residential footfall peaks between 18:00 and 20:30"))
    }
}
