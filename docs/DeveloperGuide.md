# Developer Guide

## Product and Technology

What Should I Eat? is an offline Java SE 25 desktop application built with JavaFX 25 and Gradle. Its release scope is CRUD for food places, text and field filtering, filtered random selection, JSON persistence, and a persistent light/dark theme.

## Architecture

The code follows a small layered design:

```text
JavaFX views (ui)
        |
        v
PlaceManager / FilterCriteria / RandomPicker (logic)
        |
        v
FoodPlace / PriceRange (model)
        |
        v
JsonPlaceStorage -> data/places.json (storage)
```

- `ui` builds JavaFX nodes, handles user events, and displays validation or I/O errors.
- `logic` owns validation, filtering, sorting, CRUD coordination, and random selection.
- `model` represents food-place data and price categories.
- `storage` converts food places to and from the local JSON file.

The UI depends on the logic layer, while logic depends on model and storage. Model classes do not depend on JavaFX, which allows core behavior to be tested without launching a window.

## Main Components

### Application and Views

`Launcher` provides the plain Java entry point used by executable JARs and delegates to `WhatShouldIEatApp`. The latter creates one `PlaceManager`, the root `AppView`, and the JavaFX scene. `AppView` owns navigation and stores the theme preference using `java.util.prefs.Preferences`. Home, saved-list, details, and form views are recreated when navigating, so each page reflects the latest manager state.

`SavedPlacesView` keeps typed search text and pending filter controls separate from their applied values. Name search is submitted with Enter or the search button, while clearing the field removes the applied query immediately. Field filters are applied only through **Apply Filters**. Both the displayed list and its random picker use the same `FilterCriteria`, preventing filtered-out places from being selected. Rows open `PlaceDetailsView` by mouse or keyboard, while their edit and delete controls keep independent actions.

`PlaceFormView` is shared by add and edit flows. UI parsing handles required numeric distance input, while `PlaceManager` repeats domain validation so invalid data cannot bypass the form.

### Domain and Logic

`FoodPlace` stores immutable `id`, `name`, `cuisine`, `distanceKm`, `priceRange`, `rating`, `tags`, and `notes`. IDs are UUID strings generated independently of names, allowing duplicate names while keeping updates unambiguous.

`PlaceManager` loads the in-memory list, returns places sorted case-insensitively by name, validates mutations, and persists CRUD operations. Names and cuisines must be non-blank, distance must be finite and non-negative, and rating must be from 1 to 5.

`FilterCriteria` combines case-insensitive name search with exact cuisine, exact price, and maximum-distance checks. The UI gives `RandomPicker` the matching results, and the picker returns `Optional.empty()` when that list is empty. Injecting `Random` through its second constructor makes selection deterministic in tests.

### Storage

`JsonPlaceStorage` persists saved places in `data/places.json` using Java NIO's `Path` and `Files` APIs. It creates the data directory when needed and returns an empty list when the file is missing, empty, or contains `[]`. If parsing fails, storage moves the malformed file to a timestamped `places-corrupted-*.json` sibling, writes a valid empty list, and exposes the backup path so startup can notify the user. Read or backup failures still propagate rather than risking data loss. Saves are written completely to a temporary file in the same directory before the original is replaced, reducing the risk of leaving partially written data. Atomic replacement is used when the file system supports it, with normal replacement as a portability fallback.

The storage format is intentionally simple and local to this application. It supports the schema written by the app, but does not aim to be a general-purpose JSON parser.

Example record:

```json
{
  "id": "9f3c4fe0-d864-4fd6-835f-080d5ca3727b",
  "name": "Pasta Bella",
  "cuisine": "Italian",
  "distanceKm": 2.4,
  "priceRange": "$$",
  "rating": 4,
  "tags": "Cozy,Carbs",
  "notes": "Reliable pasta and warm lighting."
}
```

## Key Design Decisions

