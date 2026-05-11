# Malha Android App Docs

This folder is the planning and implementation guide for the Android app.

The official product direction for this project is:

- App name: **Malha**
- AI assistant / mascot: **Aidi**
- Platform: **native Android**
- Language: **Kotlin**
- UI: **Jetpack Compose**
- Design system: **Material 3**
- Google services: **Firebase Authentication / Google Sign-In** and **Cloud Firestore**

Implementation should still keep app display strings centralized, but Malha/Aidi is the correct brand pair for the Android app.

## Documents

1. [Product Vision](01-product-vision.md)
2. [Implementation Guidelines](02-implementation-guidelines.md)
3. [Android Architecture](03-android-architecture.md)
4. [UX/UI Guidelines](04-ux-ui-guidelines.md)
5. [Data Model](05-data-model.md)
6. [AI And Backend Strategy](06-ai-and-backend-strategy.md)
7. [MVP Roadmap](07-mvp-roadmap.md)
8. [Gamification Economy](08-gamification-economy.md)
9. [Brand Identity](09-brand-identity.md)
10. [Implementation Plan](10-implementation-plan.md)
11. [Firebase Setup](11-firebase-setup.md)

## Product Principle

Reward creation, not consumption.

The app should help users finish handmade projects, organize materials, plan realistically, and use AI as a careful assistant rather than as an unquestioned authority.



---

11/05/2026

Listed directory malha-android
Viewed README.md:1-28
Listed directory docs
Viewed 10-implementation-plan.md:1-161

**Malha – Android App Overview**

Malha is an Android companion app for knitting and crochet enthusiasts. Its goal is to give users an **offline‑first, project‑centric workspace** for managing patterns, materials, and project progress, while laying the groundwork for future AI‑assisted features (the “Aidi” assistant). The app is built with **Kotlin**, **Jetpack Compose**, and **Material 3**, and currently runs a fully local data stack (Room database) with a clean architecture that will later allow synchronization with Firebase.

---

### Core Purpose  
- **Track knitting/crochet projects** from start to finish.  
- **Store and edit patterns** (including step‑by‑step instructions).  
- **Manage materials** (yarn, tools, quantities).  
- Provide **local insights** via the rule‑based “Aidi” engine.  
- Prepare for **cloud sync & Google Sign‑In** (future release).  

---

### Most Important Features (implemented or planned)

| Feature | Status | Description |
|--------|--------|-------------|
| **Offline‑first data layer** | ✅ Implemented | Room database with entities for Projects, Patterns, Steps, Materials, etc.; DAOs & repositories; seeded starter patterns. |
| **Jetpack Compose UI** | ✅ Implemented | Material 3 theme, bottom navigation, data‑backed screens for Home, Projects, Patterns, Materials. |
| **Home & Projects screens** | ✅ Implemented | Lists active projects, shows progress, handles empty/loading states, create‑archive flows. |
| **Pattern creation & editor** | ✅ Implemented | List of patterns, detail view, manual pattern creation with step editor. |
| **Project execution flow** | ✅ Implemented | Current step screen, navigation (next/prev), mark‑done, per‑step notes, progress calculation, resume after restart. |
| **Materials management** | ✅ Implemented | List/add/edit materials, assign to projects, track planned vs. used quantities. |
| **Aidi local insights** | ✅ Implemented (rule‑based) | Insight cards on Home (e.g., “project almost finished”), no external AI needed yet. |
| **Voice command support** | ✅ Implemented (basic) | Speech parser for common actions (next/prev step, mark done, repeat step) with manual fallback. |
| **Firebase & Google Sign‑In** | 🚧 Planned | Add `google-services.json`, enable Google authentication, sync Room data to Firestore, keep offline usability. |
| **Future AI assistance** | 🚧 Planned | Integration of remote AI (Aidi) for richer suggestions once backend is ready. |

---

### Architectural Highlights  

- **Clean architecture** with domain models separate from DB entities.  
- **Repository pattern** abstracts data sources (local Room, future Firestore).  
- **Modular codebase**: `app/` contains the Android module; `docs/` holds product, UX, and roadmap documentation.  
- **Bottom navigation** provides quick access to the four main sections (Home, Projects, Patterns, Materials).  

---

### Roadmap (high‑level)

1. **Room foundation** (already done).  
2. **Connect UI screens** to the local data layer.  
3. **Add creation/edit flows** for projects, patterns, materials.  
4. **Project execution** with step navigation and notes.  
5. **Aidi local insights** and voice commands.  
6. **Google Services integration** (Auth + Firestore sync).  

---

**In short**, Malha is a fully functional, offline‑first knitting & crochet app that lets users manage their creative projects end‑to‑end, with a polished Compose UI and a roadmap toward cloud sync and AI‑enhanced assistance