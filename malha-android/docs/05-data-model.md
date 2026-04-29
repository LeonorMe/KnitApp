# Data Model

## MVP Entities

## UserPreferences

Local settings for the app.

Fields:

- id
- language
- unitSystem
- knittingNotation
- crochetTerminology
- themeMode

## Project

A user's active or completed craft project.

Fields:

- id
- name
- craftType: knitting, crochet, other
- patternId nullable
- status: planned, active, paused, completed, archived
- currentStepIndex
- progressPercent
- startedAt nullable
- completedAt nullable
- createdAt
- updatedAt

## Pattern

A reusable set of instructions.

Fields:

- id
- title
- description
- craftType
- difficulty
- sourceType: manual, included, imported, aiDraft, community
- verificationStatus: personal, aiDraft, communityReviewed, peopleApproved
- isPremium
- createdAt
- updatedAt

## PatternStep

One instruction inside a pattern.

Fields:

- id
- patternId
- orderIndex
- rowNumber nullable
- instruction
- stepType nullable
- repeatInfo nullable
- createdAt
- updatedAt

## ProjectStepProgress

Project-specific progress for a pattern step.

Fields:

- id
- projectId
- patternStepId
- isDone
- doneAt nullable
- note nullable

## Material

Yarn, needles, hooks, or other tools.

Fields:

- id
- type: yarn, needle, hook, accessory
- name
- color nullable
- fiber nullable
- weight nullable
- quantity
- unit
- lengthMeters nullable
- costCents nullable
- purchasedAt nullable
- createdAt
- updatedAt

## ProjectMaterial

Assignment of materials to projects.

Fields:

- id
- projectId
- materialId
- plannedQuantity nullable
- usedQuantity nullable

## Post-MVP Entities

## PatternVariant

A modified version of a pattern.

Fields:

- id
- parentPatternId
- title
- changeSummary
- authorUserId
- createdAt

## Review

Community validation for patterns.

Fields:

- id
- patternId
- userId
- rating
- comment
- verifiedCompletion
- createdAt

## CraftSession

Tracked active work session.

Fields:

- id
- projectId
- startedAt
- endedAt
- durationMinutes
- stepsCompleted
- rewardCoinsEarned

## RewardLedgerEntry

Economy transaction history.

Fields:

- id
- userId
- projectId nullable
- type
- amount
- reason
- createdAt

## AIRequest

Audit trail for AI-generated content.

Fields:

- id
- userId
- requestType
- inputSummary
- outputSummary
- confidence nullable
- createdAt

## Important Model Rule

Pattern content and project progress must stay separate.

The same pattern can be used by many projects, but each project has its own current step, notes, and completion state.