- **Stable UUID identity:** Restaurant names are editable and need not be unique, so updates and deletions use generated UUIDs instead of names.
- **Immutable place values:** Editing creates a replacement `FoodPlace` while preserving the existing UUID. This avoids partially updated objects and keeps failed operations from leaking changes.
- **Save before changing memory:** `PlaceManager` builds an updated list and asks storage to save it before changing its managed list. If saving fails, the visible in-memory state remains unchanged.
- **Safe file replacement:** Storage writes the complete JSON document to a sibling temporary file before replacing the active file. This reduces the chance of leaving partially written saved data.
- **One eligible result set:** `SavedPlacesView` applies one `FilterCriteria` to produce both the displayed rows and the candidates passed to `RandomPicker`. A hidden place therefore cannot be selected.
- **Platform-specific release JARs:** JavaFX contains operating-system and processor-specific native libraries. Separate JARs avoid native-resource collisions and make each supported target explicit.

## Add-Place Execution Flow

```text
User presses Save Place
        |
        v
PlaceFormView parses the fields and creates a FoodPlace
        |
        v
PlaceManager validates the place and builds a copied list
        |
        v
JsonPlaceStorage writes a temporary file and replaces places.json
        |
        v
PlaceManager adds the place to its in-memory list
        |
        v
AppView recreates SavedPlacesView with the latest sorted data
```

If parsing or validation fails, the form shows an error and does not call storage. If saving fails, the exception returns to the form and the manager does not change its in-memory list.

## Error Handling

Form and filter validation errors are displayed in wrapping application dialogs. CRUD methods propagate `IOException` to the UI, where users receive an operation-specific error. Malformed saved data is backed up and reset during startup, after which a wrapping dialog reports the backup path. An unreadable file or a failed backup still prevents launch because the app cannot preserve the user's data safely.

## Build and Test Process

End users launch the matching JAR from `release/`; Gradle commands are maintained for development, testing, CI, and release generation only. The official Gradle wrapper downloads Gradle 9.7.1. The Java toolchain and compiler release are both fixed at Java 25, matching the assignment default. The OpenJFX Gradle plugin resolves JavaFX 25 native libraries for developer launches on the current operating system. Useful macOS/Linux commands from the project root are:

```bash
./gradlew run          # compile and launch the app
./gradlew test         # run the JUnit 6 suite
./gradlew check        # run tests and Checkstyle
./gradlew clean build  # clean, check, and package
./gradlew releaseJars  # build all platform-specific executable JARs
```

Windows uses the equivalent commands with `gradlew.bat`. Checkstyle 14.0.0 checks main and test code for consistent imports, naming, braces, whitespace, line length, public API Javadocs, and common correctness issues. Its rules are stored in `config/checkstyle/checkstyle.xml`, and violations fail the Gradle `check` task. The four release tasks merge the application classes and the matching JavaFX modules into separate JARs for Windows x64, Linux x64, macOS x64, and macOS ARM64. `Launcher` does not extend `javafx.application.Application`, which allows `java -jar` to reach the bundled JavaFX runtime correctly. Tests use JUnit's `@TempDir`; they never touch production data.

The 25 automated scenarios are grouped by responsibility:

| Test file              | Observable behavior covered                                                                                                                                                                                                                                                                                                                                 |
| ---------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `PlaceManagerTest`     | Verifies persistent CRUD and ID preservation; unchanged managed state after failed writes; invalid and boundary field values; rejection of unknown update IDs; case-insensitive sorting; name-only search combined with field filters; filtered random selection and no-match behavior; and independent update and deletion of places with duplicate names. |
| `FoodPlaceTest`        | Verifies generated and supplied IDs, identity independent of duplicate names, and defensive tag copies on input and output.                                                                                                                                                                                                                                 |
| `PriceRangeTest`       | Verifies conversion of every display label and enum name, plus the documented fallback for unsupported labels.                                                                                                                                                                                                                                              |
| `FilterCriteriaTest`   | Verifies inclusive maximum-distance filtering and that null, blank, and `Any` values leave filters inactive.                                                                                                                                                                                                                                                |
| `RandomPickerTest`     | Verifies deterministic candidate selection through an injected random source and no selection from an empty list.                                                                                                                                                                                                                                           |
| `JsonPlaceStorageTest` | Verifies missing and whitespace-only files; round trips of every stored field and escaped character; replacement without leftover temporary files; creation of missing parent directories; and preservation and reset of malformed data.                                                                                                                    |
| `PlaceFormatterTest`   | Verifies rating stars and distance formatting, including fractional values and values larger than a 32-bit integer.                                                                                                                                                                                                                                         |

