package com.example.culturequest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturequest.MyApp
import com.example.culturequest.data.Theme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing app settings, such as the theme.
 *
 * This ViewModel interacts with [SettingsDataStore] to persist and retrieve user preferences.
 */
class SettingsViewModel : ViewModel() {
    private val settingsDataStore = MyApp.from(MyApp.context).getSettingsDataStore()

    /**
     * A [StateFlow] that emits the currently selected [Theme].
     *
     * This flow is collected by the UI to observe theme changes and update the app's appearance.
     * It is configured to stop emitting updates when the UI is not visible to save resources.
     */
    val theme: StateFlow<Theme> =
        settingsDataStore.theme.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Theme.SYSTEM,
        )

    /**
     * Saves the selected [Theme] to the [SettingsDataStore].
     *
     * @param theme The new theme to be persisted.
     */
    fun saveTheme(theme: Theme) {
        viewModelScope.launch {
            settingsDataStore.saveTheme(theme)
        }
    }
}
