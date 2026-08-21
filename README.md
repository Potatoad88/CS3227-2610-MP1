# What Should I Eat?

A Java desktop app for saving food places and randomly choosing where to eat.

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
