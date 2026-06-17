package com.truebubble.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.truebubble.data.AppSettings
import com.truebubble.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = SettingsRepository(application)

    val settings: StateFlow<AppSettings> = repo.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    fun setDarkTheme(dark: Boolean) = viewModelScope.launch { repo.setDarkTheme(dark) }
    fun setSound(enabled: Boolean) = viewModelScope.launch { repo.setSound(enabled) }
    fun setVibrate(enabled: Boolean) = viewModelScope.launch { repo.setVibrate(enabled) }
    fun setHighContrast(enabled: Boolean) = viewModelScope.launch { repo.setHighContrast(enabled) }
    fun setBubbleColor(idx: Int) = viewModelScope.launch { repo.setBubbleColor(idx) }
    fun setLanguage(code: String) = viewModelScope.launch { repo.setLanguage(code) }
}
