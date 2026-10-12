# Release status

Versioning and the cumulative change ledger are maintained in [VERSIONING.md](VERSIONING.md) and [CHANGELOG.md](CHANGELOG.md).

The current public shipping version is `1.0.0`. The APK embeds a timestamped `BUILD_ID` such as `1.0.0-20261012.2321`, which is also shown in Profile > About this app.

Current release target is an Android WebView wrapper around the bundled local web app. The production gate is currently: `pnpm test`, `pnpm build`, and both Gradle APK tasks pass, no secrets are committed, and the browser flow is manually checked.

Debug and locally signed release APKs are available for device testing. The release APK uses the debug signing configuration and is not store-ready until a production upload key is supplied.

