# City Explorer Challenge

## 1. Project Overview
**City Explorer Challenge** is a context-aware Android application designed to act as a personal urban exploration assistant. Moving beyond traditional static CRUD applications, this system actively engages users by dynamically generating location-based tasks and motivating them to discover nearby points of interest. 

The application integrates real-time GPS data, external mapping APIs, and a custom decision-making engine to ensure that every exploration is unique, safe, and tailored to the user's recent activity.

## 2. Dynamic Challenge Generation Algorithm (Engine)
The core component of this application is its **Recommendation Engine**, which dynamically synthesizes challenges rather than pulling them from a predefined static database. 

Upon receiving the user's geographical coordinates, the application queries the **Google Places API** for nearby Points of Interest (POIs). The retrieved raw data is then processed through a rigorous evaluation pipeline comprising **five dynamic filtering heuristics**:

### Rule 1: Historical Exclusion & Anti-Repetition
To guarantee novel experiences, the engine evaluates candidate locations against a locally persisted `Room Database`. It extracts the IDs of the last 10 completed challenges and immediately discards these locations from the current candidate pool. Furthermore, it utilizes a short-term memory variable (`lastGeneratedPlaceId`) to ensure that if a user skips a challenge, the engine will never suggest the exact same location consecutively.

### Rule 2: Category Fatigue Prevention
The algorithm actively monitors the user's behavioral patterns to encourage diversity. It scans the last 3 completed challenges; if a specific category (e.g., "Food") dominates the recent history (e.g., ≥ 2 visits), the engine temporarily blacklists all food-related POIs from the current generation cycle. This forces the user to explore alternative categories such as "Culture" or "Park".

### Rule 3: Time-Based Safety Protocol
The engine incorporates the device's system clock to adjust its recommendations based on the time of day. If the request is made during nighttime hours (defined as 19:00 to 06:00), the engine completely filters out potentially unlit or secluded categories, such as "Park", to prioritize user safety.

### Rule 4: Dynamic Distance Constraints
Distance thresholds are not static; they scale according to environmental context. During daytime operations, the algorithm allows challenges up to a 3,000-meter radius to promote extensive walking. However, during nighttime, the engine dynamically restricts the maximum allowed radius to 1,000 meters, ensuring that users are not sent on long expeditions in the dark.

### Rule 5: Minimum Exploration Threshold
To ensure that generated tasks require actual physical movement and effort, the algorithm drops any POIs located within a 50-meter radius of the user's current exact coordinates.

### Final Selection Phase
After the raw API data passes through these 5 strict filters, the surviving valid locations are pooled together. Instead of predictably selecting the closest location, the engine applies a **randomized selection algorithm** (`randomOrNull()`) to pick the final target, maximizing the element of surprise for the user.

## 3. Architecture & Technologies
The application is built upon a modern Android tech stack:
*   **UI Framework:** Jetpack Compose (Modern declarative UI).
*   **Location Services:** `FusedLocationProviderClient` for high-accuracy GPS tracking.
*   **External API:** Google Places API (Retrieving POIs, coordinates, ratings, and photographic references).
*   **Mapping:** `OSMdroid` implementation for rendering dynamic maps and placing interactive markers.
*   **Local Persistence:** `Room Database` for robust local storage of user history and statistics.
*   **Asynchronous Operations:** Kotlin Coroutines and `Dispatchers.IO` for non-blocking network calls.
*   **Image Loading:** `Coil` for asynchronous image fetching from the Google Places endpoint.

## 4. Application Modules (Screens)
The application adheres to a clean, minimalist design across five main screens:
1.  **Main Screen:** Displays the current active challenge, daily progress, and dynamic generation controls.
2.  **Map Screen:** Renders a dynamic map showing both the user's real-time location and the target destination marker, alongside distance metrics.
3.  **Challenge Details Screen:** Provides maximum transparency by listing the exact algorithmic reasoning (the executed rules) behind the current recommendation, alongside a real photograph of the place.
4.  **History Screen:** A chronological record of successfully completed exploration tasks.
5.  **Statistics Screen:** Aggregates user metrics, including total distance walked and categorical preferences.

## 5. Setup & Execution Instructions (IMPORTANT)

> **⚠️ Security Notice regarding API Keys:**
> In compliance with repository security best practices, **private API keys have been intentionally omitted** from this repository. 

To build and run the application locally, please follow these steps:

1. **Clone the Repository** and open the project in Android Studio (API 34+ recommended).
2. **Inject API Key:** Open the file `app/src/main/java/com/example/cityexplorerfinal/api/GooglePlacesApiService.kt`. Locate the `googleApiKey` variable and paste your active Google Cloud Console API Key (with Places API enabled).
3. **Emulator GPS Configuration:** If testing on a virtual device, the emulator's GPS cache may initially be null. 
    * Open the emulator's extended controls (`...` menu) -> **Location**.
    * Set a coordinate (e.g., a major European city like Krakow).
    * Click **Set Location**.
    * For optimal results, open the native Google Maps app on the emulator once and click the "My Location" button to force the Android OS to cache the injected coordinates before launching the City Explorer Challenge application.
4. **Run the Application** using `Shift + F10`.

---
*Developed as the Final Project for Mobile App Development.*
```
