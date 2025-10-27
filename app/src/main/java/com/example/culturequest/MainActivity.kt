package com.example.culturequest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.culturequest.ui.screens.HomeScreen
import com.example.culturequest.ui.screens.AboutPageScreen
import com.example.culturequest.ui.screens.GamePageScreen
import com.example.culturequest.ui.theme.CultureQuestTheme
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

    var lastGameScore by remember { mutableIntStateOf(0) }

    // Display the appropriate screen based on currentScreen
    when (currentScreen) {
        "home" -> HomeScreen(
            onAboutClick = { currentScreen = "about" },
            onGameClick = {
                // Start a fresh game when "Play now" is clicked
                gameViewModel.resetGame(resetUserScore = true)
                currentScreen = "game"
            },
            lastGameScore = lastGameScore,
            gameViewModel = gameViewModel
        )
        "about" -> AboutPageScreen(
            onBackClick = { currentScreen = "home" }    // Navigate back to Home
        )
        "game" -> GamePageScreen(
            onBackClick = {
                // Capture last score when user backs out mid-game
                lastGameScore = gameViewModel.user.value?.score ?: 0
                currentScreen = "home"
                // Reset game for next session but keep last score
                gameViewModel.resetGame(resetUserScore = false)
            },
            onGameEnd = {
                // Capture final score at the end of the game
                lastGameScore = it
                currentScreen = "home"
                // Reset game for next session but keep last score
                gameViewModel.resetGame(resetUserScore = false)
            },
            viewModel = gameViewModel                   // Pass the shared GameViewModel
        )
    }
}