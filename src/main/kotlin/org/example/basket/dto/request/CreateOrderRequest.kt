package org.example.basket.dto.request

data class CreateOrderRequest(
    val orderNumber: String,
    val shippingAddress: String,
    val billingAddress: String
)
