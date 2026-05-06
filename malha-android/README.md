# Malha Android

Android app workspace for **Malha**, a knitting and crochet companion with offline-first project tracking and future AI assistance through **Aidi**.

## Structure

- `app/` - Android application module
- `docs/` - product, UX, architecture, and roadmap documents
- `gradle/libs.versions.toml` - dependency versions

## Current State

This is the initial offline-first foundation:

- Kotlin Android app
- Jetpack Compose UI
- Material 3 theme
- Bottom navigation
- Data-backed screens for Home, Projects, Patterns, and Materials
- Initial domain models and repository interfaces
- Room database entities, DAOs, repository implementations, and starter seed data

## Next Implementation Step

Add the real Firebase `google-services.json`, then implement Google Sign-In UI and Firestore sync behind the existing repository interfaces.

See [Firebase Setup](docs/11-firebase-setup.md).
