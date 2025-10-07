package com.example.culturequest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.culturequest.ui.screens.HomeScreen
import com.example.culturequest.ui.screens.AboutPageScreen
import com.example.culturequest.ui.screens.GamePageScreen
import com.example.culturequest.ui.theme.CultureQuestTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.culturequest.ui.viewmodel.GameViewModel


// Main activity of the app, entry point for the Compose UI
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable full-screen edge-to-edge content
        enableEdgeToEdge()

        // Set the Compose content
        setContent {
            // Apply custom theme to the whole app
            CultureQuestTheme(dynamicColor = false) {
                // Surface provides a background for Compose content
                Surface(modifier = Modifier.fillMaxSize()) {
                    // Launch the navigation composable to switch between screens
                    AppNavigation()
                }
            }
        }
    }
}

// Composable to handle app navigation between Home, About, and Game screens
@Composable
fun AppNavigation() {
    // Track the current screen: "home", "about", or "game"
    var currentScreen by remember { mutableStateOf("home") }

    // Create a single instance of GameViewModel for sharing state across screens
    val gameViewModel: GameViewModel = viewModel()

    // Display the appropriate screen based on currentScreen
    when (currentScreen) {
        "home" -> HomeScreen(
            onAboutClick = { currentScreen = "about" }, // Navigate to About page
            onGameClick = { currentScreen = "game" },   // Navigate to Game page
            gameViewModel = gameViewModel               // Pass ViewModel for score updates
        )
        "about" -> AboutPageScreen(
            onBackClick = { currentScreen = "home" }    // Navigate back to Home
        )
        "game" -> GamePageScreen(
            onBackClick = { currentScreen = "home" },  // Navigate back to Home manually
            onGameEnd = { currentScreen = "home" },    // Navigate back automatically when game ends
            viewModel = gameViewModel                   // Pass the shared GameViewModel
        )
    }
}