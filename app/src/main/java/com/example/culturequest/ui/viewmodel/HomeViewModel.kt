// ViewModel for Home screen; observes the user score for live updates
package com.example.culturequest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.culturequest.MyApp
import com.example.culturequest.data.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    // Build Room database instance
    private val db = Room.databaseBuilder(
        MyApp.context,
        AppDatabase::class.java,
        "culturequest-db"
    ).build()

    // Current user score (stateful, observable)
    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score

    init {
        viewModelScope.launch {
            // Collect the user profile Flow from the database
            // Updates _score whenever user's score changes
            db.userDao().getUserFlow().collect { user ->
                _score.value = user?.score ?: 0
            }
        }
    }
}
