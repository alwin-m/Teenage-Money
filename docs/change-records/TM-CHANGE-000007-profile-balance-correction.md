# TM-CHANGE-000007 — Profile balance correction

- **Date:** 2026-11-02, Asia/Calcutta
- **Author:** Codex
- **Base version:** `1.0.0`
- **Target version:** Proposed `1.1.0`
- **Affected components:** Profile Compose screen, existing balance correction sheet, local SQLite-backed space state, release documentation.
- **Category:** UI, UX, feature, financial behavior, documentation.
- **Previous behavior:** Profile showed the Spending balance but offered no Profile action to correct it when cash was lost or the real-world balance changed outside the app.
- **New behavior:** Profile > Local money snapshot exposes Edit spending balance. The action opens the existing confirmation sheet for the persisted Spending space.
- **Data impact:** No schema change. `correctSpaceBalance` updates the Spending space and records a manual balance-correction transaction; Home, Activity, receipts, and Profile continue to read refreshed repository state.
- **Visual impact:** Added one compact secondary action using the existing Profile card, colors, typography, spacing, and bottom-sheet pattern.
- **Security and privacy impact:** No new permissions, accounts, network calls, or external data sources.
- **Verification:** `testDebugUnitTest`, `lintDebug`, and `assembleDebug` pending for this change. Device/emulator acceptance remains unverified.
- **Release status:** In progress; proposed for `1.1.0`, not released.
