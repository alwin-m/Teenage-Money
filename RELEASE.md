# Release status

Current release target is an Android WebView wrapper around the bundled local web app. The production gate is currently: `pnpm test`, `pnpm build`, and both Gradle APK tasks pass, no secrets are committed, and the browser flow is manually checked.

Debug and locally signed release APKs are available for device testing. The release APK uses the debug signing configuration and is not store-ready.

