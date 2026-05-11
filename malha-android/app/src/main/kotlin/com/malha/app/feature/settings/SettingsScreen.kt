package com.malha.app.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malha.app.ui.theme.ThemeMode
import com.malha.app.ui.theme.ThemeViewModel

@Composable
fun SettingsScreen(themeViewModel: ThemeViewModel = viewModel()) {
    val context = LocalContext.current
    val currentTheme = themeViewModel.themeMode

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = "Appearance",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        ThemeOptionRow(
            label = "System Default",
            selected = currentTheme == ThemeMode.SYSTEM,
            onClick = { themeViewModel.setTheme(context, ThemeMode.SYSTEM) }
        )
        ThemeOptionRow(
            label = "Light",
            selected = currentTheme == ThemeMode.LIGHT,
            onClick = { themeViewModel.setTheme(context, ThemeMode.LIGHT) }
        )
        ThemeOptionRow(
            label = "Dark",
            selected = currentTheme == ThemeMode.DARK,
            onClick = { themeViewModel.setTheme(context, ThemeMode.DARK) }
        )
        ThemeOptionRow(
            label = "Warm (Reduce Blue Light)",
            selected = currentTheme == ThemeMode.WARM,
            onClick = { themeViewModel.setTheme(context, ThemeMode.WARM) }
        )
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
