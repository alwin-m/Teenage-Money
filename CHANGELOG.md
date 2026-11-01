# Teenage Money changelog

This is the cumulative product ledger. Entries describe changes that are present in the repository and are intended to make an APK identifiable without relying on memory or chat history.

## Unreleased — 2026-11-01 UI/UX Modernization

### Changed

- Modernized fintech UI/UX with elevated card-based surfaces, subtle outlines, and refined dark/light color palette.
- Updated typography tokens, spacing hierarchy, and consistent corner radius scaling across components.
- Polished dashboard components, quick balance cards, activity list styling, and bottom navigation bar.

## Unreleased — proposed 1.1.0 — 2026-10-26 cleanup

### Removed

- Removed the unused browser/Vite prototype, Node dependency tree, browser tests, copied Android web assets, root APK duplicates, and unreferenced logo preview from the active workspace. Recoverable copies are archived at `D:\m2m_cleanup_archive_20261026`.
- Removed unreachable Goals and Insights screens, goal creation/contribution sheets, Home goal callbacks, and the obsolete goal card from the active Compose UI.
- Removed unused goal mutation and transaction deletion methods that had no reachable product action.

### Preserved

- Kept legacy goal rows and export reads so cleanup does not delete existing local goal data.
- Kept the SQLite schema and non-destructive migration path for profile, spaces, transactions, preferences, and compatibility records.

### Documentation

- Updated architecture, navigation, security, database, release, licensing, component, and testing docs to describe the native Android app only.
- Added [docs/CLEANUP_PLAN.md](docs/CLEANUP_PLAN.md) with the cleanup scope, retained capabilities, and deferred work.

### Verification

- `./gradlew.bat testDebugUnitTest lintDebug assembleDebug` passed.
- Debug APK SHA-256: `B9529DAF3F073926C7DFF096FAEC20634C3CD01B190DEFDFDAD0E8EEACC3C08C`.
- Device/emulator acceptance remains unverified because `adb` is unavailable.

## Unreleased — proposed 1.1.0 — 2026-10-26

### Changed

- Made Activity's primary action functional by opening the real expense logger instead of showing an inert search control.
- Added a real expense description field so saved transaction titles reflect what the user entered, while preserving category selection and SQLite persistence.
- Replaced Profile's decorative money-tree panel and inert amount control with a local snapshot sourced from stored spaces and transactions.

### Data and privacy

- Applied amount masking consistently to Activity totals, Profile totals/category summaries, and receipt details.
- Profile's spending-left value now reads only from the persisted Spending space; savings and emergency balances are not presented as spendable money.
- Replaced the destructive database upgrade reset with a preserving migration for the transaction timestamp column.
- Removed the hard-coded Alex identity fallback and use a neutral name placeholder until the user provides their name.

### Accessibility and resilience

- Kept the expense sheet vertically scrollable so the description field and keypad remain reachable on smaller screens and with the keyboard open.
- Preserved 48dp action targets for the new Activity action and existing controls.

### Verification

- `./gradlew.bat testDebugUnitTest lintDebug assembleDebug`
- `pnpm test`
- `pnpm build` (completed with the installed Node/Vite compatibility warning)
- Debug APK SHA-256: `E6C6D310C897F700963D8344916DECB885BEE982AFC908F21C00C4C320B13B4A`
- Manual device/emulator verification remains required for visual layout, keyboard, navigation, and persistence acceptance.

## Unreleased — proposed 1.1.0 — 2026-10-22

### Changed

- Replaced the Home date/greeting header with the documented stacked lowercase `teenage money` wordmark and profile avatar action.
- Simplified the bottom navigation to the documented plain-text Home and Activity destinations.
- Kept the shared warm-beige, taupe, near-black, and deep-orange UI palette consistent across the main screens.

### Verification

- `./gradlew.bat lint testDebugUnitTest assembleDebug`
- Debug APK SHA-256: `E231E9DBA82BA43212D49BCA359F402E5BEA1DAC19670ACAAC610F635F3D11DE`

## Unreleased — proposed 1.1.0 — 2026-10-13

### Changed

- Aligned Home navigation with the product direction: Week is the default view, the removed goal section is no longer rendered, and only Home and Activity remain in the bottom bar.
- Profile is now a separate destination without a bottom bar and has a functional back action.
- Reworked Activity into a borderless, calm history view with days-lived/days-ahead context, a 75-year perspective row, grouped day totals, and a clear empty state.
- Updated the shared light palette to the documented warm beige canvas, near-black text, taupe secondary text, and deep-orange action color.

