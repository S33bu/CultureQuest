package com.example.culturequest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.culturequest.ui.screens.LoginPageScreen
import com.example.culturequest.ui.screens.ProfilePageScreen
import com.example.culturequest.ui.screens.SignupPageScreen
import com.example.culturequest.ui.theme.CultureQuestTheme
import com.example.culturequest.ui.viewmodel.AuthViewModel
import com.example.culturequest.ui.viewmodel.GameViewModel
import com.example.culturequest.ui.viewmodel.HomeViewModel
import com.example.culturequest.ui.viewmodel.LeaderboardViewModel
import com.example.culturequest.ui.viewmodel.SettingsViewModel

/**
 * Main entry point of the CultureQuest application.
 *
 * Responsible for:
 * - Initializing edge-to-edge layout
 * - Observing user theme preferences
 * - Applying the correct application theme
 * - Hosting the root composable responsible for app navigation
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is first created.
     *
     * Sets up Compose content, observes theme settings from [SettingsViewModel],
     * and applies the appropriate theme before rendering the application UI.
     *
     * @param savedInstanceState Previously saved state of the activity, if available.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val theme by settingsViewModel.theme.collectAsState()

            val useDarkTheme =
                when (theme) {
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

/**
 * Root navigation composable for the application.
 *
 * Manages:
 * - Screen navigation using a simple state-based approach
 * - Shared ViewModels across screens
 * - Authentication state changes and side effects
 * - Passing callbacks and data between screens
 */
@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf("login") }

    // Shared ViewModels used across multiple screens
    val homeViewModel: HomeViewModel = viewModel()
    val gameViewModel: GameViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val leaderboardViewModel: LeaderboardViewModel = viewModel()

    var lastGameScore by remember { mutableIntStateOf(0) }

    // Observe authentication state
    val authState by authViewModel.authState

    /**
     * Reacts to authentication state changes.
     *
     * When the user logs in, reloads user-related game data and navigates to the home screen.
     * When the user logs out, returns to the login screen.
     */
    LaunchedEffect(authState.isLoggedIn) {
        if (authState.isLoggedIn) {
            gameViewModel.reloadUserForCurrentAccount()
            currentScreen = "home"
        } else {
            currentScreen = "login"
        }
    }

    when (currentScreen) {
        "login" ->
            LoginPageScreen(
                onSignUpClick = {
                    authViewModel.clearError()
                    currentScreen = "signup"
                },
                onSignInClick = { email, password ->
                    authViewModel.signIn(email, password)
                },
                isLoading = authState.isLoading,
                errorMessage = authState.errorMessage,
                onClearError = { authViewModel.clearError() },
            )

        "signup" ->
            SignupPageScreen(
                onBackClick = {
                    authViewModel.clearError()
                    currentScreen = "login"
                },
                onSignupClick = { email, password ->
                    authViewModel.signUp(email, password)
                },
                isLoading = authState.isLoading,
                errorMessage = authState.errorMessage,
                onClearError = { authViewModel.clearError() },
            )

        "home" ->
            HomeScreen(
                onAboutClick = { currentScreen = "about" },
                onProfileClick = { currentScreen = "profile" },
                homeViewModel = homeViewModel,
                onGameClick = {
                    gameViewModel.resetGame(resetUserScore = true)
                    currentScreen = "game"
                },
                lastGameScore = lastGameScore,
            )

        "about" ->
            AboutPageScreen(
                onBackClick = { currentScreen = "home" },
            )

        "game" ->
            GamePageScreen(
                onBackClick = {
                    lastGameScore = gameViewModel.user.value?.score ?: 0
                    currentScreen = "home"
                    gameViewModel.resetGame(resetUserScore = false)
                },
                onGameEnd = { score ->
                    lastGameScore = score
                    currentScreen = "home"
                    gameViewModel.resetGame(resetUserScore = false)
                },
                viewModel = gameViewModel,
            )

        "profile" ->
            ProfilePageScreen(
                onBackClick = { currentScreen = "home" },
                gameViewModel = gameViewModel,
                leaderboardViewModel = leaderboardViewModel,
            )
    }
}
