package kz.itzhiti.donernaabaya

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Просто показываем разметку с NavHostFragment
        setContentView(R.layout.activity_main)
    }
}
