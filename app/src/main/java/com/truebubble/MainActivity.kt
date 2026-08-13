package com.truebubble

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.truebubble.ui.settings.SettingsViewModel
import com.truebubble.ui.theme.TrueBubbleTheme
import androidx.compose.material3.Surface
import com.truebubble.ui.theme.LocalAppColors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            val settingsVm: SettingsViewModel = viewModel()
            val settings by settingsVm.settings.collectAsStateWithLifecycle()

            // Ikony paska statusu/nawigacji muszą reagować na motyw z ustawień
            // aplikacji, nie na motyw systemu — enableEdgeToEdge() ustawia je
            // tylko raz przy starcie, więc trzeba je odświeżać ręcznie.
            val view = LocalView.current
            SideEffect {
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !settings.darkTheme
                insetsController.isAppearanceLightNavigationBars = !settings.darkTheme
            }

            TrueBubbleTheme(darkTheme = settings.darkTheme) {
                // Tło wypełnia cały ekran (także pod przezroczystymi paskami systemowymi),
                // dzięki czemu ich kolor podąża za motywem; treść jest wcięta osobno.
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = LocalAppColors.current.bg,
                ) {
                    Box(modifier = Modifier.safeDrawingPadding()) {
                        AppNavHost()
                    }
                }
            }
        }
    }
}
