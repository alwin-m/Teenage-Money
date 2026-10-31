package com.teenagemoney.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.teenagemoney.app.data.TeenageMoneyRepository
import com.teenagemoney.app.ui.TeenageMoneyApp
import com.teenagemoney.app.ui.theme.TeenageMoneyTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Light system bars using the modern enableEdgeToEdge API (no deprecated color setters)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.parseColor("#FBFAF8"),
                android.graphics.Color.parseColor("#FBFAF8")
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.WHITE,
                android.graphics.Color.WHITE
            )
        )
        WindowCompat.setDecorFitsSystemWindows(window, true)

        val repository = TeenageMoneyRepository(this)

        setContent {
            TeenageMoneyTheme {
                TeenageMoneyApp(repository = repository)
            }
        }
    }
}
