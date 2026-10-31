# Data storage

The active application uses the local SQLite database `teenagemoney.db` through `TeenageMoneyDbHelper` and coordinates reads/writes through `TeenageMoneyRepository`.

- Preferences store onboarding, profile, privacy, sound, salary, and custom-category settings.
- Spaces store persisted balances.
- Transactions store income, expense, receipt, and timestamp records.
- Legacy goal rows remain readable/exportable for compatibility even though goal creation is not part of the current navigation.

The app has no server or external financial API. SQLite is not encrypted at rest; encryption is a separate security project and is not being introduced as an unverified cleanup shortcut. Database upgrades must preserve existing user rows.

