package kz.itzhiti.donernaabaya.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey
    val id: Int = 1, // Всегда одна запись
    val isDarkMode: Boolean = false,
    val lastSyncTimestamp: Long = 0L
)

