package com.example.culturequest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.culturequest.data.*
import com.example.culturequest.ui.screens.AboutPageScreen
import com.example.culturequest.ui.screens.HomeScreen
import com.example.culturequest.ui.theme.CultureQuestTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CultureQuestTheme(dynamicColor = false) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // This is the only composable needed here.
                    // It correctly handles showing HomeScreen or AboutPageScreen.
                    AppNavigation()
                }
            }
            // The extra HomeScreen() call has been removed.
        }

        // ------------------------
        // Temporary database test
        // ------------------------
        lifecycleScope.launch {
            // Move the database building and all DAO operations to a background thread
            withContext(Dispatchers.IO) {
                val db = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    "culturequest-db"
                ).build()

                val userDao = db.userDao()
                val questionDao = db.questionDao()

                // Insert a test user
                userDao.insertUser(UserProfile(username = "Kaspar", score = 100))

                // Insert a test question
                questionDao.insertQuestion(
                    QuizQuestion(
                        questionText = "What is the capital of Estonia?",
                        correctAnswer = "Tallinn"
                    )
                )

                // Read back user
                val user = userDao.getUser()
                println("Database test: User = ${user?.username}, Score = ${user?.score}")

                // Read back a random question
                val randomQ = questionDao.getRandomQuestion()
                println("Database test: Random question = ${randomQ?.questionText}")
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf("home") }

    when (currentScreen) {
        "home" -> HomeScreen(
            onAboutClick = { currentScreen = "about" }
        )
        "about" -> AboutPageScreen(
            onBackClick = { currentScreen = "home" }
        )
    }
}
