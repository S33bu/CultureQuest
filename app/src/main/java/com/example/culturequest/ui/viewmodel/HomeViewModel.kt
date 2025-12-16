// ViewModel for Home screen; observes the user score for live updates
package com.example.culturequest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturequest.MyApp
import com.example.culturequest.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

class HomeViewModel : ViewModel() {

    private val userRepository = UserRepository(MyApp.instance.database.userDao())

    // Current user score (stateful, observable)
    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score

    private val _bestScore = MutableStateFlow(0)
    val bestScore: StateFlow<Int> = _bestScore

    init {
        viewModelScope.launch {
            // Determine current Firebase user id (or fallback guest id)
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            val uid = firebaseUser?.uid ?: "local_guest"

            // Collect the user profile Flow from the database
            // Updates _score whenever user's score changes
            userRepository.getUserProfile(uid).collect { user ->
                _score.value = user?.score ?: 0
                _bestScore.value = user?.bestScore ?: 0
            }
        }
    }
}
