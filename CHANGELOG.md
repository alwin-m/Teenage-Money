# Teenage Money changelog

This is the cumulative product ledger. Entries describe changes that are present in the repository and are intended to make an APK identifiable without relying on memory or chat history.

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
