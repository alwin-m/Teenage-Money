# TM-CHANGE-000010 — Home-screen widget and quick transaction entry

- **Base version:** `1.1.0`
- **Status:** Complete
- **Date:** 2026-11-09
- **Surface:** Android home screen widget and quick transaction overlay
- **Design intent:** Extend the existing Teenage Money visual language to a responsive home-screen widget with direct transaction entry without creating a second financial data path.
- **Behavior:**
  - Multi-size responsive widget support: Compact (2x1), Medium (3x2), and Expanded (4x2/4x3).
  - Displays current available balance, weekly income, weekly expenses, financial health indicator (`● Healthy`, `● On track`, `● Careful`, `● Low funds`), current date, and recent activity.
  - Dedicated `QuickExpenseActivity` overlay: Bottom sheet modal supporting both Expense and Income logging, numeric keypad with tactile haptics, category selection chips with icons, destination space selection for income, and subtle visual confirmation before dismissing directly back to the home screen.
- **Data impact:** Zero schema changes. Direct persistence to `teenage_money.db` SQLite database using `TeenageMoneyRepository`. Automatic real-time widget refresh on all transaction and balance modifications. Zero cloud/network permissions.
- **Accessibility:** Content descriptions on all buttons; respects privacy masking mode; high-contrast typography; full RTL support.
- **Verification:** Unit tests passed (`testDebugUnitTest`), debug APK assembled (`assembleDebug`), and release APK assembled (`assembleRelease`).
