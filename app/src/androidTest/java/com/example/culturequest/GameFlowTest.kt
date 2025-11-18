package com.example.culturequest

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.culturequest.ui.theme.CultureQuestTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameFlowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun fullAppFlow_withUIConsistencyTest() {
        composeTestRule.setContent {
            CultureQuestTheme {
                AppNavigation()
            }
        }

        //Starting UI Consistency Check

        // Check Login page title
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()

        // Navigate to Sign Up page and check title
        composeTestRule.onNodeWithText("Don't have an account? Sign up").performClick()
        composeTestRule.waitUntil(10_000) {
            composeTestRule.onAllNodesWithText("Sign Up").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Sign Up").assertIsDisplayed()

        // Navigate back to Login page
        composeTestRule.onNodeWithText("Already have an account? Login").performClick()
        composeTestRule.waitUntil(10_000) {
            composeTestRule.onAllNodesWithText("Login").fetchSemanticsNodes().isNotEmpty()
        }

        // 1. Login Flow Check
        composeTestRule.onNodeWithText("Email").performTextInput("test@test.com")
        composeTestRule.onNodeWithText("Password").performTextInput("password")
        composeTestRule.onNodeWithText("Login").performClick()

        // Wait for home screen to load
        composeTestRule.waitUntil(30_000) {
            composeTestRule.onAllNodesWithText("Play now").fetchSemanticsNodes().isNotEmpty()
        }

        // 2. Continue UI Consistency Check
        // Check Home Page title
        composeTestRule.onNodeWithText("Culture\nQuest").assertIsDisplayed()

        // Navigate to About Page and check title
        composeTestRule.onNodeWithText("About").performClick()
        composeTestRule.waitUntil(10_000) {
            composeTestRule.onAllNodesWithText("About CultureQuest").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("About CultureQuest").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // Navigate to Profile Page and check title
        composeTestRule.onNodeWithText("Profile").performClick()
        composeTestRule.waitUntil(10_000) {
            composeTestRule.onAllNodesWithText("Profile").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Profile").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // 3. Game Flow
        // Click the "Play now" button to start a new game.
        composeTestRule.onNodeWithText("Play now").performClick()

        // Wait for the game screen to load, which is identified by the question text.
        composeTestRule.waitUntil(30_000) {
            composeTestRule.onAllNodesWithText("What country is it?").fetchSemanticsNodes().isNotEmpty()
        }

        // Input an answer into the text field and submit it.
        // Note: The answer "Estonia" is hardcoded. This might pass or fail depending on the question generated.
        composeTestRule.onNodeWithText("What country is it?").performTextInput("Estonia")
        composeTestRule.onNodeWithText("Submit").performClick()

        // After submitting, the result is displayed.
        // We check if either the "Correct!" or "Incorrect!" message appears.
        // This is necessary because the correct answer is dynamic and unknown to the test.
        try {
            composeTestRule.onNodeWithText("✅ Correct!").assertIsDisplayed()
        } catch (e: AssertionError) {
            composeTestRule.onNodeWithText("❌ Incorrect!").assertIsDisplayed()
        }

        // Click "Next" to proceed to the score screen (which is the home screen in this app).
        composeTestRule.onNodeWithText("Next").performClick()

        // Wait until the home screen is visible again, identified by the "Play now" button.
        composeTestRule.waitUntil(30_000) {
            composeTestRule.onAllNodesWithText("Play now").fetchSemanticsNodes().isNotEmpty()
        }

        // Verify that we are back on the home screen and that the "Last Game Score" is displayed.
        composeTestRule.onNodeWithText("Play now").assertIsDisplayed()
        composeTestRule.onNodeWithText("Last Game Score:", substring = true).assertIsDisplayed()
    }

    @Test
    fun gameFlow_invalidInputShowsError() {
        composeTestRule.setContent {
            CultureQuestTheme {
                AppNavigation()
            }
        }

        // Login to get to the home screen
        composeTestRule.onNodeWithText("Email").performTextInput("test@test.com")
        composeTestRule.onNodeWithText("Password").performTextInput("password")
        composeTestRule.onNodeWithText("Login").performClick()

        // Wait for home screen to load
        composeTestRule.waitUntil(30_000) {
            composeTestRule.onAllNodesWithText("Play now").fetchSemanticsNodes().isNotEmpty()
        }

        // Start the game
        composeTestRule.onNodeWithText("Play now").performClick()

        // Wait for the game screen to load
        composeTestRule.waitUntil(30_000) {
            composeTestRule.onAllNodesWithText("What country is it?").fetchSemanticsNodes().isNotEmpty()
        }

        // Input an invalid answer with numbers and special characters
        composeTestRule.onNodeWithText("What country is it?").performTextInput("Invalid123!")

        // Check that the error message is displayed
        composeTestRule.onNodeWithText("Only letters, spaces, and hyphens are allowed.").assertIsDisplayed()

        // Replace the text with a valid input and check that the error message disappears
        composeTestRule.onNodeWithText("What country is it?").performTextReplacement("Valid-Input")
        composeTestRule.onNodeWithText("Only letters, spaces, and hyphens are allowed.").assertDoesNotExist()
    }
}
