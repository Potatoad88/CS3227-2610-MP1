# Malformed Storage Recovery

## Request

I asked whether the app could remain usable when `data/places.json` is malformed instead of requiring the user to repair or delete the file manually. I chose to preserve the invalid file and notify the user rather than overwrite the only copy silently.

## Changes

`JsonPlaceStorage` now treats parsing failures as recoverable. It moves the original file to a timestamped `places-corrupted-*.json` sibling, writes a valid empty list to `places.json`, and reports the backup path to application startup. The app then opens normally with an empty saved-place list and shows a wrapping warning dialog containing that path.

Unreadable files and failures while preserving or resetting data still propagate, because continuing without a safe backup could lose information. A focused temporary-file test verifies that malformed content is retained exactly, the active file becomes `[]`, and subsequent loading succeeds with an empty list.

## Verification

The complete Java 25 Gradle `clean check` lifecycle was run after implementation. All 25 JUnit tests and both Checkstyle tasks passed. The four platform-specific release JARs were then rebuilt with the recovery behavior.
