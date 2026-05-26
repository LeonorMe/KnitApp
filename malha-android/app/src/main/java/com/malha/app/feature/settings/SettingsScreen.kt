package com.malha.app.feature.settings

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.malha.app.R
import com.malha.app.core.preferences.AppLanguage
import com.malha.app.core.preferences.AppTheme
import com.malha.app.core.preferences.AppUnits

@Composable
fun SettingsScreen(
    onNavigateToProfileEdit: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val googleSignInClient = remember(context) {
        val webClientId = context.resolveGoogleWebClientId()
        val optionsBuilder = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
        if (webClientId.isNotBlank()) {
            optionsBuilder.requestIdToken(webClientId)
        }
        GoogleSignIn.getClient(
            context,
            optionsBuilder.build()
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            runCatching {
                task.getResult(ApiException::class.java)
            }.onSuccess { account ->
                viewModel.signInWithGoogleToken(account.idToken)
            }.onFailure { error ->
                viewModel.showSignInError(error.message ?: "Google sign-in was cancelled.")
            }
        } else {
            viewModel.showSignInError("Google sign-in was cancelled.")
        }
    }

    fun launchGoogleAuth(mode: AuthMode) {
        if (context.resolveGoogleWebClientId().isBlank()) {
            viewModel.showSignInError("Google Sign-In needs a web client ID in google-services.json.")
            return
        }
        viewModel.beginAuth(mode)
        launcher.launch(googleSignInClient.signInIntent)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(PaddingValues(horizontal = 24.dp, vertical = 28.dp))
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Profile, sync, language, units, and theme preferences.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            AnimatedVisibility(visible = uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage.orEmpty(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
            AnimatedVisibility(visible = uiState.successMessage != null) {
                Text(
                    text = uiState.successMessage.orEmpty(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            ProfileCard(
                uiState = uiState,
                onSignIn = {
                    viewModel.clearMessages()
                    launchGoogleAuth(AuthMode.Login)
                },
                onCreateAccount = {
                    viewModel.clearMessages()
                    launchGoogleAuth(AuthMode.CreateAccount)
                },
                onSignOut = {
                    googleSignInClient.signOut()
                    viewModel.signOut()
                },
                onEditProfile = onNavigateToProfileEdit
            )

            SettingsSection(title = stringResource(R.string.section_appearance)) {
                ThemeSelector(
                    currentTheme = uiState.preferences.theme,
                    onThemeSelected = viewModel::updateTheme
                )
            }

            SettingsSection(title = stringResource(R.string.section_language)) {
                LanguageSelector(
                    currentLanguage = uiState.preferences.language,
                    onLanguageSelected = viewModel::updateLanguage
                )
            }

            SettingsSection(title = stringResource(R.string.section_units)) {
                UnitsSelector(
                    currentUnits = uiState.preferences.units,
                    onUnitsSelected = viewModel::updateUnits
                )
            }

            SettingsSection(title = "Accessibility") {
                Text(
                    text = "Adjust text size for better readability.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextSizeSelector(
                    currentMultiplier = uiState.preferences.textSizeMultiplier,
                    onMultiplierSelected = viewModel::updateTextSize
                )
            }
        }
    }
}

private fun android.content.Context.resolveGoogleWebClientId(): String {
    val resourceId = resources.getIdentifier(
        "default_web_client_id",
        "string",
        packageName
    )
    return if (resourceId != 0) getString(resourceId) else ""
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        content()
    }
}

@Composable
private fun ProfileCard(
    uiState: SettingsUiState,
    onSignIn: () -> Unit,
    onCreateAccount: () -> Unit,
    onSignOut: () -> Unit,
    onEditProfile: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(
                        imageVector = if (uiState.user == null) Icons.Default.AccountCircle else Icons.Default.CloudDone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = if (uiState.user == null) "Malha account" else "Connected account",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (uiState.user != null) {
                    TextButton(onClick = onEditProfile) {
                        Text("Edit")
                    }
                }
            }

            AnimatedContent(
                targetState = uiState.user,
                label = "settings-account-state"
            ) { user ->
                if (user == null) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Use Google to create or access your Malha account. Your profile is stored in Firebase Auth and Firestore.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = onCreateAccount,
                            enabled = !uiState.isSigningIn,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                when {
                                    uiState.isSigningIn && uiState.authMode == AuthMode.CreateAccount -> stringResource(R.string.action_signing_in)
                                    else -> stringResource(R.string.action_create_google_account)
                                }
                            )
                        }
                        OutlinedButton(
                            onClick = onSignIn,
                            enabled = !uiState.isSigningIn,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                when {
                                    uiState.isSigningIn && uiState.authMode == AuthMode.Login -> stringResource(R.string.action_signing_in)
                                    else -> stringResource(R.string.action_sign_in_google)
                                }
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = uiState.preferences.username ?: user.displayName ?: "Signed in",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (uiState.preferences.bio != null) {
                            Text(
                                text = uiState.preferences.bio,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = user.email ?: user.id,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        ) {
                            Text(
                                text = "Firestore sync ready",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        OutlinedButton(
                            onClick = onSignOut,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.action_sign_out))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSelector(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = when (currentTheme) {
                AppTheme.SYSTEM -> stringResource(R.string.theme_system)
                AppTheme.LIGHT -> stringResource(R.string.theme_light)
                AppTheme.DARK -> stringResource(R.string.theme_dark)
                AppTheme.WARM -> stringResource(R.string.theme_warm)
            },
            onValueChange = {},
            readOnly = true,
            label = { Text("Theme") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            AppTheme.entries.forEach { theme ->
                DropdownMenuItem(
                    text = {
                        Text(
                            when (theme) {
                                AppTheme.SYSTEM -> stringResource(R.string.theme_system)
                                AppTheme.LIGHT -> stringResource(R.string.theme_light)
                                AppTheme.DARK -> stringResource(R.string.theme_dark)
                                AppTheme.WARM -> stringResource(R.string.theme_warm)
                            }
                        )
                    },
                    onClick = {
                        onThemeSelected(theme)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageSelector(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = when (currentLanguage) {
                AppLanguage.SYSTEM -> "System Default"
                AppLanguage.ENGLISH_UK -> "English (UK)"
                AppLanguage.ENGLISH_US -> "English (US)"
                AppLanguage.PORTUGUESE_PT -> "Português (PT)"
                AppLanguage.SPANISH_ES -> "Español (ES)"
            },
            onValueChange = {},
            readOnly = true,
            label = { Text("App Language") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            AppLanguage.entries.forEach { lang ->
                DropdownMenuItem(
                    text = {
                        Text(
                            when (lang) {
                                AppLanguage.SYSTEM -> "System Default"
                                AppLanguage.ENGLISH_UK -> "English (UK)"
                                AppLanguage.ENGLISH_US -> "English (US)"
                                AppLanguage.PORTUGUESE_PT -> "Português (PT)"
                                AppLanguage.SPANISH_ES -> "Español (ES)"
                            }
                        )
                    },
                    onClick = {
                        onLanguageSelected(lang)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitsSelector(
    currentUnits: AppUnits,
    onUnitsSelected: (AppUnits) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = when (currentUnits) {
                AppUnits.METRIC -> stringResource(R.string.unit_metric)
                AppUnits.IMPERIAL -> stringResource(R.string.unit_imperial)
            },
            onValueChange = {},
            readOnly = true,
            label = { Text("Measurement Units") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            AppUnits.entries.forEach { unit ->
                DropdownMenuItem(
                    text = {
                        Text(
                            when (unit) {
                                AppUnits.METRIC -> stringResource(R.string.unit_metric)
                                AppUnits.IMPERIAL -> stringResource(R.string.unit_imperial)
                            }
                        )
                    },
                    onClick = {
                        onUnitsSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun TextSizeSelector(
    currentMultiplier: Float,
    onMultiplierSelected: (Float) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Text Size",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Adjust the app's text size for better readability.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Slider(
                value = currentMultiplier,
                onValueChange = onMultiplierSelected,
                valueRange = 0.8f..1.6f,
                steps = 4,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Small", style = MaterialTheme.typography.labelSmall)
                Text("Normal", style = MaterialTheme.typography.labelSmall)
                Text("Large", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
