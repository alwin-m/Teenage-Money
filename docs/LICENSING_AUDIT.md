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

README and security documentation explicitly describe the software as proprietary. No public GitHub remote is configured.

## Third-party components identified

### Android dependencies

The native app uses AndroidX Core KTX, AndroidX Activity, Jetpack Compose Material 3, Android Gradle Plugin, Kotlin tooling, JUnit, and Gradle wrapper components. Their upstream license and notice obligations remain applicable. No dependency license files were deleted or rewritten.

### Project-owned assets

The repository contains original Teenage Money UI styling, receipt treatment, icon resources, visual assets, and the owner-supplied `teenage-money-logo-lockup.png` brand asset. No copied third-party images, logos, or proprietary illustrations were identified in the project-owned source tree.

## Removed or replaced components

No component was removed or replaced for licensing reasons. No project-level open-source license was found that needed removal.

## Required ongoing practice

Re-run the dependency license audit whenever Android dependencies, fonts, icons, or bundled assets change. Keep third-party notices separate from the proprietary ownership notice. Never add a project-level MIT, Apache, GPL, LGPL, BSD, MPL, ISC, Unlicense, or similar license to Teenage Money.

