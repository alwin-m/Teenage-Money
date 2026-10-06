# Data storage

The current prototype uses browser `localStorage` as an offline persistence layer:

- `tm-transactions` stores serialized transaction records.
- `tm-spaces` stores the current allocation balances by space name.

This is suitable for a browser prototype only. It is not encrypted and must not be described as production-grade financial storage. A native release should migrate these records to Room/SQLite, use typed migrations, and protect sensitive data with Android Keystore-backed encryption.

