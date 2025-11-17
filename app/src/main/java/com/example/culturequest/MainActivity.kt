package com.example.culturequest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.culturequest.data.Theme
import com.example.culturequest.ui.screens.AboutPageScreen
import com.example.culturequest.ui.screens.GamePageScreen
import com.example.culturequest.ui.screens.HomeScreen
import com.example.culturequest.ui.screens.ProfilePageScreen
import com.example.culturequest.ui.theme.CultureQuestTheme
import com.example.culturequest.ui.viewmodel.GameViewModel
import com.example.culturequest.ui.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val theme by settingsViewModel.theme.collectAsState()

            val useDarkTheme = when (theme) {
                Theme.LIGHT -> false
                Theme.DARK -> true
                Theme.SYSTEM -> isSystemInDarkTheme()
            }

            CultureQuestTheme(darkTheme = useDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf("home") }
    val gameViewModel: GameViewModel = viewModel()
    var lastGameScore by remember { mutableIntStateOf(0) }

    when (currentScreen) {
        "home" -> HomeScreen(
            onAboutClick = { currentScreen = "about" },
            onProfileClick = { currentScreen = "profile" },
            onGameClick = {
                gameViewModel.resetGame(resetUserScore = true)
                currentScreen = "game"
            },
            lastGameScore = lastGameScore
        )
        "about" -> AboutPageScreen(
            onBackClick = { currentScreen = "home" }
        )
        "game" -> GamePageScreen(
            onBackClick = {
                lastGameScore = gameViewModel.user.value?.score ?: 0
                currentScreen = "home"
                gameViewModel.resetGame(resetUserScore = false)
            },
            onGameEnd = {
                lastGameScore = it
                currentScreen = "home"
                gameViewModel.resetGame(resetUserScore = false)
            },
            viewModel = gameViewModel
        )
        "profile" -> ProfilePageScreen(
            onBackClick = { currentScreen = "home" }
        )
    }
}
