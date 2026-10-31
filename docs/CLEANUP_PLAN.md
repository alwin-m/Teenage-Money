# Cleanup plan and record

Date: 2026-10-26

## Goal

Keep one working product surface: the native offline-first Android Compose app. Remove duplicate runtimes, unreachable UI, stale build paths, and misleading documentation without deleting persisted financial data.

## Phases

1. **Inventory** — identify the Android entry point, reachable destinations, active data paths, generated artifacts, duplicate runtimes, and stale documentation.
2. **Remove duplicate runtime** — take the unused Vite/React prototype, Node dependencies, browser tests, root browser entry point, and copied Android web assets out of the active workspace.
3. **Remove unreachable UI** — remove Goals and Insights screens, goal creation/contribution sheets, unused Home goal callbacks, and the obsolete goal card from the active Compose UI.
4. **Preserve data compatibility** — keep legacy goal rows and export reads so existing local data is not deleted or silently discarded. Keep SQLite migrations non-destructive.
5. **Align documentation** — update architecture, navigation, build, security, data, licensing, release, and component docs to describe only the native app.
6. **Verify** — run Kotlin tests, lint, debug assembly, inspect the diff, and report device/emulator limits separately.

## Removed from the active workspace

- Root Vite/React prototype: browser entry point, `src/`, `tests/`, `public/`, package manifest, lockfile, and Vite config.
- Copied browser assets under `android/app/src/main/assets/`.
- Duplicate root APKs and the unreferenced logo preview image.
- Unreachable Compose Goals and Insights UI.
- Goal creation/contribution UI and the obsolete Home goal card.
- Stale browser/WebView descriptions in project documentation.

The removed web material is recoverable at `D:\m2m_cleanup_archive_20261026` and is not part of the Android build.

## Intentionally kept

- Home, Activity, Profile, onboarding, splash, receipt, spaces, add money, expense logging, categories, export, privacy masking, and receipt feedback.
- SQLite profile, spaces, transactions, preferences, and legacy goal storage/export compatibility.
- Android launcher resources, Compose theme, repository logic, JVM tests, Gradle wrapper, and release traceability.

## Deferred, not silently removed

- Transaction edit/delete.
- SQLite encryption at rest.
- Android instrumentation/device acceptance tests.
- Any new feature that would require a new source of financial truth.
