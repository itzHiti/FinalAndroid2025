package kz.itzhiti.donernaabaya.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kz.itzhiti.donernaabaya.data.database.entities.DonorCoinEntity

@Dao
interface DonorCoinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBalance(balance: DonorCoinEntity)

    @Query("SELECT * FROM donor_coins WHERE userId = :userId")
    suspend fun getBalance(userId: String): DonorCoinEntity?

    @Query("UPDATE donor_coins SET balance = :newBalance, updatedAt = :updatedAt WHERE userId = :userId")
    suspend fun updateBalance(userId: String, newBalance: Double, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT COALESCE(balance, 0.0) FROM donor_coins WHERE userId = :userId")
    suspend fun getBalanceAmount(userId: String): Double

    @Query("DELETE FROM donor_coins WHERE userId = :userId")
    suspend fun deleteBalance(userId: String)
}

