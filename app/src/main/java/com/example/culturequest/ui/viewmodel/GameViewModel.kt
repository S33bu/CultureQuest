package com.example.culturequest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.culturequest.MyApp
import com.example.culturequest.R
import com.example.culturequest.data.AppDatabase
import com.example.culturequest.data.Country
import com.example.culturequest.data.CountryRepository
import com.example.culturequest.data.Hint
import com.example.culturequest.data.HintTier
import com.example.culturequest.data.QuizQuestion
import com.example.culturequest.data.UserProfile
import kotlinx.coroutines.Dispatchers
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

    //tracks whether countries have been loaded
    private val _countriesLoaded = MutableStateFlow(false)
    val countriesLoaded: StateFlow<Boolean> = _countriesLoaded

    //holds generated hints for the current country
    private val _hints = MutableStateFlow<List<Hint>>(emptyList())
    val hints: StateFlow<List<Hint>> = _hints


    // 0 = none, 1 = hard, 2 = medium, 3 = easy (all)
    private val _tierShown = MutableStateFlow(0)
    val tierShown: StateFlow<Int> = _tierShown

    private val _lastGameScore = MutableStateFlow(0)
    val lastGameScore: StateFlow<Int> = _lastGameScore




    // Initialization block
    init {
        //launch everything in the background (IO thread)
        viewModelScope.launch(Dispatchers.IO) {
            //load country data - used for hints
            CountryRepository.loadCountries(MyApp.context) {
                _countriesLoaded.value = true
            }
            preloadQuestionsIfNeeded() // insert starter questions if DB empty
            _questions.value =
                db.questionDao().getAllQuestions().shuffled() // shuffle for random order

            //load or create user profile
            val userDao = db.userDao()
            var user = userDao.getUser()
            if (user == null) {
                // Create a default user if none exists
                user = UserProfile(
                    username = "Player",
                    score = 0,
                    bestScore = 0,
                    gamesPlayed = 0
                )
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

        if (isCorrect) {
            // Calculate score based on hints used
            val pointsEarned = (4 - _tierShown.value).coerceAtLeast(1)
            val newScore = user.score + pointsEarned
            val newBest = maxOf(user.bestScore, newScore)

            // Update user in DB and StateFlow
            viewModelScope.launch {
                val updatedUser = user.copy(score = newScore, bestScore = newBest)
                db.userDao().insertUser(updatedUser)
                _user.value = updatedUser
            }
        }

        return isCorrect
    }

    //FUNCTIONS FOR COUNTRY NAMES
    private val aliasMap = mapOf(
        // canonical -> aliases
        "united states" to listOf("usa","us","u.s.","u.s.a.","america","united states of america"),
        "united kingdom" to listOf("uk","u.k.","great britain","britain","gb"),
        "south korea" to listOf("korea, republic of","republic of korea","korea (republic of)"),
        "czechia" to listOf("czech republic"),
        "ivory coast" to listOf("côte d’ivoire","cote d’ivoire"),
        //adding more when encountered, or found needed
    )

    private fun key(s: String) = s
        .lowercase()
        .trim()
        .replace(Regex("[^a-z\\s]"), "") // remove punctuation/accents simple pass
        .replace(Regex("\\s+"), " ")


    private fun normalizeToRepoName(raw: String): String {
        val k = key(raw)
        // direct alias hit
        aliasMap.forEach { (canon, aliases) ->
            if (key(canon) == k || aliases.any { key(it) == k }) return canon
        }
        // default: simple capitalization (handles "france" -> "France" )
        return k
    }


    // Move to the next question, or finish the game if last question
    fun moveToNextQuestion() {
        val nextIndex = _currentIndex.value + 1
        if (nextIndex < _questions.value.size) {
            _currentIndex.value = nextIndex
        } else {
            _user.value?.let { currentUser ->
                _lastGameScore.value = currentUser.score
                val updatedUser = currentUser.copy(
                    gamesPlayed = currentUser.gamesPlayed + 1
                )
                viewModelScope.launch {
                    db.userDao().insertUser(updatedUser)
                    _user.value = updatedUser
                }
            }
            _isGameFinished.value = true // signal that game has ended
        }
    }

    // Reset game state: start over
    fun resetGame(resetUserScore: Boolean = true) {
        _currentIndex.value = 0
        _isGameFinished.value = false
        _tierShown.value = 0

        viewModelScope.launch {
            if (resetUserScore) {
                _user.value?.let { user ->
                    val resetUser = user.copy(score = 0)
                    db.userDao().insertUser(resetUser)
                    _user.value = resetUser
                }
            }
            _questions.value = db.questionDao().getAllQuestions().shuffled()
            resetHints()
            prepareHintsForCurrent()
        }
    }

    //HINTS LOGIC:

    // finding the current question's country (by name) and builds the hint list
    // resets hints to 0
    fun prepareHintsForCurrent() {
        val raw = getCurrentQuestion()?.correctAnswer ?: return
        val norm = normalizeToRepoName(raw)
        val country =
            CountryRepository.getCountryByName(norm)   // implement this to compare on lowercase
                ?: return
        _hints.value = buildHints(country)
        _tierShown.value = 0
    }

    // incremeanting revelead hints +1
    fun resetHints() {
        _tierShown.value = 0
    }

    //showing the next level
    fun revealNextTierAndPenalize() {
        if (_tierShown.value >= 3) return
        android.util.Log.d("HintDebug", "Initial _tierShown: ${_tierShown.value}")
        _tierShown.value = _tierShown.value + 1
        android.util.Log.d("Hints", "tierShown=${_tierShown.value}")
    }

    fun visibleHintsForTier(): List<Hint> = when (_tierShown.value) {
        0 -> emptyList()
        1 -> _hints.value.filter { it.tier == HintTier.HARD }
        2 -> _hints.value.filter { it.tier == HintTier.HARD || it.tier == HintTier.MEDIUM }
        else -> _hints.value

    }


    //FORMATTING HELPERS

    private fun formatKm2(value: Double): String{
        return "%.0f km²".format(value)
    }

    private fun classifyArea(km2: Double): String = when {
        km2 > 2_000_000 -> "very large"
        km2 > 500_000   -> "large"
        km2 > 100_000   -> "medium-sized"
        km2 > 10_000    -> "small"
        else            -> "one of the smallest"
    }

    private fun capitalPrefix(capital: String, letters: Int = 2): String = capital.take(letters).uppercase()


    //TIERING HINTS: HARD, MEDIUM, EASY


    private fun buildHints(country: Country): List<Hint> {
        android.util.Log.d("HintBuild", "--- Building hints for: ${country.name.common} ---")
        //HARD level - least revealing
        val hard = buildList {
            country.area?.let {
                add(Hint("Area: ${formatKm2(it)}, (${classifyArea(it)})", HintTier.HARD))
            }
            country.landlocked?.let { add(Hint("Landlocked: $it", HintTier.HARD)) }
        }
        android.util.Log.d("HintBuild", "Generated ${hard.size} HARD hints: ${hard.map { it.text }}")
        //added logs to each of them, because I wanted to know if I'm still getting the right data
        //MEDIUM LEVEL - medium revealing not too easy not too hard
        val medium = buildList {
            country.region?.let { add(Hint("Region: $it", HintTier.MEDIUM))}
            //commented languages out, because a lot of countries have just the common language

           // country.languages?.let { languages ->
             //   val languageList = languages.values.joinToString(", ")
               // add(Hint("Languages: $languageList", HintTier.MEDIUM))
            //}
            country.borders?.let { borders ->
                val borderList = borders.joinToString(", ")
                add(Hint("Borders: $borderList", HintTier.MEDIUM))
            }
            //this clue might be too easy!!!! NB might remove it later
            country.currencies?.let { currencies ->
                val currencyList = currencies.values.joinToString(", ") { it.name ?: "" }
                add(Hint("Currencies: $currencyList", HintTier.MEDIUM))
            }
        }
        android.util.Log.d("HintBuild", "Generated ${medium.size} MEDIUM hints: ${medium.map { it.text }}")

        // EASY LEVEL - very revealing
        val easy = buildList {
            country.subregion?.let { add(Hint("Subregion: $it", HintTier.EASY)) }
            country.capital?.firstOrNull().let { cap ->
                add(Hint("Capital starts with '${capitalPrefix(cap ?: "")}'", HintTier.EASY))}
        }
        android.util.Log.d("HintBuild", "Generated ${easy.size} EASY hints: ${easy.map { it.text }}")

         val allHints = hard + medium + easy
        android.util.Log.i("HintBuild", "Total hints generated: ${allHints.size}. Tiers: ${allHints.map { it.tier }}")

        // Order: HARD → MEDIUM → EASY
        return allHints
    }


}
