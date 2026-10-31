# Build

The application is built natively for Android using Gradle and Android SDK.

- Minimum SDK: 26 (Android 8.0 Oreo)
- Target SDK: 36
- JVM Toolchain: Java 21
- Kotlin: 2.1.0
- Jetpack Compose BOM: 2024.10.01

## Build Commands

From `android/`:

```bash
# Run unit tests
./gradlew.bat testDebugUnitTest

# Run lint checks
./gradlew.bat lintDebug

# Build Debug and Release APKs
./gradlew.bat assembleDebug assembleRelease
```

The compiled APKs are generated under:
- `android/app/build/outputs/apk/release/app-release.apk`
- `android/app/build/outputs/apk/debug/app-debug.apk`
