package com.example.culturequest.ui.viewmodel

// Import necessary testing libraries.
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for the [AuthViewModel].
 * These tests verify the logic for handling sign-in and sign-up validation.
 */
class AuthViewModelTest {

    // Test case to verify that attempting to sign in with a blank email triggers an error.
    @Test
    fun `signIn with blank email shows error`() {
        // Create an instance of the AuthViewModel.
        val viewModel = AuthViewModel()
        // Call the signIn method with a blank email and a valid password.
        viewModel.signIn("", "password")
        // Assert that the errorMessage in the authState now reflects the expected validation error.
        assertEquals("Email and password are required", viewModel.authState.value.errorMessage)
    }

    // Test case to verify that signing up with a password shorter than 6 characters shows an error.
    @Test
    fun `signUp with short password shows error`() {
        val viewModel = AuthViewModel()
        viewModel.signUp("test@test.com", "123")
        // Assert that the correct error message for a short password is set.
        assertEquals("Password must be at least 6 characters", viewModel.authState.value.errorMessage)
    }

    // Test case to verify that signing up with a badly formatted email shows an error.
    @Test
    fun `signUp with badly formatted email shows error`() {
        val viewModel = AuthViewModel()
        viewModel.signUp("test", "password")
        // Assert that the correct error message for a badly formatted email is set.
        assertEquals("The email address is badly formatted.", viewModel.authState.value.errorMessage)
    }

    // Test case to verify that signing in with an incorrect password shows an error.
    @Test
    fun `signIn with incorrect password shows error`() {
        val viewModel = AuthViewModel()
        viewModel.signIn("nonexistent@user.com", "wrongpassword")
        // Assert that the correct error message for an incorrect credential is set.
        assertEquals("The supplied auth credential is incorrect, malformed or has expired.", viewModel.authState.value.errorMessage)
    }

}
