# Teenage Money

A polished React/Vite prototype for the Teenage Money local-first money dashboard.

## Run locally

Install Node.js 18+ first, then:

```bash
npm install
npm run dev
```

The current prototype keeps expense entries in in-memory React state so the interaction loop can be tested quickly. The visual system is original: charcoal, clay, mint, and lavender surfaces with a small orbit illustration as the dashboard's living-world motif.

## Included

- Home dashboard with spending balance, weekly rhythm, money spaces, recent activity, and a savings goal.
- Quick-log bottom sheet with amount, description, category, validation, and a real activity update.
- Activity, Goals, and Insights navigation states.
- Responsive layout for desktop and phone-sized screens.

## Next native Android step

Port the state model into Kotlin + Compose with Room/DataStore and Android Keystore-backed encryption. The UI intentionally keeps money spaces as allocation buckets rather than representing bank accounts.
