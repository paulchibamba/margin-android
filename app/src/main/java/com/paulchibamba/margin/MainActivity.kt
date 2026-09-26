package com.paulchibamba.margin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.catalog.DesignSystemCatalog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MarginTheme {
                DesignSystemCatalog()
            }
        }
    }
}
