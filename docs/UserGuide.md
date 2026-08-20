# User Guide

## What Should I Eat?

What Should I Eat? is an offline Java desktop app for maintaining a personal list of food places and choosing one at random. Places can be searched and filtered by cuisine, price, and manually entered distance.

## Requirements

- macOS or Linux with a terminal
- JDK 17 or newer
- Internet access on the first build so Gradle can download JavaFX and test dependencies

No global Gradle installation is required. The included `gradlew` script installs Gradle 8.10.2 inside the project folder when needed.

## Setup and Launch

1. Open a terminal in the project root, the folder containing `build.gradle`.
2. Ensure the launcher is executable: `chmod +x gradlew test.sh`.
3. Start the app: `./gradlew run`.

The first launch may take longer while dependencies are downloaded. Smaller content areas scroll when needed.

## Testing the System

Run the automated tests from the project root:

```bash
./gradlew test
```

`./test.sh` runs the same Gradle test task. A successful run ends with `BUILD SUCCESSFUL`. The tests use temporary files and do not change `data/places.json`.

For a quick manual acceptance test:

1. Add a place from **Saved Places** and confirm it appears in alphabetical order.
2. Click its row and confirm the details page includes its personal notes, then return using **Back**.
3. Search using part of its name with the search button, then clear the field and confirm all results return immediately.
4. Edit that place, return to the list, and confirm the changes appear.
5. Apply a filter that includes only that place and press **Random**; the result must come from the visible filtered set.
6. Delete the place and confirm the deletion dialog before removal.
7. Restart the app and confirm the place and the selected light/dark theme are retained.

## Home Page

The Home page shows a summary of the app and the current number of saved places. **Random Craving Picker** chooses from every saved place; filters from the Saved Places page do not carry over to Home. If there are no places, the app asks the user to add one first.

## Saved Places

Saved places are displayed alphabetically by name. Each row shows the place's cuisine, price range, distance, rating, and tags. Click a row, or focus it and press Enter, to open a read-only page containing all saved details, including personal notes. Use **Back** to return to the list or **Edit Place** to open the edit form.

### Search

Enter text in **Search saved places**, then press Enter or the search-icon button to refresh the list. Search is case-insensitive and matches restaurant names only. Text that has not been submitted does not affect the displayed list or random picker. Clearing the field immediately restores all places allowed by the applied cuisine, price, and distance filters.

### Filters

1. Press **Filter** to open the filter panel.
2. Select an exact cuisine, an exact price range, and/or enter a maximum distance in kilometres.
3. Press **Apply Filters**.

All active filters and the submitted search text are combined. Maximum distance must be blank or a finite number greater than or equal to zero. The number beside **Filter** reports how many field filters are active. **Clear** removes all field filters but does not clear the search box.

### Random Selection

Press **Random** on the Saved Places page to choose only from places matching the current search and applied filters. Places excluded from the displayed results cannot be selected. If nothing matches, the app shows a **No Match** message.

### Add a Place

Press **Add Place**, complete the form, and press **Save Place**. The fields are:

- **Name**: required; surrounding spaces are removed.
- **Cuisine Type**: required selection; defaults to Japanese.
- **Price Range**: `$` to `$$$$`; defaults to `$$`.
- **Rating**: 1 to 5 stars; defaults to 4.
- **Distance**: required, finite, and at least 0 km.
- **Tags**: optional comma-separated labels; blank labels are ignored.
- **Personal Notes**: optional free text.

Place names do not need to be unique. The app uses an internal generated ID so two different places may share a name.

### Edit or Delete a Place

Press the pencil button on a place row to edit it, then press **Update Place**. The internal ID is preserved. Press the cross button to delete a place; deletion occurs only after confirmation. Add, edit, and delete operations are written to disk immediately.

## Light and Dark Modes

Press the moon/sun button at the right of the navigation bar to switch themes. The app stores the choice in the operating system's Java user preferences and restores it on the next launch.

## Saved Data

Places are stored locally in `data/places.json`. If the file does not exist, the app starts with an empty saved-place list. The file is created the next time a place is added, edited, or deleted. An existing file containing `[]` also produces an empty list.

To reset the saved-place list, close the app and delete `data/places.json` or replace its contents with `[]`. Manual editing is not recommended because malformed JSON can prevent startup.

## Limitations

- Distance is entered manually; there is no geocoding, live location, route calculation, or Google Maps integration.
- There are no place images and user accounts.
- The supplied launcher targets macOS and Linux shells; no Windows launcher is included.

## Troubleshooting

- `Permission denied: ./gradlew`: run `chmod +x gradlew`.
- Java toolchain error: install JDK 17 or newer and ensure `java -version` works.
- Dependency download error: reconnect to the Internet and rerun `./gradlew run`.
- App fails after manual data edits: close the app and restore valid JSON, use `[]`, or delete `data/places.json` to start empty again.
