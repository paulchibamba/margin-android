# Margin

Your books, in the moments between. An offline Android app that replaces doom scrolling with a feed of one-minute
posts from books you own. Reading unlocks the feed, and spaced repetition (FSRS-6) makes the ideas stick.

Kotlin · Jetpack Compose · Room · Hilt · clean architecture (`:app` → `:domain` ← `:data`).

## Build
The content pack isn't in this repository. It's generated locally and copied in before building.

1. In `local.properties`, point `margin.packSource` at a content pack directory (one that contains `manifest.json`).
2. Copy it into the project: `scripts/sync-content-pack.sh`
3. Install: `./gradlew :app:installDebug`

## Development
- `./gradlew build test`: build and run the unit tests.
- Git hooks: `git config core.hooksPath .githooks`.
- Branches: `<type>/t<NN>-<slug>`. Commits follow [Conventional Commits](https://www.conventionalcommits.org).
