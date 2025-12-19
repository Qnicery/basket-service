package org.example.basket.service

import org.example.basket.model.Cart
import org.example.basket.model.CartItems
import org.example.basket.model.Orders
import org.example.basket.dao.CartDao
import org.example.basket.dao.CartItemDao
import org.example.basket.dao.OrderDao
import org.example.basket.dao.OrderItemDao
import org.example.basket.dto.request.CreateOrderRequest
import org.example.basket.dto.request.ItemAddRequest
import org.example.basket.dto.response.UserDataResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@Service
class CartItemService(
    private val cartItemDao: CartItemDao,
    private val cartDao: CartDao,
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao
) {

    fun addItem(item: ItemAddRequest, user: UserDataResponse): Cart {
        // Получаем корзину пользователя
        val cart = cartDao.findByUserId(user.id).firstOrNull()
        val cartId = if (cart == null) {
            cartDao.createCart(user.id)
        } else {
            cart.id
        }

        // Проверяем, есть ли такой товар в корзине
        val items = cartItemDao.getByCartAndProduct(cartId, item.productId)
        if (items == null) {
            cartItemDao.createCartItem(cartId, item.productId, item.quantity)
        } else {
            // обновляем количество
            cartItemDao.updateQuantity(items.cartId, items.productId, items.quantity + item.quantity)
        }

        return cartDao.findById(cartId)!!
    }

    fun clearCart(user: UserDataResponse) {
        val cart = cartDao.findByUserId(user.id).firstOrNull() ?: return
        cartItemDao.deleteByCartId(cart.id)
    }

    fun deleteItem(user: UserDataResponse, productId: Long) {
        val cart = cartDao.findByUserId(user.id).firstOrNull() ?: return
        cartItemDao.deleteByProductId(productId)
    }

    fun createOrder(user: UserDataResponse, cartId: Long, request: CreateOrderRequest): Orders {
        val cart = cartDao.findById(cartId)!!

        val items = cartItemDao.fetchByCartId(cartId)

        val orderId = orderDao.createOrder(
            userId = user.id,
            totalAmount = getTotalPrice(items),
            orderNumber = request.orderNumber,
            shippingAddress = request.shippingAddress,
            billingAddress = request.billingAddress
        )

        // Сохраняем товары в заказ
        for (item in items) {
            orderItemDao.createOrderItem(orderId, item.productId, item.quantity, 100)
        }

        // Чистим корзину
        cartItemDao.deleteByCartId(cart.id)
        cartDao.deleteById(cart.id)

        return Orders(orderId, user.id, getTotalPrice(items), request.orderNumber, request.shippingAddress, request.billingAddress, LocalDateTime.now(), LocalDateTime.now())
    }

    private fun getTotalPrice(items: List<CartItems>): Int {
        return items.sumOf { it.quantity * 100 }
    }

    fun addItemNoAuth(item: ItemAddRequest): Cart {
        // Всегда используем корзину "по умолчанию" с userId = 0
        val cart = cartDao.findByUserId(0).firstOrNull()
        val cartId = cart?.id ?: cartDao.createCart(0)

        // Проверяем, есть ли такой товар в корзине
        val existingItem = cartItemDao.getByCartAndProduct(cartId, item.productId)
        if (existingItem == null) {
            cartItemDao.createCartItem(cartId, item.productId, item.quantity)
        } else {
            cartItemDao.updateQuantity(existingItem.cartId, existingItem.productId, existingItem.quantity + item.quantity)
        }

        // Возвращаем корзину с товарами
        return cartDao.findById(cartId) ?: throw Exception("Failed to fetch cart after adding item")
    }


}
