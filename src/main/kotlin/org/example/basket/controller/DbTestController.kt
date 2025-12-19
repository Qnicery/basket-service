package org.example.basket.controller

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DbTestController(private val jdbcTemplate: JdbcTemplate) {

    @GetMapping("/db-test")
    fun test(): String {
        return try {
            val result = jdbcTemplate.queryForObject("SELECT 1", Int::class.java)
            "DB connected, SELECT 1 = $result"
        } catch (e: Exception) {
            "DB connection failed: ${e.message}"
        }
    }
    @GetMapping("/db-check")
    fun checkTables(): String {
        return try {
            val cartCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM basket.cart", Int::class.java)
            val cartItemsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM basket.cart_items", Int::class.java)
            val ordersCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM basket.orders", Int::class.java)
            val orderItemsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM basket.order_items", Int::class.java)

            "cart=$cartCount, cart_items=$cartItemsCount, orders=$ordersCount, order_items=$orderItemsCount"
        } catch (e: Exception) {
            "DB query failed: ${e.message}"
        }
    }
}
