package com.malha.app.core.preferences

enum class AppTheme {
    SYSTEM, LIGHT, DARK, WARM
}

enum class AppLanguage(val localeTag: String) {
    SYSTEM(""),
    ENGLISH_UK("en-GB"),
    ENGLISH_US("en-US"),
    PORTUGUESE_PT("pt-PT"),
    SPANISH_ES("es-ES")
}

enum class AppUnits {
    METRIC, IMPERIAL
}

data class UserPreferences(
    val theme: AppTheme = AppTheme.SYSTEM,
    val language: AppLanguage = AppLanguage.SYSTEM,
    val units: AppUnits = AppUnits.METRIC,
    val username: String? = null,
    val bio: String? = null
)
