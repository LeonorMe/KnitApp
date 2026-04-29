# Implementation Plan

This plan converts the product docs into an implementation sequence for the Android app.

## Current Goal

Build the offline-first MVP foundation:

- Local data persistence.
- Starter patterns.
- Project progress tracking.
- Materials inventory.
- Simple Aidi local insights.

## Phase 1: Local Data Foundation

Deliverables:

- Room dependencies.
- Database.
- Entities:
  - ProjectEntity
  - PatternEntity
  - PatternStepEntity
  - MaterialEntity
  - ProjectMaterialEntity
  - ProjectStepProgressEntity
  - UserPreferencesEntity
- DAOs for projects, patterns, and materials.
- Seed data for 3 starter patterns.
- Repository implementations.

Acceptance:

- App can read projects, patterns, and materials from local Room database.
- Seed data is created automatically on first launch.
- Domain models stay separate from database entities.

## Phase 2: Home And Projects

Deliverables:

- Home screen reads active projects from repository.
- Projects screen lists projects from repository.
- Empty and loading states.
- Create project flow.
- Archive project action.

Acceptance:

- A user can see at least one starter project.
- Project progress is visible.
- App restart preserves data.

## Phase 3: Pattern Input

Deliverables:

- Pattern list backed by Room.
- Pattern detail screen.
- Manual pattern creation.
- Step editor.

Acceptance:

- A user can create a pattern with steps.
- Created pattern can be used in a project.

## Phase 4: Project Execution

Deliverables:

- Current step screen.
- Next and previous controls.
- Mark step done.
- Notes per step.
- Progress calculation.

Acceptance:

- A user can follow a pattern step by step.
- Closing and reopening resumes the last step.

## Phase 5: Materials

Deliverables:

- Materials list.
- Add/edit material.
- Assign material to project.
- Track planned and used quantities.

Acceptance:

- A user can track yarn/tools and connect them to projects.

## Phase 6: Aidi Local Insights

Deliverables:

- Rule-based insight engine.
- Home insight card.
- No network or AI dependency.

Examples:

- Project is almost finished.
- Project has been paused.
- Materials are assigned.

Acceptance:

- Aidi gives useful local suggestions without external API calls.

## Phase 7: Voice Commands

Deliverables:

- Limited speech command parser.
- Supported commands:
  - Next step.
  - Previous step.
  - Mark done.
  - Repeat step.

Acceptance:

- Voice is optional and every action has a manual fallback.

## Implementation Order

1. Build Room foundation.
2. Seed patterns and one starter project.
3. Connect Home screen to data.
4. Connect Projects/Patterns/Materials lists.
5. Add create/edit flows.
6. Add project execution.
7. Add Aidi local insights.
8. Add voice commands.

