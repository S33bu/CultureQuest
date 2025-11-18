package com.example.culturequest.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.culturequest.R
import com.google.android.gms.maps.StreetViewPanoramaView
import com.example.culturequest.data.HintTier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.android.gms.maps.model.LatLng
import androidx.compose.ui.platform.LocalContext
import com.example.culturequest.ui.viewmodel.GameViewModel

// Composable function to display a Google Street View panorama.
@Composable
fun StreetViewPanoramaComposable(
    location: LatLng,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // remember a new instance ONLY when this composable enters composition
    val streetView = remember {
        StreetViewPanoramaView(context).apply { onCreate(null) }
    }

    // Use DisposableEffect to manage the lifecycle of the StreetViewPanoramaView.
    // This ensures that the Street View's lifecycle methods are called correctly
    // in sync with the composable's lifecycle.
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> streetView.onStart()
                Lifecycle.Event.ON_RESUME -> streetView.onResume()
                Lifecycle.Event.ON_PAUSE -> streetView.onPause()
                Lifecycle.Event.ON_STOP -> streetView.onStop()
                Lifecycle.Event.ON_DESTROY -> streetView.onDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // apply new location when it changes
    // Use LaunchedEffect to update the Street View panorama when the location changes.
    // It's an async call, and this coroutine scope is tied to the composable's lifecycle.
    LaunchedEffect(location) {
        streetView.getStreetViewPanoramaAsync { panorama ->
            panorama.setPosition(location, 50000)
            panorama.isUserNavigationEnabled = false
            panorama.isStreetNamesEnabled = false
        }
    }

    // Integrate the Android View (StreetViewPanoramaView) into the Compose UI.
    AndroidView(
        factory = { streetView },
        modifier = modifier
    )
}

