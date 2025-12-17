package com.example.culturequest.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

/**
 * Represents the authentication-related UI state.
 *
 * @property isLoading Indicates whether an authentication request is in progress.
 * @property isLoggedIn Indicates whether the user is currently authenticated.
 * @property errorMessage Optional error message to display to the user.
 */
data class AuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null,
)

/**
 * ViewModel responsible for handling user authentication logic.
 *
 * Uses Firebase Authentication to support:
 * - sign in
 * - sign up
 * - sign out
 *
 * Exposes the current authentication state via [authState].
 */
class AuthViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
) : ViewModel() {

    private val _authState =
        mutableStateOf(
            AuthState(
                isLoading = false,
                isLoggedIn = auth.currentUser != null,
                errorMessage = null,
            ),
        )

    /**
     * Publicly exposed authentication state.
     */
    val authState: State<AuthState> = _authState

    /**
     * Signs in an existing user using email and password.
     *
     * Updates [authState] to reflect loading, success, or error states.
     *
     * @param email User email address.
     * @param password User password.
     */
    fun signIn(
        email: String,
        password: String,
    ) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value =
                _authState.value.copy(
                    errorMessage = "Email and password are required",
                )
            return
        }

        _authState.value =
            _authState.value.copy(
                isLoading = true,
                errorMessage = null,
            )

        auth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value =
                        _authState.value.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            errorMessage = null,
                        )
                } else {
                    _authState.value =
                        _authState.value.copy(
                            isLoading = false,
                            isLoggedIn = false,
                            errorMessage = task.exception?.message ?: "Login failed",
                        )
                }
            }
    }

    /**
     * Creates a new user account using email and password.
     *
     * The password must be at least 6 characters long.
     *
     * @param email User email address.
     * @param password User password.
     */
    fun signUp(
        email: String,
        password: String,
    ) {
        if (email.isBlank() || password.length < 6) {
            _authState.value =
                _authState.value.copy(
                    errorMessage = "Password must be at least 6 characters",
                )
            return
        }

        _authState.value =
            _authState.value.copy(
                isLoading = true,
                errorMessage = null,
            )

        auth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value =
                        _authState.value.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            errorMessage = null,
                        )
                } else {
                    _authState.value =
                        _authState.value.copy(
                            isLoading = false,
                            isLoggedIn = false,
                            errorMessage = task.exception?.message ?: "Sign up failed",
                        )
                }
            }
    }

    /**
     * Signs out the currently authenticated user and resets the authentication state.
     */
    fun signOut() {
        auth.signOut()
        _authState.value =
            AuthState(
                isLoading = false,
                isLoggedIn = false,
                errorMessage = null,
            )
    }

    /**
     * Clears the current authentication error message.
     *
     * Useful after the error has been shown to the user.
     */
    fun clearError() {
        _authState.value = _authState.value.copy(errorMessage = null)
    }
}
