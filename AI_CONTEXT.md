# AI context

Teenage Money is a playful offline-first money dashboard prototype. It currently runs as a Vite/React browser app, not a native Android app.

Important files are `src/main.jsx`, `src/styles.css`, and `src/finance.js`. The app has Home, Activity, Goals, and Insights views, Add Money, expense logging, local balance updates, and a generated receipt overlay. Transactions and money-space balances persist under `tm-transactions` and `tm-spaces` in localStorage.

Do not reintroduce seeded financial data, fake balances, fake charts, cloud financial storage, analytics, or bank integrations. All displayed financial values must derive from the current local state. Use `pnpm test` and `pnpm build` after changes.

Known limitations: no Kotlin/Compose module, no APK generation, no onboarding/profile persistence, no edit/delete transaction flow, no encrypted storage, no automated browser tests, and no GitHub remote configured. The next production step is a deliberate native migration with a shared domain model, Room persistence, Keystore protection, and Android UI/instrumentation tests.

