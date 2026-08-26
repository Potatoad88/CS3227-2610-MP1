# Meaningful Test Coverage Expansion

## Request

I asked AI to identify missing tests that protect meaningful behavior rather than adding cases only to increase the count. I also requested one authoritative description of what every test file covers.

## Decisions and Changes

I kept JavaFX interaction as manual acceptance coverage and did not add TestFX, Mockito, JaCoCo, or another dependency. The existing integration suite was separated by responsibility, and focused tests were added for UUID identity with duplicate names, defensive tag copies, filter boundaries and inactive values, price-label conversion, deterministic random selection, whitespace-only storage, and automatic creation of storage directories.

Storage-specific scenarios were moved from `PlaceManagerTest` into `JsonPlaceStorageTest`. `PlaceManagerTest` now concentrates on validation, queries, filtered selection, and persistent CRUD coordination. A duplicate-name integration scenario confirms that update and deletion use UUIDs and survive a reload without affecting the other same-named place.

The Developer Guide now contains the authoritative coverage table, grouped by all seven test files. It separately identifies JavaFX navigation, controls, dialogs, theme persistence, responsive layout, restart behavior, and platform appearance as manual checks. Malformed-data recovery was considered separately after this test-focused change.

## Verification

I ran `./gradlew clean check --no-daemon` with Java 25. All 24 JUnit tests, Checkstyle for production code, and Checkstyle for test code passed. Every persistence test uses JUnit `@TempDir`, so the suite does not access or modify `data/places.json`.
