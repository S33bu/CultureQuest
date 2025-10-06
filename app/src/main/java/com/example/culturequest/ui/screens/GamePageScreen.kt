package com.example.culturequest.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.culturequest.ui.viewmodel.GameViewModel
import kotlinx.coroutines.launch

@Composable
fun GamePageScreen(
    onBackClick: () -> Unit,
    onGameEnd: () -> Unit, // Navigate back to HomeScreen
    viewModel: GameViewModel = viewModel()
) {
    val allQuestions by viewModel.questions.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val user by viewModel.user.collectAsState()
    val isGameFinished by viewModel.isGameFinished.collectAsState()

    val currentQuestion = if (allQuestions.isNotEmpty()) allQuestions.getOrNull(currentIndex) else null

    var answer by remember { mutableStateOf(TextFieldValue("")) }
    var showDialog by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    // Navigate back when game finishes
    LaunchedEffect(isGameFinished) {
        if (isGameFinished) {
            onGameEnd()
            viewModel.resetGame()
        }
    }

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

    fun handleSubmit() {
        val validationError = validateAnswer(answer.text)
        if (validationError != null) {
            errorMessage = validationError
            return
        }

        isCorrect = viewModel.submitAnswer(answer.text)
        showDialog = true
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
            // Display question image or loading
            if (currentQuestion != null) {
                Image(
                    painter = painterResource(id = currentQuestion.imageResId),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading...", color = Color.White)
                }
            }

            // Live Score at top center
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "Score: ${user?.score ?: 0}",
                    color = Color.White,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Answer input row at bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 90.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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

            // Back button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .size(50.dp)
            ) {
                Image(
                    painter = painterResource(id = com.example.culturequest.R.drawable.backbutton),
                    contentDescription = "Back",
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Error dialog
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

            // Feedback dialog
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showDialog = false
                        answer = TextFieldValue("")
                        coroutineScope.launch { viewModel.moveToNextQuestion() }
                    },
                    title = {
                        Text(
                            if (isCorrect) "✅ Correct!" else "❌ Incorrect!",
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
