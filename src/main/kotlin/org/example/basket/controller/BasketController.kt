package org.example.basket.controller


import org.example.basket.client.AuthClient
import org.example.basket.model.Cart
import org.example.basket.model.CartItems
import org.example.basket.model.Orders
import org.example.basket.dto.response.UserDataResponse
import org.example.basket.dto.request.CreateOrderRequest
import org.example.basket.dto.request.ItemAddRequest
import java.time.LocalDateTime
import org.example.basket.service.CartItemService
import org.springframework.http.ResponseEntity

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/basket")
class BasketController(
    private val orderItemService: CartItemService,
    private val authClient: AuthClient
) {
    companion object {
        const val TOKEN: String = "Authorization"
    }

    @PostMapping("/item/add")
    fun addItemNoAuth(@RequestBody request: ItemAddRequest): ResponseEntity<Any> {
        return try {
            val cart = orderItemService.addItemNoAuth(request)
            ResponseEntity.ok(cart)
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.status(500).body(mapOf(
                "error" to "Internal Server Error",
                "message" to e.message
            ))
        }
    }


    


    @DeleteMapping("/product/{productId}/delete")
    fun deleteItem(@RequestHeader(TOKEN) token: String, @PathVariable productId: Long): ResponseEntity<String> {
        val user = authClient.getUserByToken(token) ?: throw Exception("User not found")
        orderItemService.deleteItem(user, productId)

        return ResponseEntity.ok("Products deleted")
    }

    @DeleteMapping("/cart/clear")
    fun cartClear(@RequestHeader(TOKEN) token: String): ResponseEntity<String> {
        val user = authClient.getUserByToken(token) ?: throw Exception("User not found")
        orderItemService.clearCart(user)

        return ResponseEntity.ok("Cart cleared")
    }

    @PostMapping("/cart/{cartId}/order/create")
    fun orderArranges(
        @RequestHeader(TOKEN) token: String,
        @PathVariable cartId: Long,
        @RequestBody request: CreateOrderRequest
    ): ResponseEntity<Orders> {
        val user = authClient.getUserByToken(token) ?: throw Exception("User not found")

        val order = orderItemService.createOrder(user, cartId, request)
        return ResponseEntity.ok(order)
    }

    @DeleteMapping("/cart/clear-test")
    fun cartClearTest(): ResponseEntity<String> {
        // Создаём тестового пользователя
        //val testUser = UserDataResponse(
        // id = 1, // Можно любой ID
            //name = "Test User"
            // Добавь другие поля, если нужно
    // )

        // Очищаем корзину
    // orderItemService.clearCart(testUser)

        return ResponseEntity.ok("Cart cleared (test mode)")
    }
   

}