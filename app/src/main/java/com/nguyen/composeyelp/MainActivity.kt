package com.nguyen.composeyelp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.nguyen.composeyelp.ui.search.YelpScreen
import com.nguyen.composeyelp.ui.theme.ComposeYelpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeYelpTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    YelpScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
