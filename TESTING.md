# Testing

Run:

```bash
pnpm test
pnpm build
```

The current automated test suite covers positive amount validation, allocation, spending deduction, insufficient-balance rejection, and percentage calculation. The browser flow was manually verified for app startup, empty state, receipt generation, local balance update, and receipt dismissal.

Not yet available: Android instrumentation tests, native UI tests, persistence restart tests in an installed APK, and full edit/delete integration coverage.

