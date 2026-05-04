package com.example.pingify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.pingify.ui.navigation.AppNavigation
import com.example.pingify.ui.theme.PingifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PingifyTheme {
                AppNavigation()
            }
        }
    }
}
