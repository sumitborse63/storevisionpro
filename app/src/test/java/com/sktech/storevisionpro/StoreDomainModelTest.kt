package com.sktech.storevisionpro

import com.sktech.storevisionpro.domain.model.BillItem
import com.sktech.storevisionpro.domain.model.ProductItem
import com.sktech.storevisionpro.domain.model.SaleRecord
import org.junit.Assert.*
import org.junit.Test

class StoreDomainModelTest {

    @Test
    fun billItem_computesLineTotalCorrectly() {
        val product = ProductItem(
            id = "P-101",
            name = "Amul Gold Milk",
            category = "Dairy",
            sellingPrice = 34.0,
            costPrice = 30.0,
            currentStock = 20.0
        )
        val billItem = BillItem(product = product, quantity = 3.5)

        assertEquals(119.0, billItem.lineTotal, 0.001)
    }

    @Test
    fun saleRecord_tracksPaymentModeAndCustomerFootfall() {
        val productA = ProductItem("P-1", "Bread", "Bakery", 40.0, 32.0, 10.0)
        val productB = ProductItem("P-2", "Butter", "Dairy", 55.0, 48.0, 15.0)

        val items = listOf(
            BillItem(productA, 2.0), // 80.0
            BillItem(productB, 1.0)  // 55.0
        )
        val subtotal = items.sumOf { it.lineTotal }

        val sale = SaleRecord(
            id = "S-99",
            saleNumber = "S-20261012-099",
            timestamp = System.currentTimeMillis(),
            items = items,
            subtotal = subtotal,
            taxAmount = 0.0,
            discountAmount = 10.0,
            totalAmount = subtotal - 10.0,
            customerCount = 2,
            paymentMethod = "UPI"
        )

        assertEquals(135.0, sale.subtotal, 0.001)
        assertEquals(125.0, sale.totalAmount, 0.001)
        assertEquals(2, sale.customerCount)
        assertEquals("UPI", sale.paymentMethod)
        assertEquals(2, sale.items.size)
    }
}
