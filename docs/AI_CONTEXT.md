# AI context

Teenage Money is a playful offline-first personal finance application for Android, built natively using Kotlin and Jetpack Compose.

The project is private proprietary software owned by Alwin Madhu. Never add an open-source license, publish the repository, or describe the project as open source. See `PROPRIETARY_NOTICE.md`, `THIRD_PARTY_NOTICES.md`, and `LICENSING_AUDIT.md`.

Important native components are located in `android/app/src/main/java/com/teenagemoney/app/`:
- `MainActivity.kt`: Native Activity entry point hosting Compose.
- `ui/TeenageMoneyApp.kt`: Root composable with state management and dialogs.
- `ui/screens/SplashScreen.kt`: Clean splash screen routing to onboarding or home.
- `ui/screens/OnboardingScreen.kt`: Conversational 3-step offline onboarding with dynamic greeting, native DatePickerDialog, dynamic age calculation, and status selection.
- `ui/screens/MainScreens.kt`: Home, Activity, and Profile views.
- `ui/components/`: OrbitVisual canvas, DashboardComponents, Sheets (Quick log expense, Add money, spaces, profile), and ReceiptDialog thermal receipt overlay.
- `data/TeenageMoneyDbHelper.kt`: Durable local SQLite persistence (`teenagemoney.db`).
- `data/TeenageMoneyRepository.kt`: Local financial calculations, space allocations, and offline state.

The application has zero internet dependency, zero remote APIs, zero cloud SDKs, and zero WebView. All user data, preferences, transactions, and spaces are stored locally on the device.
