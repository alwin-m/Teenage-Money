<p align="center">
  <img src="android/app/src/main/res/drawable/logo_teenage_money.png" alt="Teenage Money" width="120" />
</p>

<h1 align="center">Teenage Money</h1>

<p align="center">
  <strong>A privacy-first, offline personal finance app for teenagers and young people.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-34A853?logo=android&logoColor=white" alt="Platform: Android" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white" alt="Language: Kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white" alt="UI: Jetpack Compose" />
  <img src="https://img.shields.io/badge/Min%20SDK-26%20(Oreo)-green" alt="Min SDK: 26" />
  <img src="https://img.shields.io/badge/License-Proprietary-red" alt="License: Proprietary" />
</p>

<p align="center">
  Understand where your money goes · Track spending · Build better habits · Separate money by purpose · Save for what matters
</p>

---

## ✨ Why Teenage Money?

Most finance apps are built for adults with bank accounts, credit cards, and complex investments. **Teenage Money** is different — it's designed for young people who are just starting to manage their own money.

No sign-ups. No bank linking. No data collection. Just a clean, private space to understand your money.

---

## 🚀 Features

### 🔒 Privacy First
- **100% offline** — zero internet permissions, zero cloud, zero analytics
- All data stays on your device in a local SQLite database
- No accounts, no sign-ups, no tracking

### 💰 Smart Money Management
- **Money Spaces** — separate your money into Spending, Savings, and Emergency
- **Quick expense logging** — log what you spend in seconds
- **Weekly rhythm chart** — see your spending patterns day by day
- **Indian currency support** — proper ₹ formatting with paise precision

### 🎨 Thoughtful Design
- **Conversational onboarding** — friendly setup that feels like a chat, not a form
- **Orbital balance card** — a beautiful animated hero card showing your spending balance
- **Thermal receipt overlay** — satisfying receipt confirmation for every transaction
- **Privacy masking** — hide your amounts with one tap when others are around

### 📱 Native Android Experience
- Built entirely with **Kotlin** and **Jetpack Compose** — no WebView, no wrappers
- Smooth 60fps animations and transitions
- Adaptive launcher icon with proper density mipmaps
- Works in airplane mode, always

---

## 🏗️ Architecture

```
android/app/src/main/java/com/teenagemoney/app/
├── MainActivity.kt                    # Entry point
├── data/
│   ├── Models.kt                      # Data classes
│   ├── TeenageMoneyDbHelper.kt        # SQLite persistence
│   └── TeenageMoneyRepository.kt      # Business logic & validation
└── ui/
    ├── TeenageMoneyApp.kt             # Root composable & state
    ├── theme/                         # Colors, typography, theming
    ├── screens/
    │   ├── SplashScreen.kt            # Branded splash
    │   ├── OnboardingScreen.kt        # Conversational setup flow
    │   └── MainScreens.kt            # Home, Activity, Profile
    └── components/
        ├── OrbitVisual.kt             # Custom canvas animation
        ├── DashboardComponents.kt     # Hero card, rhythm chart, spaces
        ├── Sheets.kt                  # Bottom sheets for actions
        └── ReceiptDialog.kt           # Thermal receipt overlay
```

| Layer | Responsibility |
|-------|---------------|
| **UI** | Declarative Compose screens with custom `TeenageMoneyTheme` |
| **Domain** | Financial calculations, validation, currency formatting |
| **Data** | Local SQLite via `TeenageMoneyDbHelper`, SharedPreferences for settings |
| **Security** | Zero network permissions — verified in `AndroidManifest.xml` |

---

## 🛠️ Build & Run

**Prerequisites:** Android SDK, Java 21, Gradle

```bash
cd android/

# Run unit tests
./gradlew testDebugUnitTest

# Run lint analysis
./gradlew lintDebug

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

**APK outputs:**
- Debug: `android/app/build/outputs/apk/debug/app-debug.apk`
- Release: `android/app/build/outputs/apk/release/app-release.apk`

### Build Configuration

| Property | Value |
|----------|-------|
| Min SDK | 26 (Android 8.0 Oreo) |
| Target SDK | 36 |
| JVM Toolchain | Java 21 |
| Kotlin | 2.1.0 |
| Compose BOM | 2024.10.01 |

---

## 🗂️ Project Documentation

| Document | Description |
|----------|-------------|
| [`CHANGELOG.md`](CHANGELOG.md) | Cumulative product change ledger |
| [`SECURITY.md`](SECURITY.md) | Security model and privacy guarantees |
| [`VERSIONING.md`](VERSIONING.md) | Version scheme and release checklist |
| [`THIRD_PARTY_NOTICES.md`](THIRD_PARTY_NOTICES.md) | Third-party dependency licenses |
| [`docs/`](docs/) | Architecture deep-dives, UI/UX history, and change records |

---

## 🔐 Security & Privacy

Teenage Money takes privacy seriously:

- **No internet permission** — the app cannot make network requests, ever
- **No analytics or telemetry** — your usage is never tracked
- **No cloud sync** — your financial data never leaves your device
- **Local-only storage** — SQLite database + Android SharedPreferences
- **No third-party SDKs** — no ads, no Firebase, no crash reporters

> [!NOTE]
> Local SQLite data is not encrypted at rest. Treat the current build as a personal tracker. Encrypted storage is planned for a future release.

---

## 📋 Versioning

Teenage Money uses **semantic versioning** with build timestamps:

- **Public version:** `1.0.0` (shown in Profile → About this app)
- **Build ID:** `1.0.0-20261012.2321` (version + build timestamp)
- **Version code:** monotonically increasing Android package number

See [`VERSIONING.md`](VERSIONING.md) for the complete version scheme and release checklist.

---

## 📄 License

**Teenage Money is proprietary software.**

Copyright © 2026 [Alwin Madhu](https://orcid.org/0009-0008-2826-5082). All rights reserved.

This software is not open source. No permission is granted to copy, modify, redistribute, sublicense, or use this project without the owner's written permission. Third-party dependencies retain their respective upstream licenses as documented in [`THIRD_PARTY_NOTICES.md`](THIRD_PARTY_NOTICES.md).

See [`PROPRIETARY_NOTICE.md`](PROPRIETARY_NOTICE.md) for full details.

---

<p align="center">
  Made with ❤️ for young people taking control of their money
</p>
