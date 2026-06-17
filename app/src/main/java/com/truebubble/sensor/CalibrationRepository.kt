package com.truebubble.sensor

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.calibrationDataStore: DataStore<Preferences> by preferencesDataStore("calibration")

data class CalibrationData(
    val pitchOffset: Float = 0f,
    val rollOffset: Float = 0f,
    val lastDate: String = "",
)

class CalibrationRepository(private val context: Context) {

    private val KEY_PITCH_OFFSET = floatPreferencesKey("pitch_offset")
    private val KEY_ROLL_OFFSET = floatPreferencesKey("roll_offset")
    private val KEY_DATE = stringPreferencesKey("calibration_date")

    val calibrationFlow: Flow<CalibrationData> = context.calibrationDataStore.data.map { prefs ->
        CalibrationData(
            pitchOffset = prefs[KEY_PITCH_OFFSET] ?: 0f,
            rollOffset = prefs[KEY_ROLL_OFFSET] ?: 0f,
            lastDate = prefs[KEY_DATE] ?: "",
        )
    }

    suspend fun save(pitchOffset: Float, rollOffset: Float) {
        context.calibrationDataStore.edit { prefs ->
            prefs[KEY_PITCH_OFFSET] = pitchOffset
            prefs[KEY_ROLL_OFFSET] = rollOffset
            prefs[KEY_DATE] = LocalDate.now().toString()
        }
    }

    suspend fun setPitchOffset(value: Float) {
        context.calibrationDataStore.edit { prefs ->
            prefs[KEY_PITCH_OFFSET] = value
            prefs[KEY_DATE] = LocalDate.now().toString()
        }
    }

    suspend fun setRollOffset(value: Float) {
        context.calibrationDataStore.edit { prefs ->
            prefs[KEY_ROLL_OFFSET] = value
            prefs[KEY_DATE] = LocalDate.now().toString()
        }
    }

    suspend fun reset() {
        context.calibrationDataStore.edit { prefs ->
            prefs[KEY_PITCH_OFFSET] = 0f
            prefs[KEY_ROLL_OFFSET] = 0f
            prefs[KEY_DATE] = ""
        }
    }
}
