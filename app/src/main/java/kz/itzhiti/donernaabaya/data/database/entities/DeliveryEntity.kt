package kz.itzhiti.donernaabaya.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deliveries")
data class DeliveryEntity(
    @PrimaryKey
    val id: Long,
    val orderId: Long,
    val address: String,
    val status: String, // PENDING, ASSIGNED, ON_DELIVERY, COMPLETED, CANCELLED
    val courierId: Long?,
    val courierName: String?,
    val etaMinutes: Int?,
    val createdAt: Long,
    val updatedAt: Long = System.currentTimeMillis(),
    val synced: Boolean = false
)

