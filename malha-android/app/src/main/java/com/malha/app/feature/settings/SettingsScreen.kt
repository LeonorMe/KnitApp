package com.malha.app.feature.settings

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
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
import com.malha.app.core.design.component.ImagePlaceholder
import com.malha.app.core.preferences.AppLanguage
import com.malha.app.core.preferences.AppTheme
import com.malha.app.core.preferences.AppUnits
import com.malha.app.core.preferences.ReminderRegularity
import java.text.DateFormatSymbols
import java.util.Calendar

@Composable
fun SettingsScreen(
    onNavigateToProfileEdit: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.updateReminder(
            enabled = granted,
            regularity = uiState.preferences.reminderRegularity,
            daysOfWeek = uiState.preferences.reminderDaysOfWeek,
            hour = uiState.preferences.reminderHour,
            minute = uiState.preferences.reminderMinute
        )
    }
    val profilePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) viewModel.updateProfilePhoto(uri.toString())
        }
    )
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
                val message = if (error is ApiException) {
                    context.getString(
                        R.string.google_sign_in_error_status_code,
                        error.statusCode,
                        error.localizedMessage
                    )
                } else {
                    error.message ?: context.getString(R.string.google_sign_in_was_cancelled)
                }
                viewModel.showSignInError(message)
            }
        } else {
            viewModel.showSignInError(context.getString(R.string.google_sign_in_was_cancelled))
        }
    }

    fun launchGoogleAuth(mode: AuthMode) {
        if (context.resolveGoogleWebClientId().isBlank()) {
            viewModel.showSignInError(context.getString(R.string.google_sign_in_needs_a_web_client_id_in_google_services_json))
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
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
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
                onEditProfile = onNavigateToProfileEdit,
                onChangePhoto = {
                    profilePhotoPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
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

            SettingsSection(title = stringResource(R.string.project_reminder)) {
                DailyReminderSettings(
                    enabled = uiState.preferences.reminderEnabled,
                    regularity = uiState.preferences.reminderRegularity,
                    daysOfWeek = uiState.preferences.reminderDaysOfWeek,
                    hour = uiState.preferences.reminderHour,
                    minute = uiState.preferences.reminderMinute,
                    onEnabledChange = { enabled ->
                        if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            viewModel.updateReminder(
                                enabled = enabled,
                                regularity = uiState.preferences.reminderRegularity,
                                daysOfWeek = uiState.preferences.reminderDaysOfWeek,
                                hour = uiState.preferences.reminderHour,
                                minute = uiState.preferences.reminderMinute
                            )
                        }
                    },
                    onRegularityChange = { regularity ->
                        val newDays = if (regularity == ReminderRegularity.WEEKLY && uiState.preferences.reminderDaysOfWeek.isEmpty()) {
                            setOf(Calendar.SUNDAY)
                        } else {
                            uiState.preferences.reminderDaysOfWeek
                        }
                        viewModel.updateReminder(
                            enabled = uiState.preferences.reminderEnabled,
                            regularity = regularity,
                            daysOfWeek = newDays,
                            hour = uiState.preferences.reminderHour,
                            minute = uiState.preferences.reminderMinute
                        )
                    },
                    onDaysChange = { days ->
                        viewModel.updateReminder(
                            enabled = uiState.preferences.reminderEnabled,
                            regularity = uiState.preferences.reminderRegularity,
                            daysOfWeek = days,
                            hour = uiState.preferences.reminderHour,
                            minute = uiState.preferences.reminderMinute
                        )
                    },
                    onTimeChange = { hour, minute ->
                        viewModel.updateReminder(
                            enabled = uiState.preferences.reminderEnabled,
                            regularity = uiState.preferences.reminderRegularity,
                            daysOfWeek = uiState.preferences.reminderDaysOfWeek,
                            hour = hour,
                            minute = minute
                        )
                    }
                )
            }

            SettingsSection(title = stringResource(R.string.accessibility)) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DailyReminderSettings(
    enabled: Boolean,
    regularity: ReminderRegularity,
    daysOfWeek: Set<Int>,
    hour: Int,
    minute: Int,
    onEnabledChange: (Boolean) -> Unit,
    onRegularityChange: (ReminderRegularity) -> Unit,
    onDaysChange: (Set<Int>) -> Unit,
    onTimeChange: (Int, Int) -> Unit
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.continue_project_reminder), style = MaterialTheme.typography.titleLarge)
                    /*Text(
                        "A nudge to continue one project, at a time you choose.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )*/
                }
                Switch(
                    checked = enabled,
                    onCheckedChange = onEnabledChange
                )
            }

            if (enabled) {
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = when (regularity) {
                            ReminderRegularity.DAILY -> stringResource(R.string.daily)
                            ReminderRegularity.WEEKLY -> stringResource(R.string.weekly)
                        },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.regularity)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        ReminderRegularity.entries.forEach { reg ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        when (reg) {
                                            ReminderRegularity.DAILY -> stringResource(R.string.daily)
                                            ReminderRegularity.WEEKLY -> stringResource(R.string.weekly)
                                        }
                                    )
                                },
                                onClick = {
                                    onRegularityChange(reg)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                if (regularity == ReminderRegularity.WEEKLY) {
                    val weekdays = listOf(
                        Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
                        Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY
                    )
                    val symbols = DateFormatSymbols().shortWeekdays
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        weekdays.forEach { day ->
                            val isSelected = daysOfWeek.contains(day)
                            Surface(
                                modifier = Modifier
                                    .size(36.dp),
                                shape = androidx.compose.foundation.shape.CircleShape,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                onClick = {
                                    val newDays = if (isSelected) {
                                        daysOfWeek - day
                                    } else {
                                        daysOfWeek + day
                                    }
                                    // Prevent empty selection
                                    if (newDays.isNotEmpty()) {
                                        onDaysChange(newDays)
                                    }
                                }
                            ) {
                                Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
                                    Text(
                                        text = symbols[day].take(1),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                var showTimePicker by remember { mutableStateOf(false) }

                if (showTimePicker) {
                    val timePickerState = rememberTimePickerState(
                        initialHour = hour,
                        initialMinute = minute,
                        is24Hour = true
                    )
                    
                    AlertDialog(
                        onDismissRequest = { showTimePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                onTimeChange(timePickerState.hour, timePickerState.minute)
                                showTimePicker = false
                            }) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showTimePicker = false }) {
                                Text("Cancel")
                            }
                        },
                        text = {
                            TimePicker(state = timePickerState)
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.time),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(onClick = { showTimePicker = true }) {
                        val formattedTime = String.format(java.util.Locale.getDefault(), "%02d:%02d", hour, minute)
                        Text(formattedTime)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileCard(
    uiState: SettingsUiState,
    onSignIn: () -> Unit,
    onCreateAccount: () -> Unit,
    onSignOut: () -> Unit,
    onEditProfile: () -> Unit,
    onChangePhoto: () -> Unit
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
            Column(
                modifier = Modifier.fillMaxWidth(),
                //horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(
                        imageVector = if (uiState.user == null || uiState.user.isAnonymous) Icons.Default.AccountCircle else Icons.Default.CloudDone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = if (uiState.user == null || uiState.user.isAnonymous) stringResource(
                            R.string.malha_account
                        ) else stringResource(R.string.connected_account),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (uiState.user != null && !uiState.user.isAnonymous) {
                    Row (horizontalArrangement = Arrangement.spacedBy(10.dp)){
                        /*TextButton(onClick = onChangePhoto) {
                            Text(stringResource(R.string.photo))
                        }*/
                        OutlinedButton(
                            onClick = onEditProfile,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.edit)) // todo
                        }
                        /*TextButton(onClick = onEditProfile) {
                            Text(stringResource(R.string.edit))
                        }*/
                    }

                }

            }

            AnimatedContent(
                targetState = uiState.user,
                label = "settings-account-state"
            ) { user ->
                if (user == null || user.isAnonymous) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = stringResource(R.string.use_google_to_create_or_access_your_account),
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(modifier = Modifier.size(72.dp)) {
                                ImagePlaceholder(
                                    label = uiState.preferences.username ?: user.displayName ?: stringResource(
                                        R.string.user
                                    ),
                                    imageUri = uiState.preferences.profilePhotoUri ?: user.photoUrl,
                                    size = 72.dp
                                )
                            }
                            /*OutlinedButton(
                                onClick = onChangePhoto,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(R.string.change_profile_photo))
                            }*/
                        }
                        Text(
                            text = uiState.preferences.username ?: user.displayName ?: stringResource(
                                R.string.signed_in
                            ),
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
                                text = stringResource(R.string.firestore_sync_ready),
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
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
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
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
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
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
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
