# Testing

Run from `android/`:

```bash
./gradlew.bat testDebugUnitTest
./gradlew.bat lintDebug
./gradlew.bat assembleDebug
```

The current JVM test suite covers age calculation, receipt identifiers, Indian currency formatting, privacy masking, and current-week expense filtering. The Android app uses local SQLite persistence for balances and transactions.

Not yet available: Android instrumentation tests, native UI tests, persistence restart tests in an installed APK, device visual acceptance, and full edit/delete integration coverage.

