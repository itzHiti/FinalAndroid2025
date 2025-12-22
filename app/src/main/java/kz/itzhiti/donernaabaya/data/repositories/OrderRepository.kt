package kz.itzhiti.donernaabaya.data.repositories

import android.content.Context
import kz.itzhiti.donernaabaya.config.ApiConfig
import kz.itzhiti.donernaabaya.data.api.ApiClient
import kz.itzhiti.donernaabaya.data.api.CreateOrderRequest
import kz.itzhiti.donernaabaya.data.api.Order
import kz.itzhiti.donernaabaya.data.api.OrderItemRequest
import kz.itzhiti.donernaabaya.data.api.OrderService
import kz.itzhiti.donernaabaya.data.api.UpdateOrderStatusRequest
import kz.itzhiti.donernaabaya.data.auth.TokenManager
import kz.itzhiti.donernaabaya.data.database.AppDatabase
import kz.itzhiti.donernaabaya.data.database.entities.OrderEntity
import kz.itzhiti.donernaabaya.data.database.entities.OrderItemEntity
import kz.itzhiti.donernaabaya.data.database.entities.CoinTransactionEntity

class OrderRepository(context: Context) {
    private val api = ApiClient.orderService(context)
    private val db = AppDatabase.getDatabase(context)
    private val orderDao = db.orderDao()
    private val orderItemDao = db.orderItemDao()
    private val coinTransactionDao = db.coinTransactionDao()
    private val donorCoinDao = db.donorCoinDao()
    private val tokenManager = TokenManager(context)

    suspend fun createOrder(items: List<OrderItemRequest>, address: String, comment: String?): Order {
        return try {
            val request = CreateOrderRequest(items, address, comment)
            val order = api.createOrder(request)

            // Сохраняем заказ в БД
            saveOrderToDatabase(order)
            order
        } catch (e: Exception) {
            throw OrderException("Ошибка создания заказа: ${e.message}", e)
        }
    }

    suspend fun getUserOrders(): List<Order> {
        return try {
            val orders = api.getUserOrders()
            // Обновляем БД
            val userId = tokenManager.getUserId() ?: return orders
            orders.forEach { saveOrderToDatabase(it) }
            orders
        } catch (e: Exception) {
            // Если нет интернета, берем из БД
            val userId = tokenManager.getUserId() ?: throw e
            orderDao.getUserOrders(userId).map { it.toOrder(emptyList()) }
        }
    }

    suspend fun getOrder(id: Long): Order {
        return try {
            val order = api.getOrder(id)
            saveOrderToDatabase(order)
            order
        } catch (e: Exception) {
            orderDao.getOrderById(id)?.toOrder(emptyList()) ?: throw e
        }
    }

    suspend fun updateOrderStatus(orderId: Long, status: String): Order {
        return try {
            val request = UpdateOrderStatusRequest(status)
            val order = api.updateOrderStatus(orderId, request)

            // Обновляем в БД
            orderDao.updateOrderStatus(orderId, status)

            // Если статус DELIVERED, начисляем дкоины
            if (status == "DELIVERED") {
                handleDeliveredOrder(orderId, order)
            }

            order
        } catch (e: Exception) {
            throw OrderException("Ошибка обновления статуса: ${e.message}", e)
        }
    }

    suspend fun cancelOrder(orderId: Long) {
        return try {
            api.cancelOrder(orderId)
            orderDao.deleteOrder(orderId)
        } catch (e: Exception) {
            throw OrderException("Ошибка отмены заказа: ${e.message}", e)
        }
    }

    suspend fun getAllOrders(): List<Order> {
        return try {
            val orders = api.getAllOrders()
            orders.forEach { saveOrderToDatabase(it) }
            orders
        } catch (e: Exception) {
            orderDao.getAllOrders().map { it.toOrder(emptyList()) }
        }
    }

    private suspend fun saveOrderToDatabase(order: Order) {
        val orderEntity = OrderEntity(
            id = order.id,
            customerId = order.customerId,
            totalPrice = order.totalPrice,
            status = order.status,
            deliveryAddress = "",
            comment = null,
            createdAt = System.currentTimeMillis(),
            synced = true
        )
        orderDao.insertOrder(orderEntity)

        // Сохраняем items
        order.items.forEach { item ->
            val itemEntity = OrderItemEntity(
                orderId = order.id,
                productId = item.productId,
                quantity = item.quantity,
                priceAtOrder = item.price
            )
            orderItemDao.insertOrderItem(itemEntity)
        }
    }

    private suspend fun handleDeliveredOrder(orderId: Long, order: Order) {
        val userId = tokenManager.getUserId() ?: return
        val coinsEarned = order.totalPrice * ApiConfig.COIN_EARN_PERCENTAGE

        // Создаем транзакцию
        val transaction = CoinTransactionEntity(
            userId = userId,
            orderId = orderId,
            amount = coinsEarned,
            type = "EARNED",
            description = "Заработано 5% за заказ #$orderId"
        )
        coinTransactionDao.insertTransaction(transaction)

        // Обновляем баланс
        val currentBalance = donorCoinDao.getBalanceAmount(userId)
        val newBalance = currentBalance + coinsEarned
        donorCoinDao.updateBalance(userId, newBalance)
    }

    private fun OrderEntity.toOrder(items: List<OrderItemEntity>): Order {
        return Order(
            id = id,
            customerId = customerId,
            totalPrice = totalPrice,
            status = status,
            createdAt = createdAt.toString(),
            items = emptyList()
        )
    }
}

class OrderException(message: String, cause: Throwable? = null) : Exception(message, cause)
