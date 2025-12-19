package org.example.basket.dao

import org.example.basket.model.Cart
import org.example.basket.model.CartItems
import org.example.basket.model.Orders
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.time.LocalDateTime


@Repository
class CartDao(private val jdbc: JdbcTemplate) {

    fun createCart(userId: Long): Long {
        val now = LocalDateTime.now()
        val sql = """
            INSERT INTO basket.cart(user_id, created_at, updated_at)
            VALUES (?, ?, ?)
            RETURNING id
        """
        return jdbc.queryForObject(sql, Long::class.java, userId, now, now)!!
    }

    fun findByUserId(userId: Long): List<Cart> {
        val sql = "SELECT * FROM basket.cart WHERE user_id = ?"
        return jdbc.query(sql, arrayOf(userId)) { rs, _ ->
            Cart(
                id = rs.getLong("id"),
                userId = rs.getLong("user_id"),
                createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
                updatedAt = rs.getTimestamp("updated_at").toLocalDateTime()
            )
        }
    }

    fun findById(cartId: Long): Cart? {
        val sql = "SELECT * FROM basket.cart WHERE id = ?"
        return jdbc.query(sql, arrayOf(cartId)) { rs, _ ->
            Cart(
                id = rs.getLong("id"),
                userId = rs.getLong("user_id"),
                createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
                updatedAt = rs.getTimestamp("updated_at").toLocalDateTime()
            )
        }.firstOrNull()
    }

    fun deleteById(cartId: Long) {
        jdbc.update("DELETE FROM basket.cart WHERE id = ?", cartId)
    }
    fun findOrCreateDefaultCart(): Long {
        val existingCart = jdbc.query("SELECT * FROM basket.cart LIMIT 1") { rs, _ ->
            rs.getLong("id")
        }.firstOrNull()

        return existingCart ?: createCart(0) // user_id = 0
    }
}