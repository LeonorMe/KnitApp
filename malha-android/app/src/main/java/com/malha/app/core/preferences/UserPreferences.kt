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

enum class ReminderRegularity {
    DAILY, WEEKLY
}

data class UserPreferences(
    val theme: AppTheme = AppTheme.SYSTEM,
    val language: AppLanguage = AppLanguage.SYSTEM,
    val units: AppUnits = AppUnits.METRIC,
    val textSizeMultiplier: Float = 1.0f,
    val username: String? = null,
    val bio: String? = null,
    val profilePhotoUri: String? = null,
    val reminderEnabled: Boolean = true,
    val reminderRegularity: ReminderRegularity = ReminderRegularity.DAILY,
    val reminderDaysOfWeek: Set<Int> = emptySet(),
    val reminderHour: Int = 18,
    val reminderMinute: Int = 30,
    val lastDailyRewardTimestamp: Long = 0L,
    val lastSquareChallengeTimestamp: Long = 0L
)
