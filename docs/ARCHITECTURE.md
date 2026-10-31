# Architecture

Teenage Money is a 100% native Android application built with Kotlin and Jetpack Compose.

## Core Layers

1. **UI Layer (`ui/`)**:
   - Declarative Jetpack Compose UI with custom `TeenageMoneyTheme`.
   - Screens: `SplashScreen`, `OnboardingScreen`, `HomeScreen`, `ActivityScreen`, and `ProfileScreen`.
   - Components: `SpendingHeroCard` with custom `OrbitVisual` canvas, `WeekRhythmCard`, `SpacesSection`, `RecentActivitySection`, `BottomNavBar`, modal sheets, and `ReceiptDialog` thermal receipt overlay.

2. **Domain & Data Layer (`data/`)**:
   - `TeenageMoneyRepository`: Business logic, validation, currency formatting (`₹`), and dynamic age calculation.
   - `TeenageMoneyDbHelper`: Durable offline SQLite persistence (`teenagemoney.db`) storing user profile, money spaces, transactions, and legacy goal records needed for compatibility/export.

3. **Offline-First Guarantee**:
   - Zero internet permissions in `AndroidManifest.xml`.
   - Zero cloud or server dependencies.
   - All state persists across application restarts and device reboots.
