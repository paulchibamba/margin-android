package com.paulchibamba.margin.feature.celebration

import android.os.VibrationEffect
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

private val RISING_TIMINGS = longArrayOf(0, 50, 50, 50, 70)
private val RISING_AMPLITUDES = intArrayOf(0, 50, 110, 180, 255)
private const val NO_REPEAT = -1

@Composable
fun RisingHapticEffect() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val vibrator = context.getSystemService(VibratorManager::class.java)?.defaultVibrator ?: return@LaunchedEffect
        vibrator.vibrate(VibrationEffect.createWaveform(RISING_TIMINGS, RISING_AMPLITUDES, NO_REPEAT))
    }
}
