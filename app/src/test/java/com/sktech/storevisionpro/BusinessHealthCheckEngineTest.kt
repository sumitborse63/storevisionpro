package com.sktech.storevisionpro

import com.sktech.storevisionpro.domain.insights.BusinessHealthCheckEngine
import com.sktech.storevisionpro.domain.model.ProductItem
import com.sktech.storevisionpro.domain.model.SaleRecord
import org.junit.Assert.*
import org.junit.Test

class BusinessHealthCheckEngineTest {

    @Test
    fun evaluateBusinessHealth_calculatesHealthyMarginPillarCorrectly() {
        val products = listOf(
            ProductItem("P-1", "Rice 5kg", "Staples", 300.0, 240.0, 20.0, 5.0)
        )
        val sales = emptyList<SaleRecord>()
        val totalRevenue = 10000.0
        val netProfit = 2400.0 // 24% Gross Margin >= 22% benchmark

        val report = BusinessHealthCheckEngine.evaluateBusinessHealth(
            products = products,
            sales = sales,
            totalRevenue = totalRevenue,
            netProfit = netProfit,
            totalCustomers = 50
        )

        val marginPillar = report.pillars.find { it.title == "Gross Profit Margin" }
        assertNotNull(marginPillar)
        assertTrue(marginPillar!!.isHealthy)
        assertEquals("Healthy", marginPillar.status)
        assertEquals(92, marginPillar.score)
    }

    @Test
    fun evaluateBusinessHealth_calculatesDeadStockPillarCorrectly() {
        val products = listOf(
            // Normal stock
            ProductItem("P-1", "Milk", "Dairy", 30.0, 25.0, 10.0, 2.0, hasExpiry = true, daysUntilExpiry = 5),
            // Dead stock: expiry > 60 days with high capital tied up
            ProductItem("P-2", "Expensive Olive Oil", "Gourmet", 900.0, 750.0, 50.0, 5.0, hasExpiry = true, daysUntilExpiry = 120)
        )
        val report = BusinessHealthCheckEngine.evaluateBusinessHealth(
            products = products,
            sales = emptyList(),
            totalRevenue = 5000.0,
            netProfit = 800.0,
            totalCustomers = 20
        )

        val inventoryPillar = report.pillars.find { it.title == "Inventory Velocity & Capital Lockup" }
        assertNotNull(inventoryPillar)
        // High dead stock capital should flag high risk
        assertFalse(inventoryPillar!!.isHealthy)
        assertEquals("High Risk", inventoryPillar.status)
    }

    @Test
    fun evaluateBusinessHealth_generatesWeightedOverallScoreAndRemediations() {
        val products = listOf(
            ProductItem("P-1", "Atta 10kg", "Staples", 450.0, 380.0, 30.0, 5.0)
        )
        val report = BusinessHealthCheckEngine.evaluateBusinessHealth(
            products = products,
            sales = emptyList(),
            totalRevenue = 20000.0,
            netProfit = 4400.0, // 22% Margin
            totalCustomers = 120
        )

        assertTrue("Overall score should be bounded between 0 and 100", report.overallScore in 0..100)
        assertNotNull(report.ratingLabel)
        assertFalse("Strategic remediations list must not be empty", report.strategicRemediations.isEmpty())
        assertEquals(3, report.pillars.size)
    }
}
