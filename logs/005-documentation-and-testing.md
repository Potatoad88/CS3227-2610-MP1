# 005 - Documentation and Testing

## Prompts and discussion

I asked for explanations of the project structure, `PriceRange` defaults, Gradle, Java NIO, defensive copies, the Developer Guide's level of detail, and automated versus manual coverage. These questions were used to refine the guides so they describe actual current behaviour rather than generic terminology.

I asked for Javadoc-style documentation. Concise class and method Javadocs were added where they clarify purpose and behaviour. I also asked for a whole-project review against code-quality, documentation-quality, and basic software-engineering criteria. The User Guide, Developer Guide, and reflection document were checked and revised to match the release.

Finally, I asked for the test file to be documented and strengthened, approved a focused implementation plan, and requested a commit. The test suite now documents its temporary-file setup and scenarios, expands JSON round-trip assertions to every persisted field, and adds tests for case-insensitive sorting, unknown-ID update rejection, and accepted boundary values.

## Verification

The test changes were run with `./gradlew --no-daemon test`: 9 tests passed with no failures, errors, or skips. The work was committed as `68c98f7 test: document and strengthen core coverage`.

The current guides explicitly distinguish automated coverage (core logic and storage) from manual acceptance coverage (JavaFX navigation, controls, layout, dialogs, theme appearance, and preference persistence).

## Engineering takeaway

I treated AI-generated documentation as a draft. I checked it against source behaviour, screenshots, Git history, and tests, then requested wording changes where it was too low-level, misleading, or not useful to an end user.
