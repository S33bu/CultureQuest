  - Testing strategy
   - Build process for APK
   - Known bugs or limitations

   - Last times mistakes fixed:
We use the **Google Geocoding API** and **Google Street View Metadata API**.  
The Geocoding API gives us country bounds and reverse-geocoding for coordinates, and the Street View Metadata API lets us check if Street View imagery is available. This fits our CultureQuest use case: generating random locations inside a country that the player can explore in Street View.

## Example Endpoints

- Geocoding (country bounds):  
  `https://maps.googleapis.com/maps/api/geocode/json?address={COUNTRY_NAME}&key=API_KEY`

- Reverse geocoding (coordinate → country):  
  `https://maps.googleapis.com/maps/api/geocode/json?latlng={LAT},{LNG}&result_type=country&key=API_KEY`

- Street View metadata (imagery check):  
  `https://maps.googleapis.com/maps/api/streetview/metadata?location={LAT},{LNG}&radius=5000&key=API_KEY`
  ## API Service & Data Model

The `RandomLocationProvider` class encapsulates all API access using `URL(...).readText()` (HttpURLConnection under the hood) and Kotlin coroutines.  
JSON is parsed with `kotlinx.serialization` into data classes such as:

- `GeocodingResponse`, `GeocodingResult`, `Geometry`, `Bounds`, `Location`
- `AddressComponent` (for country name)
- `StreetViewMetaDataResponse` (for imagery status)

## UI Integration

The UI screen calls `getRandomLocationForCountry(countryName)` from a ViewModel / coroutine scope:

- Shows a **loading indicator** while the location is being fetched.
- Displays the **Street View panorama** for the found `LatLng`.
- Shows a **Toast or placeholder message** if no valid location is found or if the API call fails.

## Error Handling Strategy

- All network calls run on `Dispatchers.IO` using `withContext`.
- Each call is wrapped in `try/catch`; failures are logged and converted into safe return values (`null` / `false`).
- `getRandomLocationForCountry`:
  - Limits attempts to 10 random coordinates per request.
  - Verifies both Street View imagery and that the coordinate belongs to the selected country.
  - Falls back to the country’s center if no suitable location is found.
- The UI reacts to `null`/error states by showing feedback instead of crashing (Toast/placeholder).

These changes fix the previous iteration’s issues around missing error handling, unstable networking, and lack of clear API modeling.
