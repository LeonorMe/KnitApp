# AI And Backend Strategy

## Product Position

AI is an assistant layer, not the core product.

The core app must work without AI. AI should make planning, interpretation, and learning easier, but it must not be required for following a project.

## Feasible AI Features

Strong candidates:

- Explain a pattern step.
- Answer technique questions.
- Convert text into structured pattern steps.
- Estimate yarn ranges.
- Suggest projects from existing materials.
- Adapt simple patterns using gauge input.
- Voice question transcription and command understanding.

Experimental candidates:

- Generate a draft pattern from one or two photos.
- Create step-by-step visual instructions.
- Fully automatic sizing changes.

## Image-To-Pattern Reality

Modern multimodal AI can often identify:

- Garment type.
- Stitch category.
- Texture style.
- Likely construction method.

It cannot reliably guarantee:

- Exact stitch counts.
- Row counts.
- Gauge.
- Complex shaping.
- Accurate sizing.

Therefore image-to-pattern should be labelled experimental and produce editable drafts only.

## AI Architecture

Future architecture:

```text
Android App
    |
Backend API
    |
AI Service
    |
External LLM / Vision / Speech APIs
```

The AI service should be separate from core backend logic so it can evolve without breaking project tracking.

## Retrieval Before Fine-Tuning

Recommended first AI improvement method:

- Store validated patterns, corrections, and community fixes.
- Retrieve relevant trusted examples.
- Ask the AI to answer using those examples.

Fine-tuning can come later, after enough high-quality validated data exists.

## Backend Phases

## Phase 1: Local-First Foundation

MVP can start local-only while the app foundation is built:

- Room database.
- No accounts.
- No sync.
- Included starter patterns.

## Phase 2: Google Services Backend

Add:

- Firebase Authentication.
- Google Sign-In.
- Cloud Firestore sync.
- Cloud backup.
- Pattern sharing.
- Reviews.
- Public/friends visibility.

Selected stack:

- Android native app in Kotlin.
- Firebase Authentication for identity.
- Cloud Firestore for cloud data and shared state.
- Firebase Storage later for images and project photos.
- Cloud Functions later if server-side validation, marketplace logic, or AI request mediation is needed.

Firestore collections can start with:

- users
- projects
- patterns
- patternSteps
- materials
- projectMaterials
- variants
- reviews
- aiRequests

## Phase 3: Community And Marketplace

Add:

- Pattern variants.
- Creator uploads.
- Payments.
- Moderation tools.
- People-approved ranking.

## Phase 4: AI Service

Add:

- AI assistant.
- Pattern parser.
- Yarn estimator.
- Stash planner.
- Image analysis beta.

## Firebase Data Categories

Future Firestore-backed data areas:

- Authentication and user profile.
- User preferences.
- Project sync.
- Pattern sharing.
- Variants and forks.
- Reviews and validation.
- Material backup.
- AI request audit trail.

## Privacy Guidelines

- Ask permission before sending patterns, images, or project data to AI services.
- Let users delete exported AI data where possible.
- Clearly separate local-only data from cloud data.
- Do not use private user patterns as public training data without explicit consent.
