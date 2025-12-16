package com.example.culturequest.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

data class AuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel (private val auth: FirebaseAuth = FirebaseAuth.getInstance()) : ViewModel() {

    private val _authState = mutableStateOf(
        AuthState(
            isLoading = false,
            isLoggedIn = auth.currentUser != null, // kui juba sisse logitud, märgime ära
            errorMessage = null
        )
    )
    val authState: State<AuthState> = _authState

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = _authState.value.copy(
                errorMessage = "Email and password are required"
            )
            return
        }

        _authState.value = _authState.value.copy(
            isLoading = true,
            errorMessage = null
        )

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        errorMessage = null
                    )
                } else {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        errorMessage = task.exception?.message ?: "Login failed"
                    )
                }
            }
    }

    fun signUp(email: String, password: String) {
        if (email.isBlank() || password.length < 6) {
            _authState.value = _authState.value.copy(
                errorMessage = "Password must be at least 6 characters"
            )
            return
        }

        _authState.value = _authState.value.copy(
            isLoading = true,
            errorMessage = null
        )

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        errorMessage = null
                    )
                } else {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        errorMessage = task.exception?.message ?: "Sign up failed"
                    )
                }
            }
    }

    fun signOut() {
        auth.signOut()
        _authState.value = AuthState(
            isLoading = false,
            isLoggedIn = false,
            errorMessage = null
        )
    }

    fun clearError() {
        _authState.value = _authState.value.copy(errorMessage = null)
    }
}
