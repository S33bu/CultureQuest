package com.example.culturequest

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.culturequest.ui.theme.CultureQuestTheme
import io.qameta.allure.kotlin.Allure.step
import io.qameta.allure.kotlin.Step
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import io.qameta.allure.android.runners.AllureAndroidJUnit4


@RunWith(AndroidJUnit4::class)
class GameFlowTest {

    //https://developer.android.com/develop/ui/compose/testing
    //NB ideally wanted to get allure report working, but had issues and could not
    //from usual experience the steps are still defined

    @Step("Ensure logged in and on home screen")
    private fun ensureLoggedInAndOnHome(email: String, password: String) {
        val alreadyOnHome = runCatching {
            composeTestRule.waitUntil(timeoutMillis = 10_000) {
                composeTestRule.onAllNodesWithText("Play now").fetchSemanticsNodes().isNotEmpty()
            }
        }.isSuccess

        if (!alreadyOnHome) {
            step("Not already on home -> performing sign-in or sign-up")
            signInOrSignUp(email, password)
            verifyHomePageIsDisplayed()
        } else {
            step("Already on home -> skipping sign-in or sign-up")
        }
    }

    @Step("Sign in or sign up")
    private fun signInOrSignUp (email: String, password: String) {

        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").performTextInput(email)
        composeTestRule.onNodeWithText("Password").performTextInput(password)
        composeTestRule.onNodeWithText("Sign In ->").performClick()

        val loginSucceeded = runCatching {
            composeTestRule.waitUntil(timeoutMillis = 5_000) {
                composeTestRule.onAllNodesWithText("Play now")
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }
        }.isSuccess

        if (loginSucceeded == false) {
            composeTestRule.onNodeWithText("Sign Up").performClick()

            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule.onAllNodesWithText("Create account").fetchSemanticsNodes()
                    .isNotEmpty()
            }

            composeTestRule.onNodeWithText("Email").performTextInput(email)
            composeTestRule.onNodeWithText("Create your password").performTextInput(password)
            composeTestRule.onNodeWithText("Create your password").performImeAction()
        }
    }

    @Step("Verify home page is displayed")
    private fun verifyHomePageIsDisplayed() {
        // Wait until the "Play now" button is visible, as it's a key indicator.
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Play now").fetchSemanticsNodes().isNotEmpty()
        }

        // Assert all key elements are present
        composeTestRule.onNodeWithText("Play now").assertIsDisplayed()
        composeTestRule.onNodeWithText("CultureQuest").assertIsDisplayed()
        composeTestRule.onNode(hasText("Last Game Score:", substring = true)).assertIsDisplayed()
        composeTestRule.onNode(hasText("Best Score:", substring = true)).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("About").assertIsDisplayed()
    }

    @Step("Verify about page is displayed")
    private fun verifyAboutPageIsDisplayed() {
        // Unique element assurance.
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("About CultureQuest").fetchSemanticsNodes().isNotEmpty()
        }

        // Assert all key elements are present
        composeTestRule.onNodeWithText("About CultureQuest").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
        composeTestRule.onNode(hasText("Ready to explore the world", substring = true)).assertIsDisplayed()
        composeTestRule.onNodeWithText("Theme").assertIsDisplayed()
        composeTestRule.onNodeWithText("Features:").performScrollTo().assertIsDisplayed()
    }

    @Step("Verify profile page is displayed")
    private fun verifyProfilePageIsDisplayed() {
        // Wait for a unique element on the Profile page.
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            // Assuming your profile page has a title "Profile" and shows the user's email.
            composeTestRule.onAllNodesWithText("Profile").fetchSemanticsNodes().isNotEmpty()
        }

        // Assert all key elements are present
        composeTestRule.onNodeWithText("Profile").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
        composeTestRule.onNode(hasText("Name", substring = true)).assertIsDisplayed()
    }

    @Step("Verify game page is displayed")
    private fun verifyGamePageIsDisplayed() {
        // Wait for the game screen to load, identified by key element so question
        composeTestRule.waitUntil(timeoutMillis = 30_000) {
            composeTestRule.onAllNodesWithText("What country is it?").fetchSemanticsNodes().isNotEmpty()
        }

        // Assert all key elements are present.
        composeTestRule.onNodeWithText("What country is it?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Submit").assertIsDisplayed()
    }



    @get:Rule
    val composeTestRule = createComposeRule()
    // use createAndroidComposeRule<YourActivity>() if you need access to
    // an activity

    @Test
    fun fullAppFlow_withUIConsistencyTest() {
        composeTestRule.setContent {
            CultureQuestTheme {
                AppNavigation()
            }
        }

        val email = "test@test.com"
        val password = "password"

        step("Ensure logged in and on home screen")
        ensureLoggedInAndOnHome(email, password)
        verifyHomePageIsDisplayed()

        step("Navigate to About")
        composeTestRule.onNodeWithContentDescription("About").performClick()
        verifyAboutPageIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        verifyHomePageIsDisplayed()

        step("Navigate to Profile")
        composeTestRule.onNodeWithContentDescription("Profile").performClick()
        verifyProfilePageIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        verifyHomePageIsDisplayed()

        step("Navigate to Game")
        composeTestRule.onNodeWithText("Play now").performClick()
        verifyGamePageIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        verifyHomePageIsDisplayed()
    }
}
