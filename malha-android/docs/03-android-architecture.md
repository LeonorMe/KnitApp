# Android Architecture

## High-Level Approach

The Android app is a native Android project written in Kotlin. It should be offline-first: the local database is the source of truth for the crafting workflow, and Google/Firebase services provide authentication, cloud sync, and shared data when online.

```text
Jetpack Compose UI
        |
ViewModels
        |
Use Cases / Domain Logic
        |
Repositories
        |
Room Database
        |
Firebase Sync Layer
        |
Google Authentication + Firestore
```

## Recommended Stack

- Kotlin
- Jetpack Compose, mandatory for all UI
- Navigation Compose
- Room
- Firebase Authentication / Google Sign-In
- Cloud Firestore
- Coroutines
- StateFlow
- WorkManager
- Material 3, mandatory design system

## UI Requirement

All Android screens must be implemented with Jetpack Compose and Material 3. XML layouts should not be introduced for app screens. Any reusable UI should live as Compose components under the design/core UI package.

## Suggested Modules

For the first version, keep a single Android app module but organize packages clearly:

```text
app/
  core/
    design/
    database/
    navigation/
    common/
  feature/
    home/
    projects/
    patterns/
    materials/
    settings/
  data/
    project/
    pattern/
    material/
  domain/
    model/
    usecase/
```

Split into Gradle modules later only if the project grows enough to justify it.

## Main Screens

1. Home
   - Active projects.
   - Progress summary.
   - Assistant insight card.

2. Project Execution
   - Current step.
   - Previous / next.
   - Mark done.
   - Notes.
   - Progress indicator.

3. Patterns
   - Pattern list.
   - Manual pattern editor.
   - Included starter patterns.

4. Materials
   - Yarn inventory.
   - Tools.
   - Material purchases.

5. Settings
   - Units.
   - Language.
   - Notation.
   - Brand/assistant legal copy later.

## Core Use Cases

- CreateProject
- UpdateProject
- ArchiveProject
- CreatePattern
- AddPatternStep
- MoveToNextStep
- MoveToPreviousStep
- MarkStepDone
- AddProjectNote
- AddMaterial
- AssignMaterialToProject
- CalculateBasicProgress

## Offline Voice Commands

Voice control should start narrow:

- "Next step"
- "Previous step"
- "Mark done"
- "Repeat step"

The command parser should map recognized text to local actions. Do not rely on AI for MVP voice control.

## Google Services

This project uses Google services for online identity and data:

- **Firebase Authentication** for user accounts.
- **Google Sign-In** as the primary sign-in option.
- **Cloud Firestore** for synced projects, patterns, materials, reviews, variants, and community state.
- **Firebase Storage** can be added later for project photos, pattern images, and generated assets.

Room remains the local source of truth during the MVP. Firestore should be introduced behind repository interfaces so screens do not depend directly on Firebase APIs.

## Sync Strategy

Prepare local entities with Firestore-friendly fields:

- localId
- remoteId nullable, mapped to Firestore document ID
- ownerUserId nullable until signed in
- createdAt
- updatedAt
- deletedAt nullable
- syncState

Conflict handling can start simple:

- User-owned progress: latest update wins.
- Pattern variants: create a new variant instead of overwriting another user's work.
