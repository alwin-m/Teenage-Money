# Teenage Money licensing audit

Audit date: 2026-10-06

## Final status

Teenage Money is private, proprietary software owned by Alwin Madhu. The project does not grant permission to copy, modify, redistribute, sublicense, publish, or commercially distribute the Teenage Money codebase, brand, design, documentation, or project-owned assets.

Owner:

- Alwin Madhu
- alwinmadhu7@gmail.com
- alwinmadhu4060@gmail.com
- https://orcid.org/0009-0008-2826-5082

No phone number is included in project documentation.

## Project-level license audit

The repository was searched for `LICENSE`, `COPYING`, `NOTICE`, SPDX headers, copyright headers, open-source wording, and redistribution grants. No project-level open-source license was found. No inappropriate project license was removed because none was present.

The project metadata also declares `private: true` in `package.json`. README and security documentation explicitly describe the software as proprietary. No public GitHub remote is configured.

## Third-party components identified

### JavaScript dependencies

`pnpm licenses list` identified the following dependency licenses:

- MIT: React, React DOM, Vite, `@vitejs/plugin-react`, Rolldown, PostCSS, nanoid, picomatch, fdir, tinyglobby, scheduler, `@oxc-project/types`, `@rolldown/pluginutils`, and the platform Rolldown binding.
- Apache-2.0: detect-libc.
- BSD-3-Clause: source-map-js.
- ISC: picocolors.
- MPL-2.0: lightningcss and its Windows binding.

These licenses apply to the dependencies, not to Teenage Money. They are compatible with a private proprietary application when their notices and conditions are respected. The dependency manager remains the source of the corresponding license texts.

### Android dependencies

The Android wrapper uses AndroidX Core KTX, AndroidX Activity KTX, AndroidX WebKit, Android Gradle Plugin, Kotlin tooling, and Gradle wrapper components. Their upstream license and notice obligations remain applicable. No dependency license files were deleted or rewritten.

### Fonts

The CSS references Manrope and DM Mono from Google Fonts. The upstream font projects identify these fonts as SIL Open Font License 1.1. The fonts are third-party assets, not Teenage Money branding or source code. If the application is distributed with bundled font files, retain the applicable OFL notice. The Android wrapper currently bundles the app CSS and can fall back to system fonts when offline.

### Project-owned assets

The repository contains original Teenage Money UI styling, receipt treatment, icon resources, and visual assets. No copied third-party images, logos, or proprietary illustrations were identified in the project-owned source tree.

## Removed or replaced components

No component was removed or replaced for licensing reasons. No project-level open-source license was found that needed removal.

## Required ongoing practice

Re-run the dependency license audit whenever `package.json`, `pnpm-lock.yaml`, Android dependencies, fonts, icons, or bundled assets change. Keep third-party notices separate from the proprietary ownership notice. Never add a project-level MIT, Apache, GPL, LGPL, BSD, MPL, ISC, Unlicense, or similar license to Teenage Money.

