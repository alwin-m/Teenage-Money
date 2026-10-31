# Financial logic

- Add Money increases the selected logical space and records an income activity.
- Logging an expense requires a positive amount and enough Spending balance.
- An expense decreases Spending immediately and creates a transaction record.
- The receipt uses the post-expense Spending balance.
- Spending totals exclude income records.
- `TeenageMoneyRepository.kt` owns money validation, balance updates, Indian currency formatting, weekly/monthly totals, and local persistence coordination.

Known limitation: editing/deleting transactions and multi-space expense selection are not yet implemented in the native app.

