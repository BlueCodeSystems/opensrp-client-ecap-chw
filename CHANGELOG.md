# Changelog

All notable changes to this project are documented here. This project adheres to Semantic Versioning.

## [2.0.0] - 2025-09-13

Breaking changes
- HIA2 Reports migrated from ViewPager to ViewPager2 with TabLayoutMediator; adapter/type changes may break subclasses or reflection-based access. (feat(core:hia2))
- Build/toolchain lift to Android Gradle Plugin 8.7.0, compile/target SDK 35, and Java 17 compatibility. Consumer apps and libraries must align their toolchains. (build(root), build(app), build(core))
- Dependency exposure adjustments (api → implementation in places) to reduce transitive leaks. Downstream code that relied on transitive compile-time deps may need explicit dependencies. (build/core/app)

New features
- ViewPager2-based HIA2 navigation via new `Hia2ViewPager2Adapter`. (feat(core:hia2))
- Defensive UI scaffolding for register screens with `BaseSafeRegisterFragment`. (feat(app/ui))
- Generic `ViewPager2Adapter` for hosting fragments. (feat(app/ui))
- Additional UI resources: selectors, drawables, toolbar/layout scaffolding. (feat(core/ui), feat(app/ui))
- Developer utility script `scripts/logcat_app.sh` to tail app logs. (chore/scripts)

Bug fixes
- Avoid NullPointerExceptions in register screens by defensively initializing ActionBar/Toolbar. (fix(core/ui))

Dependencies and build updates
- opensrp-client-core → v7.0.0 (JitPack). (build/app/core)
- opensrp-client-native-form-new → 3.1.5 (JitPack). (build/core)
- opensrp-client-utils → v1.0.0 (JitPack). (build/core)
- opensrp-client-growth-monitoring → v3.0.0 (JitPack). (build/app/core)
- opensrp-client-pnc → v0.0.7 (JitPack). (build/core)
- Material Components → 1.12.0; AppCompat → 1.7.0; ConstraintLayout → 2.2.0. (build/app)
- AndroidX additions: viewpager2, preference, sqlite, recyclerview. (build/app/core)
- opensrp-client-reporting → v0.3.0 (JitPack). (build/app/core)
- Networking: AndroidX Volley (replace legacy). (build/app/core)
- Map stack: prefer MapLibre; exclude Mapbox GeoJSON/Turf. (build/app/core)
- Misc: Koin 3.x, CircleImageView, Android-SpinKit, Easy Rules support module. (build/app/core)

Internationalization
- Added string override bundles for core and FP flows; normalized app strings and added missing defaults. (i18n/core, i18n/app)

Housekeeping
- Resource palette/dimens expanded; minor refactors to align imports and namespaces. (refactor/core, refactor/app)

Migration notes
- Ensure project uses JDK 17+, AGP 8.7.0+, and compile/target SDK 35.
- Add any previously transitively available dependencies explicitly to consuming apps as needed.
- If you subclassed or referenced HIA2 ViewPager types, update to ViewPager2 equivalents.
