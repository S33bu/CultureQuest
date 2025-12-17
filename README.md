# CultureQuest
CultureQuest is a mobile game where players guess the country based on real-world panorama images, implemented using the Google Maps API and Android SDK.

The game is time-sensitive — each round lasts 1 minute, challenging players to think fast. To assist, tier-based hints (Easy, Medium, Hard) can be unlocked at any point during the game. Player performance is tracked through a score system and leaderboard, making the experience both fun and competitive.

Team Members:
<br/>

- **Armin Liiv** — Developer, Editor  
  [GitHub Profile](https://github.com/Rover-M) 

- **Caroline Markov** — Researcher, Developer  
  [GitHub Profile](https://github.com/CarolineMarkov)  

- **Kaspar Aednik** — Developer, Project Manager  
  [Github Profile](https://github.com/Lontloom)

- **Riika Seeba** — Developer, Editor  
  [Github Profile](https://github.com/riikaseeba)  

- **Sebastian Mais** — Developer, Presenter   
  [GitHub Profile](https://github.com/S33bu)

<br/>

### Goals
- Deliver an engaging geography quiz experience

- Integrate modern Android technologies seamlessly

- Establish a clean, maintainable MVVM architecture

- Demonstrate professional development workflows

- Build a scalable foundation for future features

### Main Features
- **Game Page:** Core experience powered by **Google Street View**  
- **Hint System:** Tier-based hints (Easy, Medium, Hard) available during gameplay  
- **Leaderboard:** Displays top players and their scores  
- **About Page:** Information about the game and developers  
- **Sign In / Log In:** User authentication to track individual scores and progress  

For a more detailed overview and upcoming features, visit our [**Wiki Page**](https://github.com/S33bu/CultureQuest/wiki).

### Installation/Build instructions
1. Prerequisites: Install [Android Studio](https://developer.android.com/studio)
2. Clone Repository: git clone https://github.com/S33bu/CultureQuest.git
3. Open Project: Launch Android Studio and select Open, then navigate to the cloned folder.
4. Firebase Configuration: Ensure the google-services.json file is present in the app/directory
5. Gradle sync: Click File > Sync Project with Gradle Files. Wait for "Build Successful" message.
6. Run application: select a physical device or emulator and click the green play button

### Development Stack

#### Tools & Environment
IDE: Android Studio
Version Control: GitHub (with Projects & Wiki)
Project Management: Jira
Design: Figma

#### Languages & Core SDKs
Language: Kotlin
Concurrency: Kotlin Coroutines
UI Framework: Jetpack Compose
Design System: Material 3
Navigation: Compose Navigation

#### Key Libraries & Dependencies
Architecture: AndroidX Lifecycle, ViewModel
Local Storage: Room Database (with KSP)
Data Serialization: kotlinx-serialization
Authentication: Firebase Authentication
Mapping: Google Maps SDK, Maps Compose
Geocoding: Google Geocoding API
Local Data: mledoze/countries dataset (embedded in assets)

#### External Services & APIs
Google Maps Platform (Street View, Geocoding)
Firebase (Authentication, Firestore)


### Usage of the app

In CultureQuest you can just click with your mouse or touch (depends if you have a touchpad or not) on buttons that seem logical.
<br/>

**Game window**
<br/>
There are multiple places you can click for example just click on "Play now" and it will start a game.
From there the game will generate a random google street view and you have to type in the box of the country you think it is and click "submit" or just enter on the keyboard.
In the game you can drag to look around and even zoom by pinching or scrolling depending on the device and its inputs.
If you want help you can click on the bulb to get hints or click "Show more hints" to get more hints but be aware you will get less points for finally correcting right

<br/>

**About window**
<br/>
Clicking on the info button will open the about page
<br/>

**login/sign in**
<br/>
on the first boot of the app you will be greeted on the login page from there you click on the button to get to the page to create an user.
for creating an user you need to insert your email, password and you have an user!
<br/>

### Project Structure
The project follows the **MVVM (Model–View–ViewModel)** architectural pattern:

- **data/**: Data layer containing Room-based local persistence, repository implementations, and external data sources (Firebase, Google Maps APIs).
- **ui/screens/**: Stateless Jetpack Compose UI components and screen layouts.
- **ui/viewmodel/**: ViewModels that manage UI state, handle user interactions, and expose state via `StateFlow`.
- **ui/theme/**: Material 3 design system configuration (colors, typography, and shapes).
- **assets/**: Bundled static resources, including the predefined country dataset used for hint generation.
- **res/**: Android resources including drawables, strings for localization, and app icons.




- **assets**: Contains raw files (images, fonts, etc.) bundled with the app. These files are not compiled.

- **java/com/example/culturequest**: Main source code for the app.
  - **data**: Handles data-related classes such as models, repositories, and API services.
  - **ui**: All UI components, split into:
    - **screens**: Represents the app's different UI screens or fragments.
    - **theme**: Defines the app's theme, colors, typography, and styles.
    - **viewmodel**: Contains ViewModels to manage UI data and handle lifecycle-related concerns.

- **res**: Non-code resources for the app.
  - **drawable**: Images and vector graphics used in the UI.
  - **drawable-night**: Night mode (dark theme) resources.
  - **mipmap**: App icons for different screen densities and Android versions.
    - `mipmap-anydpi-v26`: Adaptive icons for Android 8.0+.
    - Other mipmap folders (`hdpi`, `mdpi`, `xhdpi`, `xxhdpi`, `xxxhdpi`) for different screen resolutions.
  - **values**: XML files for colors, strings, dimensions, and styles.
  - **values-night**: Resources for the night theme.
  - **xml**: Configuration files like preferences or custom XML layouts.


