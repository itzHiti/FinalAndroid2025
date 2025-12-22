package kz.itzhiti.donernaabaya.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val id: Long,
    val customerId: String,
    val totalPrice: Double,
    val status: String, // PENDING, CONFIRMED, PREPARING, READY, ON_DELIVERY, DELIVERED, CANCELLED
    val deliveryAddress: String,
    val comment: String?,
    val createdAt: Long,
    val updatedAt: Long = System.currentTimeMillis(),
    val synced: Boolean = false // для offline support
)
