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


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CultureQuestTheme(dynamicColor = false) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
                //HomeScreen()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf("home") }
    val gameViewModel: GameViewModel = viewModel()


    when (currentScreen) {
        "home" -> HomeScreen(
            onAboutClick = { currentScreen = "about" },
            onGameClick = { currentScreen = "game" },
            gameViewModel = gameViewModel
        )
        "about" -> AboutPageScreen(
            onBackClick = { currentScreen = "home" }
        )
        "game" -> GamePageScreen(
            onBackClick = { currentScreen = "home" },
            onGameEnd = { currentScreen = "home" },
            viewModel = gameViewModel

        )
    }
}

