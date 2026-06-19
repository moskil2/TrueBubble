package com.truebubble.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

data class OrientationAngles(
    val pitch: Float,  // degrees, positive = top up
    val roll: Float,   // degrees, positive = right side down
    val azimuth: Float // degrees 0-360
)

fun orientationFlow(context: Context): Flow<OrientationAngles> = callbackFlow {
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    val rotMat = FloatArray(9)
    val orientation = FloatArray(3)

    // Low-pass filter state for accelerometer fallback
    var lpPitch = 0f
    var lpRoll = 0f
    val alpha = 0.1f
    var smoothPitch = 0f
    var smoothRoll = 0f
    var smoothAzimuth = 0f

    if (rotationVector != null) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                SensorManager.getRotationMatrixFromVector(rotMat, event.values)
                SensorManager.getOrientation(rotMat, orientation)
                val rawAzimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                // Elevation-angle formula: measures angle of each device axis from horizontal.
                // Uses column 1 (Y_device) and column 0 (X_device) of the rotation matrix.
                // Invariant to screen-facing direction → stable readings when phone is held
                // edge-against-wall in any screen orientation (no ±180° discontinuity).
                val rawPitch = Math.toDegrees(Math.atan2(-rotMat[7].toDouble(), Math.sqrt((rotMat[1] * rotMat[1] + rotMat[4] * rotMat[4]).toDouble()))).toFloat()
                val rawRoll  = Math.toDegrees(Math.atan2(-rotMat[6].toDouble(), Math.sqrt((rotMat[0] * rotMat[0] + rotMat[3] * rotMat[3]).toDouble()))).toFloat()
                // Exponential smoothing
                smoothAzimuth = lerpAngle(smoothAzimuth, rawAzimuth, 0.3f)
                smoothPitch = lerp(smoothPitch, rawPitch, 0.3f)
                smoothRoll = lerp(smoothRoll, rawRoll, 0.3f)
                trySend(OrientationAngles(smoothPitch, smoothRoll, normalizeAzimuth(smoothAzimuth)))
            }
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit
        }
        sensorManager.registerListener(listener, rotationVector, SensorManager.SENSOR_DELAY_UI)
        awaitClose { sensorManager.unregisterListener(listener) }
    } else if (accelerometer != null) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                lpPitch = alpha * Math.toDegrees(Math.atan2(y.toDouble(), z.toDouble())).toFloat() + (1 - alpha) * lpPitch
                lpRoll = alpha * Math.toDegrees(Math.atan2(-x.toDouble(), z.toDouble())).toFloat() + (1 - alpha) * lpRoll
                smoothPitch = lerp(smoothPitch, lpPitch, 0.3f)
                smoothRoll = lerp(smoothRoll, lpRoll, 0.3f)
                trySend(OrientationAngles(smoothPitch, smoothRoll, 0f))
            }
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit
        }
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        awaitClose { sensorManager.unregisterListener(listener) }
    } else {
        awaitClose { }
    }
}

private fun lerp(a: Float, b: Float, t: Float) = a + (b - a) * t

private fun lerpAngle(a: Float, b: Float, t: Float): Float {
    var diff = b - a
    while (diff > 180f) diff -= 360f
    while (diff < -180f) diff += 360f
    return a + diff * t
}

private fun normalizeAzimuth(a: Float): Float {
    var v = a % 360f
    if (v < 0f) v += 360f
    return v
}
