package com.malha.app.ui.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

enum class ThemeMode { LIGHT, DARK, SYSTEM, WARM }

private val Context.dataStore by preferencesDataStore(name = "settings")
private val THEME_KEY = stringPreferencesKey("theme_mode")

class ThemeViewModel : ViewModel() {
    var themeMode by mutableStateOf(ThemeMode.SYSTEM)
        private set

    fun initTheme(context: Context) {
        viewModelScope.launch {
            val saved = context.dataStore.data.map { it[THEME_KEY] }.first()
            themeMode = when (saved) {
                "LIGHT" -> ThemeMode.LIGHT
                "DARK" -> ThemeMode.DARK
                "WARM" -> ThemeMode.WARM
                else -> ThemeMode.SYSTEM
            }
        }
    }

    fun setTheme(context: Context, mode: ThemeMode) {
        themeMode = mode
        viewModelScope.launch {
            context.dataStore.edit { it[THEME_KEY] = mode.name }
        }
    }
}

private val LightColors = lightColorScheme(
    primary = Color(0xFF006688),
    secondary = Color(0xFF88AACC),
    background = Color(0xFFF5F5F5),
    surface = Color(0xFFFFFFFF)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF66B2CC),
    secondary = Color(0xFF557788),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E)
)

private val WarmColors = darkColorScheme(
    primary = Color(0xFFCC8855),
    secondary = Color(0xFFB26644),
    background = Color(0xFF2B1D14),
    surface = Color(0xFF3B2A1E),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onBackground = Color(0xFFFFEEDD),
    onSurface = Color(0xFFFFEEDD)
)

@Composable
fun MalhaTheme(
    themeViewModel: ThemeViewModel = viewModel(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { themeViewModel.initTheme(context) }

    val colors = when (themeViewModel.themeMode) {
        ThemeMode.LIGHT -> LightColors
        ThemeMode.DARK -> DarkColors
        ThemeMode.WARM -> WarmColors
        ThemeMode.SYSTEM -> if (isSystemInDarkTheme()) DarkColors else LightColors
    }
    
    MaterialTheme(
        colorScheme = colors,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
