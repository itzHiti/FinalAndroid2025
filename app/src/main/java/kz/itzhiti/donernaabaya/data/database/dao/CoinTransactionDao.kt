package kz.itzhiti.donernaabaya.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kz.itzhiti.donernaabaya.data.database.entities.CoinTransactionEntity

@Dao
interface CoinTransactionDao {
    @Insert
    suspend fun insertTransaction(transaction: CoinTransactionEntity)

    @Insert
    suspend fun insertTransactions(transactions: List<CoinTransactionEntity>)

    @Query("SELECT * FROM coin_transactions WHERE userId = :userId ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getUserTransactions(userId: String, limit: Int = 100): List<CoinTransactionEntity>

    @Query("SELECT * FROM coin_transactions WHERE orderId = :orderId ORDER BY createdAt DESC")
    suspend fun getOrderTransactions(orderId: Long): List<CoinTransactionEntity>

    @Query("SELECT * FROM coin_transactions WHERE userId = :userId AND type = :type ORDER BY createdAt DESC")
    suspend fun getTransactionsByType(userId: String, type: String): List<CoinTransactionEntity>

    @Query("SELECT SUM(amount) FROM coin_transactions WHERE userId = :userId AND type = 'EARNED'")
    suspend fun getTotalEarned(userId: String): Double?

    @Query("SELECT SUM(amount) FROM coin_transactions WHERE userId = :userId AND type = 'SPENT'")
    suspend fun getTotalSpent(userId: String): Double?

    @Query("DELETE FROM coin_transactions WHERE userId = :userId")
    suspend fun deleteUserTransactions(userId: String)
}