@Composable
// The main screen for the game page.
fun GamePageScreen(
    onBackClick: () -> Unit,
    onGameEnd: (lastScore: Int) -> Unit,
    viewModel: GameViewModel = viewModel()
) {
    val allQuestions by viewModel.questions.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    // observing the user state to update the score in the UI.
    val user by viewModel.user.collectAsState()
    val isGameFinished by viewModel.isGameFinished.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()

    val currentQuestion =
        if (allQuestions.isNotEmpty()) allQuestions.getOrNull(currentIndex) else null

    // State for the user's answer, dialog visibility, correctness of the answer, and error messages.
    var answer by remember { mutableStateOf(TextFieldValue("")) }
    var showDialog by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Coroutine scope for launching async operations.
    val coroutineScope = rememberCoroutineScope()

    //FOR HINTS
    var showHintsDialog by remember { mutableStateOf(false)}
    val countriesLoaded by viewModel.countriesLoaded.collectAsState()
    // Tracks which tier of hints is currently visible to the user.
    val tierShown by viewModel.tierShown.collectAsState()
    val visibleHints by remember(tierShown) {
        derivedStateOf { viewModel.visibleHintsForTier() }
    }

    var timeLeft by remember { mutableStateOf(60) }

    // Prepare hints for the current question as soon as it's loaded.
    LaunchedEffect(currentIndex, countriesLoaded) {
        if (countriesLoaded) viewModel.prepareHintsForCurrent()
    }

    // Navigate back when game finishes
    LaunchedEffect(isGameFinished) {
        if (isGameFinished) {
            onGameEnd(viewModel.lastGameScore.value)
            viewModel.resetGame()
        }
    }

    // Function to validate the user's input.
    fun validateAnswer(input: String): String? {
        val trimmed = input.trim()
        return when {
            trimmed.isEmpty() -> "Please enter something!"
            trimmed.length > 30 -> "Your answer is too long (max 30 characters)."
            trimmed.any { it.isDigit() } -> "The answer cannot contain numbers."
            trimmed.any { !it.isLetter() && it != ' ' && it != '-' } ->
                "Only letters, spaces, and hyphens are allowed."
            else -> null
        }
    }

    // Function to handle the submission of an answer.
    fun handleSubmit() {
        val validationError = validateAnswer(answer.text)
        if (validationError != null) {
            errorMessage = validationError
            return
        }

        isCorrect = viewModel.submitAnswer(answer.text)
        showDialog = true
    }


    // Start countdown when the question is loaded
    LaunchedEffect(Unit) {
        while (timeLeft > 0 && !isGameFinished) {
            delay(1000)
            // make it so the timer doesnt go down when pop ups are up or when the location is changing
            if (currentLocation != null && !showDialog){
                timeLeft -= 1
            }
        }
        if (!isGameFinished) {
            val finalScore = viewModel.user.value?.score ?: 0
            onGameEnd(finalScore)
            viewModel.resetGame(resetUserScore = false) // keep last game score intact
        }
    }

    // Dialog to show hints.
    if (showHintsDialog) {

        AlertDialog(
            onDismissRequest = { showHintsDialog = false },
            //different title headings based on tiers
            title = {
                Text(
                    text = when (tierShown) {
                        1 -> "Hints: Hard"
                        2 -> "Hints: Hard + Medium"
                        3 -> "Hints: Hard + Medium + Easy"
                        else -> "Hints"
                    },
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (visibleHints.isEmpty()) {
                        Text("No hints yet.")
                    } else {
                        // Display each hint
                        visibleHints.forEach { hint ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when (hint.tier) {
                                    HintTier.HARD -> Color(0x332196F3)
                                    HintTier.MEDIUM -> Color(0x334CAF50)
                                    HintTier.EASY -> Color(0x33FFC107)
                                }
                            ) {
                                Text(
                                    text = hint.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    //if the tier is below 3, then it is shown also show more hints
                    if (tierShown < 3) {
                        TextButton(onClick = { viewModel.revealNextTierAndPenalize() }) {
                            Text("Show more hints (−1)", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                    //always show close
                    TextButton(onClick = { showHintsDialog = false }) {
                        Text("Close", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        )
    }

    Scaffold(
        containerColor = Color.Black,
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            //Siin mässame street viewga
            if (currentLocation == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                StreetViewPanoramaComposable(
                    location = currentLocation!!,
                    modifier = Modifier.fillMaxSize()
                )

            }


            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp) // added some padding for better visibility
            ) {
                //for debugging, remove later :)
                Text(
                    text = "Country: ${currentQuestion?.correctAnswer?.replaceFirstChar { it.uppercase() } ?: "Finding..."}",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            // Top bar with timer, score, back button, and hint button.
            TopGameBar(timeLeft, user?.score ?: 0, onBackClick, {
                if (tierShown == 0) viewModel.revealNextTierAndPenalize()
                showHintsDialog = true
                if (tierShown <= 3) showHintsDialog = true
            }, tierShown <= 3)

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 90.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Input field and submit button at the bottom of the screen.
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(56.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedTextField(
                        value = answer,
                        onValueChange = { answer = it },
                        placeholder = {
                            Text(
                                "What country is it?",
                                color = Color.Black.copy(alpha = 0.6f),
                                fontSize = 18.sp
                            )
                        },
                        textStyle = LocalTextStyle.current.copy(fontSize = 20.sp),
                        singleLine = true,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White,
                            focusedContainerColor = Color(0xAA99FF99),
                            unfocusedContainerColor = Color(0xAA99FF99),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = Color.Black
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { handleSubmit() })
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { handleSubmit() },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text("Submit", color = Color.White)
                    }
                }
            }

            // Dialog for showing invalid input messages.
            errorMessage?.let { msg ->
                AlertDialog(
                    onDismissRequest = { errorMessage = null },
                    title = { Text("⚠️ Invalid Input") },
                    text = { Text(msg) },
                    confirmButton = {
                        TextButton(onClick = { errorMessage = null }) {
                            Text("OK")
                        }
                    }
                )
            }

            // Dialog to show whether the answer was correct or incorrect.
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showDialog = false
                        answer = TextFieldValue("")
                        coroutineScope.launch { viewModel.moveToNextQuestion() }
                    },
                    title = {
                        Text(
                            if (isCorrect) "✅ Correct!" else "❌ Incorrect! \n Correct answer: ${currentQuestion?.correctAnswer}",
                            textAlign = TextAlign.Center
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            showDialog = false
                            answer = TextFieldValue("")
                            coroutineScope.launch { viewModel.moveToNextQuestion() }
                        }) {
                            Text("Next")
                        }
                    }
                )
            }
        }
    }
}

// Composable for the top bar of the game screen.
@Composable
fun TopGameBar(
    timeLeft: Int,
    score: Int,
    onBackClick: () -> Unit,
    onHintClick: () -> Unit,
    isHintEnabled: Boolean
) {
    // Adapting UI to system theme (dark/light).
    val iconColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    val buttonBackgroundColor = Color(0xAA99FF99)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp, start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button.
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(56.dp)
                .background(buttonBackgroundColor, CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = iconColor
            )
        }

        // Display for time left and score.
        Text(
            text = "Time left: $timeLeft s | Score: $score",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        // Hint button.
        IconButton(
            onClick = onHintClick,
            enabled = isHintEnabled,
            modifier = Modifier
                .size(56.dp)
                .background(buttonBackgroundColor, CircleShape)
        ) {
            Image(
                painter = painterResource(id = R.drawable.hint_icon),
                contentDescription = "Hint",
                colorFilter = ColorFilter.tint(iconColor)
            )
        }
    }
}
