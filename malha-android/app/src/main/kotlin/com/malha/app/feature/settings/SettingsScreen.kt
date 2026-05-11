package com.malha.app.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malha.app.R
import com.malha.app.ui.theme.ThemeMode
import com.malha.app.ui.theme.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(themeViewModel: ThemeViewModel = viewModel()) {
    val context = LocalContext.current
    val currentTheme = themeViewModel.themeMode

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_settings)) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.section_appearance),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            ThemeOptionRow(
                label = stringResource(R.string.theme_system),
                selected = currentTheme == ThemeMode.SYSTEM,
                onClick = { themeViewModel.setTheme(context, ThemeMode.SYSTEM) }
            )
            ThemeOptionRow(
                label = stringResource(R.string.theme_light),
                selected = currentTheme == ThemeMode.LIGHT,
                onClick = { themeViewModel.setTheme(context, ThemeMode.LIGHT) }
            )
            ThemeOptionRow(
                label = stringResource(R.string.theme_dark),
                selected = currentTheme == ThemeMode.DARK,
                onClick = { themeViewModel.setTheme(context, ThemeMode.DARK) }
            )
            ThemeOptionRow(
                label = stringResource(R.string.theme_warm),
                selected = currentTheme == ThemeMode.WARM,
                onClick = { themeViewModel.setTheme(context, ThemeMode.WARM) }
            )
        }
    }
}

@Composable
private fun ThemeOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
