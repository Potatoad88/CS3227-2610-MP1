# User Guide

## What Should I Eat?

What Should I Eat? is an offline Java desktop app for maintaining a personal list of food places and choosing one at random. Places can be searched and filtered by cuisine, price, and manually entered distance.

## Requirements

- Windows, macOS, or Linux with a terminal
- A 64-bit JDK 25
- Internet access to download the JAR and JDK

## Setup and Launch

1. Download the JAR matching the operating system and active JDK architecture:

- `WhatShouldIEat-windows-x64.jar` for 64-bit Windows with an x64 JDK
- `WhatShouldIEat-linux-x64.jar` for 64-bit Linux with an x64 JDK
- `WhatShouldIEat-macos-x64.jar` when the macOS JDK reports `x86_64`
- `WhatShouldIEat-macos-arm64.jar` when the macOS JDK reports `aarch64`

On macOS or Linux, check the active JDK architecture with:

```bash
java -XshowSettings:properties -version 2>&1 | grep os.arch
```

On Windows PowerShell, use:

```powershell
java -XshowSettings:properties -version 2>&1 | Select-String "os.arch"
```

An Apple silicon Mac can run either an ARM64 JDK natively or an x64 JDK through Rosetta. Select the JAR using the architecture reported by Java, even if it differs from the physical processor.

2. Place the JAR in a dedicated folder and open a terminal in that folder.
3. Launch it with Java 25. For example:

```bash
java -jar WhatShouldIEat-macos-arm64.jar
```

The JAR already includes JavaFX, so Gradle and the project source are not needed. It does not include the JDK. Saved places are written to a `data/` folder beside the working location from which the command is run. Smaller content areas scroll when needed.

## Testing the System

Use this quick acceptance workflow after launching the downloaded JAR:

1. Add a place from **Saved Places** and confirm it appears in alphabetical order.
2. Click its row and confirm the details page includes its personal notes, then return using **Back**.
3. Search using part of its name with the search button, then clear the field and confirm all results return immediately.
4. Edit that place, return to the list, and confirm the changes appear.
5. Apply a filter that includes only that place and press **Random**; the result must come from the visible filtered set.
6. Restart the app and confirm the place and the selected light/dark theme are retained.
7. Delete the place and confirm the deletion dialog before removal.

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
- No native installer or bundled JDK is provided; launch the matching platform JAR using JDK 25.

## Troubleshooting

- `'java' is not recognized` on Windows: install a supported 64-bit JDK, then reopen the terminal and run `java -version`.
- Unsupported Java version: install JDK 25 and ensure `java -version` reports version 25.
- `UnsupportedClassVersionError`: the selected `java` command is older than Java 25; update `JAVA_HOME` and the system path.
- JavaFX native-library error from a release JAR: confirm that the JAR matches both the operating system and the architecture reported by the active JDK.
- App fails after manual data edits: close the app and restore valid JSON, use `[]`, or delete `data/places.json` to start empty again.
