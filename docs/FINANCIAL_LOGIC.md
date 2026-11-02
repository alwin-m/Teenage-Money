# Financial logic

- Add Money increases the selected logical space and records an income activity.
- Logging an expense requires a positive amount and enough Spending balance.
- An expense decreases Spending immediately and creates a transaction record.
- The receipt uses the post-expense Spending balance.
- Spending totals exclude income records.
- Profile balance correction updates the selected space and records the manual correction without changing the existing expense totals.
- `TeenageMoneyRepository.kt` owns money validation, balance updates, Indian currency formatting, weekly/monthly totals, and local persistence coordination.

Known limitation: editing/deleting individual transaction details and multi-space expense selection are not yet implemented in the native app. Profile balance correction is available for correcting the persisted space balance.

