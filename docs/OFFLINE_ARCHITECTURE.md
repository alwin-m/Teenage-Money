# Offline architecture

Core flows are native Jetpack Compose screens backed by local SQLite and Android preferences. Adding money, logging expenses, computing balances, rendering activity, and showing receipts require no network request. The app declares no internet permission and uses system/packaged Android resources only.

