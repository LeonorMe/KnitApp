package com.malha.app.core.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

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

@Composable
fun MalhaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = MalhaTypography,
        content = content
    )
}

private object ColorTokens {
    val SurfaceVariant = androidx.compose.ui.graphics.Color(0xFFEDE1D2)
}

