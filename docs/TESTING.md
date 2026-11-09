# Testing

Run from `android/`:

```bash
./gradlew.bat testDebugUnitTest
./gradlew.bat lintDebug
./gradlew.bat assembleDebug
```

The current JVM test suite covers age calculation, receipt identifiers, Indian currency formatting including lakh/crore grouping, privacy masking, salary allocation totals, Code 128 receipt encoding, and current-week expense filtering. The Android app uses local SQLite persistence for balances and transactions.

Not yet available: Android instrumentation tests, native UI tests, persistence restart tests in an installed APK, device visual acceptance, and full edit/delete integration coverage.