### Data and Storage

- No schema, transaction, balance, or persisted-data changes.
- Activity perspective values are calculated from the stored date of birth and current date.

### Accessibility

- Preserved semantic transaction rows and existing touch-target behavior while removing unreachable Profile/Goals navigation affordances.

### Verification

- `./gradlew.bat lint testDebugUnitTest assembleDebug`
- Debug APK SHA-256: `5190FB2B03A0B1B7D861D69D0C808064F509727D134383970FE2C510356BB59F`
- Debug build output: `android/app/build/outputs/apk/debug/app-debug.apk`

### Known limitation

- Device/emulator visual acceptance remains outstanding because no Android device is connected.

## Unreleased — proposed 1.1.0

### Added

- Local Profile editing with persisted display-name updates.
- A Profile identity card with initials and local-storage context.
- A Privacy section with a functional Hide financial amounts switch.

### Changed

- Reordered Profile content into identity, preferences, privacy, metrics, category summary, and About sections.
- Replaced the receipt sound text toggle with a proper switch control.
- Clarified Profile copy as “Your space, your way.” and kept technical build details inside About this app.

### Data and Storage

- No schema, migration, transaction, balance, goal, or money-space changes.
- Display-name edits reuse the existing local profile preference storage.

### Accessibility

- Replaced text-only preference toggles with Material switches that expose checked state semantics.
- Kept the edit action and settings controls in standard Compose touch-target components.

### Documentation

- Added change record `TM-CHANGE-000002` and UI/UX history for this work.

### Release status

- In progress; not released as `1.1.0` yet.
- Release-candidate build identifier: `1.0.0-20261012.2355`
- Release-candidate APK SHA-256: `1D01721EAFF007FD49D8AD9DACF9A35E5445C731A33AF6251FF67B1C781092B6`

## 1.0.0 — Shipping version — 2026-10-12

### Product and UI/UX

- Native Android application with Jetpack Compose screens for Home, Activity, Goals, and Profile.
- Preserved the existing identity: Teenage Money wordmark, greeting/date header, charcoal orbital balance card, coral expense action, rounded surfaces, and bottom navigation.
- Refined Home hierarchy to: header, balance, Log expense, weekly rhythm, recent activity, spaces, and goal card.
- Clarified the balance card as `Available to spend` and stated that other spaces are excluded.
- Added weekly selected-day semantics and visible copy that identifies the selected date and amount spent.
- Kept Log expense as the single dominant Home action.
- Added privacy masking for amounts with local persistence.

### Money, data, and behavior

- Preserved the offline-first local SQLite data model and existing money spaces.
- Preserved existing navigation routes and back behavior.
- Preserved expense, income, balance correction, savings goal, export, and receipt flows.
- Money formatting preserves paise and Indian grouping, including values such as `₹99.50` and `₹12,34,567.89`.

### Accessibility and resilience

- Added or retained meaningful semantics for icon/action controls, weekly day selection, transactions, and bottom navigation.
- Kept touch targets at or above the existing 48dp minimum where the refined controls are involved.
- Added reduced-motion handling to app transition feedback.
- Kept the app local-only: no accounts, network calls, analytics, push notifications, or notification permission flow.

### Release traceability

- Added an in-app Profile > About this app section showing the public version and timestamped internal build identifier.
- Added `VERSIONING.md` with serial version rules, build stamp format, and release checklist.
- Added this cumulative changelog.

### Verification

- `pnpm test`
- `pnpm build`
- `./gradlew.bat lint testDebugUnitTest assembleDebug`
- `./gradlew.bat assembleRelease`
- Release build identifier: `1.0.0-20261012.2321`
- Release APK SHA-256: `F6B12E3A55EAC75EDC1D421A55773FF9DCD48C3A43C833CA84E994FB89999131`

### Known release limitation

- The current release APK uses the repository's debug signing configuration and is suitable for sideload/device testing, but it is not yet signed with a production Play Store upload key.

## Repository history before the 1.0.0 ledger

The current repository state does not contain an older formal release ledger. The following baseline is reconstructed from the source and existing project documentation and is included so future entries have a complete starting point:

- Migrated the Android experience to native Compose screens and local repository-backed state.
- Added local SQLite persistence for profile, spaces, transactions, goals, categories, preferences, and exports.
- Added Home, Activity, Goals, and Profile flows with local navigation.
- Added receipt detail feedback and the local receipt sound/haptic preference.
- Added app assets, launcher resources, offline bundled assets, and release build configuration.
