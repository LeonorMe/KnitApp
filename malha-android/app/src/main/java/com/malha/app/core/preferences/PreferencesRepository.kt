package com.malha.app.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
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

    suspend fun updateLastDailyReward(timestamp: Long) {
        context.dataStore.edit { it[Keys.LAST_REWARD_TS] = timestamp }
    }
}
