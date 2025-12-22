package kz.itzhiti.donernaabaya.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kz.itzhiti.donernaabaya.data.database.entities.OrderEntity

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderEntity>)

    @Query("SELECT * FROM orders WHERE customerId = :customerId ORDER BY createdAt DESC")
    suspend fun getUserOrders(customerId: String): List<OrderEntity>

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getOrderById(id: Long): OrderEntity?

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    suspend fun getAllOrders(): List<OrderEntity>

    @Query("UPDATE orders SET status = :status, updatedAt = :updatedAt WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Long, status: String, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM orders WHERE id = :orderId")
    suspend fun deleteOrder(orderId: Long)

    @Query("SELECT * FROM orders WHERE synced = 0")
    suspend fun getUnsyncedOrders(): List<OrderEntity>

    @Query("UPDATE orders SET synced = 1 WHERE id = :orderId")
    suspend fun markAsSynced(orderId: Long)

    @Query("SELECT * FROM orders WHERE status IN ('PENDING', 'CONFIRMED', 'PREPARING', 'READY', 'ON_DELIVERY')")
    suspend fun getActiveOrders(): List<OrderEntity>
}
