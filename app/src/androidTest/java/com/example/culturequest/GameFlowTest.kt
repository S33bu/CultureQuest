package com.example.culturequest

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.culturequest.ui.theme.CultureQuestTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI test for the main game flow.
 * It simulates a user navigating from the home screen, playing a short game, and returning.
 */
@RunWith(AndroidJUnit4::class)
class GameFlowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testGameFlow() {
        // Set the content to our app's navigation starting point
        composeTestRule.setContent {
            CultureQuestTheme {
                AppNavigation()
            }
        }

        // 1. Start on the Home Screen and click "Play now"
        composeTestRule.onNodeWithText("Play now").performClick()

        // 2. We are on the Game Screen. Let's play one round.
        // Wait for the game to load (up to 30 seconds).
        composeTestRule.waitUntil(30_000) {
            composeTestRule
                .onAllNodesWithText("What country is it?")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Type an answer into the text field
        composeTestRule.onNodeWithText("What country is it?").performTextInput("Estonia")

        // Click the submit button
        composeTestRule.onNodeWithText("Submit").performClick()

        // 3. A result dialog should appear. Check for either "Correct" or "Incorrect".
        try {
            composeTestRule.onNodeWithText("✅ Correct!").assertIsDisplayed()
        } catch (e: AssertionError) {
            composeTestRule.onNodeWithText("❌ Incorrect!").assertIsDisplayed()
        }

        // 4. Click "Next" to move to the next question or finish the game.
        composeTestRule.onNodeWithText("Next").performClick()

        // For this test, we'll assume a short game. After a few questions, the game
        // should end and navigate back to the home screen. Wait up to 30 seconds.
        composeTestRule.waitUntil(30_000) {
            composeTestRule
                .onAllNodesWithText("Play now")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // 5. Assert that we are back on the home screen
        composeTestRule.onNodeWithText("Play now").assertIsDisplayed()
        // Use substring matching to find the score text
        composeTestRule.onNodeWithText("Last Game Score:", substring = true).assertIsDisplayed()
    }
}
