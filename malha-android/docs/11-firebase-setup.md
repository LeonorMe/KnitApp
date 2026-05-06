# Firebase Setup

Malha uses Google/Firebase services for online identity and cloud data:

- Firebase Authentication
- Google Sign-In
- Cloud Firestore
- Firebase Storage later for project photos and generated assets

The app remains offline-first with Room as the local source of truth.

## Android App

Package name:

```text
com.malha.app
```

## Required Firebase Console Setup

1. Create a Firebase project.
2. Add an Android app using package name `com.malha.app`.
3. Download the generated `google-services.json`.
4. Put the real file here:

```text
malha-android/app/google-services.json
```

Do not use `google-services.example.json` for real builds. It only documents the expected shape.

## Authentication

Enable these providers:

- Google
- Email/password later if needed

The project currently includes a Firebase auth wrapper:

- `AuthService`
- `FirebaseAuthService`

Google Sign-In UI is implemented on the Settings/Profile screen. It uses the `default_web_client_id` generated from the real `google-services.json`, then signs into Firebase with the Google ID token.

## Firestore

Initial collection names are centralized in `CloudCollections`.

Planned collections:

- `users`
- `projects`
- `patterns`
- `patternSteps`
- `materials`
- `projectMaterials`
- `variants`
- `reviews`
- `aiRequests`

The current Firestore wrapper can:

- Observe a user profile.
- Upsert a user profile.

Project/material/pattern sync should be added behind repository interfaces so screens continue to work offline.

## Gradle

Firebase setup is declared in:

- `gradle/libs.versions.toml`
- root `build.gradle.kts`
- `app/build.gradle.kts`

The app applies:

- `com.google.gms.google-services`

And depends on:

- Firebase BOM
- Firebase Auth
- Cloud Firestore
- kotlinx-coroutines-play-services
