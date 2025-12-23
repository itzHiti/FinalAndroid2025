package kz.itzhiti.donernaabaya.data.api

import retrofit2.http.GET
import retrofit2.http.Path

interface CoinService {
    @GET("api/coins/balance")
    suspend fun getBalance(): CoinBalance

    @GET("api/coins/history")
    suspend fun getHistory(): List<CoinTransaction>

    @GET("api/coins/order/{orderId}/transactions")
    suspend fun getOrderTransactions(@Path("orderId") orderId: Long): List<CoinTransaction>
}

data class CoinBalance(
    val user_id: String,
    val doner_coins: Double,
    val created_at: String,
    val updated_at: String
)

data class CoinTransaction(
    val id: Long,
    val user_id: String,
    val order_id: Long?,
    val amount: Double,
    val type: String, // EARNED, SPENT, REFUNDED, BONUS
    val description: String,
    val created_at: String
)

