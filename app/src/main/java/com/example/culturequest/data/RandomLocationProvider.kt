package com.example.culturequest.data

import android.os.Build
import android.util.Log
import com.example.culturequest.BuildConfig
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.URL

object RandomLocationProvider {

    private val jsonParser = Json { ignoreUnknownKeys = true }
    private const val API_KEY = BuildConfig.GEOLOCATION_API_KEY

   //checks if the street view has a panorama available, otherwise displays a black screen
    private suspend fun hasStreetViewImagery(point: LatLng): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val url = "https://maps.googleapis.com/maps/api/streetview/metadata?location=${point.latitude},${point.longitude}&key=${BuildConfig.MAPS_API_KEY}"
                val json = URL(url).readText()
                val response = jsonParser.decodeFromString<StreetViewMetaDataResponse>(json)

                //api returns "OK" if imagery is found.
                //we need this to make sure a street view panorama is available for this location, otherwise it will display a black screen
                val hasImagery = response.status == "OK"
                Log.d("RLP_DEBUG", "StreetView imagery check for $point: ${response.status}")
                return@withContext hasImagery

            } catch (e: Exception) {
                Log.e("RandomLocation", "Street View metadata check failed", e)
                return@withContext false
            }
        }

   //uses the country borders to generate random coordinates within them
    suspend fun getRandomLocationForCountry(countryName: String): LatLng? =
        withContext(Dispatchers.IO) {
            Log.d("RandomLocationProvider", "Requesting location for: $countryName")
            try {
                val bounds = getCountryBounds(countryName) ?: return@withContext null
                val (sw, ne) = bounds

                var attempt = 1
                while (attempt <= 10) { // antique solution to limit api requests, maybe better solution later
                    attempt++
                    val lat = kotlin.random.Random.nextDouble(sw.lat, ne.lat)
                    val lng = kotlin.random.Random.nextDouble(sw.lng, ne.lng)
                    val candidate = LatLng(lat, lng)

                    val inCountry = isPointInCountry(candidate, countryName)
                    if (!inCountry) {
                        Log.d("RLP_DEBUG", "Attempt $attempt: $candidate is not in $countryName")
                        continue
                    }

                    val hasImagery = hasStreetViewImagery(candidate)
                    if (hasImagery) {
                        Log.d("RLP_DEBUG", "Valid candidate found on attempt $attempt: $candidate")
                        return@withContext candidate
                    } else {
                        Log.d("RLP_DEBUG", "Attempt $attempt: $candidate has no Street View imagery")
                    }
                }

                // fallback, return center of the country if no valid candidates found (hopefully works)
                LatLng((sw.lat + ne.lat) / 2, (sw.lng + ne.lng) / 2)

            } catch (e: Exception) {
                Log.e("RandomLocation", "Critical error", e)
                null
            }
        }

   //gets the correct answer as the country name and searches for the borders to get random coordinates within them later
    private fun getCountryBounds(countryName: String): Pair<Location, Location>? {
        return try {
            val url =
                "https://maps.googleapis.com/maps/api/geocode/json?address=$countryName&key=$API_KEY"
            val json = URL(url).readText()
            val response = jsonParser.decodeFromString<GeocodingResponse>(json)

            val geometry = response.results.firstOrNull()?.geometry ?: return null
            val bounds = geometry.bounds ?: geometry.viewport ?: return null
            Pair(bounds.southwest, bounds.northeast)

        } catch (e: Exception) {
            Log.e("CountryBounds", "Failed bounds for $countryName", e)
            null
        }
    }

    //checks if the coordinates are in the country we need
    private suspend fun isPointInCountry(point: LatLng, expected: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val url =
                    "https://maps.googleapis.com/maps/api/geocode/json?latlng=${point.latitude},${point.longitude}&result_type=country&key=$API_KEY"
                val json = URL(url).readText()
                val response = jsonParser.decodeFromString<GeocodingResponse>(json)

                val found = response.results.firstOrNull()
                    ?.address_components
                    ?.firstOrNull { "country" in it.types }
                    ?.long_name

                Log.d("RLP_DEBUG", "Reverse geocode for $point returned country: $found")
                found?.equals(expected, ignoreCase = true) == true

            } catch (e: Exception) {
                Log.e("RandomLocation", "Reverse geocode failed", e)
                false
            }
        }


    @Serializable
    data class GeocodingResponse(
        val results: List<GeocodingResult>,
        val status: String
    )

    @Serializable
    data class StreetViewMetaDataResponse(
        val status: String
    )

    @Serializable
    data class GeocodingResult(
        val geometry: Geometry,
        @SerialName("address_components")
        val address_components: List<AddressComponent>
    )

    @Serializable
    data class Geometry(
        val location: Location,
        val bounds: Bounds? = null,
        val viewport: Bounds? = null
    )

    @Serializable
    data class Bounds(
        val northeast: Location,
        val southwest: Location
    )

    @Serializable
    data class Location(
        val lat: Double,
        val lng: Double
    )

    @Serializable
    data class AddressComponent(
        @SerialName("long_name")
        val long_name: String,
        val types: List<String>
    )
}
