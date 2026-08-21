# Developer Guide

## Product and Technology

What Should I Eat? is an offline Java 17 desktop application built with JavaFX 21 and Gradle. Its release scope is CRUD for food places, text and field filtering, filtered random selection, JSON persistence, and a persistent light/dark theme.

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

`WhatShouldIEatApp` creates one `PlaceManager`, the root `AppView`, and the JavaFX scene. `AppView` owns navigation and stores the theme preference using `java.util.prefs.Preferences`. Home, saved-list, details, and form views are recreated when navigating, so each page reflects the latest manager state.

`SavedPlacesView` keeps typed search text and pending filter controls separate from their applied values. Name search is submitted with Enter or the search button, while clearing the field removes the applied query immediately. Field filters are applied only through **Apply Filters**. Both the displayed list and its random picker use the same `FilterCriteria`, preventing filtered-out places from being selected. Rows open `PlaceDetailsView` by mouse or keyboard, while their edit and delete controls keep independent actions.

`PlaceFormView` is shared by add and edit flows. UI parsing handles required numeric distance input, while `PlaceManager` repeats domain validation so invalid data cannot bypass the form.

### Domain and Logic

`FoodPlace` stores `id`, `name`, `cuisine`, `distanceKm`, `priceRange`, `rating`, `tags`, and `notes`. IDs are UUID strings generated independently of names, allowing duplicate names while keeping updates unambiguous.

`PlaceManager` loads the in-memory list, returns places sorted case-insensitively by name, validates mutations, and persists CRUD operations. Names and cuisines must be non-blank, distance must be finite and non-negative, and rating must be from 1 to 5.

`FilterCriteria` combines case-insensitive name search with exact cuisine, exact price, and maximum-distance checks. `RandomPicker` first filters the supplied list and returns `Optional.empty()` when no eligible place exists. Injecting `Random` through its second constructor makes selection deterministic in tests.

### Storage

`JsonPlaceStorage` persists saved places in `data/places.json` using Java NIO's `Path` and `Files` APIs. It creates the data directory when needed and returns an empty list when the file is missing, empty, or contains `[]`.

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

## Error Handling

Form and filter validation errors are displayed in wrapping application dialogs. CRUD methods propagate `IOException` to the UI, where users receive an operation-specific error. An unreadable or malformed data file encountered during initial startup currently prevents launch; the User Guide documents how to recover the file.

## Build and Test Process

The official Gradle wrapper downloads Gradle 8.10.2 and resolves the JavaFX native libraries for the current operating system. Useful macOS/Linux commands from the project root are:

```bash
./gradlew run          # compile and launch the app
./gradlew test         # run the JUnit 5 suite
./gradlew clean build  # clean, compile, test, and package
```

Windows uses the equivalent commands `gradlew.bat run`, `gradlew.bat test`, and `gradlew.bat clean build`. The optional `test.sh` delegates to `./gradlew test` on macOS/Linux so there is one test definition and one build lifecycle. Tests use JUnit's `@TempDir`; they never touch production data.

The automated suite covers:

- add, update, delete, ID preservation, and reload from disk;
- domain validation, accepted boundary values, and unknown update IDs;
- case-insensitive alphabetical sorting;
- name-only search combined with cuisine, price, and distance filtering;
- random selection restricted to eligible places and no-match behavior;
- JSON round trips for every stored field, including escaped special characters.

JavaFX layout and theme appearance remain manual-test concerns. The release should be checked at the minimum 720 x 480 window size and after an application restart.

## Continuous Integration and Dependency Updates

GitHub Actions runs the **Tests** workflow on every push to `master` and on pull requests targeting `master`. It uses Temurin Java 17 and runs the suite separately on Ubuntu and Windows with each platform's native Gradle wrapper launcher.

The separate **CodeQL** workflow runs on the same events and once a week. It analyses the Java source with read-only repository access plus permission to publish security results. Keeping the workflows separate makes test failures and security-analysis results easy to distinguish.

Dependabot checks Gradle and GitHub Actions dependencies every Monday at 09:00 Asia/Singapore. It opens at most three update pull requests per ecosystem and never merges them automatically. Each update should be reviewed and pass both workflows before it is merged.

## Software Engineering Process

Development was iterative and risk-driven. The first scope review deferred Google Maps because API keys, billing, network failures, and geocoding would add peer-testing risk without strengthening the core CRUD workflow. The implementation then separated UI from testable domain logic, followed by focused passes for validation, filtered random behavior, stable IDs, dark-mode persistence, accessibility labels, and documentation accuracy.

AI output was treated as a draft rather than accepted blindly. Changes were checked through compilation, automated tests, manual launches, and screenshot comparison. Reported regressions, such as a null ID during update and low dark-mode contrast, were traced to shared model or CSS behavior before correction.

## Future Extension: Maps

A future release may introduce a location service only when Maps is implemented. That service should translate an address into coordinates and calculate distance from user-defined presets such as Home or Work. API keys must remain outside source control, and manual distance should remain available when the network or API is unavailable.

## Acknowledgements

- The visual direction was adapted from three prototype screenshots supplied by the project author. No image assets or source code were copied from them.
- Product planning, implementation drafts, reviews, debugging, Javadocs, tests, and documentation were developed with OpenAI ChatGPT and Codex. All generated output was reviewed and adapted for this project.
- Code-simplification reviews used Dietrich Gebert's Ponytail Codex plugin. Its guidance influenced removal of unused favourite-related behavior and speculative abstractions; no Ponytail source code is included in the app.
- The project uses [OpenJFX](https://openjfx.io/) for its desktop UI, [Gradle](https://gradle.org/) for builds, and [JUnit 5](https://junit.org/junit5/) for automated tests.
- Repository automation uses [GitHub Actions](https://github.com/features/actions), [CodeQL](https://codeql.github.com/), and [Dependabot](https://docs.github.com/en/code-security/dependabot).
- JavaFX and Unicode symbols provide the interface icons. No third-party icon artwork is bundled.
