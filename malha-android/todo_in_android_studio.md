Listed directory app
Searched web: "PlaceholderScreen.kt"
Searched web: "PlaceholderScreen"
Listed directory app
Listed directory core
Searched web: "PlaceholderScreen"
Listed directory app
Searched web: "Malha PlaceholderScreen.kt MalhaApp"

**What’s happening**

Your build is failing for two different reasons:

| Error | Why it appears | How to fix it |
|-------|----------------|----------------|
| **`PlaceholderScreen.kt` – Composable call errors** | A temporary *placeholder screen* was added to satisfy a navigation destination, but the composable isn’t defined (or is defined in the wrong package) and the `ExtendedFloatingActionButton` call is being used outside a `@Composable` context. | Create a simple `PlaceholderScreen` composable (or replace the placeholder destinations with real screens) and make sure any `ExtendedFloatingActionButton` calls live inside a `@Composable` function. |
| **`default_web_client_id` unresolved** | The Google Services Gradle plugin injects `default_web_client_id` from the `google-services.json`. The file was missing, you added a copy of the *example* JSON, but the placeholder values (`000000000000`, `example-api-key`) don’t provide a real `default_web_client_id`. | Replace the placeholder JSON with a **real** Firebase project configuration (or generate a new `google-services.json` from the Firebase console). After that, run a clean build. |

Below is a concrete plan to get the app compiling again.

---

## 🔧 Step‑by‑step fix plan

### 1️⃣ Add a real `google-services.json`
1. Open the Firebase console for the **Malha** project (or create a new one).  
2. In *Project Settings → General → Your apps*, add an Android app with the package name `com.malha.app`.  
3. Download the generated `google-services.json`.  
4. Overwrite the existing `malha-android/app/google-services.json` with this file (the one you created from the example is only a placeholder).  
5. Run a clean rebuild:

```
./gradlew clean assembleDebug
```

*Result*: `default_web_client_id` will be generated and the “Unresolved reference” error disappears.

### 2️⃣ Implement `PlaceholderScreen.kt`
Create a minimal composable that can be used for any “TODO” destination.

**File:** `app/src/main/kotlin/com/malha/app/PlaceholderScreen.kt`  

```kotlin
package com.malha.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun PlaceholderScreen(message: String = "Coming soon…") {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
```

Now replace the `TODO()` entries in `MalhaApp.kt` navigation items:

```kotlin
MalhaDestination.PatternDetail -> { PlaceholderScreen("Pattern Detail") }
MalhaDestination.ProjectExecution -> { PlaceholderScreen("Project Execution") }
```

### 3️⃣ Fix the `ExtendedFloatingActionButton` usage
The error shows the call is happening **outside a `@Composable`** scope. Locate the offending code (search for `ExtendedFloatingActionButton(`) and wrap it in a composable, e.g.:

```kotlin
@Composable
fun AddProjectFab(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        text = { Text("Add Project") },
        icon = { Icon(Icons.Default.Add, contentDescription = null) }
    )
}
```

Then place `AddProjectFab { /* navigation to create project */ }` inside a composable (e.g., the Home screen).

### 4️⃣ Verify everything builds
Run the full build again:

```
./gradlew assembleDebug
```

If the command finishes without errors, launch the app on an emulator or device:

```
./gradlew installDebug
```

---

## 📂 New / Modified Files

| Path | Change |
|------|--------|
| `app/google-services.json` | Overwritten with real Firebase config (downloaded from console). |
| `app/src/main/kotlin/com/malha/app/PlaceholderScreen.kt` | **NEW** – minimal composable placeholder. |
| `app/src/main/kotlin/com/malha/app/MalhaApp.kt` | Updated navigation destinations to use `PlaceholderScreen`. |
| `app/src/main/kotlin/com/malha/app/<any FAB file>` | Wrapped `ExtendedFloatingActionButton` in a `@Composable`. |

---

### Next steps for you
1. **Generate/download** the actual `google-services.json` as described.  
2. **Commit** the new placeholder file and the minor code updates.  
3. Run the build and verify the app launches.

If you hit any new errors after these changes, share the build output and I’ll help you troubleshoot further