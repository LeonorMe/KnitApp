package com.malha.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.malha.app.core.design.theme.MalhaTheme

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.malha.app.core.app.appContainer
import com.malha.app.core.preferences.AppLanguage
import java.util.Locale

import androidx.compose.runtime.LaunchedEffect
import com.malha.app.core.app.appContainer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            val preferences by this.appContainer.preferencesRepository.userPreferences.collectAsState(
                initial = com.malha.app.core.preferences.UserPreferences()
            )
            
            // Handle Locale change
            LaunchedEffect(preferences.language) {
                applyLocale(preferences.language)
            }

            MalhaTheme(
                appTheme = preferences.theme,
                textSizeMultiplier = preferences.textSizeMultiplier
            ) {
                MalhaApp()
            }
        }
    }

    private fun applyLocale(language: AppLanguage) {
        if (language == AppLanguage.SYSTEM) return
        
        val locale = Locale(language.localeTag)
        val currentLocale = resources.configuration.locales[0]
        
        if (currentLocale.language != locale.language) {
            Locale.setDefault(locale)
            val config = resources.configuration
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)
            // Note: This is a bit legacy, but works for simple cases. 
            // In a real app, you'd use AppCompatDelegate.setApplicationLocales()
        }
    }
}

