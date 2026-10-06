# Security and privacy

Teenage Money is a private proprietary project owned by Alwin Madhu. Source code, application design, branding, assets, documentation, and project materials are confidential project materials unless explicitly released by the owner.

The current prototype has no backend, analytics, advertising, bank integration, or external financial API. Financial state stays in the browser's local storage. Because browser local storage is not encrypted, this prototype is not suitable for real sensitive financial data without a native security layer.

Never commit `.env` files, signing keys, credentials, tokens, or production financial exports. A native Android implementation must use Keystore-backed keys, encrypted storage, privacy-safe logs, and explicit local export/backup controls.

