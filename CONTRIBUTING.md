# Contributing

> **Teenage Money is proprietary software.** External contributions are not currently accepted. This guide is for internal reference.

## Development Setup

### Prerequisites
- Android SDK (API 26+)
- Java 21 (JDK)
- Kotlin 2.1.0
- Android Studio (recommended) or Gradle CLI

### Building
```bash
cd android/
./gradlew assembleDebug
```

### Testing
```bash
# Unit tests
./gradlew testDebugUnitTest

# Lint analysis
./gradlew lintDebug

# Full verification
./gradlew testDebugUnitTest lintDebug assembleDebug
```

## Code Standards

- **Kotlin** with Jetpack Compose for all UI
- **No WebView or web assets** — the app is 100% native
- **Offline-only** — never add network permissions or cloud dependencies
- **Privacy by default** — no analytics, telemetry, or data collection
- **Semantic versioning** — see [`VERSIONING.md`](../VERSIONING.md) for version rules

## Architecture Rules

1. Business logic belongs in `TeenageMoneyRepository`, not in composables
2. Database changes must preserve existing user data (no destructive migrations)
3. All financial calculations must be tested with JVM unit tests
4. UI must maintain 48dp minimum touch targets for accessibility

## Commit Messages

Follow conventional commit format:
```
feat: add weekly spending chart
fix: correct currency formatting for paise
docs: update build instructions
```
