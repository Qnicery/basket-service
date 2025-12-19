package org.example.basket.dao

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class OrderDao(private val jdbc: JdbcTemplate) {

    fun createOrder(
        userId: Long,
        totalAmount: Int,
        orderNumber: String,
        shippingAddress: String,
        billingAddress: String
    ): Long {
        val now = LocalDateTime.now()
        val sql = """
            INSERT INTO basket.orders(user_id, total_amount, order_number, shipping_address, billing_address, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            RETURNING id
        """
        return jdbc.queryForObject(sql, Long::class.java, userId, totalAmount, orderNumber, shippingAddress, billingAddress, now, now)!!
    }
}