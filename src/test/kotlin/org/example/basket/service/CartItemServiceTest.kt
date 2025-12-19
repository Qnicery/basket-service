package org.example.basket.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.basket.dao.CartDao
import org.example.basket.dao.CartItemDao
import org.example.basket.dao.OrderDao
import org.example.basket.dao.OrderItemDao
import org.example.basket.dto.request.CreateOrderRequest
import org.example.basket.dto.request.ItemAddRequest
import org.example.basket.dto.response.UserDataResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

class CartItemServiceTest {

    private lateinit var cartItemDao: CartItemDao
    private lateinit var cartDao: CartDao
    private lateinit var orderDao: OrderDao
    private lateinit var orderItemDao: OrderItemDao
    private lateinit var cartItemService: CartItemService

    @BeforeEach
    fun setUp() {
        cartItemDao = mockk()
        cartDao = mockk()
        orderDao = mockk()
        orderItemDao = mockk()
        cartItemService = CartItemService(cartItemDao, cartDao, orderDao, orderItemDao)
    }

    @Test
    fun `addItem should create new cart when cart does not exist`() {
        val request = ItemAddRequest(productId = 1L, quantity = 2)
        val user = UserDataResponse(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val cartId = 1L
        val cart = Cart(cartId, user.id, LocalDateTime.now(), LocalDateTime.now())

        every { cartDao.fetchByUserId(user.id) } returns emptyList()
        every { cartDao.createCart(user.id) } returns cartId
        every { cartItemDao.getByCartAndProduct(cartId, request.productId) } returns emptyList()
        every { cartItemDao.createCartItem(cartId, request.productId, request.quantity) } returns Unit
        every { cartDao.fetchById(cartId) } returns listOf(cart)

        val result = cartItemService.addItem(request, user)

        assertNotNull(result)
        assertEquals(cartId, result.id)
        verify { cartDao.fetchByUserId(user.id) }
        verify { cartDao.createCart(user.id) }
        verify { cartItemDao.getByCartAndProduct(cartId, request.productId) }
        verify { cartItemDao.createCartItem(cartId, request.productId, request.quantity) }
    }

    @Test
    fun `addItem should update quantity when item already exists in cart`() {
        val request = ItemAddRequest(productId = 1L, quantity = 2)
        val user = UserDataResponse(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val cartId = 1L
        val cart = Cart(cartId, user.id, LocalDateTime.now(), LocalDateTime.now())
        val existingItem = CartItems(
            1L,
            cartId,
            request.productId,
            3,
            LocalDateTime.now()
        )

        every { cartDao.fetchByUserId(user.id) } returns listOf(cart)
        every { cartItemDao.getByCartAndProduct(cartId, request.productId) } returns listOf(existingItem)
        every { cartDao.fetchById(cartId) } returns listOf(cart)

        val result = cartItemService.addItem(request, user)

        assertNotNull(result)
        verify { cartDao.fetchByUserId(user.id) }
        verify { cartItemDao.getByCartAndProduct(cartId, request.productId) }
    }

    @Test
    fun `clearCart should delete all items from cart`() {
        val user = UserDataResponse(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val cart = Cart(1L, user.id, LocalDateTime.now(), LocalDateTime.now())

        every { cartDao.fetchByUserId(user.id) } returns listOf(cart)
        every { cartItemDao.deleteByCartId(cart.id) } returns Unit

        cartItemService.clearCart(user)

        verify { cartDao.fetchByUserId(user.id) }
        verify { cartItemDao.deleteByCartId(cart.id) }
    }

    @Test
    fun `createOrder should create order and clear cart`() {
        val user = UserDataResponse(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val cartId = 1L
        val cart = Cart(cartId, user.id, LocalDateTime.now(), LocalDateTime.now())
        val request = CreateOrderRequest(
            orderNumber = "ORD-001",
            shippingAddress = "123 Main St",
            billingAddress = "123 Main St"
        )
        val items = listOf(
            CartItems(1L, cartId, 1L, 2, LocalDateTime.now())
        )
        val order = Orders(
            1L,
            user.id,
            request.orderNumber,
            200,
            "pending",
            request.shippingAddress,
            request.billingAddress,
            LocalDateTime.now(),
            LocalDateTime.now()
        )

        every { cartDao.fetchById(cartId) } returns listOf(cart)
        every { cartItemDao.fetchByCartId(cartId) } returns items
        every { orderDao.createOrder(cart, 200, request) } returns order
        every { orderItemDao.createItems(order, items) } returns Unit
        every { cartDao.deleteById(cart.id) } returns Unit
        every { cartItemDao.deleteByCartId(cart.id) } returns Unit

        val result = cartItemService.createOrder(user, cartId, request)

        assertNotNull(result)
        assertEquals(order.id, result.id)
        verify { cartDao.fetchById(cartId) }
        verify { cartItemDao.fetchByCartId(cartId) }
        verify { orderDao.createOrder(cart, 200, request) }
        verify { orderItemDao.createItems(order, items) }
        verify { cartDao.deleteById(cart.id) }
        verify { cartItemDao.deleteByCartId(cart.id) }
    }

    @Test
    fun `createOrder should throw exception when cart belongs to different user`() {
        val user = UserDataResponse(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val cartId = 1L
        val cart = Cart(cartId, 999L, LocalDateTime.now(), LocalDateTime.now())
        val request = CreateOrderRequest(
            orderNumber = "ORD-001",
            shippingAddress = "123 Main St",
            billingAddress = "123 Main St"
        )

        every { cartDao.fetchById(cartId) } returns listOf(cart)

        assertThrows(ResponseStatusException::class.java) {
            cartItemService.createOrder(user, cartId, request)
        }
        verify { cartDao.fetchById(cartId) }
        verify(exactly = 0) { orderDao.createOrder(any(), any(), any()) }
    }
}

