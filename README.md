# Teenage Money

**Private proprietary software. Copyright © 2026 Alwin Madhu. All rights reserved.**

Teenage Money is not open source and must not be redistributed, relicensed, or published as a public repository. See [PROPRIETARY_NOTICE.md](PROPRIETARY_NOTICE.md) and [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).

A private local-first money dashboard with a Vite/React UI bundled into an Android WebView wrapper.

## Run locally

Install Node.js 18+ first, then:

```bash
npm install
npm run dev
```

Expense entries and money-space balances persist locally in the browser/WebView. The visual system is original: charcoal, clay, mint, and lavender surfaces with a small orbit illustration as the dashboard's living-world motif.

## Build Android APK

```bash
cd android
./gradlew.bat :app:assembleDebug :app:assembleRelease
```

APK outputs are written to `android/app/build/outputs/apk/`. The root APK copies are for handoff only and are ignored by Git.

## Included

- Home dashboard with spending balance, weekly rhythm, money spaces, recent activity, and a savings goal.
- Quick-log bottom sheet with amount, description, category, validation, and a real activity update.
- Activity, Goals, and Insights navigation states.
- Responsive layout for desktop and phone-sized screens.

## Native data-layer limitation

The current Android shell packages the web app locally. Financial records use WebView localStorage rather than Room/Keystore. A production-hardening step should migrate the data layer to native encrypted storage. Money spaces remain logical allocations, not bank accounts.
