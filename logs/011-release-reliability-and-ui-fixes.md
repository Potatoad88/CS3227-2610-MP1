# Release Reliability and UI Fixes

## Request

I asked for an examiner-style review of the complete project to find logic, storage, UI, release, and documentation bugs. I then requested fixes for long text that could be truncated, unsafe replacement of the saved-place file, and unclear selection between the platform-specific release JARs.

## Findings and Changes

- Enabled wrapping for restaurant names in saved-place rows and the place-details heading.
- Enabled wrapping in the three compact Home summary boxes so their titles, descriptions, and containers grow with narrower layouts.
- Removed the fixed preferred height from the three Home feature cards while retaining a consistent minimum height.
- Changed JSON saving to write a complete temporary file before replacing `data/places.json`. The replacement is atomic when supported by the file system and uses a portable normal replacement fallback otherwise. Temporary files are removed after the operation.
- Added a storage test confirming that existing data is replaced and no temporary file remains after a successful save.
- Rebuilt all four JavaFX release JARs after the source and stylesheet changes.

## Release Architecture Review

I compared this project with my earlier CS2103T iP. The iP uses the Shadow plugin to place Windows x64, Linux x64, and Intel macOS JavaFX libraries in one JAR, but it does not include Apple silicon libraries. Intel and ARM JavaFX libraries use the same resource filenames, so combining both macOS architectures directly would cause collisions.

I retained four explicit release JARs for Windows x64, Linux x64, Intel macOS, and Apple silicon macOS. The README and User Guide now explain that the selected JAR must match the operating system and the architecture of the active JDK process. They include commands for checking `os.arch` and explain that an Apple silicon Mac may run either an ARM64 JDK natively or an x64 JDK through Rosetta.

## Verification

I ran the Java 25 Gradle `clean check` lifecycle after the changes. All 13 JUnit tests and both Checkstyle tasks passed. I also ran `releaseJars`, which successfully rebuilt all four platform-specific artifacts with the updated application classes and stylesheet.
