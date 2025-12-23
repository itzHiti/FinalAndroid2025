package kz.itzhiti.donernaabaya.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kz.itzhiti.donernaabaya.data.database.entities.AppSettingsEntity

@Dao
interface AppSettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: AppSettingsEntity)

    @Update
    suspend fun updateSettings(settings: AppSettingsEntity)

    @Query("UPDATE app_settings SET isDarkMode = :isDark WHERE id = 1")
    suspend fun setDarkMode(isDark: Boolean)

    @Query("UPDATE app_settings SET lastSyncTimestamp = :timestamp WHERE id = 1")
    suspend fun updateLastSync(timestamp: Long)
}
