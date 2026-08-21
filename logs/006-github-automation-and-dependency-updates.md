# 006 - GitHub Automation and Dependency Updates

## Prompts and decisions

I asked whether CodeQL and Dependabot would be useful for this project, considering the grading criteria for code quality and basic software-engineering practices. The discussion concluded that they are useful supporting evidence of a maintained public repository, but they do not replace a sound design, meaningful tests, or accurate documentation.

I then asked whether the JUnit suite should run in GitHub Actions and what happens when checks fail after a push. I chose separate workflows so test failures and CodeQL findings would be clearly visible. The configuration runs tests and CodeQL on pushes to `master` and pull requests targeting `master`; CodeQL also runs weekly. Dependabot checks Gradle and GitHub Actions dependencies weekly on Monday morning in the Singapore time zone, opens at most three pull requests per ecosystem, and does not merge anything automatically.

I asked whether the checks cancel a push. I learned that a failed workflow marks the pushed commit or pull request as failed, but it does not undo a direct push. Branch protection can later require successful checks before pull requests are merged.

## Initial verification and follow-up

After I pushed the automation configuration, the initial Tests, CodeQL, and Dependabot checks passed. Dependabot then opened three update pull requests: updates for `actions/checkout`, `actions/setup-java`, and the JUnit BOM.

The two GitHub Actions update pull requests passed their checks. I reviewed their purpose: `checkout` obtains the repository for a workflow, while `setup-java` installs the configured Java 17 runtime. They affect the CI environment rather than the desktop application.

The JUnit BOM update pull request failed during test discovery. I supplied the GitHub Actions output for investigation. Reproducing the change showed that JUnit 6 needs an explicit `testRuntimeOnly "org.junit.platform:junit-platform-launcher"` dependency with this Gradle version; adding it in a temporary copy allowed all nine tests to pass. This was diagnosed but deliberately not applied to the main branch in this interaction, because the update pull request still needs to be changed, rechecked, and reviewed before merging.

## Engineering takeaway

Automation is most useful when it supports review rather than replacing it. A green check confirms the configured tasks passed; it does not prove that every dependency update is appropriate. Reproducing the JUnit failure locally before changing the pull request made the compatibility fix specific and testable instead of guessing from a short failure summary.
