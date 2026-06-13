This document is mandatory and overrides default coding behavior.

Before making any code changes:

1. Summarize the relevant rules that apply.
2. Analyze the affected architecture.
3. Identify risks and edge cases.
4. Explain the implementation plan.

Then implement the solution following all rules in the constitution.

Do not take shortcuts.
Do not use hardcoded strings, colors, dimensions, URLs, secrets, or magic values.
Maintain production-quality code.
Only modify files that require changes.
Minimize token usage by showing only relevant code changes.

---

# AI Development Standards for Malha/MyWool

## Purpose

This document defines the mandatory engineering, architecture, UI/UX, security, performance, accessibility, maintainability, and code quality standards for this Android application.

All AI assistants, agents, bots, IDE copilots, and automated tools MUST read and follow this document before proposing, generating, refactoring, or modifying code.

When requirements conflict, this document takes precedence.

---

# Core Principles

## Rule 1: Never Take Shortcuts

Always implement production-quality solutions.

Do not:

* Use temporary fixes.
* Add technical debt.
* Hardcode values.
* Skip validation.
* Skip error handling.
* Ignore edge cases.
* Ignore accessibility.
* Ignore offline behavior.
* Ignore localization.
* Ignore security concerns.

Prefer maintainable, scalable, testable solutions even if they require more work.

---

## Rule 2: Preserve Existing Functionality

Before modifying code:

1. Analyze dependencies.
2. Analyze side effects.
3. Analyze navigation flows.
4. Analyze state management.
5. Analyze data synchronization.

Never remove functionality unless explicitly instructed.

---

## Rule 3: Fix Root Causes

Never patch symptoms.

Always:

* Identify root cause.
* Explain root cause.
* Implement root fix.
* Verify no regressions.

---

# Architecture Standards

## Required Architecture

Use:

* Clean Architecture
* MVVM
* Repository Pattern
* Use Cases
* Dependency Injection

Layers:

### Presentation

* Jetpack Compose
* ViewModels
* UI State classes

### Domain

* Use Cases
* Domain Models
* Business Rules

### Data

* Repository Implementations
* Remote Data Sources
* Local Data Sources
* DTO Mapping

Dependencies must flow inward only.

---

# Kotlin Standards

## Use Modern Kotlin

Use:

* Coroutines
* Flow
* StateFlow
* SharedFlow
* Data Classes
* Sealed Classes
* Value Classes where appropriate

Avoid:

* Global mutable state
* Blocking calls
* runBlocking
* Excessive inheritance
* God classes

---

## Null Safety

Prefer:

* Safe calls
* Explicit nullable types
* Defensive validation

Avoid:

* !!
* Unsafe casts

---

## Naming

Names must be:

* Descriptive
* Consistent
* Self-documenting

Avoid abbreviations unless universally known.

---

# Jetpack Compose Standards

## State Management

Use:

* Immutable UI state
* StateFlow
* rememberSaveable
* Lifecycle-aware collection

Avoid:

* Mutable shared state
* Business logic inside composables

---

## Composables

Composables must:

* Be small
* Be reusable
* Have a single responsibility

Extract reusable UI components.

---

## Previews

Every reusable composable should have previews.

---

# Material 3 Standards

Use Material 3.

Requirements:

* Dynamic color support when appropriate.
* Dark mode support.
* Light mode support.
* Warm light mode support.
* Material typography system.
* Material spacing system.
* Material motion patterns.

Do not mix Material 2 and Material 3.

---

# Design System

## Colors

All colors must originate from:

Theme.kt

Never:

* Use hardcoded colors.
* Use inline Color(...) values.

Use semantic colors:

* Primary
* Secondary
* Tertiary
* Surface
* Background
* Error

---

## Typography

Use:

* Google Fonts
* Material Typography

Never:

* Hardcode font sizes repeatedly.

Create reusable typography definitions.

---

## Icons

Use:

* Material Symbols
* Material Icons
* Custom Icons I made just for this project

Avoid making new custom icons unless necessary.

---

# Accessibility Requirements

Every feature must support:

* Screen readers
* Content descriptions
* Dynamic font scaling
* High contrast
* Touch targets >= 48dp

Accessibility is mandatory.

---

# Localization Standards

## No Hardcoded Strings

