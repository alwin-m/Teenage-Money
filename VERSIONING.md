# Teenage Money versioning

Teenage Money uses two identifiers so a released package can be identified by both its product version and its build moment.

## Public version

- `versionName` is the user-facing semantic version, for example `1.0.0`.
- It is displayed in Profile > About this app.
- Increment rules:
  - `MAJOR`: incompatible product or data changes.
  - `MINOR`: new backward-compatible user-facing capability.
  - `PATCH`: fixes, accessibility improvements, polish, and documentation-only release updates.
- `versionCode` is the monotonically increasing Android package number. Never reuse it for a different package.

## Internal build identifier

Every Gradle build embeds `BUILD_ID` in `BuildConfig` as:

```text
<versionName>-<yyyyMMdd.HHmm>
```

Example: `1.0.0-20261012.2321`.

The timestamp is generated at build time. For reproducible builds, set `TM_BUILD_STAMP` or pass `-PtmBuildStamp=yyyyMMdd.HHmm`.

## Release checklist

1. Choose the next serial version and increase `versionCode`.
2. Add a dated entry to [CHANGELOG.md](CHANGELOG.md) before building.
3. Describe UI/UX, behavior, data/storage, accessibility, build, and known limitations.
4. Build with an explicit timestamp and record the resulting `BUILD_ID` and APK hash.
5. Run `pnpm test`, `pnpm build`, `./gradlew.bat lint testDebugUnitTest assembleDebug`, and `./gradlew.bat assembleRelease`.
6. Review the APK signing certificate before distribution.

## Data safety rule

Versioning must never require a schema reset, migration shortcut, deletion of user data, or a change to existing stored money values without a separately documented migration and test.
