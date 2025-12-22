package kz.itzhiti.donernaabaya.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kz.itzhiti.donernaabaya.data.database.entities.DeliveryEntity

@Dao
interface DeliveryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDelivery(delivery: DeliveryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeliveries(deliveries: List<DeliveryEntity>)

    @Query("SELECT * FROM deliveries WHERE id = :id")
    suspend fun getDeliveryById(id: Long): DeliveryEntity?

    @Query("SELECT * FROM deliveries ORDER BY createdAt DESC")
    suspend fun getAllDeliveries(): List<DeliveryEntity>

    @Query("SELECT * FROM deliveries WHERE orderId = :orderId")
    suspend fun getDeliveryByOrderId(orderId: Long): DeliveryEntity?

    @Query("UPDATE deliveries SET status = :status, updatedAt = :updatedAt WHERE id = :deliveryId")
    suspend fun updateDeliveryStatus(deliveryId: Long, status: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE deliveries SET courierId = :courierId, courierName = :courierName WHERE id = :deliveryId")
    suspend fun assignCourier(deliveryId: Long, courierId: Long, courierName: String)

    @Query("SELECT * FROM deliveries WHERE synced = 0")
    suspend fun getUnsyncedDeliveries(): List<DeliveryEntity>

    @Query("UPDATE deliveries SET synced = 1 WHERE id = :deliveryId")
    suspend fun markAsSynced(deliveryId: Long)
}

