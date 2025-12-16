package com.example.culturequest.ui.viewmodel

// Import necessary testing libraries.

import com.example.culturequest.MyApp
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import com.google.firebase.auth.FirebaseAuth
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import com.google.android.gms.tasks.Tasks
import android.os.Looper.getMainLooper
import org.robolectric.Shadows.shadowOf

/**
 * Unit tests for [AuthViewModel].
 * * These tests verify the authentication logic, including input validation and
 * handling of Firebase authentication responses.
 * * @property viewModel The instance of the ViewModel being tested.
 * @property mockAuth The mocked Firebase authentication service.
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = MyApp::class, sdk = [28])
class AuthViewModelTest {
    private lateinit var viewModel: AuthViewModel
    private lateinit var mockAuth: FirebaseAuth

    /**
     * Sets up the testing environment before each test case.
     * Initializes the [mockAuth] and injects it into the [viewModel].
     */

    @Before
    fun setUp() {
        // Mockito creates a 'hollow' version of FirebaseAuth to avoid real API calls
        mockAuth = mock(FirebaseAuth::class.java)
        // Dependency Injection: We provide the mock to the ViewModel so it doesn't use the real Firebase
        viewModel = AuthViewModel(mockAuth)
    }

    /**
     * Verifies that [AuthViewModel.signIn] correctly identifies empty input.
     * * Usage: Ensures the UI displays "Email and password are required" without contacting Firebase.
     */
    @Test
    fun `signIn with blank email shows error`() {
        viewModel.signIn("", "password")
        assertEquals("Email and password are required", viewModel.authState.value.errorMessage)
    }

    /**
     * Verifies that [AuthViewModel.signUp] enforces minimum password length.
     * * @see AuthViewModel.signUp
     */
    @Test
    fun `signUp with short password shows error`() {
        viewModel.signUp("test@test.com", "123")
        assertEquals("Password must be at least 6 characters", viewModel.authState.value.errorMessage)
    }

    /**
     * Verifies that the ViewModel correctly handles and displays Firebase formatting errors.
     * * This test mocks a failed Task return from Firebase to simulate an invalid email string.
     */
    @Test
    fun `signUp with badly formatted email shows error`() {
        //Creating a "Fake" Exception that looks like FireBase's error
        val exception = Exception("The email address is badly formatted.")

        //Telling the mock "When someone calls createuser, return a failed task immediately"
        whenever(mockAuth.createUserWithEmailAndPassword(any(), any()))
            .thenReturn(Tasks.forException(exception))

        viewModel.signUp("test", "password")

        //Forces the Main Looper to process the 'addOnCompleteListener' result, without this errormessage would remain null
        shadowOf(getMainLooper()).idle()
        assertEquals("The email address is badly formatted.", viewModel.authState.value.errorMessage)
    }

    /**
     * Verifies that the ViewModel handles incorrect login credentials appropriately.
     * * @throws Exception Simulated Firebase Auth exception.
     */
    @Test
    fun `signIn with incorrect password shows error`() {
        val exception = Exception("The supplied auth credential is incorrect, malformed or has expired.")
        //Directing the mock to return a failed task when signIn is called.
        whenever(mockAuth.signInWithEmailAndPassword(any(), any()))
            .thenReturn(Tasks.forException(exception))
        viewModel.signIn("nonexistent@user.com", "wrongpassword")
        //Ensures the ViewModel state update is processed before assertion.
        shadowOf(getMainLooper()).idle()
        assertEquals("The supplied auth credential is incorrect, malformed or has expired.", viewModel.authState.value.errorMessage)
    }

}
