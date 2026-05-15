package com.malha.app.core.design.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

fun getMalhaTypography(scale: Float = 1.0f): Typography {
    return Typography(
        headlineLarge = Typography().headlineLarge.copy(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = (30 * scale).sp,
            lineHeight = (36 * scale).sp
        ),
        headlineMedium = Typography().headlineMedium.copy(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = (24 * scale).sp,
            lineHeight = (32 * scale).sp
        ),
        titleLarge = Typography().titleLarge.copy(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = (22 * scale).sp,
            lineHeight = (28 * scale).sp
        ),
        titleMedium = Typography().titleMedium.copy(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = (18 * scale).sp,
            lineHeight = (24 * scale).sp
        ),
        bodyLarge = Typography().bodyLarge.copy(
            fontFamily = FontFamily.SansSerif,
            fontSize = (18 * scale).sp,
            lineHeight = (26 * scale).sp
        ),
        bodyMedium = Typography().bodyMedium.copy(
            fontFamily = FontFamily.SansSerif,
            fontSize = (16 * scale).sp,
            lineHeight = (24 * scale).sp
        ),
        labelLarge = Typography().labelLarge.copy(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = (14 * scale).sp,
            lineHeight = (20 * scale).sp
        )
    )
}

val MalhaTypography = getMalhaTypography()
