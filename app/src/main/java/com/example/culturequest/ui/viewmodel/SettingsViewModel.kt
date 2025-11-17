package com.example.culturequest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturequest.MyApp
import com.example.culturequest.data.SettingsDataStore
import com.example.culturequest.data.Theme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val settingsDataStore = MyApp.from(MyApp.context).getSettingsDataStore()

    // Expose the theme as a StateFlow
    val theme: StateFlow<Theme> = settingsDataStore.theme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Theme.SYSTEM
        )

    // Save the new theme setting
    fun saveTheme(theme: Theme) {
        viewModelScope.launch {
            settingsDataStore.saveTheme(theme)
        }
    }
}
