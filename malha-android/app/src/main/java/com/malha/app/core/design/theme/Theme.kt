package com.malha.app.core.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import com.malha.app.core.preferences.AppTheme

private val LightColors = lightColorScheme(
    primary = MalhaGreenDark,
    onPrimary = MalhaSurface,
    primaryContainer = MalhaGreen,
    onPrimaryContainer = MalhaGreenDark,
    secondary = MalhaBlueDark,
    onSecondary = MalhaSurface,
    secondaryContainer = MalhaBlue,
    onSecondaryContainer = MalhaBlueDark,
    tertiary = MalhaAidiBlue,
    background = MalhaBackground,
    onBackground = MalhaTextPrimary,
    surface = MalhaSurface,
    onSurface = MalhaTextPrimary,
    surfaceVariant = MalhaBlue,
    onSurfaceVariant = MalhaTextSecondary
)

private val DarkColors = darkColorScheme(
    primary = MalhaGreenMedium,
    onPrimary = MalhaTextPrimary,
    primaryContainer = MalhaGreenDark,
    onPrimaryContainer = MalhaGreen,
    secondary = MalhaBlueMedium,
    onSecondary = MalhaTextPrimary,
    secondaryContainer = MalhaBlueDark,
    onSecondaryContainer = MalhaBlue,
    tertiary = MalhaAidiBlue,
    background = ColorTokens.DarkBackground,
    onBackground = MalhaBackground,
    surface = ColorTokens.DarkSurface,
    onSurface = MalhaBackground,
    surfaceVariant = ColorTokens.DarkSurfaceVariant,
    onSurfaceVariant = MalhaTextSecondary
)

@Composable
fun MalhaTheme(
    appTheme: AppTheme = AppTheme.SYSTEM,
    darkTheme: Boolean = isSystemInDarkTheme(),
    textSizeMultiplier: Float = 1.0f,
    content: @Composable () -> Unit
) {
    val colorScheme = when (appTheme) {
        AppTheme.LIGHT -> LightColors
        AppTheme.DARK -> DarkColors
        AppTheme.SYSTEM -> if (darkTheme) DarkColors else LightColors
        AppTheme.WARM -> LightColors.copy(background = ColorTokens.WarmBackground)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = getMalhaTypography(textSizeMultiplier),
        content = content
    )
}

private object ColorTokens {
    val DarkBackground = androidx.compose.ui.graphics.Color(0xFF121212)
    val DarkSurface = androidx.compose.ui.graphics.Color(0xFF1E1E1E)
    val DarkSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF2C2C2C)
    val WarmBackground = androidx.compose.ui.graphics.Color(0xFFFFF9F0)
}
