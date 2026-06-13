# Pattern to Project Execution Workflow

This plan outlines the end-to-end implementation of taking a pattern, configuring a project with the required materials, and executing its steps, culminating in an AI reward.

## User Review Required
> [!IMPORTANT]
> The database schema will be updated to version 8 to accommodate the new `weightCategory` field in materials, which is necessary to filter yarns properly as requested (e.g., "4 medium").
> The voice instructions are explicitly requested as "icons for later", so they will be implemented as visual stubs in this iteration.
> We will seed the local database with the `example-pattern_knit_square.json` on app startup if it doesn't exist.

## Open Questions
> [!WARNING]
> Do you want the store suggestions to deduct coins immediately when clicked, or just add them to the "Shopping List" as plain text for now?
> How should "marking 20 stitches as done" look? I propose a simple "+1" and "+10" button interface for steps that involve multiple stitches or rows. Let me know if you prefer a slider or a long-press action.

## Proposed Changes

### Database & Models Layer
#### [MODIFY] `core/database/entity/MaterialEntity.kt`
- Add `val weightCategory: String? = null` to support filtering by "4 medium".
#### [MODIFY] `core/database/DatabaseProvider.kt`
- Add Migration from version 7 to 8.
#### [MODIFY] `domain/model/Material.kt`
- Reflect the new `weightCategory` property.
#### [MODIFY] `core/database/seed/DatabaseSeeder.kt`
- Parse and insert the `example-pattern_knit_square.json` into `PatternEntity` and `PatternStepEntity` tables upon initialization.

---

### Materials & Patterns UI
#### [MODIFY] `feature/materials/MaterialsScreen.kt`
- Add the "Patterns" tab to the top navigation.
- Render a list of available patterns in the new tab.
#### [MODIFY] `feature/patterns/detail/PatternDetailScreen.kt`
- Enhance the UI to show required materials, gauge, and sizes.
- Add "Add to new project" action that creates a draft project and routes to `ProjectSettingsScreen`.

---

### Project Settings (New Screen)
#### [NEW] `feature/projects/ProjectSettingsScreen.kt`
- Allows customizing the Project Name, Color, and Size.
- **Materials Matcher**: Dynamically reads the pattern's required materials (e.g., Yarn: "medium 4", Needles: 4.0mm) and presents dropdowns filtered by the user's existing stash.
- **Store Integration**: If the user lacks a material, display an option to "Buy from store (X coins)" which adds it to their shopping list.
- **Start Project**: Saves settings and navigates to the Execution Screen.

---

### Project Execution
#### [MODIFY] `feature/execution/ProjectExecutionScreen.kt`
- Completely overhaul the UI to focus strictly on the current step instruction.
- **Navigation Controls**: Left/Right arrows for step traversal, Up arrow to return to Settings.
- **Voice Stubs**: Microphone and Speaker icon buttons placed prominently for future Voice AI integration.
- **Counter Tool**: For steps with multiple rows/stitches (e.g., "p20" or "25 rows"), show a counter UI with increment buttons.
- **Completion & Gamification**: Upon reaching the final step, trigger a "Congratulations" dialog, award the pattern's designated coin reward to `UserPreferences`, and display a button offering to "Create a Social Post" for the finished project.

## Verification Plan

### Automated Tests
- Build verification (`.\gradlew assembleDebug`).

### Manual Verification
- Launch the app, go to Materials -> Patterns.
- Select "Simple Knit Square in purl" and click "Add to new project".
- Verify the dropdowns in Project Settings filter materials accurately.
- Verify the shopping list suggestion appears if materials are missing.
- Step through the execution UI, mark steps as complete, and confirm the coin reward triggers at the end.
