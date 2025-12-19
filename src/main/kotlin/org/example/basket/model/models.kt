package org.example.basket.model

import java.time.LocalDateTime

data class Cart(
    val id: Long,
    val userId: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class CartItems(
    val cartId: Long,
    val productId: Long,
    var quantity: Int,
    var addedAt: LocalDateTime
)

data class Orders(
    val id: Long,
    val userId: Long,
    val totalAmount: Int,
    val orderNumber: String,
    val shippingAddress: String,
    val billingAddress: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
