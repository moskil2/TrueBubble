package com.truebubble.feedback

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class FeedbackController(context: Context) {

    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    private var toneGenerator: ToneGenerator? = try {
        ToneGenerator(AudioManager.STREAM_MUSIC, 90)
    } catch (_: Exception) { null }

    private var wasInTolerance = false

    fun onAngleUpdate(withinTolerance: Boolean, soundEnabled: Boolean, vibrateEnabled: Boolean) {
        if (withinTolerance && !wasInTolerance) {
            if (soundEnabled) beep()
            if (vibrateEnabled) vibrate()
        }
        wasInTolerance = withinTolerance
    }

    fun resetDebounce() {
        wasInTolerance = false
    }

    private fun beep() {
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
    }

    private fun vibrate() {
        vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    fun release() {
        toneGenerator?.release()
        toneGenerator = null
    }
}
