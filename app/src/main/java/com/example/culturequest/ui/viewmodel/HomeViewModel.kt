package com.example.culturequest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.culturequest.MyApp
import com.example.culturequest.data.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val db = Room.databaseBuilder(
        MyApp.context,
        AppDatabase::class.java,
        "culturequest-db"
    ).build()

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score

    init {
        viewModelScope.launch {
            // Collect the Flow from the DAO for live updates
            db.userDao().getUserFlow().collect { user ->
                _score.value = user?.score ?: 0
            }
        }
    }
}
