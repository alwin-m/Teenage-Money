# Code review

## Licensing and ownership audit

- No project-owned open-source license file is present.
- `package.json` is marked private.
- JavaScript dependencies have been inventoried with `pnpm licenses list`.
- Android/Gradle dependencies remain under their upstream licenses; no notices were removed.
- `android/local.properties`, build directories, APKs, and signing artifacts are now ignored to prevent accidental commits.
- The Android wrapper had an unnecessary `INTERNET` permission for bundled offline assets; it was removed.

## Findings

| Severity | Area | Finding | Status |
| --- | --- | --- | --- |
| High | Platform | Workspace is Vite/React, not an Android/Gradle project, so APK requirements cannot be met here | Open, documented |
| High | Storage | `localStorage` is unencrypted and browser-only | Open, native migration required |
| Medium | Domain | UI state still contains some domain calculations that should move fully into `finance.js` | Partially improved |
| Medium | Transactions | Edit, delete, date/time selection, and multi-space expense selection are not implemented | Open |
| Low | Maintainability | Most UI is in one JSX file | Open; preserve current behavior before extraction |

## Completed fixes

- Removed seeded demo balances and seeded transactions.
- Added persistent local state for transactions and money spaces.
- Added positive amount and insufficient-balance validation.
- Added pure financial helper functions and executable tests.
- Removed fake income from the Activity screen.
- Added a production build verification command.

