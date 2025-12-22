package kz.itzhiti.donernaabaya.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donor_coins")
data class DonorCoinEntity(
    @PrimaryKey
    val userId: String,
    val balance: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

