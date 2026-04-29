# Implementation Guidelines

## Product Rules

1. Offline execution is sacred.
2. AI must be optional.
3. User progress must never be lost.
4. Manual editing must always be available.
5. Verified community content should rank above generated content.
6. The app should reward creation, not consumption.

## Android Engineering Rules

- Language: Kotlin.
- UI: Jetpack Compose. This is mandatory for all Android UI work.
- Design system: Material 3. New screens and components must use Material 3 Compose APIs.
- Architecture: MVVM with clear domain/data boundaries.
- Local database: Room.
- Authentication: Firebase Authentication with Google Sign-In.
- Cloud data: Cloud Firestore.
- Async state: Kotlin coroutines and StateFlow.
- Background sync: WorkManager.
- Dependency injection: Hilt or Koin, chosen once project setup begins.

## Naming Rules

Brand names are fixed for this project:

- App name: Malha.
- AI assistant / mascot: Aidi.

Implementation guideline:

- Do not hardcode the brand name across screens.
- Put brand-facing strings in Android resources.
- Keep assistant naming configurable through resources.
- Avoid package names tied to a temporary brand if possible.

## Feature Scope Rules

MVP includes:

- Project creation and editing.
- Manual pattern input.
- Step-by-step pattern execution.
- Progress persistence.
- Materials tracking.
- Basic voice commands if feasible.
- Local-only data.

MVP excludes:

- Accounts.
- Marketplace.
- Public community sharing.
- AI image generation.
- Full backend sync.
- PDF parsing.

## AI Safety Guidelines

AI content must:

- Be clearly labelled as AI-generated.
- Include confidence or reliability signals when used for planning.
- Be editable by the user.
- Avoid presenting draft patterns as verified truth.
- Encourage checking gauge, sizing, and stitch counts.

Good AI use cases:

- Explain a technique.
- Parse a user question.
- Draft pattern structure from text.
- Suggest materials based on user inventory.
- Estimate yarn as a range, not a precise guarantee.

Risky AI use cases:

- Exact full pattern from one or two photos.
- Complex shaping without user validation.
- Step-by-step generated images for technical stitches.

## UX Rules

- Large touch targets, minimum 48dp.
- Readable step text, around 18-22sp.
- One primary action per screen.
- Always show current step and progress.
- The project execution screen must be usable with one hand.
- Avoid pressure-based notifications or addictive urgency.

## Data Rules

- Local data is the source of truth in the MVP.
- Use IDs that can later sync with a backend.
- Track created/updated timestamps.
- Keep project progress separate from reusable pattern content.
- Keep user edits and variants as first-class concepts.

## Testing Rules

Before an MVP milestone is considered done:

- Project progress survives app restart.
- Offline usage works.
- Empty states are handled.
- Delete actions ask for confirmation.
- Basic accessibility checks pass.
