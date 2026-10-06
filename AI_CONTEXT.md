# AI context

Teenage Money is a playful offline-first money dashboard. It runs as a Vite/React app bundled into an Android WebView wrapper under `android/`.

The project is private proprietary software owned by Alwin Madhu. Never add an open-source license, publish the repository, or describe the project as open source. See `PROPRIETARY_NOTICE.md`, `THIRD_PARTY_NOTICES.md`, and `LICENSING_AUDIT.md`.

Important files are `src/main.jsx`, `src/styles.css`, and `src/finance.js`. The app has Home, Activity, Goals, and Insights views, Add Money, expense logging, local balance updates, and a generated receipt overlay. Transactions and money-space balances persist under `tm-transactions` and `tm-spaces` in localStorage.

Do not reintroduce seeded financial data, fake balances, fake charts, cloud financial storage, analytics, or bank integrations. All displayed financial values must derive from the current local state. Use `pnpm test` and `pnpm build` after changes.

Known limitations: the Android shell is not a full Kotlin/Compose rewrite; financial data currently uses WebView localStorage rather than Room/Keystore; onboarding/profile persistence, edit/delete transaction flow, automated browser tests, and physical-device verification are incomplete; no GitHub remote is configured.

