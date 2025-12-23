package kz.itzhiti.donernaabaya.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageUrl: String?,
    val available: Boolean,
    val updatedAt: Long = System.currentTimeMillis()
)

