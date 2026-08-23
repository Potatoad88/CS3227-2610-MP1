# What Should I Eat?

A Java desktop app for saving food places and randomly choosing where to eat when indecisive.

## Features

- **Manage food places:** Add, view, edit, and delete places with cuisine, price, rating, distance, tags, and personal notes.
- **Search and filter:** Find saved places by name and filter them by cuisine, price range, and maximum distance.
- **Random place picker:** Choose randomly from places matching the submitted search and active filters.
- **Local persistence:** Retain saved places and light/dark theme preferences between launches.

## Demo Workflows

### Manage Food Places

![Manage food places demo](docs/media/manage-food-places.gif)

### Search and Filter

![Search and filter demo](docs/media/search-and-filter.gif)

### Filtered Random Picker

![Filtered random picker demo](docs/media/filtered-random-picker.gif)

## Requirements

- Windows, macOS, or Linux
- A 64-bit JDK 17 or newer
- Internet access for the first build

Gradle does not need to be installed separately.

## Run

On Windows:

```bat
gradlew.bat run
```

On macOS or Linux:

```bash
./gradlew run
```

The Gradle wrapper downloads Gradle and the JavaFX dependencies when needed.

## Test

On Windows:

```bat
gradlew.bat test
```

On macOS or Linux:

```bash
./gradlew test
```

See [the User Guide](docs/UserGuide.md) for all features, setup details, and troubleshooting. Saved data is written to `data/places.json` when the app runs.
