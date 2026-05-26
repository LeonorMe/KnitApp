package com.malha.app.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
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
        val DAILY_REMINDER_ENABLED = booleanPreferencesKey("daily_reminder_enabled")
        val DAILY_REMINDER_HOUR = intPreferencesKey("daily_reminder_hour")
        val DAILY_REMINDER_MINUTE = intPreferencesKey("daily_reminder_minute")
        val LAST_REWARD_TS = androidx.datastore.preferences.core.longPreferencesKey("last_reward_ts")
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
            dailyReminderEnabled = preferences[Keys.DAILY_REMINDER_ENABLED] ?: false,
            dailyReminderHour = preferences[Keys.DAILY_REMINDER_HOUR] ?: 18,
            dailyReminderMinute = preferences[Keys.DAILY_REMINDER_MINUTE] ?: 0,
            lastDailyRewardTimestamp = preferences[Keys.LAST_REWARD_TS] ?: 0L
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

    suspend fun updateDailyReminder(enabled: Boolean, hour: Int, minute: Int) {
        context.dataStore.edit { preferences ->
            preferences[Keys.DAILY_REMINDER_ENABLED] = enabled
            preferences[Keys.DAILY_REMINDER_HOUR] = hour.coerceIn(0, 23)
            preferences[Keys.DAILY_REMINDER_MINUTE] = minute.coerceIn(0, 59)
        }
    }

    suspend fun updateLastDailyReward(timestamp: Long) {
        context.dataStore.edit { it[Keys.LAST_REWARD_TS] = timestamp }
    }
}
