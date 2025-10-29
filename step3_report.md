### Which API was chosen and why
<br>
We used Google Maps API and Google Maps SDK for Android
Because it has the most extensive street view documentation, allows panorama views and is easy to implement
<br>

### Example API endpoint used
<br>
Because we use the Google Maps SDK, we can't make manual calls to a traditional REST API endpoint. The SDK handles the communication with Google's backend services automatically. If we were to use the static Street View Static API, the endpoint would look something like this
"https://maps.googleapis.com/maps/api/streetview?size=600x300&location=41.89021,12.492231&key=YOUR_API_KEY"
<br>
In our code the closest thing to a 'API call' in our code is this: "panorama.setPosition(location, 50)"
<br>
<br>

### Error handling strategy
<br>
The Google Maps SDK for Android includes built-in error handling for connection and imagery issues. If the SDK cannot load Street View data (for example, due to missing imagery, an invalid API key, or no internet connection), 
it automatically displays a black screen instead of crashing the app. 

Originally, we attempted to implement a custom error handling mechanism for the Street View component to detect and display a user-friendly message (for example, “Street View not available for this location”) when imagery failed to load.
However, our custom approach did not work reliably — the StreetViewPanoramaView API does not provide direct callbacks or status codes to indicate whether imagery is available at a given location. As a result, our implementation could not consistently detect load failures and sometimes caused unnecessary recompositions or black screens.

Because of these limitations, we decided to rely on the built-in error handling provided by the Google Maps SDK for Android.
The SDK already includes internal handling for missing imagery, invalid API keys, or network issues — automatically displaying a blank (black) screen when Street View data cannot be loaded. This approach prevents the app from crashing and ensures stable performance, even if no imagery is available.
