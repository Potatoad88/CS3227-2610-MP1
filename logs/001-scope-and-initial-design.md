# 001 - Scope and Initial Design

## Prompts and decisions

I supplied the CS3227 MP1 brief and proposed a personal food-place utility inspired by three screenshots of the UI crafted with Google Stitch: a landing page, a saved-place list, and an add/edit form. I asked for a review of the concept, strengths, risks, practical scope, architecture, and a possible Google Maps integration for distance calculation before implementation.

The discussion concluded that the concept was appropriate because it formed a complete personal workflow: save a place, organise it, filter it, and choose one at random. The agreed first-release features were create, view, update, and delete; name search; cuisine, price, and distance filters; random selection from the active results; notes and tags; local persistence; and light/dark mode. Google Maps integration for distance calculation was explicitly deferred.

## AI contribution and verification

The AI recommended JavaFX for the required Java desktop application, Gradle for builds, a small `ui` / `logic` / `model` / `storage` structure, and local JSON persistence. It identified that Maps would add API-key management, billing, network availability, geocoding failures, and route/distance semantics. I accepted manual distance entry for the release instead of introducing that external dependency.

I verified that this scope met the assignment's focus on a usable personal utility app and software-engineering practice without becoming over-scoped. The resulting architecture is recorded in `docs/DeveloperGuide.md` and the reflection document.

## Engineering takeaway

I treated the screenshots as visual direction, not as a complete specification. I decided that a reliable, testable CRUD workflow was more valuable than an untested network integration in MP1.
