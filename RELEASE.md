# Release status

Versioning and the cumulative change ledger are maintained in [VERSIONING.md](VERSIONING.md) and [CHANGELOG.md](CHANGELOG.md).

The current public shipping version is `1.0.0`. The APK embeds a timestamped `BUILD_ID` such as `1.0.0-20261012.2321`, which is also shown in Profile > About this app.

Current release target is the native Android Compose app. The production gate is currently: `./gradlew.bat testDebugUnitTest lintDebug assembleDebug`, no secrets are committed, and device/emulator verification is completed before distribution.

Debug and locally signed release APKs are available for device testing. The release APK uses the debug signing configuration and is not store-ready until a production upload key is supplied.

