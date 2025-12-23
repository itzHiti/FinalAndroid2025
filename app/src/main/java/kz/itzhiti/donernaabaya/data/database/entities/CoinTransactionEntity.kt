package kz.itzhiti.donernaabaya.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coin_transactions")
data class CoinTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val orderId: Long?,
    val amount: Double,
    val type: String, // EARNED, SPENT, REFUNDED, BONUS
    val description: String,
    val createdAt: Long = System.currentTimeMillis()
)

