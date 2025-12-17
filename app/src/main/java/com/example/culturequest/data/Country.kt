package com.example.culturequest.data

import kotlinx.serialization.Serializable

/**
 * Represents a country's data retrieved from the REST Countries API.
 *
 * This data class is used to store geographical and cultural information
 * used within the quiz and country discovery features of the app.
 *
 * @property cca2 The ISO 3166-1 alpha-2 two-letter country code (e.g., "EE" for Estonia).
 * @property name The localized and common name information for the country.
 * @property region The broad geographic region (e.g., Europe, Americas).
 * @property subregion The specific sub-geographic region.
 * @property capital A list of capital cities associated with the country.
 * @property languages A map of language codes to their full names.
 * @property borders A list of neighboring countries by their ISO codes.
 * @property currencies A map of currency codes to their details.
 * @property area The total surface area of the country in square kilometers ($km^2$).
 * @property landlocked Indicates whether the country is completely surrounded by land.
 */
@Serializable
data class Country(
    val cca2: String,
    val name: Name,
    val region: String? = null,
    val subregion: String? = null,
    val capital: List<String>? = null,
    val languages: Map<String, String>? = null,
    val borders: List<String>? = null,
    val currencies: Map<String, Currency>? = null,
    val area: Double? = null,
    val landlocked: Boolean? = null,
)

/**
 * Contains the naming information for a country.
 *
 * @property common The commonly used name of the country in English.
 */
@Serializable
data class Name(
    val common: String,
)

/**
 * Represents currency information for a specific country.
 *
 * @property name The full name of the currency (e.g., "Euro").
 * @property symbol The currency symbol (e.g., "€").
 */
@Serializable
data class Currency(
    val name: String? = null,
    val symbol: String? = null,
)
