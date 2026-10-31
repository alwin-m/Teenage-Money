# Code review

## Licensing and ownership audit

- No project-owned open-source license file is present.
- The repository contains no package manager manifest or JavaScript runtime; the active product is native Android.
- Android/Gradle dependencies remain under their upstream licenses; no notices were removed.
- `android/local.properties`, build directories, APKs, and signing artifacts are now ignored to prevent accidental commits.
- The native app has no network permission and does not load web assets.

## Findings

| Severity | Area | Finding | Status |
| --- | --- | --- | --- |
| High | Platform | Device/emulator acceptance is unavailable because `adb` is not accessible in this environment | Open, documented |
| Medium | Storage | Local SQLite data is not encrypted at rest | Open, requires a separately scoped security change |
| Medium | Domain | Some calculations remain in the UI layer and should move to repository helpers over time | Open, preserve current behavior |
| Medium | Transactions | Edit, delete, date/time selection, and multi-space expense selection are not implemented | Open |
| Low | Maintainability | Most UI is in one JSX file | Open; preserve current behavior before extraction |

## Completed fixes

- Removed seeded demo balances and seeded transactions.
- Added persistent local state for transactions and money spaces.
- Added positive amount and insufficient-balance validation.
- Added repository-backed financial helpers and JVM tests.
- Removed fake income from the Activity screen.
- Added a production build verification command.

