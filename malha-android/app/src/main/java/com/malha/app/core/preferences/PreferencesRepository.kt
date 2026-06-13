package com.malha.app.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesRepository(private val context: Context) {
    private object Keys {
        val THEME = stringPreferencesKey("theme")
        val LANGUAGE = stringPreferencesKey("language")
        val UNITS = stringPreferencesKey("units")
        val TEXT_SIZE = floatPreferencesKey("text_size")
        val USERNAME = stringPreferencesKey("username")
        val BIO = stringPreferencesKey("bio")
        val PROFILE_PHOTO_URI = stringPreferencesKey("profile_photo_uri")
        val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val REMINDER_REGULARITY = stringPreferencesKey("reminder_regularity")
        val REMINDER_DAYS_OF_WEEK = stringSetPreferencesKey("reminder_days_of_week")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
        val LAST_REWARD_TS = androidx.datastore.preferences.core.longPreferencesKey("last_reward_ts")
        val LAST_SQUARE_TS = androidx.datastore.preferences.core.longPreferencesKey("last_square_ts")
    }

    val userPreferences: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        UserPreferences(
            theme = AppTheme.valueOf(preferences[Keys.THEME] ?: AppTheme.SYSTEM.name),
            language = AppLanguage.valueOf(preferences[Keys.LANGUAGE] ?: AppLanguage.SYSTEM.name),
            units = AppUnits.valueOf(preferences[Keys.UNITS] ?: AppUnits.METRIC.name),
            textSizeMultiplier = preferences[Keys.TEXT_SIZE] ?: 1.0f,
            username = preferences[Keys.USERNAME],
            bio = preferences[Keys.BIO],
            profilePhotoUri = preferences[Keys.PROFILE_PHOTO_URI],
            reminderEnabled = preferences[Keys.REMINDER_ENABLED] ?: true,
            reminderRegularity = ReminderRegularity.valueOf(preferences[Keys.REMINDER_REGULARITY] ?: ReminderRegularity.DAILY.name),
            reminderDaysOfWeek = preferences[Keys.REMINDER_DAYS_OF_WEEK]?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet(),
            reminderHour = preferences[Keys.REMINDER_HOUR] ?: 18,
            reminderMinute = preferences[Keys.REMINDER_MINUTE] ?: 30,
            lastDailyRewardTimestamp = preferences[Keys.LAST_REWARD_TS] ?: 0L,
            lastSquareChallengeTimestamp = preferences[Keys.LAST_SQUARE_TS] ?: 0L
        )
    }

    suspend fun updateTheme(theme: AppTheme) {
        context.dataStore.edit { it[Keys.THEME] = theme.name }
    }

    suspend fun updateLanguage(language: AppLanguage) {
        context.dataStore.edit { it[Keys.LANGUAGE] = language.name }
    }

    suspend fun updateUnits(units: AppUnits) {
        context.dataStore.edit { it[Keys.UNITS] = units.name }
    }

    suspend fun updateTextSize(multiplier: Float) {
        context.dataStore.edit { it[Keys.TEXT_SIZE] = multiplier }
    }

    suspend fun updateProfile(username: String?, bio: String?) {
        context.dataStore.edit { preferences ->
            if (username != null) preferences[Keys.USERNAME] = username
            else preferences.remove(Keys.USERNAME)
            
            if (bio != null) preferences[Keys.BIO] = bio
            else preferences.remove(Keys.BIO)
        }
    }

    suspend fun updateProfilePhoto(profilePhotoUri: String?) {
        context.dataStore.edit { preferences ->
            if (profilePhotoUri != null) preferences[Keys.PROFILE_PHOTO_URI] = profilePhotoUri
            else preferences.remove(Keys.PROFILE_PHOTO_URI)
        }
    }

    suspend fun updateReminder(
        enabled: Boolean,
        regularity: ReminderRegularity,
        daysOfWeek: Set<Int>,
        hour: Int,
        minute: Int
    ) {
        context.dataStore.edit { preferences ->
            preferences[Keys.REMINDER_ENABLED] = enabled
            preferences[Keys.REMINDER_REGULARITY] = regularity.name
            preferences[Keys.REMINDER_DAYS_OF_WEEK] = daysOfWeek.map { it.toString() }.toSet()
            preferences[Keys.REMINDER_HOUR] = hour.coerceIn(0, 23)
            preferences[Keys.REMINDER_MINUTE] = minute.coerceIn(0, 59)
        }
    }

    suspend fun updateLastDailyReward(timestamp: Long) {
        context.dataStore.edit { it[Keys.LAST_REWARD_TS] = timestamp }
    }

    suspend fun updateLastSquareChallenge(timestamp: Long) {
        context.dataStore.edit { it[Keys.LAST_SQUARE_TS] = timestamp }
    }

    suspend fun clearProfile() {
        context.dataStore.edit { preferences ->
            preferences.remove(Keys.USERNAME)
            preferences.remove(Keys.BIO)
            preferences.remove(Keys.PROFILE_PHOTO_URI)
        }
    }
}
