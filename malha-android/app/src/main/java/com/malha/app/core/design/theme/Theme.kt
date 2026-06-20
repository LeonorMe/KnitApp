package com.malha.app.core.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import com.malha.app.core.preferences.AppTheme

private val LightColors = lightColorScheme(
    primary = BlueGreen,
    onPrimary = BrightSnow,
    primaryContainer = LightTeal,
    onPrimaryContainer = StormyTeal,

    secondary = HarvestGold,
    onSecondary = BrightSnow,
    secondaryContainer = SoftGoldBackground,
    onSecondaryContainer = OldGold,

    tertiary = PacificBlue,
    onTertiary = BrightSnow,
    tertiaryContainer = SoftBlueBackground,
    onTertiaryContainer = StormyTeal,

    error = Raspberry,
    onError = BrightSnow,

    background = BrightSnow,
    onBackground = InkBlack,

    surface = BrightSnow,
    onSurface = InkBlack,

    surfaceContainer = SoftBlueBackground,

    surfaceVariant = SoftBlueBackground,
    onSurfaceVariant = StormyTeal,

    outline = BlueGreen.copy(alpha = 0.5f),
    outlineVariant = BlueGreen.copy(alpha = 0.2f)
)

private val DarkColors = darkColorScheme(
    primary = PacificBlue,
    onPrimary = InkBlack,

    primaryContainer = StormyTeal,
    onPrimaryContainer = BrightSnow,

    secondary = HarvestGold,
    onSecondary = InkBlack,

    secondaryContainer = OldGold,
    onSecondaryContainer = BrightSnow,

    tertiary = BlueGreen,
    onTertiary = BrightSnow,

    tertiaryContainer = DarkSurfaceVariant,
    onTertiaryContainer = PacificBlue,

    error = Raspberry,
    onError = BrightSnow,

    background = InkBlack,
    onBackground = BrightSnow,

    surface = DarkSurface,
    onSurface = BrightSnow,

    surfaceContainer = DarkSurface,

    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = BrightSnow.copy(alpha = 0.75f),

    outline = PacificBlue.copy(alpha = 0.5f),
    outlineVariant = PacificBlue.copy(alpha = 0.25f)
)

private val WarmColors = lightColorScheme(
    primary = WarmPrimary,
    onPrimary = WarmOnPrimary,
    primaryContainer = WarmPrimaryContainer,
    onPrimaryContainer = WarmOnPrimaryContainer,

    secondary = WarmSecondary,
    onSecondary = WarmOnSecondary,
    secondaryContainer = WarmSecondaryContainer,
    onSecondaryContainer = WarmOnSecondaryContainer,

    tertiary = WarmTertiary,
    onTertiary = WarmOnTertiary,
    tertiaryContainer = WarmTertiaryContainer,
    onTertiaryContainer = WarmOnTertiaryContainer,

    error = WarmError,
    onError = WarmOnError,

    background = WarmBackground,
    onBackground = WarmInk,

    surface = WarmSurface,
    onSurface = WarmInk,

    surfaceContainer = WarmSurfaceContainer,

    surfaceVariant = WarmSurfaceVariant,
    onSurfaceVariant = WarmInkMuted,

    outline = WarmPrimary.copy(alpha = 0.5f),
    outlineVariant = WarmPrimary.copy(alpha = 0.2f)
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
        AppTheme.WARM -> WarmColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = getMalhaTypography(textSizeMultiplier),
        content = content
    )
}
