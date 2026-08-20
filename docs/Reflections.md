# Reflections on AI-Assisted Software Engineering

## 1. Reviewing Scope Before Implementation

**Prompt:** I asked the LLM to review my proposed food-place app and three UI screenshots, discuss strengths, risks, scope, and architecture, and evaluate Google Maps before writing code.

I formulated the prompt in two stages: product review first and implementation later. This prevented a visually attractive screenshot from silently becoming the specification and forced the LLM to consider whether every shown feature belonged in MP1. The most important assumption it made was that Google Maps would require API-key management, billing configuration, network access, and more failure handling than a peer-tested desktop release needed. That assumption was sensible, but “Maps is valuable” and “Maps belongs in the first release” are different claims.

I verified the recommendation against the assignment's emphasis on a complete personal utility app and software engineering practice. CRUD, filtering, random selection, persistence, validation, and documentation already formed a coherent workflow. I therefore kept manually entered distance but deferred geocoding and route calculations. The engineering judgement was deciding that reliability and testability were more valuable than another external integration. Next time I would state an explicit time budget and acceptance criteria in the first prompt so scope advice can be tied to measurable constraints.

## 2. Requesting a JavaFX and Gradle Implementation

**Prompt:** After agreeing to defer Maps, I asked the LLM to implement the Java desktop app with JavaFX and Gradle, based on the landing, saved-list, and add/edit screenshots.

The prompt named both the technology and the visible screens while leaving internal structure open. The LLM assumed a layered `ui`, `logic`, `model`, and `storage` design and chose JSON for local persistence. This was useful because the logic could be tested without starting JavaFX. However, the first launcher assumed that either `gradlew` or a global Gradle installation already existed. That was wrong in my environment: both `./gradlew run` and `gradle run` initially failed.

The prompt evolved after observing the real environment. I reported the exact terminal errors, and the launcher was changed so the included script downloads a local Gradle distribution. I verified the result by compiling and launching through the wrapper rather than trusting the generated configuration. Manual screenshot checks then exposed visual issues such as low input contrast, truncated dialog text, and inconsistent button corners. This showed that compilation verifies structure, not usability. Next time I would include “fresh-machine setup with no global Gradle” and minimum-window visual checks in the initial acceptance criteria.

## 3. Verifying Filtered Random Selection

**Prompt:** I asked whether random generation on the Saved Places page excludes places removed by the active filters.

This short prompt was effective because it asked about one important behavioral invariant instead of vaguely asking whether filtering “works.” The LLM needed to trace the UI's current search text and applied field filters into `FilterCriteria`, then confirm that `RandomPicker` filters before selecting. A possible mistaken assumption was that the visible list itself was passed to the picker. In fact, the implementation passes all managed places together with the same criteria used by the list, which produces the same eligible set.

I verified the answer in two ways: by inspecting the shared criteria flow and by adding a deterministic test with one nearby and one filtered-out place. The test asserts that only the eligible place can be selected and that no match returns an empty result. The engineering judgement was to test the invariant below the JavaFX layer, where a seeded `Random` makes the result repeatable. Next time I would phrase feature prompts as invariants such as “the picker candidate set must equal the displayed filtered set,” because those translate directly into tests.

## 4. Simplification, Favourite Removal, and Stable IDs

**Prompts:** I invoked Ponytail full to clean up the project, then asked to remove the favourite flag completely. After an update caused a null-ID exception, I reported the stack-trace message and asked whether place names should become IDs.

The cleanup prompt intentionally asked the LLM to look for unnecessary code rather than add features. It removed favourite-related state that had no user workflow and reduced stale model, JSON, CSS, and test behavior. The later exception showed a risk of broad AI refactoring: older saved records could still have missing IDs even though new objects generated UUIDs. The model had assumed loaded data always satisfied the latest schema.

I supplied the exact exception instead of asking for a generic fix. The correction was placed in the `FoodPlace` constructor so every loading path regenerates a missing or blank ID. I rejected names as IDs because duplicate restaurant names are legitimate and names can change during editing. UUIDs separate identity from editable display data. I verified the fix through update/reload tests and a storage round trip with a missing ID. The lesson is that simplification still requires migration thinking: deleting a feature from source code does not automatically remove legacy states from persisted data.

## Overall Reflection

Prompting was most effective for exploring scope, generating a complete vertical slice, and tracing behavior across layers. It was less effective for visual polish and environment-specific setup, where screenshots, terminal output, and manual observation were faster and more reliable than abstract prompting. The LLM accelerated implementation, but I still had to choose release scope, reject name-based identity, decide what belonged in tests, inspect persistence compatibility, and keep documentation synchronized with the product. In future AI-assisted projects, I would define acceptance tests earlier, submit smaller change requests, and ask the model to state migration and failure-mode assumptions before editing persistent data code.
