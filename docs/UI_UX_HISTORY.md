# Teenage Money UI/UX history

## TM-CHANGE-000006 — Active-surface cleanup

- **Base version:** `1.0.0`
- **Target version:** Proposed `1.1.0`
- **Status:** In progress
- **Date:** 2026-10-26
- **Screens:** Home, Activity, Profile, navigation, and app packaging
- **Removed:** Unreachable Goals/Insights UI, goal creation/contribution sheets, obsolete Home goal callbacks/card, and duplicate browser/WebView assets.
- **Kept:** Home, Activity, Profile, onboarding, receipt, expense logging, add money, spaces, export, privacy, and receipt feedback. Legacy goal records remain readable/exportable for compatibility.
- **Rationale:** Reduce cognitive load and maintenance surface to the working product paths without deleting persisted financial data.
- **Verification:** `testDebugUnitTest`, `lintDebug`, and `assembleDebug` passed. Debug APK SHA-256: `B9529DAF3F073926C7DFF096FAEC20634C3CD01B190DEFDFDAD0E8EEACC3C08C`. Device/emulator acceptance remains outstanding.

## TM-CHANGE-000005 — Working data and action cleanup

- **Base version:** `1.0.0`
- **Target version:** Proposed `1.1.0`
- **Status:** In progress
- **Date:** 2026-10-26
- **Screens:** Activity, expense sheet, Profile, receipt detail, database upgrade path
- **Previous behavior:** Activity exposed an inert search glyph; the expense sheet used the selected category as the transaction title; Profile included a decorative money-tree panel and a no-op amount action; several financial values bypassed privacy masking; upgrades could delete local tables.
- **New behavior:** Activity exposes a working Log expense action; expenses capture a user-entered description; Profile presents stored space/transaction facts; receipt, Activity, and Profile values honor the amount mask; database upgrades preserve stored data.
- **Rationale:** Make the prototype useful as a real local money tracker, with every visible action connected to behavior and every financial value derived from persisted state.
- **Accessibility:** The expense sheet scrolls vertically for smaller screens and keyboard states; the Activity action retains a minimum 48dp target; amount masking is applied across the visible financial surfaces.
- **Data impact:** Existing SQLite data is preserved. Version 2 upgrades add `epoch_ms` only when missing; no balances, transactions, goals, or preferences are deleted.
- **Implementation:** `TeenageMoneyApp.kt`, `MainScreens.kt`, `Sheets.kt`, `ReceiptDialog.kt`, `TeenageMoneyDbHelper.kt`, `Dimens.kt`, and onboarding/profile identity copy.
- **Verification:** `testDebugUnitTest`, `lintDebug`, `assembleDebug`, `pnpm test`, and `pnpm build` passed. Debug APK SHA-256: `E6C6D310C897F700963D8344916DECB885BEE982AFC908F21C00C4C320B13B4A`. Device/emulator visual and persistence verification remains outstanding.

## TM-CHANGE-000004 — Home chrome and navigation simplification

- **Base version:** `1.0.0`
- **Target version:** Proposed `1.1.0`
- **Status:** In progress
- **Date:** 2026-10-22
- **Screens:** Home header, shared bottom navigation, shared color system
- **Previous behavior:** Home still showed a date/greeting header and the bottom navigation included icon glyphs and extra selection decoration.
- **New behavior:** Home uses the stacked lowercase wordmark with a 44dp avatar action; navigation is reduced to plain-text Home and Activity items as specified.
- **Rationale:** Keep attention on money left and remove chrome that competes with the core task.
- **Accessibility:** Both navigation items retain tab semantics and 48dp minimum touch areas; the avatar retains a spoken Profile action label.
- **Data impact:** None.
- **Implementation:** `DashboardComponents.kt` and `Color.kt`.
- **Verification:** `lint`, `testDebugUnitTest`, and `assembleDebug` passed. Manual device/emulator verification remains outstanding.

## TM-CHANGE-000003 — UI/UX alignment and navigation correction

- **Base version:** `1.0.0`
- **Target version:** Proposed `1.1.0`
- **Status:** In progress
- **Date:** 2026-10-13
- **Screens:** Home, Activity, Profile, shared theme/navigation
- **Previous behavior:** Home defaulted to Month mode and still displayed the removed goal section; the bottom bar exposed Goals and Profile; Profile retained the bottom bar; Activity used the earlier card-based money-trail layout.
- **New behavior:** Home defaults to Week, the removed Home goal section is hidden, bottom navigation contains only Home and Activity, Profile has its own back action, Activity presents life perspective and grouped history without the old container card, and the shared palette follows the documented warm beige/orange system.
- **Rationale:** Restore the documented product hierarchy: spending money left first, calm activity history second, and Profile as an avatar-opened destination rather than a tab.
- **Accessibility:** Existing transaction semantics and navigation touch targets remain in place; the 75-year perspective is informational and not color-only because it is paired with labels and copy.
- **Data impact:** None. The UI reads existing persisted transactions and date-of-birth data only.
- **Implementation:** `MainScreens.kt`, `TeenageMoneyApp.kt`, `DashboardComponents.kt`, and `Color.kt`.
- **Verification:** `lint`, `testDebugUnitTest`, and `assembleDebug` passed. Manual device/emulator verification remains outstanding.

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
