## Testing strategy
For UI testing we decided to use Compose UI tests instead of Espresso. Espresso works best with XML-based layouts because it interacts with the traditional Android View hierarchy. Our app, however, is built almost completely with Jetpack Compose (LoginPageScreen, SignupPageScreen, HomePageScreen, etc.). Compose does not produce View objects — instead it creates a semantics tree, which Espresso cannot properly work with.

So using Compose UI testing was the more natural option.
With Compose UI tests we were able to check navigation, content display, button clicks, and form input using the test rule and Compose semantics. We also split the navigation tests into private helper functions for each screen to keep the code easier to read and maintain.

We attempted to add Allure reporting to improve test result visualization, but this integration kept failing due to compatibility issues with Compose UI tests. Because of this we decided to keep the Compose UI test setup simple and reliable, without advanced reporting tools.
  
## Build process for APK
  
## Known bugs or limitations
  
No logout option:
At the moment users cannot log out once they are signed in. This is a major limitation because it prevents switching accounts and reduces overall usability. A proper logout button needs to be added to the Home screen or settings.

Sometimes the random coordinate generated doesn't fall into the range of available Street View imagery, resulting (in an error handled by Google) a black screen. Solution is to randomly guess a country and move on to the next question. 

No “Forgot Password” feature:
If a user forgets their password, there is currently no way to reset it. This is a serious missing feature for any login system, and it needs to be implemented to allow users to recover their accounts.

These do not break the app, they impact user experience

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
