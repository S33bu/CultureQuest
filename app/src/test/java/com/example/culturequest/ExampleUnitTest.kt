package com.example.culturequest

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.culturequest.ui.viewmodel.GameViewModel
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(application = MyApp::class, sdk = [28])
class ExampleUnitTest {

    private lateinit var viewModel: GameViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        val context = ApplicationProvider.getApplicationContext<Context>()
        // 2. Initialize Firebase manually for this test run
        FirebaseApp.initializeApp(context)

        viewModel = GameViewModel()
    }

    @After
    fun tearDown() {
        viewModel.closeDataBase()
        Dispatchers.resetMain()
    }

    @Test
    fun submitAnswer_withCorrectAndIncorrectAnswers() = runTest {
        // wait for the ViewModel to finish loading its questions
        viewModel.questions.first { it.isNotEmpty() }

        // get the first question
        val question = viewModel.questions.value.first()
        println("Question: $question")

        val answer = question.correctAnswer

        println("Question again: $question")
        // test with a correct answer
        val isCorrect = viewModel.submitAnswerForTest(answer)
        println(answer)
        println("isCorrect: $isCorrect")
        assertTrue("The answer should be correct", isCorrect)


        // test with an incorrect answer
        val isIncorrect = viewModel.submitAnswer("a wrong answer")
        assertFalse("The answer should be incorrect", isIncorrect)
    }
}
