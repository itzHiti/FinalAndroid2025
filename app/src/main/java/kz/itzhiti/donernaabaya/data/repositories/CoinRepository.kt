package kz.itzhiti.donernaabaya.data.repositories

import android.content.Context
import kz.itzhiti.donernaabaya.data.api.ApiClient
import kz.itzhiti.donernaabaya.data.api.CoinBalance
import kz.itzhiti.donernaabaya.data.api.CoinService
import kz.itzhiti.donernaabaya.data.api.CoinTransaction
import kz.itzhiti.donernaabaya.data.auth.TokenManager
import kz.itzhiti.donernaabaya.data.database.AppDatabase
import kz.itzhiti.donernaabaya.data.database.entities.DonorCoinEntity

class CoinRepository(context: Context) {
    private val api = ApiClient.coinService(context)
    private val db = AppDatabase.getDatabase(context)
    private val coinDao = db.donorCoinDao()
    private val transactionDao = db.coinTransactionDao()
    private val tokenManager = TokenManager(context)

    suspend fun getBalance(): CoinBalance {
        return try {
            val balance = api.getBalance()
            // Сохраняем в БД
            val entity = DonorCoinEntity(
                userId = balance.user_id,
                balance = balance.doner_coins
            )
            coinDao.insertOrUpdateBalance(entity)
            balance
        } catch (e: Exception) {
            // Если нет интернета, берем из БД
            val userId = tokenManager.getUserId() ?: throw e
            val entity = coinDao.getBalance(userId) ?: throw CoinException("Баланс не найден", e)
            CoinBalance(
                user_id = entity.userId,
                doner_coins = entity.balance,
                created_at = entity.createdAt.toString(),
                updated_at = entity.updatedAt.toString()
            )
        }
    }

    suspend fun getHistory(): List<CoinTransaction> {
        return try {
            val transactions = api.getHistory()
            // Сохраняем в БД
            transactions.forEach { transaction ->
                val entity = kz.itzhiti.donernaabaya.data.database.entities.CoinTransactionEntity(
                    userId = transaction.user_id,
                    orderId = transaction.order_id,
                    amount = transaction.amount,
                    type = transaction.type,
                    description = transaction.description,
                    createdAt = System.currentTimeMillis()
                )
                transactionDao.insertTransaction(entity)
            }
            transactions
        } catch (e: Exception) {
            // Если нет интернета, берем из БД
            val userId = tokenManager.getUserId() ?: throw e
            transactionDao.getUserTransactions(userId).map { entity ->
                CoinTransaction(
                    id = entity.id,
                    user_id = entity.userId,
                    order_id = entity.orderId,
                    amount = entity.amount,
                    type = entity.type,
                    description = entity.description,
                    created_at = entity.createdAt.toString()
                )
            }
        }
    }

    suspend fun getOrderTransactions(orderId: Long): List<CoinTransaction> {
        return try {
            val transactions = api.getOrderTransactions(orderId)
            transactions.forEach { transaction ->
                val entity = kz.itzhiti.donernaabaya.data.database.entities.CoinTransactionEntity(
                    userId = transaction.user_id,
                    orderId = transaction.order_id,
                    amount = transaction.amount,
                    type = transaction.type,
                    description = transaction.description,
                    createdAt = System.currentTimeMillis()
                )
                transactionDao.insertTransaction(entity)
            }
            transactions
        } catch (e: Exception) {
            transactionDao.getOrderTransactions(orderId).map { entity ->
                CoinTransaction(
                    id = entity.id,
                    user_id = entity.userId,
                    order_id = entity.orderId,
                    amount = entity.amount,
                    type = entity.type,
                    description = entity.description,
                    created_at = entity.createdAt.toString()
                )
            }
        }
    }
}

class CoinException(message: String, cause: Throwable? = null) : Exception(message, cause)
