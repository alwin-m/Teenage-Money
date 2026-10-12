# Teenage Money UI/UX history

## TM-CHANGE-000002 — Profile screen refinement

- **Base version:** `1.0.0`
- **Target version:** Proposed `1.1.0`
- **Status:** In progress
- **Screen:** Profile
- **Previous behavior:** Profile showed salary, receipt sound text toggle, export, summary metrics, category totals, and build information, but did not offer local name editing or a dedicated privacy section.
- **New behavior:** Profile now presents a personal identity card, local edit action, functional amount-masking switch, standard sound switch, data export, summaries, and About information.
- **Rationale:** Give users understandable control over their identity and privacy without introducing accounts, cloud dependencies, or unsupported settings.
- **Accessibility:** Material switches expose state; edit uses a standard text button; content remains vertically scrollable for small screens and large font scales.
- **Visual continuity:** Existing warm off-white background, rounded white cards, charcoal/coral palette, editorial typography, and bottom navigation are retained.
- **Data impact:** None to schema or financial records. The existing profile name preference is updated only after explicit save.
- **Implementation:** `MainScreens.kt`, `TeenageMoneyApp.kt`, and `Sheets.kt`.
- **Verification:** `pnpm test`, `pnpm build`, `testDebugUnitTest`, `lint`, `assembleDebug`, and `assembleRelease` passed. Manual emulator/screenshot verification remains outstanding.
