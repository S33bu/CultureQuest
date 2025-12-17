package com.example.culturequest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturequest.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home screen that manages user score state.
 *
 * This class observes the [UserRepository] for real-time updates to the user's
 * current and best scores, exposing them as observable [StateFlow] streams to the UI.
 *
 * @property userRepository The repository providing access to user profile data.
 */
class HomeViewModel(
    private val userRepository: UserRepository = UserRepository(),
) : ViewModel() {
    private val _score = MutableStateFlow(0)

    /**
     * Observable stream of the current user's score.
     */
    val score: StateFlow<Int> = _score

    private val _bestScore = MutableStateFlow(0)

    /**
     * Observable stream of the user's all-time best score.
     */
    val bestScore: StateFlow<Int> = _bestScore

    init {
        loadUserProfile()
    }

    /**
     * Initializes the data stream for the current user's profile.
     *
     * Fetches the unique identifier from Firebase Authentication and collects
     * the profile [kotlinx.coroutines.flow.Flow] to update stateful scores.
     * Use "local_guest" as a fallback if no authenticated user is found.
     */
    private fun loadUserProfile() {
        viewModelScope.launch {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            val uid = firebaseUser?.uid ?: "local_guest"

            userRepository.getUserProfile(uid).collect { user ->
                _score.value = user?.score ?: 0
                _bestScore.value = user?.bestScore ?: 0
            }
        }
    }
}
