# Smart Agriculture - Native Android Mobile Application

Smart Agriculture is a production-grade, native Android application built using **Kotlin**, **Jetpack Compose**, **Material Design 3 (M3)**, and **MVVM Clean Architecture**. It serves as an agronomic companion for farmers and agricultural extension officers in Pakistan and surrounding agricultural regions.

The application provides:
- **Leaf disease visual diagnosis** (with CameraX and photo gallery picker).
- **Responsible AI diagnosis screens** featuring confidence meters, identified symptoms, environmental causes, organic treatments, chemical interventions, and mandatory pesticide safety notices.
- **Low-confidence / inconclusive leaf handling** with guidance for re-sampling under diffuse daylight or consulting extension officers.
- **Dynamic crop selection** (calibrated per-crop neural models, e.g., Wheat, Cotton, Rice, Maize, Tomato, Sugarcane, Chili).
- **Micro-climate weather and spraying advisories** (Open-Meteo API integration with spray window alarms, fungal pathogen risk alerts, and chemical drift prevention).
- **Soil health analysis & NPK scorecard** (nutrient status, pH reaction, moisture adequacy, and tailored fertilizer plans including Urea, DAP, and SOP).
- **Local field records and history** backed by an offline **Room database**.
- **Settings & Backend management** (dynamic FastAPI base URL configuration with a built-in connection tester).
- **Bilingual localization** (English and Urdu / اردو).

---

## Architecture & Project Structure

The project strictly follows MVVM and repository patterns with a replaceable API service layer:

```
app/src/main/java/com/example/
├── MainActivity.kt                         # Single activity with Edge-to-Edge
├── data/
│   ├── model/                             # Domain & Network data classes
│   │   ├── Crop.kt                         # Dynamic crop definition
│   │   ├── DiagnosisResult.kt              # Leaf pathology result & status
│   │   ├── WeatherAdvisory.kt              # Microclimate conditions & risks
│   │   ├── SoilAnalysis.kt                 # NPK, pH, moisture & fertilizer plans
│   │   └── HealthResponse.kt               # Health check and model metadata
│   ├── local/                             # Persistence & preferences
│   │   ├── AppDatabase.kt                  # Room database singleton
│   │   ├── HistoryDao.kt                   # Scans and soil test queries
│   │   ├── HistoryEntity.kt                # ScanRecordEntity & SoilRecordEntity
│   │   └── AppPreferences.kt               # SharedPreferences for API URL & settings
│   ├── remote/                            # Retrofit & OkHttp networking
│   │   ├── SmartAgricultureApi.kt          # Retrofit endpoints (FastAPI contract)
│   │   └── ApiClient.kt                    # OkHttp & Moshi builder
│   └── repository/                        # Clean repository layer
│       ├── CropRepository.kt               # Interface, MockCropRepository & ApiCropRepository
│       ├── DiagnosisRepository.kt          # Interface, MockDiagnosisRepository & ApiDiagnosisRepository
│       ├── WeatherRepository.kt            # Interface, MockWeatherRepository & ApiWeatherRepository
│       ├── SoilRepository.kt               # Interface, MockSoilRepository & ApiSoilRepository
│       ├── HistoryRepository.kt             # Room DB local records repository
│       └── RepositoryProvider.kt           # Central factory swapping Mock <-> API
└── ui/
    ├── navigation/
    │   ├── Screen.kt                       # Type-safe routes
    │   └── NavGraph.kt                     # NavigationHost & bottom bar logic
    ├── theme/
    │   ├── Color.kt                        # Agricultural Material 3 palette
    │   ├── Theme.kt                        # Light/Dark ColorScheme
    │   └── Type.kt                         # Inter typography scale
    ├── components/
    │   ├── AppTopBar.kt                    # Common header with status chips
    │   ├── AppBottomNavBar.kt              # Bottom navigation (Home, Scan, Advisories, History)
    │   ├── StatusBadge.kt                  # Semantic severity badges
    │   └── LeafGuideReticle.kt             # Animated viewfinder reticle
    └── screens/
        ├── splash/                         # Splash screen & DB seeding
        ├── home/                           # Dashboard & quick actions
        ├── crops/                          # Crop selection sheet & Crop guide
        ├── scan/                           # CameraX scanner, photo picker, image preview, analyzing
        ├── diagnosis/                      # Diagnosis detail & low confidence flow
        ├── weather/                        # Weather & spraying advisory
        ├── soil/                           # Soil input form & nutrient scorecard
        ├── history/                        # Filterable scan & soil records
        └── settings/                       # API URL editor & connection tester
```

---

## Replaceable API Service Layer (Python FastAPI Backend)

The application communicates with backend endpoints through `SmartAgricultureApi`:

### Expected Backend Endpoints:
1. `GET /health` → Returns `{ "status": "ok", "service": "Smart Agriculture API", "version": "1.0.0", "active_models": 5 }`
2. `GET /crops` → Returns a list of active crops and supported disease models.
3. `GET /crops/{crop_id}` → Returns detailed agronomic parameters for a specific crop.
4. `GET /models/{crop_id}/status` → Returns training accuracy and active status.
5. `POST /predictions` → Accepts `multipart/form-data` with `image` (binary file) and `crop_id` (string), returning a `DiagnosisResult`.
6. `POST /soil/analyze` → Accepts JSON `{ "crop_id", "nitrogen", "phosphorus", "potassium", "ph", "moisture_pct" }` and returns nutrient scorecards and fertilizer dosage.
7. `POST /weather/live-advisory` → Accepts `{ "crop_id", "latitude", "longitude", "purpose" }` and returns microclimate risk analysis.

### Connecting to your Real FastAPI Server:
1. Open the application and tap the profile/settings icon in the top right.
2. In the **Backend API Configuration** card, enter your server URL (e.g., `http://192.168.1.100:8000` or your deployed cloud address).
3. Tap **Test Connection** to execute a test `GET /health` request.
4. Toggle off **Use Mock Data & Repositories** to activate the live `Api*Repository` instances.
5. All UI screens immediately consume the live backend without changing a single line of Compose code.

---

## Local Persistence & Hardware
- **Local History:** All diagnosed leaf scans and soil records are saved into the local SQLite database via Android Room (`smart_agriculture.db`).
- **CameraX:** Built-in live camera preview with flash toggle, 3x3 grid alignment, and stylized organic leaf reticle.
- **Photo Picker:** Modern zero-permission Android Photo Picker for importing leaf images from the device gallery.

---

## Build & Run in Android Studio
1. Export or download this repository as a ZIP.
2. Open **Android Studio** (Ladybug / Iguana or later).
3. Select **File > Open** and choose the root folder of this project.
4. Allow Gradle to sync dependencies automatically.
5. Run on an Android device or emulator running Android 7.0+ (API 24 to 36).
