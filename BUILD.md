# Build

Install dependencies with `pnpm install` and run `pnpm build`. The output is a browser bundle in `dist/`.

The repository includes an Android Gradle wrapper in `android/`. It packages the built Vite assets into a local-only WebView shell with application ID `com.teenagemoney.app`, minimum API 26, and target API 36.

Build APKs with:

```bash
cd android
./gradlew.bat :app:assembleDebug :app:assembleRelease
```

The release build currently uses the debug signing configuration for local testing and is not store-ready.

