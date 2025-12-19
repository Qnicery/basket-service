package org.example.basket.dao

import org.example.basket.model.Cart
import org.example.basket.model.CartItems
import org.example.basket.model.Orders
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class CartItemDao(private val jdbc: JdbcTemplate) {

    fun createCartItem(cartId: Long, productId: Long, quantity: Int) {
        val now = LocalDateTime.now()
        val sql = """
            INSERT INTO basket.cart_items(cart_id, product_id, quantity, added_at)
            VALUES (?, ?, ?, ?)
        """
        jdbc.update(sql, cartId, productId, quantity, now)
    }

    fun deleteByCartId(cartId: Long) {
        jdbc.update("DELETE FROM basket.cart_items WHERE cart_id = ?", cartId)
    }

    fun deleteByProductId(productId: Long) {
        jdbc.update("DELETE FROM basket.cart_items WHERE product_id = ?", productId)
    }
    fun getByCartAndProduct(cartId: Long, productId: Long): CartItems? {
        val sql = "SELECT * FROM basket.cart_items WHERE cart_id = ? AND product_id = ?"
        return jdbc.query(sql, arrayOf(cartId, productId)) { rs, _ ->
            CartItems(
                cartId = rs.getLong("cart_id"),
                productId = rs.getLong("product_id"),
                quantity = rs.getInt("quantity"),
                addedAt = rs.getTimestamp("added_at").toLocalDateTime()
            )
        }.firstOrNull()
    }

    fun fetchByCartId(cartId: Long): List<CartItems> {
        val sql = "SELECT * FROM basket.cart_items WHERE cart_id = ?"
        return jdbc.query(sql, arrayOf(cartId)) { rs, _ ->
            CartItems(
                cartId = rs.getLong("cart_id"),
                productId = rs.getLong("product_id"),
                quantity = rs.getInt("quantity"),
                addedAt = rs.getTimestamp("added_at").toLocalDateTime()
            )
        }
    }


    fun updateQuantity(cartId: Long, productId: Long, quantity: Int) {
        val sql = "UPDATE basket.cart_items SET quantity = ?, added_at = ? WHERE cart_id = ? AND product_id = ?"
        jdbc.update(sql, quantity, LocalDateTime.now(), cartId, productId)
    }

}