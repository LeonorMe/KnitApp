# Pattern to Project Execution Workflow Tasks

## 1. Database & Models
- [ ] Add `weightCategory` to `MaterialEntity.kt` and `Material.kt`.
- [ ] Update `DatabaseProvider.kt` with Migration 7_to_8.
- [ ] Seed the `example-pattern_knit_square.json` into local DB (`DatabaseSeeder.kt`).

## 2. Materials & Patterns UI
- [ ] Update `MaterialsScreen.kt` to include the "Patterns" tab.
- [ ] Render pattern list in Patterns tab.
- [ ] Update `PatternDetailScreen.kt` UI with materials/sizes info.
- [ ] Add "Add to new project" logic routing to `ProjectSettingsScreen`.

## 3. Project Settings Screen
- [ ] Create `ProjectSettingsScreen.kt`.
- [ ] Migrate Color picker logic from `ProjectExecutionScreen`.
- [ ] Add Materials Matcher Dropdowns (Yarn filtering, Needle filtering).
- [ ] Add Store/Shopping List integration (plain text fallback).

## 4. Project Execution Screen
- [ ] Rebuild `ProjectExecutionScreen.kt` layout for step-by-step instructions.
- [ ] Add Navigation controls (Next/Prev step, Back to settings).
- [ ] Add Voice stub icons (Mic/Speaker).
- [ ] Add +1 / +10 Counter buttons for rows/stitches.
- [ ] Add Theme quick toggle (Dark/Light/Warm).
- [ ] Add "Keep Screen On" toggle.
- [ ] Add Gamification reward logic upon finish (give coins, suggest publication).

## 5. Verification
- [ ] Verify build and manually test flow.
