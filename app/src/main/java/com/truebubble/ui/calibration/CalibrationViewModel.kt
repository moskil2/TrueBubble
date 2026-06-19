package com.truebubble.ui.calibration

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.truebubble.data.SettingsRepository
import com.truebubble.sensor.CalibrationRepository
import com.truebubble.sensor.orientationFlow
import com.truebubble.ui.level.bubbleColorFromIndex
import com.truebubble.ui.level.isMetallicColorIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlin.math.round

data class CalibrationUiState(
    val pitchOffset: Float = 0f,
    val rollOffset: Float = 0f,
    val lastCalibrationDate: String = "",
    val isCalibrating: Boolean = false,
    val showSuccess: Boolean = false,
    val pitchInputText: String = "0.00",
    val rollInputText: String = "0.00",
    val livePitch: Float = 0f,
    val liveRoll: Float = 0f,
    val bubbleColor: Color = Color.White,
    val isMetallicBubble: Boolean = false,
    val isPitchEditing: Boolean = false,
    val isRollEditing: Boolean = false,
)

class CalibrationViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = CalibrationRepository(application)
    private val settingsRepo = SettingsRepository(application)

    private val _state = MutableStateFlow(CalibrationUiState())
    val state: StateFlow<CalibrationUiState> = _state

    init {
        viewModelScope.launch {
            combine(
                orientationFlow(getApplication()),
                repo.calibrationFlow,
                settingsRepo.settingsFlow,
            ) { o, cal, settings -> Triple(o, cal, settings) }
            .collect { (o, cal, settings) ->
                val s = _state.value
                _state.value = s.copy(
                    pitchOffset = cal.pitchOffset,
                    rollOffset = cal.rollOffset,
                    lastCalibrationDate = cal.lastDate,
                    pitchInputText = if (s.isCalibrating || s.isPitchEditing) s.pitchInputText else "%.2f".format(cal.pitchOffset),
                    rollInputText = if (s.isCalibrating || s.isRollEditing) s.rollInputText else "%.2f".format(cal.rollOffset),
                    livePitch = if (!s.isCalibrating) o.pitch - cal.pitchOffset else s.livePitch,
                    liveRoll = if (!s.isCalibrating) o.roll - cal.rollOffset else s.liveRoll,
                    bubbleColor = bubbleColorFromIndex(settings.bubbleColorIndex),
                    isMetallicBubble = isMetallicColorIndex(settings.bubbleColorIndex),
                )
            }
        }
    }

    fun startCalibration() {
        if (_state.value.isCalibrating) return
        _state.value = _state.value.copy(isCalibrating = true, showSuccess = false)
        viewModelScope.launch {
            val samples = orientationFlow(getApplication()).take(50).toList()
            val avgPitch = samples.map { it.pitch }.average().toFloat()
            val avgRoll = samples.map { it.roll }.average().toFloat()
            repo.save(avgPitch, avgRoll)
            _state.value = _state.value.copy(isCalibrating = false, showSuccess = true)
            delay(2000)
            _state.value = _state.value.copy(showSuccess = false)
        }
    }

    fun incrementPitch() = adjustPitch(+0.01f)
    fun decrementPitch() = adjustPitch(-0.01f)

    fun setPitchText(text: String) {
        _state.value = _state.value.copy(pitchInputText = text, isPitchEditing = true)
    }

    fun commitPitchText() {
        _state.value = _state.value.copy(isPitchEditing = false)
        val v = _state.value.pitchInputText.replace(",", ".").toFloatOrNull() ?: return
        viewModelScope.launch { repo.setPitchOffset(round(v * 100f) / 100f) }
    }

    private fun adjustPitch(delta: Float) {
        val newVal = round((_state.value.pitchOffset + delta) * 100f) / 100f
        _state.value = _state.value.copy(isPitchEditing = false)
        viewModelScope.launch { repo.setPitchOffset(newVal) }
    }

    fun incrementRoll() = adjustRoll(+0.01f)
    fun decrementRoll() = adjustRoll(-0.01f)

    fun setRollText(text: String) {
        _state.value = _state.value.copy(rollInputText = text, isRollEditing = true)
    }

    fun commitRollText() {
        _state.value = _state.value.copy(isRollEditing = false)
        val v = _state.value.rollInputText.replace(",", ".").toFloatOrNull() ?: return
        viewModelScope.launch { repo.setRollOffset(round(v * 100f) / 100f) }
    }

    private fun adjustRoll(delta: Float) {
        val newVal = round((_state.value.rollOffset + delta) * 100f) / 100f
        _state.value = _state.value.copy(isRollEditing = false)
        viewModelScope.launch { repo.setRollOffset(newVal) }
    }

    fun resetCalibration() {
        viewModelScope.launch { repo.reset() }
    }
}
