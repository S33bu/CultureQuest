package com.example.culturequest.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

/**
 * Repository responsible for managing country data.
 *
 * This singleton object handles the asynchronous loading of country information
 * from the local assets and provides methods to query specific country details.
 */
object CountryRepository {
    private var countries: List<Country>? = null

    // JSON configuration used for parsing country data.
    private val jsonConfig =
        Json {
            ignoreUnknownKeys = true
        }

    /**
     * Loads the country data from the assets folder in a background thread.
     *
     * @param context The application context used to access the assets folder.
     * @param onLoaded An optional callback invoked after the data has been successfully parsed.
     */
    fun loadCountries(
        context: Context,
        onLoaded: (() -> Unit)? = null,
    ) {
        // Using IO Dispatcher to prevent UI blocking during file I/O operations.
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jsonText =
                    context.assets
                        .open("countries_min.json")
                        .bufferedReader()
                        .use { it.readText() }
                countries = jsonConfig.decodeFromString<List<Country>>(jsonText)

                onLoaded?.invoke()
            } catch (e: Exception) {
                Log.e("CountryRepository", "Error loading countries: ${e.message}")
            }
        }
    }

    /**
     * Retrieves a country by its common name.
     *
     * @param name The common name of the country (case-insensitive).
     * @return The [Country] object if found, otherwise null.
     */
    fun getCountryByName(name: String): Country? = countries?.find { it.name.common.equals(name, ignoreCase = true) }

    /**
     * Checks if the country data has been successfully loaded into memory.
     *
     * @return True if the countries list is not null.
     */
    fun isLoaded(): Boolean = countries != null
}
