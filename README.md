# iCCMS — Integrated Community Case Management System

iCCMS is the Android app community caseworkers use to register, follow up and report on children,
adolescents, caregivers and households in their caseload. It works offline in the field and syncs
with an OpenSRP server when a connection is available.

It is built and maintained by [BlueCode Limited](https://bluecodeltd.com) for AFGHS-PCZ
(Project Concern Zambia), on the open-source [OpenSRP](https://smartregister.org) platform.

## Features

- **Registers** for children and adolescents (CA/VCA), households, mother index, HIV testing
  services (HTS), PMTCT, appointments, HIV prevention (PrEP) and referral linkages, each with search,
  status indicators and a client profile.
- **Dashboard** with caseload totals, visits due and a subpopulation chart, filterable to the
  signed-in caseworker's own caseload.
- **Structured data capture** through JSON forms for screening, enrollment, assessments, visits,
  case plans and referrals.
- **Reports** for malaria, nutrition, TB and community-based surveillance.
- **Offline first**: all data is stored on the device in an encrypted (SQLCipher) database and
  synced with the server in the background.
- **Device-to-device sharing** of records where connectivity is limited.
- **Phones and tablets**: layouts scale for small phones through 10" tablets.

## Tech stack

| | |
|---|---|
| Language | Java 17 and Kotlin |
| Build | Gradle 8.14 (wrapper included) |
| Android | `minSdk` 28 (Android 9), `targetSdk` / `compileSdk` 36 |
| Platform | OpenSRP client libraries (core, family, native forms, CHW core) |
| Storage | SQLite with SQLCipher encryption |
| Charts | MPAndroidChart |

## Project structure

```
.
├── opensrp-ecap-chw/          The app: activities, registers, dashboard, forms and resources
│   └── src/ecap/              The "ecap" product flavor: forms, configuration and branding assets
├── opensrp-ecap-chw-core/     Shared CHW core: navigation drawer, base activities, utilities
├── android-job/               Background job scheduling module used by sync
├── gradle/                    Gradle wrapper
└── .github/workflows/         Continuous integration
```

## Getting started

### Prerequisites

- Android Studio (latest stable) with the Android SDK for API 36
- JDK 17
- A GitHub account with a personal access token that has the `read:packages` scope, used to download
  OpenSRP libraries from GitHub Packages

### 1. Clone

```bash
git clone https://github.com/BlueCodeSystems/opensrp-client-iccms.git
cd opensrp-client-iccms
```

### 2. GitHub Packages credentials

Some dependencies are published to GitHub Packages. Provide credentials in **one** of these ways:

- Environment variables `GPR_USER` and `GPR_API_KEY`, or
- A `github.properties` file in the project root (it is ignored by git):

  ```properties
  gpr.user=your-github-username
  gpr.key=your-personal-access-token
  ```

### 3. Local configuration

Create `local.properties` in the project root (it is ignored by git). Android Studio adds `sdk.dir`
for you; add the keys your build needs:

| Key | Purpose |
|---|---|
| `oauth.client.id`, `oauth.client.secret` | OpenSRP server authentication |
| `firebase.app.id`, `firebase.api.key`, `firebase.project.id`, `firebase.sender.id` | Firebase, when no `google-services.json` is present |
| `directus.base.url`, `directus.email`, `directus.password`, `directus.flags.collection`, `directus.flags.caseworker.field`, `directus.flags.auth.token` | Flags and caseworker data service |
| `mobile.devices.url` | Device registration endpoint |
| `mapbox.sdk.token` | Maps, where used |
| `signing.store.file`, `signing.store.password`, `signing.key.alias`, `signing.key.password` | Release signing (see below) |

Most keys can also be supplied as environment variables, which CI uses (for example
`FIREBASE_API_KEY`, `DIRECTUS_BASE_URL`, `SIGNING_STORE_FILE`). Ask the project maintainers for the
values for each environment.

### 4. Build and run

Open the project in Android Studio and run the **`ecapDebug`** variant of `opensrp-ecap-chw`, or use
the command line:

```bash
./gradlew :opensrp-ecap-chw:assembleEcapDebug     # build a debug APK
./gradlew :opensrp-ecap-chw:installEcapDebug      # build and install on a connected device
```

The debug app installs as `com.bluecodeltd.chw.ecap`.

## Testing

```bash
./gradlew :opensrp-ecap-chw:testEcapDebugUnitTest
./gradlew :opensrp-ecap-chw-core:testDebugUnitTest
```

## Release builds

Release APKs are signed with the keystore given by `signing.*` in `local.properties` or the
`SIGNING_STORE_FILE`, `SIGNING_STORE_PASSWORD`, `SIGNING_KEY_ALIAS` and `SIGNING_KEY_PASSWORD`
environment variables. Without them the release build is left unsigned.

```bash
./gradlew :opensrp-ecap-chw:assembleEcapRelease
```

`versionCode` and `versionName` can be overridden with the `VERSION_CODE` and `VERSION_NAME`
Gradle properties; on GitHub Actions the version code is derived from the run number.

## Customisation

- **Brand colours** live in `opensrp-ecap-chw/src/main/res/values/colors.xml` and
  `opensrp-ecap-chw-core/src/main/res/values/colors.xml`. The theme uses `#0F5230` for app bars,
  `#0A3A23` for the status bar and `#188038` for buttons and accents.
- **Subpopulations** shown on the dashboard are defined in
  `opensrp-ecap-chw/src/main/java/com/bluecodeltd/ecap/chw/util/Subpopulation.java`. Each entry names
  its database column, short name, description and colour; adding a subpopulation is one new entry
  and its description string.
- **Forms** are JSON files under `opensrp-ecap-chw/src/ecap/assets/json.form/`.

## Security

Never commit credentials. `local.properties`, `github.properties` and `google-services.json` are
ignored by git; keep them that way, and supply secrets to CI through repository secrets.

## Continuous integration

GitHub Actions (`.github/workflows/android-build.yml`) builds the project and verifies the APK's
16 KB page alignment for pushes and pull requests to the `development` branch.

## Contributing

1. Create a branch from `main`.
2. Keep changes focused, and match the style of the surrounding code.
3. Make sure the project builds and the unit tests pass.
4. Open a pull request describing the change and how it was tested.

## License

Licensed under the Apache License 2.0. See [LICENSE](LICENSE).

## Acknowledgements

Built on [OpenSRP](https://smartregister.org), an open-source platform for frontline health workers,
originally developed by Ona and the OpenSRP community.
