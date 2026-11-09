# Teenage Money Home-Screen Widget Architecture

## Implementation Overview

The native Android app exposes a responsive home-screen widget named `Teenage Money` and a lightweight quick-entry transaction activity (`QuickExpenseActivity`).

### 1. Multi-Size Responsive Widget
The widget adapts to different home screen sizes and launcher dimensions:

- **Compact (2x1 / 2x2):**
  - Current date and Teenage Money brand indicator
  - Prominent Spending balance
  - Financial health position indicator (`● Healthy`, `● On track`, `● Careful`, `● Low funds`)
  - Direct `＋ Add` quick-action button
- **Medium (3x2 / 4x1):**
  - Brand header and current date
  - Spending balance (primary visual hero)
  - Weekly Income metric card (`+₹...` in Mint)
  - Weekly Expenses metric card (`−₹...` in Coral)
  - Financial position badge
  - Direct `＋ Add` quick-action button
- **Expanded (4x2 / 4x3):**
  - Comprehensive financial overview with date and financial status
  - Spending balance with clear label
  - Weekly Income and Expense split metric cards
  - Recent activity list (up to 3 latest transactions with category icons, titles, and amounts)
  - Direct `＋ Add` quick-action button

On Android 12+ (API 31+), the widget uses size-mapped `RemoteViews(mapOf(...))` for automatic system size responsiveness. On earlier versions, `onAppWidgetOptionsChanged` dynamically selects the layout based on `OPTION_APPWIDGET_MIN_WIDTH` and `OPTION_APPWIDGET_MIN_HEIGHT`.

### 2. Lightweight Quick Transaction Entry
Tapping `＋ Add` on any widget size launches `QuickExpenseActivity` without navigating into the main application:
- Translucent, bottom-sheet style activity (`Theme.TeenageMoney.QuickExpense`) with `excludeFromRecents="true"`.
- Segmented switch between **Expense** (Coral accent) and **Income** (Mint accent).
- 4-row numeric keypad with tactile haptic clicks on each keystroke.
- Category chips with emojis (Food ☕, Transport ⚡, Shopping 🛍, Education ✎, Entertainment ★, Allowance 🎁, Salary 💼, Freelance 💻, Gift ✨, Others ✦).
- Space destination selector when adding income (Spending, Savings, Emergency).
- Optional description field.
- Animated confirmation visual checkmark with updated spending balance before automatically returning to the launcher.

### 3. Real-Time Data Synchronization
- Single source of truth: Local SQLite database (`teenage_money.db`).
- Every transaction logged via widget or main app writes directly to the local database and triggers `TeenageMoneyWidget.refreshAll(context)`.
- Respects the app's privacy masking mode (`isPrivacyMode()`).
- 100% offline-first: zero network permissions, zero analytics, zero external dependencies.
