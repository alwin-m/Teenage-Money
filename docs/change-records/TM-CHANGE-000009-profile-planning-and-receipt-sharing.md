# TM-CHANGE-000009 — Profile planning and receipt sharing

- **Date:** 2026-11-03, Asia/Calcutta
- **Author:** Codex, for Alwin Madhu / © j_e_e_n._
- **Base version:** `1.0.0`
- **Target version:** `1.1.0`
- **Affected components:** Profile salary planning, About metadata, receipt rendering/sharing, Code 128 barcode, balance-correction refresh, version metadata, and tests.
- **Category:** UI, UX, feature, accessibility, data, release traceability.
- **New behavior:** Saving monthly salary opens a bottom-sheet percentage planner. The plan must total 100% and shows calculated amounts without changing persisted money spaces. Triple-tapping About this app opens author/watermark information. Receipt sharing creates a PNG with receipt identity, amount, date/time, saved-success message, remaining balance visibility choice, and a scannable Code 128 barcode. A profile photo can be selected from local device storage and is reused on Home and Profile. Home Week and Month views can browse prior periods while opening on the current period.
- **Synchronization:** Profile name and avatar URI remain shared `UserProfile` state; balance corrections now refresh both spaces and transactions; salary allocation preferences are read back from local storage.
- **Data and privacy:** No network or new broad permission is added. Shared receipt images use a cache file exposed only through a non-exported `FileProvider`; the user can hide the remaining balance before sharing.
- **Version:** `versionName 1.1.0`, `versionCode 2`.
- **Verification:** `./gradlew.bat testDebugUnitTest lintDebug assembleDebug assembleRelease` passed; 12 unit tests passed with 0 failures/errors and lint reported 36 non-blocking warnings. Build ID: `1.1.0-20261103.1151`. Debug APK SHA-256: `071ED06EBD04EDF26E9D57342DD0EF5902A48EA707AF964B8760124C07E7EFCF`. Release APK SHA-256: `DDB8CC2CB19293933A5E24743DBA53BB93723B69C946AE13481D36272DB8CC70`. Device/emulator verification remains outstanding because `adb` is unavailable.
