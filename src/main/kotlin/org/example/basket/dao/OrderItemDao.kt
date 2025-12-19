package org.example.basket.dao

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class OrderItemDao(private val jdbc: JdbcTemplate) {

    fun createOrderItem(orderId: Long, productId: Long, quantity: Int, unitPrice: Int) {
        val now = LocalDateTime.now()
        val totalPrice = quantity * unitPrice
        val sql = """
            INSERT INTO basket.order_items(order_id, product_id, quantity, unit_price, total_price, created_at)
            VALUES (?, ?, ?, ?, ?, ?)
        """
        jdbc.update(sql, orderId, productId, quantity, unitPrice, totalPrice, now)
    }
}