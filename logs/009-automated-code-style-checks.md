# Automated Code Style Checks

## Request

I asked whether Checkstyle would provide useful evidence for the code-quality and software-engineering grading criteria, then requested its implementation.

## Implementation

- Added Gradle's built-in Checkstyle plugin and pinned Checkstyle 14.0.0.
- Added a focused configuration covering imports, naming, braces, whitespace, 120-character line length, public API Javadocs, and common correctness patterns.
- Applied the checks to both production and test Java code through Gradle's standard `check` lifecycle.
- Changed the cross-platform GitHub Actions workflow from `test` to `check`, so style violations and test failures both block a successful CI result.

## Engineering Judgement

I used a small project-specific ruleset instead of importing a large style guide unchanged. This keeps the checks objective and consistent with the existing code while avoiding suppressions for normal JavaFX patterns. Checkstyle complements code review and tests; it cannot assess architecture, user experience, or runtime behaviour.

## Verification

I ran `./gradlew clean check` with Java 25 and corrected every reported violation. The final build passes without ignoring failures or suppressing files.
