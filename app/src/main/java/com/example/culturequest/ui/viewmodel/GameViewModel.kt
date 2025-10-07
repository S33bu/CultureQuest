package com.example.culturequest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.culturequest.MyApp
import com.example.culturequest.R
import com.example.culturequest.data.AppDatabase
import com.example.culturequest.data.QuizQuestion
import com.example.culturequest.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ViewModel to manage the game state
class GameViewModel : ViewModel() {

    // Initialize the Room database
    private val db = Room.databaseBuilder(
        MyApp.context,
        AppDatabase::class.java,
        "culturequest-db"
    ).build()

    // StateFlow holding the list of quiz questions
    private val _questions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val questions: StateFlow<List<QuizQuestion>> = _questions

    // StateFlow tracking the current question index
    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex

    // StateFlow tracking the current user (including score)
    private val _user = MutableStateFlow<UserProfile?>(null)
    val user: StateFlow<UserProfile?> = _user

    // StateFlow to indicate if the game has finished
    private val _isGameFinished = MutableStateFlow(false)
    val isGameFinished: StateFlow<Boolean> = _isGameFinished

    // Initialization block: preload questions and user profile
    init {
        viewModelScope.launch {
            preloadQuestionsIfNeeded() // insert starter questions if DB empty
            _questions.value = db.questionDao().getAllQuestions().shuffled() // shuffle for random order

            val userDao = db.userDao()
            var user = userDao.getUser()
            if (user == null) {
                // Create a default user if none exists
                user = UserProfile(username = "Player", score = 0)
                userDao.insertUser(user)
            }
            _user.value = user
        }
    }

    // Preload default questions only if DB is empty
    private suspend fun preloadQuestionsIfNeeded() {
        val questionDao = db.questionDao()
        val existing = questionDao.getAllQuestions()
        if (existing.isEmpty()) {
            val starterQuestions = listOf(
                QuizQuestion(
                    questionText = "Which country is shown?",
                    correctAnswer = "australia",
                    imageResId = R.drawable.australia
                ),
                QuizQuestion(
                    questionText = "Which country is shown?",
                    correctAnswer = "china",
                    imageResId = R.drawable.china
                ),
                QuizQuestion(
                    questionText = "Which country is shown?",
                    correctAnswer = "egypt",
                    imageResId = R.drawable.egypt
                ),
                QuizQuestion(
                    questionText = "Which country is shown?",
                    correctAnswer = "estonia",
                    imageResId = R.drawable.estonia
                ),
                QuizQuestion(
                    questionText = "Which country is shown?",
                    correctAnswer = "france",
                    imageResId = R.drawable.france
                ),
                QuizQuestion(
                    questionText = "Which country is shown?",
                    correctAnswer = "india",
                    imageResId = R.drawable.india
                ),
                QuizQuestion(
                    questionText = "Which country is shown?",
                    correctAnswer = "indonesia",
                    imageResId = R.drawable.indonesia
                ),
                QuizQuestion(
                    questionText = "Which country is shown?",
                    correctAnswer = "italy",
                    imageResId = R.drawable.italy
                ),
                QuizQuestion(
                    questionText = "Which country is shown?",
                    correctAnswer = "uk",
                    imageResId = R.drawable.uk
                ),
                QuizQuestion(
                    questionText = "Which country is shown?",
                    correctAnswer = "usa",
                    imageResId = R.drawable.usa
                )
            )
            questionDao.insertAll(starterQuestions)
        }
    }

    // Returns the current question object, or null if out of bounds
    fun getCurrentQuestion(): QuizQuestion? {
        return _questions.value.getOrNull(_currentIndex.value)
    }

    // Submit an answer: returns true if correct, false if wrong
    fun submitAnswer(answer: String): Boolean {
        val current = getCurrentQuestion() ?: return false
        val user = _user.value ?: return false

        // Check if the answer is correct (case-insensitive)
        val isCorrect = answer.trim().equals(current.correctAnswer, ignoreCase = true)

        // Calculate the new score (never negative)
        val newScore = if (isCorrect) user.score + 1 else (user.score - 1).coerceAtLeast(0)

        // Update user in DB and StateFlow
        viewModelScope.launch {
            val updatedUser = user.copy(score = newScore)
            db.userDao().insertUser(updatedUser)
            _user.value = updatedUser
        }

        return isCorrect
    }

    // Move to the next question, or finish the game if last question
    fun moveToNextQuestion() {
        val nextIndex = _currentIndex.value + 1
        if (nextIndex < _questions.value.size) {
            _currentIndex.value = nextIndex
        } else {
            _isGameFinished.value = true // signal that game has ended
        }
    }

    // Reset game state: start over
    fun resetGame() {
        _currentIndex.value = 0
        _isGameFinished.value = false
        viewModelScope.launch {
            _questions.value = db.questionDao().getAllQuestions().shuffled()
        }
    }
}
