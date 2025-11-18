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

enum class Theme { 
    SYSTEM, LIGHT, DARK 
}

class SettingsDataStore(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val THEME_KEY = stringPreferencesKey("theme_option")
    }

    // Flow to get the current theme preference
    val theme: Flow<Theme> = dataStore.data.map {
        Theme.valueOf(it[THEME_KEY] ?: Theme.SYSTEM.name)
    }

    // Function to save the theme preference
    suspend fun saveTheme(theme: Theme) {
        dataStore.edit {
            it[THEME_KEY] = theme.name
        }
    }
}