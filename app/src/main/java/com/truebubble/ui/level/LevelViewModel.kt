package com.truebubble.ui.level

import android.app.Application
import androidx.compose.ui.graphics.Color
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
import kotlin.math.abs

fun bubbleColorFromIndex(idx: Int): Color = when (idx) {
    1 -> Color(0xFFFF7A00)
    2 -> Color(0xFFFFD600)
    3 -> Color(0xFF00CCFF)
    4 -> Color(0xFFFF4444)
    5 -> Color(0xFF1C1C1E)
    6 -> Color(0xFF9B30FF)
    else -> Color.White
}

data class LevelUiState(
    val pitch: Float = 0f,
    val roll: Float = 0f,
    val pitchStatus: AngleStatus = AngleStatus.OK,
    val rollStatus: AngleStatus = AngleStatus.OK,
    val pitchWithinTolerance: Boolean = true,
    val rollWithinTolerance: Boolean = true,
    val bothLevel: Boolean = true,
    val locked: Boolean = false,
    val calibrated: Boolean = false,
    val rememberedPitch: Float? = null,
    val rememberedRoll: Float? = null,
    val highContrastBubble: Boolean = false,
    val bubbleColor: Color = Color.White,
)

enum class AngleStatus { OK, WARN, ERROR }

fun angleStatus(deg: Float): AngleStatus = when {
    abs(deg) <= 0.5f -> AngleStatus.OK
    abs(deg) <= 2.0f -> AngleStatus.WARN
    else -> AngleStatus.ERROR
}

class LevelViewModel(application: Application) : AndroidViewModel(application) {

    private val calibrationRepo = CalibrationRepository(application)
    private val settingsRepo = SettingsRepository(application)
    private val pitchFeedback = FeedbackController(application)
    private val rollFeedback = FeedbackController(application)

    private val _state = MutableStateFlow(LevelUiState())
    val state: StateFlow<LevelUiState> = _state

    init {
        viewModelScope.launch {
            combine(
                orientationFlow(application),
                calibrationRepo.calibrationFlow,
                settingsRepo.settingsFlow
            ) { orientation, cal, settings ->
                Triple(orientation, cal, settings)
            }.collect { (orientation, cal, settings) ->
                val pitch = orientation.pitch - cal.pitchOffset
                val roll = orientation.roll - cal.rollOffset
                val pitchOk = abs(pitch) <= 0.5f
                val rollOk = abs(roll) <= 0.5f
                val both = pitchOk && rollOk
                val newState = LevelUiState(
                    pitch = pitch,
                    roll = roll,
                    pitchStatus = angleStatus(pitch),
                    rollStatus = angleStatus(roll),
                    pitchWithinTolerance = pitchOk,
                    rollWithinTolerance = rollOk,
                    bothLevel = both,
                    locked = _state.value.locked,
                    calibrated = cal.lastDate.isNotEmpty(),
                    rememberedPitch = _state.value.rememberedPitch,
                    rememberedRoll = _state.value.rememberedRoll,
                    highContrastBubble = settings.highContrastBubble,
                    bubbleColor = bubbleColorFromIndex(settings.bubbleColorIndex),
                )
                pitchFeedback.onAngleUpdate(pitchOk, settings.soundOnLevel, settings.vibrateOnLevel)
                rollFeedback.onAngleUpdate(rollOk, settings.soundOnLevel, settings.vibrateOnLevel)
                _state.value = newState
            }
        }
    }

    fun toggleLock() {
        val current = _state.value
        if (current.locked) {
            _state.update { it.copy(locked = false, rememberedPitch = null, rememberedRoll = null) }
            pitchFeedback.resetDebounce()
            rollFeedback.resetDebounce()
        } else {
            _state.update { it.copy(locked = true, rememberedPitch = current.pitch, rememberedRoll = current.roll) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pitchFeedback.release()
        rollFeedback.release()
    }

}
