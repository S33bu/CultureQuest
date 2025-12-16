package com.example.culturequest.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturequest.MyApp
import com.example.culturequest.data.Country
import com.example.culturequest.data.CountryRepository
import com.example.culturequest.data.Hint
import com.example.culturequest.data.HintTier
import com.example.culturequest.data.QuizQuestion
import com.example.culturequest.data.RandomLocationProvider
import com.example.culturequest.data.UserProfile
import com.example.culturequest.data.UserRepository
import com.example.culturequest.data.GameRepository
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


// ViewModel to manage the game state
class GameViewModel : ViewModel() {

    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation.asStateFlow()

    // Firestore instance for syncing user stats to the cloud
    private val firestore = FirebaseFirestore.getInstance()

    private val db = MyApp.instance.database
    private val userRepository = UserRepository(db.userDao())
    private val gameRepository = GameRepository(db.questionDao())

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
        viewModelScope.launch(Dispatchers.IO) {
            CountryRepository.loadCountries(MyApp.context) {
                // this runs only AFTER countries are loaded
                _countriesLoaded.value = true

                // now that countries are loaded, we can safely prepare questions and hints
                viewModelScope.launch(Dispatchers.IO) {
                    preloadQuestionsIfNeeded()
                    val allQuestions = db.questionDao().getAllQuestions()
                    _questions.value = allQuestions.shuffled()

                    prepareHintsForCurrent()

                    // Laeme kasutaja kõigepealt Firestore'ist (kui olemas), siis Roomist
                    val loadedUser = loadUserForCurrentAccountFromServerOrLocal()
                    _user.value = loadedUser

                    // kui Firebase kasutaja on olemas, peegeldame lokaalse seisu Firestore'i (merge)
                    if (FirebaseAuth.getInstance().currentUser != null) {
                        syncUserToFirestore(loadedUser)
                    }
                }
            }
        }
    }

    // Preload default questions only if DB is empty
    private suspend fun preloadQuestionsIfNeeded() {
        val existing = gameRepository.getAllQuestions()
        if (existing.isEmpty()) {
            val starterQuestions = listOf(
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
                QuizQuestion(questionText = "Which country is shown?", correctAnswer = "morocco")

            )
            gameRepository.refreshQuestions(starterQuestions)
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
                userRepository.saveLocalProgress(updatedUser)
                _user.value = updatedUser

                // sync updated stats to Firestore
                syncUserToFirestore(updatedUser)
            }
        }

        return isCorrect
    }

    fun submitAnswerForTest(answer: String): Boolean { //EXACT SAME AS THE CORE ONE JUST WITHOUT THE POINT ADDING
        val current = getCurrentQuestion() ?: return false
        //REMOVED USER AS WE DONT NEED AN USER RIGHT NOW FOR TESTING

        // Check if the answer is correct (case-insensitive)
        val isCorrect = answer.equals(current.correctAnswer, ignoreCase = true)


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
        //stop the api calls after game ends
        if (_isGameFinished.value) {
            android.util.Log.d("GameViewModel", "Game is already finished!")
            return
        }
        val nextIndex = _currentIndex.value + 1
        if (nextIndex < _questions.value.size) {
            _currentIndex.value = nextIndex
            // first, reset and prepare hints for the new question
            resetHints()
            prepareHintsForCurrent()
            // fetch the new location
            if(nextIndex != 0){
                fetchRandomLocationForCurrentQuestion()
            }
        } else {
            _user.value?.let { currentUser ->
                _lastGameScore.value = currentUser.score
                val updatedUser = currentUser.copy(
                    gamesPlayed = currentUser.gamesPlayed + 1
                )
                viewModelScope.launch {
                    db.userDao().insertUser(updatedUser)
                    _user.value = updatedUser

                    // sync updated stats to Firestore
                    syncUserToFirestore(updatedUser)
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

                    // sync reset stats to Firestore
                    syncUserToFirestore(resetUser)
                }
            }
            _questions.value = db.questionDao().getAllQuestions().shuffled()
            fetchRandomLocationForCurrentQuestion()
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

    private fun capitalPrefix(capital: String, letters: Int): String =
        capital.take(letters).uppercase()


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
                add(Hint("Capital starts with '${capitalPrefix(cap ?: "", 2)}'", HintTier.EASY))}
        }
        android.util.Log.d("HintBuild", "Generated ${easy.size} EASY hints: ${easy.map { it.text }}")

        val allHints = hard + medium + easy
        android.util.Log.i("HintBuild", "Total hints generated: ${allHints.size}. Tiers: ${allHints.map { it.tier }}")

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
     */
    private fun syncUserToFirestore(user: UserProfile) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser ?: return
        val uid = firebaseUser.uid
        val email = firebaseUser.email

        // Derive a display name from email if possible, fall back to local username.
        val displayName = email
            ?.substringBefore('.')
            ?.substringBefore('@')
            ?.replaceFirstChar { ch ->
                if (ch.isLowerCase()) ch.titlecase() else ch.toString()
            }
            ?: user.username

        val data = mapOf(
            "uid" to uid,
            "email" to email,
            "displayName" to displayName,
            "bestScore" to user.bestScore,
            "gamesPlayed" to user.gamesPlayed,
            "score" to user.score
        )

        firestore.collection("users")
            .document(uid)
            .set(data, SetOptions.merge())
            .addOnFailureListener { e ->
                Log.e("GameViewModel", "Failed to sync user to Firestore", e)
            }
    }

    /**
     * Laeb aktiivse Firebase kasutaja profiili:
     *  - kui Firebase user on olemas, proovib kõigepealt Firestore'ist lugeda
     *  - uuendab / loob vastava rea lokaalses Room DB-s
     *  - kui Firestore'is veel midagi pole, kasutab ainult Roomi / loob default profiili
     */
    private suspend fun loadUserForCurrentAccountFromServerOrLocal(): UserProfile {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val uid = firebaseUser?.uid ?: "local_guest"

        // Kui Firebase kasutaja olemas, proovime Firestore'ist lugeda
        if (firebaseUser != null) {
            try {
                val snapshot = firestore
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
                    val username = remoteName
                        ?: localExisting?.username
                        ?: firebaseUser.email
                            ?.substringBefore('.')
                            ?.substringBefore('@')
                            ?.replaceFirstChar { ch ->
                                if (ch.isLowerCase()) ch.titlecase() else ch.toString()
                            }
                        ?: "Player"

                    val finalScore = if (localExisting != null) localExisting.score else score

                    val user = UserProfile(
                        id = localExisting?.id ?: 0,
                        uid = uid,
                        username = username,
                        score = finalScore,
                        bestScore = bestScore,
                        gamesPlayed = gamesPlayed
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
            val defaultName = firebaseUser?.email
                ?.substringBefore('.')
                ?.substringBefore('@')
                ?.replaceFirstChar { ch ->
                    if (ch.isLowerCase()) ch.titlecase() else ch.toString()
                }
                ?: "Player"

            user = UserProfile(
                uid = uid,
                username = defaultName,
                score = 0,
                bestScore = 0,
                gamesPlayed = 0
            )
            userRepository.saveLocalProgress(user)
        }
        return user
    }

    /**
     * Reload the local user profile based on the *current* Firebase user.
     * Kasutatakse pärast login/logout'i.
     */
    fun reloadUserForCurrentAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = loadUserForCurrentAccountFromServerOrLocal()
            _user.value = user
        }
    }

    private fun fetchRandomLocationForCurrentQuestion() {
        viewModelScope.launch {
            _currentLocation.value = null // set to loading state
            val question = questions.value.getOrNull(currentIndex.value)
            val country = question?.correctAnswer

            if (country == null) {
                Log.e("ViewModel", "Cannot fetch location because country is null!")
                _currentLocation.value = LatLng(0.00,0.00) //just a fallback just in case
                return@launch
            }

            try {
                //buchn of debugging stuff with logs
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
