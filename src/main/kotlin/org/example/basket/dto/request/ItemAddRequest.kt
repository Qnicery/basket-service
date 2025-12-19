package org.example.basket.dto.request

data class ItemAddRequest(
    val productId: Long,
    val quantity: Int
)
