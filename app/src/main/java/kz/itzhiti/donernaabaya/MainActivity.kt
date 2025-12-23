package kz.itzhiti.donernaabaya

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kz.itzhiti.donernaabaya.data.database.AppDatabase

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply saved theme from database
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val settings = db.appSettingsDao().getSettings()
            val isDarkMode = settings?.isDarkMode ?: false
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        // Просто показываем разметку с NavHostFragment
        setContentView(R.layout.activity_main)
    }
}
