# 003 - Refinement and Reliability

## Prompts and decisions

I requested removal of an unused favourite flag after a code-simplification pass. Favourite-related model, storage, UI, CSS, and test behaviour was removed because no remaining user workflow used it.

When editing a place caused a null-ID exception, I supplied the exact stack-trace message and asked whether restaurant names should be used as IDs to prevent duplicates. I decided to keep generated UUIDs: names can be duplicated or edited, while UUIDs provide stable identity for updates.

I requested dark mode with the last chosen option restored on launch. The app uses Java user preferences for that setting. I also reported several visual inconsistencies in dark mode: poor contrast for the “Culinary Utility System” label and square focus outlines around rounded edit, delete, and theme buttons. These were corrected in the shared stylesheet.

## Additional product decisions

I asked whether the default saved-place list could be empty; it was changed from sample data to an empty list as I did not want to populate the list with random places that users don't know of and have them go through the hassle of editing/deleting them (commit `5650d8d`). I also asked about the fixed minimum window size and chose a smaller `720 x 480` minimum, with scrolling for content that does not fit (commit `27a37b7`).

## Verification

The ID fix was checked through update and storage tests. Theme and layout fixes were checked manually through the reported screenshots and application launch. The UI focus-outline fix is recorded in commit `76a4787`.

## Engineering takeaway

Removing code still requires consideration of persisted data and old application states. The null-ID issue was fixed at the model boundary so all load paths receive a usable ID.
