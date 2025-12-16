package com.example.culturequest.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

// this is an Kotlin object, singleton, so only one instance of it is created
object CountryRepository {
    private var countries: List<Country>? = null

    // Load countries in the background
    // context - needed to open file in assets, onLoaded - callback function
    fun loadCountries(
        context: Context,
        onLoaded: (() -> Unit)? = null,
    ) {
        // CorotineScope - used to run code in the background
        // needed so the game doesn't freeze while loading
        CoroutineScope(Dispatchers.IO).launch {
            val jsonText =
                context.assets
                    .open("countries_min.json")
                    .bufferedReader()
                    .use { it.readText() }
            // ignoreunkown keys, didn't use everything that is in the assets file
            countries = Json { ignoreUnknownKeys = true }.decodeFromString(jsonText)
            onLoaded?.invoke()
        }
    }

    // because the user has to type in the name, so needed the name
    fun getCountryByName(name: String): Country? = countries?.find { it.name.common.equals(name, ignoreCase = true) }

    fun isLoaded(): Boolean = countries != null
}