JavaFX row navigation, event consumption, search and filter controls, dialogs, theme persistence, responsive scrolling, and appearance on each operating system remain manual-test concerns. Manual release checks also cover the minimum 720 x 480 window size, application restart, the malformed-data warning dialog, and launch of each matching platform JAR.

## Continuous Integration and Dependency Updates

GitHub Actions runs the **Tests** workflow on every push to `master` and on pull requests targeting `master`. It uses Temurin Java 25 to run Gradle `check`, including the complete JUnit 6 suite and Checkstyle, and package the matching release JAR on Ubuntu x64, Windows x64, Apple silicon macOS, and Intel macOS. Each successful job uploads its JAR as a workflow artifact. This verifies compilation, tests, static style checks, and packaging on all three required operating systems; JavaFX interaction and appearance still require manual launches on representative machines.

The separate **CodeQL** workflow runs on the same events and once a week. It analyses the Java source with read-only repository access plus permission to publish security results. Keeping the workflows separate makes test failures and security-analysis results easy to distinguish.

Dependabot checks Gradle and GitHub Actions dependencies every Monday at 09:00 Asia/Singapore. It opens at most three update pull requests per ecosystem and never merges them automatically. Each update should be reviewed and pass both workflows before it is merged.

## Software Engineering Process

Development was iterative and risk-driven. The first scope review deferred Google Maps because API keys, billing, network failures, and geocoding would add peer-testing risk without strengthening the core CRUD workflow. The implementation then separated UI from testable domain logic, followed by focused passes for validation, filtered random behavior, stable IDs, dark-mode persistence, accessibility labels, and documentation accuracy.

AI output was treated as a draft rather than accepted blindly. Changes were checked through compilation, automated tests, manual launches, and screenshot comparison. Reported regressions, such as a null ID during update and low dark-mode contrast, were traced to shared model or CSS behavior before correction.

## Future Extensions

### Maps

A future release may introduce a location service only when Maps is implemented. That service should translate an address into coordinates and calculate distance from user-defined presets such as Home or Work. API keys must remain outside source control, and manual distance should remain available when the network or API is unavailable.

### User Accounts and Cloud Storage

A future release may add user registration and login so each user can access the same saved places across devices. The JavaFX client should communicate with an authenticated backend API, which would enforce ownership and store places in a database. The client should not connect directly to a remote database or contain database credentials. Introducing this feature would also require secure password handling, session management, migration from local JSON, network-error handling, and a decision on whether local data remains available offline.

## Acknowledgements

- The visual direction was adapted from three prototype screenshots supplied by myself. No image assets or source code were copied from them.
- Product planning, implementation drafts, reviews, debugging, Javadocs, tests, and documentation were developed with OpenAI ChatGPT and Codex. All generated output was reviewed and adapted for this project.
- Code-simplification reviews used Dietrich Gebert's Ponytail Codex plugin. Its guidance influenced removal of unused favourite-related behaviour, unnecessary mutation, and duplicated filtering responsibility; no Ponytail source code is included in the app.
- The project uses [OpenJFX](https://openjfx.io/) for its desktop UI, [Gradle](https://gradle.org/) for builds, and [JUnit](https://junit.org/) for automated tests.
- Repository automation uses [GitHub Actions](https://github.com/features/actions), [CodeQL](https://codeql.github.com/), and [Dependabot](https://docs.github.com/en/code-security/dependabot).
- JavaFX and Unicode symbols provide the interface icons. No third-party icon artwork is bundled.
