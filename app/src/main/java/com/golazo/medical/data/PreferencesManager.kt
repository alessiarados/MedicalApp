package com.golazo.medical.data

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("golazo_settings", Context.MODE_PRIVATE)

    // Theme state flows for live updates - separate for Doctor and Player
    private val _doctorThemeFlow = MutableStateFlow(prefs.getString("doctor_theme", "Dark") ?: "Dark")
    val doctorThemeFlow: StateFlow<String> = _doctorThemeFlow.asStateFlow()
    
    private val _playerThemeFlow = MutableStateFlow(prefs.getString("player_theme", "Light") ?: "Light")
    val playerThemeFlow: StateFlow<String> = _playerThemeFlow.asStateFlow()

    // Notifications
    var pushNotificationsEnabled: Boolean
        get() = prefs.getBoolean("push_notifications", true)
        set(value) = prefs.edit().putBoolean("push_notifications", value).apply()

    var emailNotificationsEnabled: Boolean
        get() = prefs.getBoolean("email_notifications", true)
        set(value) = prefs.edit().putBoolean("email_notifications", value).apply()

    var injuryAlertsEnabled: Boolean
        get() = prefs.getBoolean("injury_alerts", true)
        set(value) = prefs.edit().putBoolean("injury_alerts", value).apply()

    // Language
    var language: String
        get() = prefs.getString("language", "English") ?: "English"
        set(value) = prefs.edit().putString("language", value).apply()

    // Appearance - Doctor theme (default Dark)
    var doctorTheme: String
        get() = prefs.getString("doctor_theme", "Dark") ?: "Dark"
        set(value) {
            prefs.edit().putString("doctor_theme", value).apply()
            _doctorThemeFlow.value = value
        }
    
    // Appearance - Player theme (default Light, always Light)
    var playerTheme: String
        get() = prefs.getString("player_theme", "Light") ?: "Light"
        set(value) {
            prefs.edit().putString("player_theme", value).apply()
            _playerThemeFlow.value = value
        }
}
