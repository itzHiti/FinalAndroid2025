package kz.itzhiti.donernaabaya.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kz.itzhiti.donernaabaya.data.database.dao.CoinTransactionDao
import kz.itzhiti.donernaabaya.data.database.dao.DeliveryDao
import kz.itzhiti.donernaabaya.data.database.dao.DonorCoinDao
import kz.itzhiti.donernaabaya.data.database.dao.OrderDao
import kz.itzhiti.donernaabaya.data.database.dao.OrderItemDao
import kz.itzhiti.donernaabaya.data.database.dao.ProductDao
import kz.itzhiti.donernaabaya.data.database.entities.CoinTransactionEntity
import kz.itzhiti.donernaabaya.data.database.entities.DeliveryEntity
import kz.itzhiti.donernaabaya.data.database.entities.DonorCoinEntity
import kz.itzhiti.donernaabaya.data.database.entities.OrderEntity
import kz.itzhiti.donernaabaya.data.database.entities.OrderItemEntity
import kz.itzhiti.donernaabaya.data.database.entities.ProductEntity

@Database(
    entities = [
        ProductEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        DeliveryEntity::class,
        DonorCoinEntity::class,
        CoinTransactionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun deliveryDao(): DeliveryDao
    abstract fun donorCoinDao(): DonorCoinDao
    abstract fun coinTransactionDao(): CoinTransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "doner_naabaya.db"
                )
                    .fallbackToDestructiveMigration() // для development
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

