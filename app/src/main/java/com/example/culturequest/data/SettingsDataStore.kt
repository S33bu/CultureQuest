package com.example.culturequest.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property to delegate DataStore creation
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Represents the available theme options for the application.
 */
enum class Theme {
    /**
     * Follows the system's theme setting.
     */
    SYSTEM,

    /**
     * Forces the light theme.
     */
    LIGHT,

    /**
     * Forces the dark theme.
     */
    DARK,
}

/**
 * Manages the app's settings using Jetpack DataStore.
 *
 * @property context The application context, used to initialize the DataStore.
 */
class SettingsDataStore(
    context: Context,
) {
    private val dataStore = context.dataStore

    companion object {
        /**
         * The key used to store the selected theme in DataStore.
         */
        val THEME_KEY = stringPreferencesKey("theme_option")
    }

    /**
     * A [Flow] that emits the currently selected [Theme].
     *
     * This flow automatically updates when the theme preference changes in the DataStore.
     */
    val theme: Flow<Theme> =
        dataStore.data.map {
            Theme.valueOf(it[THEME_KEY] ?: Theme.SYSTEM.name)
        }

    /**
     * Saves the selected [Theme] to the DataStore.
     *
     * @param theme The new theme to be persisted.
     */
    suspend fun saveTheme(theme: Theme) {
        dataStore.edit {
            it[THEME_KEY] = theme.name
        }
    }
}
