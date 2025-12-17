package com.example.culturequest.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturequest.MyApp
import com.example.culturequest.data.Country
import com.example.culturequest.data.CountryRepository
import com.example.culturequest.data.GameRepository
import com.example.culturequest.data.Hint
import com.example.culturequest.data.HintTier
import com.example.culturequest.data.QuizQuestion
import com.example.culturequest.data.RandomLocationProvider
import com.example.culturequest.data.UserProfile
import com.example.culturequest.data.UserRepository
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel to manage the game state and logic.
 *
 * It handles the lifecycle of a game session, including question preloading,
 * hint generation, score calculation, and cloud synchronization.
 */
class GameViewModel : ViewModel() {
    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation.asStateFlow()

    // Firestore instance for syncing user stats to the cloud
    private val firestore = FirebaseFirestore.getInstance()
    private val userRepository = UserRepository()
    private val gameRepository = GameRepository()

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

    // tracks whether countries have been loaded
    private val _countriesLoaded = MutableStateFlow(false)
    val countriesLoaded: StateFlow<Boolean> = _countriesLoaded

    // holds generated hints for the current country
    private val _hints = MutableStateFlow<List<Hint>>(emptyList())

    // 0 = none, 1 = hard, 2 = medium, 3 = easy (all)
    private val _tierShown = MutableStateFlow(0)
    val tierShown: StateFlow<Int> = _tierShown

    private val _lastGameScore = MutableStateFlow(0)
    val lastGameScore: StateFlow<Int> = _lastGameScore

    // Initialization block
    init {
        viewModelScope.launch(Dispatchers.IO) {
            CountryRepository.loadCountries(MyApp.context) {
                // this runs only AFTER countries are loaded
                _countriesLoaded.value = true

                // now that countries are loaded, we can safely prepare questions and hints
                viewModelScope.launch(Dispatchers.IO) {
                    preloadQuestionsIfNeeded()
                    val allQuestions = gameRepository.getAllQuestions()
                    _questions.value = allQuestions.shuffled()

                    prepareHintsForCurrent()

                    // Load user from Firestore (if we can), otherwise load from local Room
                    val loadedUser = loadUserForCurrentAccountFromServerOrLocal()
                    _user.value = loadedUser

                    // if Firebase user exists, sync to Firestore (merge)
                    if (FirebaseAuth.getInstance().currentUser != null) {
                        syncUserToFirestore(loadedUser)
                    }
                }
            }
        }
    }

