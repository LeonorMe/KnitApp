package com.malha.app.core.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import com.malha.app.core.preferences.AppTheme

private val LightColors = lightColorScheme(
    primary = Sage,
    onPrimary = Cream,
    secondary = Indigo,
    onSecondary = Cream,
    tertiary = Clay,
    background = Cream,
    onBackground = Ink,
    surface = Cream,
    onSurface = Ink,
    surfaceVariant = ColorTokens.SurfaceVariant,
    onSurfaceVariant = WarmGray
)

private val DarkColors = darkColorScheme(
    primary = SageLight,
    onPrimary = InkDark,
    secondary = Lavender,
    onSecondary = InkDark,
    tertiary = ClayDark,
    background = InkDark,
    onBackground = Cream,
    surface = InkDark,
    onSurface = Cream,
    surfaceVariant = Ink,
    onSurfaceVariant = WarmGray
)

@Composable
fun MalhaTheme(
    appTheme: AppTheme = AppTheme.SYSTEM,
    darkTheme: Boolean = isSystemInDarkTheme(),
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
        typography = MalhaTypography,
        content = content
    )
}

private object ColorTokens {
    val SurfaceVariant = androidx.compose.ui.graphics.Color(0xFFEDE1D2)
    val WarmBackground = androidx.compose.ui.graphics.Color(0xFFFFF9F0)
}

