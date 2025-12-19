package org.example.basket.controller

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.basket.client.AuthClient
import org.example.basket.dto.request.CreateOrderRequest
import org.example.basket.dto.request.ItemAddRequest
import org.example.basket.dto.response.UserDataResponse
import org.example.basket.service.CartItemService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class BasketControllerTest {

    private lateinit var cartItemService: CartItemService
    private lateinit var authClient: AuthClient
    private lateinit var basketController: BasketController

    @BeforeEach
    fun setUp() {
        cartItemService = mockk()
        authClient = mockk()
        basketController = BasketController(cartItemService, authClient)
    }

    @Test
    fun `addItem should return cart when item is added successfully`() {
        val token = "Bearer test-token"
        val request = ItemAddRequest(productId = 1L, quantity = 2)
        val user = UserDataResponse(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val cart = Cart(1L, 1L, LocalDateTime.now(), LocalDateTime.now())

        every { authClient.getUserByToken(token) } returns user
        every { cartItemService.addItem(request, user) } returns cart

        val response = basketController.addItem(token, request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(cart, response.body)
        verify { authClient.getUserByToken(token) }
        verify { cartItemService.addItem(request, user) }
    }

    @Test
    fun `addItem should throw exception when user not found`() {
        val token = "Bearer test-token"
        val request = ItemAddRequest(productId = 1L, quantity = 2)

        every { authClient.getUserByToken(token) } returns null

        assertThrows(Exception::class.java) {
            basketController.addItem(token, request)
        }
        verify { authClient.getUserByToken(token) }
        verify(exactly = 0) { cartItemService.addItem(any(), any()) }
    }

    @Test
    fun `deleteItem should return success message`() {
        val token = "Bearer test-token"
        val productId = 1L
        val user = UserDataResponse(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { authClient.getUserByToken(token) } returns user
        every { cartItemService.deleteItem(user, productId) } returns Unit

        val response = basketController.deleteItem(token, productId)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Products deleted", response.body)
        verify { authClient.getUserByToken(token) }
        verify { cartItemService.deleteItem(user, productId) }
    }

    @Test
    fun `cartClear should return success message`() {
        val token = "Bearer test-token"
        val user = UserDataResponse(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { authClient.getUserByToken(token) } returns user
        every { cartItemService.clearCart(user) } returns Unit

        val response = basketController.cartClear(token)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Cart cleared", response.body)
        verify { authClient.getUserByToken(token) }
        verify { cartItemService.clearCart(user) }
    }

    @Test
    fun `orderArranges should return order when created successfully`() {
        val token = "Bearer test-token"
        val cartId = 1L
        val request = CreateOrderRequest(
            orderNumber = "ORD-001",
            shippingAddress = "123 Main St",
            billingAddress = "123 Main St"
        )
        val user = UserDataResponse(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val order = Orders(
            1L,
            1L,
            "ORD-001",
            200,
            "pending",
            "123 Main St",
            "123 Main St",
            LocalDateTime.now(),
            LocalDateTime.now()
        )

        every { authClient.getUserByToken(token) } returns user
        every { cartItemService.createOrder(user, cartId, request) } returns order

        val response = basketController.orderArranges(token, cartId, request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(order, response.body)
        verify { authClient.getUserByToken(token) }
        verify { cartItemService.createOrder(user, cartId, request) }
    }
}

