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
