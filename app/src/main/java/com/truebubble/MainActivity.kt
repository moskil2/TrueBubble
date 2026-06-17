package com.truebubble

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.truebubble.data.SettingsRepository
import com.truebubble.ui.settings.SettingsViewModel
import com.truebubble.ui.theme.TrueBubbleTheme
import kotlinx.coroutines.flow.first
import androidx.compose.runtime.collectAsState
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

            TrueBubbleTheme(darkTheme = settings.darkTheme) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding(),
                    color = LocalAppColors.current.bg,
                ) {
                    AppNavHost()
                }
            }
        }
    }
}
