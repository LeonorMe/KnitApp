# MVP Roadmap

## MVP Goal

Build a usable offline-first Android app that lets users create projects, follow pattern steps, track progress, and manage materials.

No AI or backend dependency is required for MVP success.

## Phase 0: Android Setup

Deliverables:

- Kotlin Android project.
- Jetpack Compose setup.
- Material 3 theme.
- Navigation structure.
- Basic package architecture.

## Phase 1: Local Data Foundation

Deliverables:

- Room database.
- Entities for Project, Pattern, PatternStep, Material.
- DAOs.
- Repositories.
- Seed data for 3 starter patterns.

## Phase 2: Home And Projects

Deliverables:

- Home screen.
- Active project list.
- Create project flow.
- Edit and archive project.
- Empty state.

## Phase 3: Pattern Input

Deliverables:

- Pattern list.
- Manual pattern creation.
- Step editor.
- Pattern detail view.

## Phase 4: Project Execution

Deliverables:

- Current step screen.
- Next / previous navigation.
- Mark step done.
- Save progress.
- Resume from last step.
- Step notes.

## Phase 5: Materials

Deliverables:

- Material list.
- Add yarn/tool.
- Assign material to project.
- Basic material usage fields.

## Phase 6: Basic Assistant Surface

MVP can include a non-AI assistant placeholder:

- Static "Aidi tip" cards.
- Local hints based on project state.
- No external AI call.

Examples:

- "This project has not been updated in 7 days."
- "You are 75% done."
- "You already assigned yarn to this project."

## Phase 7: Voice Commands If Time Allows

Deliverables:

- Android speech recognition integration.
- Limited command parser.
- Manual fallback for every action.

Commands:

- Next step.
- Previous step.
- Mark done.
- Repeat step.

## Phase 8: Polish And Test

Deliverables:

- Dark mode.
- PT-PT and EN strings if possible.
- Accessibility pass.
- App restart persistence test.
- Offline mode test.

## MVP Success Criteria

Users should be able to:

- Start a project in under 2 minutes.
- Add or choose a pattern.
- Follow steps without confusion.
- Close the app and resume correctly.
- Track materials used.

## Suggested First Validation Test

Run the app with 10 target users:

- 5 beginner makers.
- 5 intermediate makers.

Observe:

- Where they hesitate.
- Whether step tracking is clear.
- Whether materials tracking feels useful.
- Whether assistant tips feel helpful or annoying.
