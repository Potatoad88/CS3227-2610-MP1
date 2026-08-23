# 007 - Final Quality Assurance and Demos

## Prompts and decisions

I asked for Windows source-launch support after confirming that the original launcher worked only on macOS and Linux. The custom launcher was replaced with the official Gradle wrapper, including `gradlew.bat`, and the test workflow was expanded to cover Ubuntu and Windows. The User Guide and Developer Guide were updated with platform-specific commands and requirements.

I then asked for a final review of the application, documentation, tests, and repository structure. This led to focused reliability fixes rather than new features. Persistence now leaves the managed list unchanged when a write fails, row actions are protected from triggering details navigation, and storage escaping preserves special characters. Several light- and dark-mode inconsistencies were also corrected, including choice-box menus, button outlines, and the notes field's focused and unfocused backgrounds.

For project presentation, I added a concise feature list and recorded workflows for CRUD, search and filtering, and filtered random selection. The recordings were shortened to double speed, converted from downloadable MP4 files into optimized GIFs, and embedded directly in the README so reviewers can see each workflow without leaving the repository page.

Finally, I asked AI to compare the completed workload with the Project Duke baseline and to run Ponytail full across the repository. The review concluded that the multi-page JavaFX interface, structured CRUD, combined search and filters, constrained random selection, persistence, theme preferences, cross-platform build, tests, and automation exceed the baseline. The cleanup removed unnecessary mutation and coupling: `FoodPlace` became immutable, `RandomPicker` now selects from an already-filtered list, and dead fields and one-use helpers were removed without changing user-visible behaviour.

## Verification

The final forced test run used `./gradlew --no-daemon test --rerun-tasks`. All 10 tests passed, covering CRUD persistence, failed writes, validation boundaries, stable IDs, sorting, name-only search, combined filters, filtered random selection, missing storage, and JSON round trips.

I also reviewed the three demo recordings for framing and workflow accuracy, checked the generated GIF dimensions and file sizes, and kept the final documentation aligned with the implemented application.

## Engineering takeaway

Late-stage review was most valuable when it targeted observable evidence: screenshots exposed theme defects, recordings exposed workflow clarity, test output verified refactoring, and comparison with the assignment baseline prevented unnecessary feature expansion. The final simplification pass reinforced that improving code quality can mean removing mutation and duplicated responsibility rather than adding abstractions.
