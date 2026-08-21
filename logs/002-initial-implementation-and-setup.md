# 002 - Initial Implementation and Setup

## Prompts and work completed

After agreeing to defer Maps, I asked for implementation of the Java desktop app and later confirmed that JavaFX and Gradle could be used. The initial version implemented the JavaFX home, saved-places, and add/edit screens; CRUD; validation; filters; random selection; JSON storage; a persistent theme; Javadocs; guides; and JUnit tests.

I then reported that both `./gradlew run` and a globally installed `gradle run` were unavailable. The build setup was adjusted to include a project-local `gradlew` launcher, so a global Gradle installation is not required.

## Feedback and fixes

I provided screenshots showing low contrast between form controls and their background, plus a validation dialog that truncated a long message. The UI was refined so input surfaces are more distinct and dialog messages wrap instead of ending in an ellipsis.

I also checked the random-selection rule. The implementation was inspected and tested to ensure that saved-place random selection uses the same criteria as the displayed results, excluding places hidden by active search or filters.

## Verification

The application compiled and launched through the included Gradle wrapper. The initial functionality is represented by commit `5da4e23`; current build and testing instructions are documented in `docs/UserGuide.md`.

## Engineering takeaway

Successful compilation did not prove the UI was usable. Screenshot feedback and manual checks were necessary for contrast, text wrapping, and control consistency.