Never place user-facing text in code.

ALL text must be in:

res/values/strings.xml

Examples:

Correct:
stringResource(R.string.login)

Incorrect:
Text("Login")

---

## Translation Ready

Every user-facing string must support localization.

Avoid string concatenation.

Use placeholders.

Correct: <string name="welcome">Welcome %1$s</string>

Incorrect:
"Welcome " + name

---

# Navigation Standards

Use:

Navigation Compose

Requirements:

* Type-safe routes
* Centralized navigation
* Deep link support when relevant

Avoid navigation logic scattered across screens.

---

# Firebase Standards

Use Firebase only when necessary.

Preferred services:

* Authentication
* Firestore
* Cloud Messaging
* Crashlytics
* Analytics

---

## Firebase Security

Always:

* Validate server-side.
* Enforce Firestore Security Rules.
* Apply least privilege.

Never trust client data.

---

# Google Authentication Standards

Use:

Credential Manager

Avoid deprecated authentication methods.

Requirements:

* Secure token handling
* Proper sign out
* Session restoration
* Error recovery

---

# Offline-First Standards

Application must remain usable offline whenever possible.

Use:

* Room
* Local cache
* Repository synchronization

Data flow:

Local Database → UI

Network updates database.

UI should not depend directly on network availability.

---

# Synchronization Standards

Implement:

* Conflict handling
* Retry policies
* Exponential backoff
* Queue-based sync when necessary

Sync must survive app restarts.

---

# Database Standards

Use:

* Room
* Migrations
* DAO interfaces

Never:

* Lose user data during migrations
* Destructively recreate databases in production

Unless there are very specific orders to do so.

---

# Security Standards

Never:

* Store secrets in source code
* Store API keys in code
* Store tokens insecurely

Use:

* EncryptedSharedPreferences
* Android Keystore
* Secure storage mechanisms

---

# Performance Standards

Optimize for:

* Battery
* Memory
* Startup time
* Network usage

Avoid:

* Unnecessary recompositions
* Excessive allocations
* Main thread blocking

---

# Gradle Standards

Use:

* Version Catalogs
* Kotlin DSL
* Dependency centralization

Prefer:

libs.versions.toml

Avoid hardcoded dependency versions.

---

# Dependency Injection

Use:

Hilt

Requirements:

* Constructor injection
* Scoped dependencies
* Testable modules

Avoid service locators.

---

# Logging Standards

Use structured logging.

Never log:

* Passwords
* Tokens
* Personal data

Remove debug logs before release.

---

# Error Handling

Every operation must include:

* Error handling
* Recovery strategy
* User feedback

Never silently fail.

---

# Testing Standards

Minimum requirements:

* Unit tests
* ViewModel tests
* Repository tests

When appropriate:

* Integration tests
* UI tests

New critical business logic should include tests.

---

# Code Quality Standards

Before submitting changes:

Verify:

* No duplicated logic
* No dead code
* No warnings
* No TODO placeholders
* No temporary hacks

Refactor when necessary.

---

# Documentation Standards

For significant changes:

Document:

* Why
* Architecture impact
* Data flow impact
* Migration requirements

---

# AI Efficiency Rules

To reduce token usage:

Do not repeat existing code.

Only show changed files.

Only show modified functions when possible.

Provide concise explanations.

Avoid generating boilerplate already present in the project.

Reuse existing architecture and patterns.

Analyze before coding.

---

# Required AI Workflow

Before implementing:

1. Analyze requirements.
2. Analyze architecture.
3. Identify affected files.
4. Identify risks.
5. Propose approach.

Then implement.

After implementation:

1. Verify compilation.
2. Verify architecture compliance.
3. Verify localization.
4. Verify accessibility.
5. Verify offline behavior.
6. Verify security.
7. Verify tests.

---

# Mandatory Final Checklist

Before completing any task, verify:

* No hardcoded strings.
* No hardcoded colors.
* No hardcoded dimensions.
* Material 3 compliant.
* Compose best practices followed.
* Localization supported.
* Accessibility supported.
* Offline support considered.
* Security reviewed.
* Firebase rules respected.
* Architecture respected.
* Hilt used correctly.
* Coroutines used correctly.
* No duplicated code.
* No shortcuts taken.
* Production ready.
