package com.example.culturequest.data

import kotlinx.serialization.Serializable

@Serializable
data class Country(
    val cca2: String, //for Estonia EE
    val name: Name,
    val region: String? = null,
    val subregion: String? = null,
    val capital: List<String>? = null,
    val languages: Map<String, String>? = null,
    val borders: List<String>? = null, //countries it is boarding
    val currencies: Map<String, Currency>? = null,
    val area: Double? = null, //in km
    val landlocked: Boolean? = null,
)

@Serializable data class Name(val common: String)
@Serializable data class Currency(val name: String? = null, val symbol: String? = null)

