# Third-party notices

Teenage Money is proprietary. The following third-party components are used by the build and remain subject to their respective licenses. Their presence does not make Teenage Money open source and does not grant rights to the Teenage Money source code or brand.

## JavaScript build dependencies

The current `pnpm licenses list` audit identified:

- MIT: React, React DOM, Vite, `@vitejs/plugin-react`, Rolldown, PostCSS, nanoid, picomatch, fdir, tinyglobby, scheduler, `@oxc-project/types`, `@rolldown/pluginutils`, and the platform Rolldown binding.
- Apache-2.0: detect-libc.
- BSD-3-Clause: source-map-js.
- ISC: picocolors.
- MPL-2.0: lightningcss and its Windows binding.

These are dependency licenses, not a license for Teenage Money. Their license text is supplied by the package manager/dependency distributions and must be retained when redistributing those dependencies.

## Android build dependencies

The Android wrapper uses AndroidX Core KTX, AndroidX Activity KTX, AndroidX WebKit, the Android Gradle Plugin, Kotlin tooling, and the Gradle wrapper. Their upstream license and notice files are part of their respective distributions. The project does not remove or rewrite those notices.

## Fonts

The stylesheet references Manrope and DM Mono from Google Fonts. These fonts are third-party assets licensed under the SIL Open Font License 1.1 by their upstream projects. The font license applies only to the font software; it does not license Teenage Money's code, brand, UI, or documentation. Preserve the applicable font notice if font files are bundled in a future release.

## Audit result

No standalone MIT, Apache, GPL, BSD, or similar license file was found in the project-owned source tree. No third-party license notice was removed. The dependency list above must be re-audited whenever dependencies change.

