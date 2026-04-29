# Android Architecture

## High-Level Approach

The Android app should be offline-first. The local database is the source of truth, and future backend sync should be added behind repository interfaces.

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
Future Remote Sync
```

## Recommended Stack

- Kotlin
- Jetpack Compose
- Navigation Compose
- Room
- Coroutines
- StateFlow
- WorkManager
- Material 3

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

## Future Sync

Prepare local entities with sync-friendly fields:

- localId
- remoteId nullable
- createdAt
- updatedAt
- deletedAt nullable
- syncState

Conflict handling can start simple:

- User-owned progress: latest update wins.
- Pattern variants: create a new variant instead of overwriting another user's work.
