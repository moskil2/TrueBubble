package com.truebubble.ui.circlelevel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.truebubble.data.SettingsRepository
import com.truebubble.feedback.FeedbackController
import com.truebubble.sensor.CalibrationRepository
import com.truebubble.sensor.orientationFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import com.truebubble.ui.level.AngleStatus
import com.truebubble.ui.level.angleStatus
import com.truebubble.ui.level.bubbleColorFromIndex
import com.truebubble.ui.level.isMetallicColorIndex
import kotlin.math.sqrt

data class CircleLevelUiState(
    val pitch: Float = 0f,
    val roll: Float = 0f,
    val pitchStatus: AngleStatus = AngleStatus.OK,
    val rollStatus: AngleStatus = AngleStatus.OK,
    val withinTolerance: Boolean = true,
    val locked: Boolean = false,
    val calibrated: Boolean = false,
    val rememberedPitch: Float? = null,
    val rememberedRoll: Float? = null,
    val highContrastBubble: Boolean = false,
    val bubbleColor: Color = Color.White,
    val isMetallicBubble: Boolean = false,
)

class CircleLevelViewModel(application: Application) : AndroidViewModel(application) {

    private val calibrationRepo = CalibrationRepository(application)
    private val settingsRepo = SettingsRepository(application)
    private val feedback = FeedbackController(application)

    private val _state = MutableStateFlow(CircleLevelUiState())
    val state: StateFlow<CircleLevelUiState> = _state

    init {
        viewModelScope.launch {
            combine(
                orientationFlow(application),
                calibrationRepo.calibrationFlow,
                settingsRepo.settingsFlow,
            ) { orientation, cal, settings -> Triple(orientation, cal, settings) }
                .collect { (orientation, cal, settings) ->
                    val pitch = orientation.pitch - cal.pitchOffset
                    val roll = orientation.roll - cal.rollOffset
                    val deviation = sqrt(pitch * pitch + roll * roll)
                    val within = deviation <= 0.5f
                    feedback.onAngleUpdate(within, settings.soundOnLevel, settings.vibrateOnLevel)
                    _state.value = CircleLevelUiState(
                        pitch = pitch,
                        roll = roll,
                        pitchStatus = angleStatus(pitch),
                        rollStatus = angleStatus(roll),
                        withinTolerance = within,
                        locked = _state.value.locked,
                        calibrated = cal.lastDate.isNotEmpty(),
                        rememberedPitch = _state.value.rememberedPitch,
                        rememberedRoll = _state.value.rememberedRoll,
                        highContrastBubble = settings.highContrastBubble,
                        bubbleColor = bubbleColorFromIndex(settings.bubbleColorIndex),
                        isMetallicBubble = isMetallicColorIndex(settings.bubbleColorIndex),
                    )
                }
        }
    }

    fun toggleLock() {
        val current = _state.value
        if (current.locked) {
            _state.update { it.copy(locked = false, rememberedPitch = null, rememberedRoll = null) }
            feedback.resetDebounce()
        } else {
            _state.update { it.copy(locked = true, rememberedPitch = current.pitch, rememberedRoll = current.roll) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        feedback.release()
    }

}
