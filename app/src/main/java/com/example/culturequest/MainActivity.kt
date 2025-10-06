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
import com.example.culturequest.ui.theme.CultureQuestTheme
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.culturequest.data.*
import kotlinx.coroutines.launch
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CultureQuestTheme(dynamicColor = false) {
                HomeScreen()
            }
        }

        // ------------------------
        // Temporary database test
        // ------------------------
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "culturequest-db"
        ).build()

        val userDao = db.userDao()
        val questionDao = db.questionDao()

        lifecycleScope.launch {
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

