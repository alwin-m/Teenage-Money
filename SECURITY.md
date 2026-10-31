# Security Policy

## Reporting a Vulnerability

If you discover a security issue, please report it responsibly by emailing **alwinmadhu7@gmail.com**. Do not open a public GitHub issue for security vulnerabilities.

## Security Model

Teenage Money is designed with privacy and security as core principles:

### Network Isolation
- The app declares **zero internet permissions** in `AndroidManifest.xml`
- No network calls, API requests, or cloud connections of any kind
- Fully functional in airplane mode

### Data Storage
- All financial data is stored locally in a SQLite database (`teenagemoney.db`)
- User preferences use Android's SharedPreferences
- No data is transmitted to any server or third party

### What We Don't Collect
- No analytics or telemetry
- No crash reports
- No usage tracking
- No advertising identifiers
- No personal data beyond what the user explicitly enters

### Known Limitations

| Area | Status | Notes |
|------|--------|-------|
| SQLite encryption | Not yet implemented | Data is not encrypted at rest; planned for a future release |
| Signing key | Debug signing | Release APK uses debug config; production key required for store distribution |

### Best Practices for Contributors

- Never commit `.env` files, signing keys, credentials, tokens, or financial exports
- Keep export/backup controls explicit and privacy-safe
- Do not claim bank-grade protection until encrypted storage is implemented and verified
- Run `./gradlew lintDebug` before submitting changes to catch security-related lint warnings
