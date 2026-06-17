package com.truebubble.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore("settings")

data class AppSettings(
    val darkTheme: Boolean = true,
    val soundOnLevel: Boolean = true,
    val vibrateOnLevel: Boolean = true,
    val highContrastBubble: Boolean = true,
    val bubbleColorIndex: Int = 0,
    val languageCode: String = "en",
)

class SettingsRepository(private val context: Context) {

    private val KEY_DARK_THEME = booleanPreferencesKey("dark_theme")
    private val KEY_SOUND = booleanPreferencesKey("sound_on_level")
    private val KEY_VIBRATE = booleanPreferencesKey("vibrate_on_level")
    private val KEY_HIGH_CONTRAST = booleanPreferencesKey("high_contrast_bubble")
    private val KEY_BUBBLE_COLOR = intPreferencesKey("bubble_color_index")
    private val KEY_LANGUAGE = stringPreferencesKey("language_code")

    val settingsFlow: Flow<AppSettings> = context.settingsDataStore.data.map { prefs ->
        AppSettings(
            darkTheme = prefs[KEY_DARK_THEME] ?: true,
            soundOnLevel = prefs[KEY_SOUND] ?: true,
            vibrateOnLevel = prefs[KEY_VIBRATE] ?: true,
            highContrastBubble = prefs[KEY_HIGH_CONTRAST] ?: true,
            bubbleColorIndex = prefs[KEY_BUBBLE_COLOR] ?: 0,
            languageCode = prefs[KEY_LANGUAGE] ?: "en",
        )
    }

    suspend fun setDarkTheme(dark: Boolean) {
        context.settingsDataStore.edit { it[KEY_DARK_THEME] = dark }
    }

    suspend fun setSound(enabled: Boolean) {
        context.settingsDataStore.edit { it[KEY_SOUND] = enabled }
    }

    suspend fun setVibrate(enabled: Boolean) {
        context.settingsDataStore.edit { it[KEY_VIBRATE] = enabled }
    }

    suspend fun setHighContrast(enabled: Boolean) {
        context.settingsDataStore.edit { it[KEY_HIGH_CONTRAST] = enabled }
    }

    suspend fun setBubbleColor(idx: Int) {
        context.settingsDataStore.edit { it[KEY_BUBBLE_COLOR] = idx }
    }

    suspend fun setLanguage(code: String) {
        context.settingsDataStore.edit { it[KEY_LANGUAGE] = code }
    }
}