    /**
     * Preloads a default set of questions into the local Room database, but only if the database is currently empty.
     * This ensures the app has content on the first run without overwriting existing data.
     */
    private suspend fun preloadQuestionsIfNeeded() {
        val existing = gameRepository.getAllQuestions()
        if (existing.isEmpty()) {
            val starterQuestions =
                listOf(
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "australia"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "china"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "egypt"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "estonia"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "france"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "india"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "indonesia"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "italy"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "uk"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "usa"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "russia"),
                    // Expanded List - Americas
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "canada"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "brazil"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "mexico"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "argentina"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "peru"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "chile"),
                    // Expanded List - Europe
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "germany"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "spain"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "greece"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "netherlands"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "switzerland"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "sweden"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "norway"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "ireland"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "portugal"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "finland"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "latvia"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "lithuania"),
                    // Expanded List - Asia
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "japan"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "south korea"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "thailand"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "vietnam"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "turkey"), // Transcontinental
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "saudi arabia"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "united arab emirates"),
                    // Expanded List - Africa
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "south africa"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "nigeria"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "kenya"),
                    QuizQuestion(questionText = "Which country is shown?", correctAnswer = "morocco"),
                )
            gameRepository.refreshQuestions(starterQuestions)
        }
    }

    /**
     * Retrieves the current question from the list based on the current index.
     *
     * @return The [QuizQuestion] object for the current round, or null if the index is out of bounds.
     */
    fun getCurrentQuestion(): QuizQuestion? = _questions.value.getOrNull(_currentIndex.value)

    /**
     * Validates the user's submitted answer, calculates points, and updates the user's score if correct.
     *
     * The points awarded are based on the number of hint tiers revealed (4 points for no hints, 3 for HARD, etc.).
     * On a correct answer, the user's score and bestScore are updated locally and then synced to Firestore.
     *
     * @param answer The answer string submitted by the user.
     * @return `true` if the answer is correct (case-insensitive), `false` otherwise.
     */
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
                userRepository.saveLocalProgress(updatedUser)
                _user.value = updatedUser

                // sync updated stats to Firestore
                syncUserToFirestore(updatedUser)
            }
        }

        return isCorrect
    }

    /**
     * A simplified version of [submitAnswer] for testing purposes.
     *
     * It checks if the provided answer is correct (case-insensitive) but does not involve user state
     * or point calculation, making it suitable for isolated unit tests.
     *
     * @param answer The answer string to be tested.
     * @return `true` if the answer is correct, `false` otherwise.
     */
    fun submitAnswerForTest(answer: String): Boolean { // EXACT SAME AS THE CORE ONE JUST WITHOUT THE POINT ADDING
        val current = getCurrentQuestion() ?: return false
        // REMOVED USER AS WE DONT NEED AN USER RIGHT NOW FOR TESTING

        // Check if the answer is correct (case-insensitive)
        val isCorrect = answer.equals(current.correctAnswer, ignoreCase = true)

        return isCorrect
    }

    // FUNCTIONS FOR COUNTRY NAMES
    private val aliasMap =
        mapOf(
            // canonical -> aliases
            "united states" to listOf("usa", "us", "u.s.", "u.s.a.", "america", "united states of america"),
            "united kingdom" to listOf("uk", "u.k.", "great britain", "britain", "gb"),
            "south korea" to listOf("korea, republic of", "republic of korea", "korea (republic of)"),
            "czechia" to listOf("czech republic"),
            "ivory coast" to listOf("côte d’ivoire", "cote d’ivoire"),
            // adding more when encountered, or found needed
        )

    private fun key(s: String) =
        s
            .lowercase()
            .trim()
            .replace(Regex("[^a-z\\s]"), "") // remove punctuation/accents simple pass
            .replace(Regex("\\s+"), " ")

    /** Normalizes a country name from the question data to match the name used in the CountryRepository.
     * This is necessary to handle minor inconsistencies between data sources (e.g., "United States" vs. "USA").
     *
     * @param raw The raw country name from a [QuizQuestion].
     * @return The normalized country name recognized by the repository.
     */
    private fun normalizeToRepoName(raw: String): String {
        val k = key(raw)
        // direct alias hit
        aliasMap.forEach { (canon, aliases) ->
            if (key(canon) == k || aliases.any { key(it) == k }) return canon
        }
        // default: simple capitalization (handles "france" -> "France" )
        return k
    }

    /**
     * Advances the game to the next question.
     *
     * If there are more questions, it increments the current index, resets and prepares new hints,
     * and fetches a new location. If it's the last question, it finalizes the user's score,
     * updates their stats (games played), syncs to Firestore, and sets the game state to finished.
     */
    fun moveToNextQuestion() {
        // stop the api calls after game ends
        if (_isGameFinished.value) {
            Log.d("GameViewModel", "Game is already finished!")
            return
        }
        val nextIndex = _currentIndex.value + 1
        if (nextIndex < _questions.value.size) {
            _currentIndex.value = nextIndex
            // first, reset and prepare hints for the new question
            resetHints()
            prepareHintsForCurrent()
            // fetch the new location
            if (nextIndex != 0) {
                fetchRandomLocationForCurrentQuestion()
            }
        } else {
            _user.value?.let { currentUser ->
                _lastGameScore.value = currentUser.score
                val updatedUser =
                    currentUser.copy(
                        gamesPlayed = currentUser.gamesPlayed + 1,
                    )
                viewModelScope.launch {
                    userRepository.saveLocalProgress(updatedUser)
                    _user.value = updatedUser

                    // sync updated stats to Firestore
                    syncUserToFirestore(updatedUser)
                }
            }
            _isGameFinished.value = true // signal that game has ended
        }
    }

    /**
     * Resets the entire game to its initial state.
     *
     * Sets the question index and hint tier back to zero and marks the game as not finished.
     * It shuffles the question list, fetches a new location, and prepares the hints for the first question.
     *
     * @param resetUserScore If true, the current user's score is reset to 0 both locally and in Firestore.
     */
    fun resetGame(resetUserScore: Boolean = true) {
        _currentIndex.value = 0
        _isGameFinished.value = false
        _tierShown.value = 0

        viewModelScope.launch {
            if (resetUserScore) {
                _user.value?.let { user ->
                    val resetUser = user.copy(score = 0)
                    userRepository.saveLocalProgress(resetUser)
                    _user.value = resetUser

                    // sync reset stats to Firestore
                    syncUserToFirestore(resetUser)
                }
            }
            _questions.value = gameRepository.getAllQuestions().shuffled()
            fetchRandomLocationForCurrentQuestion()
            resetHints()
            prepareHintsForCurrent()
        }
    }

    // HINTS LOGIC:

    /**
     * Prepares the list of hints for the current question.
     *
     * It retrieves the current question's correct answer, finds the corresponding [Country] object,
     * and uses [buildHints] to generate a fresh list of hints. It also resets the revealed hint tier to 0.
     */
    fun prepareHintsForCurrent() {
        val raw = getCurrentQuestion()?.correctAnswer ?: return
        val norm = normalizeToRepoName(raw)
        val country =
            CountryRepository.getCountryByName(norm) // implement this to compare on lowercase
                ?: return
        _hints.value = buildHints(country)
        _tierShown.value = 0
    }

    /**
     * Resets the count of revealed hint tiers to 0.
     * This is typically called when moving to a new question.
     */
    fun resetHints() {
        _tierShown.value = 0
    }

    /**
     * Reveals the next tier of hints (HARD -> MEDIUM -> EASY).
     *
     * Increments the tier counter, making more hints visible to the user.
     */
    fun revealNextTierAndPenalize() {
        if (_tierShown.value >= 3) return
        Log.d("HintDebug", "Initial _tierShown: ${_tierShown.value}")
        _tierShown.value = _tierShown.value + 1
        Log.d("Hints", "tierShown=${_tierShown.value}")
    }

    /**
     * Returns a list of hints that should be visible based on the currently revealed tier.
     *
     * - Tier 0: No hints are visible.
     * - Tier 1: Only HARD hints are visible.
     * - Tier 2: HARD and MEDIUM hints are visible.
     * - Tier 3 (or more): All hints are visible.
     *
     * @return A list of [Hint] objects visible for the current tier.
     */
    fun visibleHintsForTier(): List<Hint> =
        when (_tierShown.value) {
            0 -> emptyList()
            1 -> _hints.value.filter { it.tier == HintTier.HARD }
            2 -> _hints.value.filter { it.tier == HintTier.HARD || it.tier == HintTier.MEDIUM }
            else -> _hints.value
        }

    // FORMATTING HELPERS

    /**
     * Formats a raw area value (in square kilometers) into a human-readable string.
     *
     * @param value The area in km².
     * @return A formatted string, e.g., "1,234,567 km²".
     */
    private fun formatKm2(value: Double): String = "%.0f km²".format(value)

    /**
     * Classifies a country's area into a descriptive category.
     *
     * @param km2 The area in square kilometers.
     * @return A descriptive string like "very large" or "small".
     */
    private fun classifyArea(km2: Double): String =
        when {
            km2 > 2_000_000 -> "very large"
            km2 > 500_000 -> "large"
            km2 > 100_000 -> "medium-sized"
            km2 > 10_000 -> "small"
            else -> "one of the smallest"
        }

    /**
     * Generates an uppercase prefix from a capital city's name.
     *
     * @param capital The name of the capital city.
     * @param letters The number of letters to include in the prefix.
     * @return The first N letters of the capital, in uppercase.
     */
    private fun capitalPrefix(
        capital: String,
        letters: Int = 2,
    ): String = capital.take(letters).uppercase()

    /**
     * Builds and tiers a list of hints for a given country.
     *
     * Gathers various facts about the country (area, region, borders, etc.) and categorizes them
     * into HARD, MEDIUM, and EASY tiers based on how revealing they are.
     *
     * @param country The [Country] object to generate hints for.
     * @return A consolidated list of all generated [Hint] objects.
     */
    private fun buildHints(country: Country): List<Hint> {
        Log.d("HintBuild", "--- Building hints for: ${country.name.common} ---")
        // HARD level - least revealing
        val hard =
            buildList {
                country.area?.let {
                    add(Hint("Area: ${formatKm2(it)}, (${classifyArea(it)})", HintTier.HARD))
                }
                country.landlocked?.let { add(Hint("Landlocked: $it", HintTier.HARD)) }
            }
        Log.d("HintBuild", "Generated ${hard.size} HARD hints: ${hard.map { it.text }}")
        // added logs to each of them, because I wanted to know if I'm still getting the right data
        // MEDIUM LEVEL - medium revealing not too easy not too hard
        val medium =
            buildList {
                country.region?.let { add(Hint("Region: $it", HintTier.MEDIUM)) }
                // commented languages out, because a lot of countries have just the common language

                // country.languages?.let { languages ->
                //   val languageList = languages.values.joinToString(", ")
                // add(Hint("Languages: $languageList", HintTier.MEDIUM))
                // }
                country.borders?.let { borders ->
                    val borderList = borders.joinToString(", ")
                    add(Hint("Borders: $borderList", HintTier.MEDIUM))
                }
                // this clue might be too easy!!!! NB might remove it later
                country.currencies?.let { currencies ->
                    val currencyList = currencies.values.joinToString(", ") { it.name ?: "" }
                    add(Hint("Currencies: $currencyList", HintTier.MEDIUM))
                }
            }
        Log.d("HintBuild", "Generated ${medium.size} MEDIUM hints: ${medium.map { it.text }}")

        // EASY LEVEL - very revealing
        val easy =
            buildList {
                country.subregion?.let { add(Hint("Subregion: $it", HintTier.EASY)) }
                country.capital?.firstOrNull().let { cap ->
                    add(Hint("Capital starts with '${capitalPrefix(cap ?: "", 2)}'", HintTier.EASY))
                }
            }
        Log.d("HintBuild", "Generated ${easy.size} EASY hints: ${easy.map { it.text }}")

        val allHints = hard + medium + easy
        Log.i("HintBuild", "Total hints generated: ${allHints.size}. Tiers: ${allHints.map { it.tier }}")

        // Order: HARD → MEDIUM → EASY
        return allHints
    }

    /**
     * Syncs the local UserProfile to Firestore under:
     *  collection: "users"
     *  document:  current Firebase uid
     *
     * Stored fields:
     *  - uid
     *  - email
     *  - displayName
     *  - bestScore
     *  - gamesPlayed
     *  - score (current run)
     *  @param user The [UserProfile] object containing the data to be synced.
     *
     */
    private fun syncUserToFirestore(user: UserProfile) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser ?: return
        val uid = firebaseUser.uid
        val email = firebaseUser.email

        // Derive a display name from email if possible, fall back to local username.
        val displayName =
            email
                ?.substringBefore('.')
                ?.substringBefore('@')
                ?.replaceFirstChar { ch ->
                    if (ch.isLowerCase()) ch.titlecase() else ch.toString()
                }
                ?: user.username

        val data =
            mapOf(
                "uid" to uid,
                "email" to email,
                "displayName" to displayName,
                "bestScore" to user.bestScore,
                "gamesPlayed" to user.gamesPlayed,
                "score" to user.score,
            )

        firestore
            .collection("users")
            .document(uid)
            .set(data, SetOptions.merge())
            .addOnFailureListener { e ->
                Log.e("GameViewModel", "Failed to sync user to Firestore", e)
            }
    }

    /**
     * Loads active Firebase user profile:
     *  - if Farebase user is logged in, tries to load from Firestore
     *  - updates / creates a local Room DB entry
     *  - if nothing in Firestore, loads from local Room DB
     *
     * @return user profile
     */
    private suspend fun loadUserForCurrentAccountFromServerOrLocal(): UserProfile {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val uid = firebaseUser?.uid ?: "local_guest"

        // Kui Firebase kasutaja olemas, proovime Firestore'ist lugeda
        if (firebaseUser != null) {
            try {
                val snapshot =
                    firestore
                        .collection("users")
                        .document(uid)
                        .get()
                        .await()

                if (snapshot.exists()) {
                    val bestScore = snapshot.getLong("bestScore")?.toInt() ?: 0
                    val gamesPlayed = snapshot.getLong("gamesPlayed")?.toInt() ?: 0
                    val score = snapshot.getLong("score")?.toInt() ?: 0
                    val remoteName = snapshot.getString("displayName")

                    val localExisting = userRepository.getUser(uid)
                    val username =
                        remoteName
                            ?: localExisting?.username
                            ?: firebaseUser.email
                                ?.substringBefore('.')
                                ?.substringBefore('@')
                                ?.replaceFirstChar { ch ->
                                    if (ch.isLowerCase()) ch.titlecase() else ch.toString()
                                }
                            ?: "Player"

                    val finalScore = if (localExisting != null) localExisting.score else score

                    val user =
                        UserProfile(
                            id = localExisting?.id ?: 0,
                            uid = uid,
                            username = username,
                            score = finalScore,
                            bestScore = bestScore,
                            gamesPlayed = gamesPlayed,
                        )

                    userRepository.saveLocalProgress(user)
                    return user
                }
            } catch (e: Exception) {
                Log.e("GameViewModel", "Failed to load user from Firestore, falling back to local", e)
            }
        }

        // Kui Firestore'ist ei saanud või pole login'itud, kasutame ainult Roomi
        var user = userRepository.getUser(uid)
        if (user == null) {
            val defaultName =
                firebaseUser
                    ?.email
                    ?.substringBefore('.')
                    ?.substringBefore('@')
                    ?.replaceFirstChar { ch ->
                        if (ch.isLowerCase()) ch.titlecase() else ch.toString()
                    }
                    ?: "Player"

            user =
                UserProfile(
                    uid = uid,
                    username = defaultName,
                    score = 0,
                    bestScore = 0,
                    gamesPlayed = 0,
                )
            userRepository.saveLocalProgress(user)
        }
        return user
    }

    /**
     * Reloads the local user profile from the data sources.
     *
     * This is intended to be called after a significant authentication event, such as login or logout,
     * to ensure the ViewModel's user state is synchronized with the current auth state.
     */
    fun reloadUserForCurrentAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = loadUserForCurrentAccountFromServerOrLocal()
            _user.value = user
        }
    }

    /**
     * Fetches a random Street View location for the country of the current question.
     *
     * Sets the location state to null to indicate loading, then calls the [RandomLocationProvider]
     * to get a valid coordinate. Updates the state with the new location or logs an error on failure.
     */
    private fun fetchRandomLocationForCurrentQuestion() {
        viewModelScope.launch {
            _currentLocation.value = null // set to loading state
            val question = questions.value.getOrNull(currentIndex.value)
            val country = question?.correctAnswer

            if (country == null) {
                Log.e("ViewModel", "Cannot fetch location because country is null!")
                _currentLocation.value = LatLng(0.00, 0.00) // just a fallback just in case
                return@launch
            }

            try {
                // buchn of debugging stuff with logs
                Log.d("ViewModel", "Asking RandomLocationProvider for a location in '$country'...")
                val normalizedCountry = normalizeToRepoName(country) // normalize before fetching, this fixed big error
                val location = RandomLocationProvider.getRandomLocationForCountry(normalizedCountry)

                if (location != null) {
                    Log.d("GameViewModel", "Updating StreetView location: $location")
                    _currentLocation.value = LatLng(location.latitude, location.longitude)
                } else {
                    Log.e("ViewModel", "FAILURE: RandomLocationProvider returned null.")
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "EXCEPTION while fetching location: ${e.message}", e)
            }
        }
    }
}
