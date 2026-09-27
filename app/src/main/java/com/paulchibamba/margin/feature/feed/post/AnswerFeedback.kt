package com.paulchibamba.margin.feature.feed.post

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val BUZZ_GAP_MILLIS = 110L
private const val SHAKE_MILLIS = 360

@Stable
class AnswerFeedback(private val haptics: HapticFeedback, private val scope: CoroutineScope) {
    private val shakeOffset = Animatable(0f)

    val shakeDp: Float
        get() = shakeOffset.value

    fun play(isCorrect: Boolean) {
        if (isCorrect) {
            haptics.performHapticFeedback(HapticFeedbackType.Confirm)
        } else {
            scope.launch { buzzTwice() }
            scope.launch { shake() }
        }
    }

    private suspend fun buzzTwice() {
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        delay(BUZZ_GAP_MILLIS)
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    private suspend fun shake() {
        shakeOffset.animateTo(
            targetValue = 0f,
            animationSpec = keyframes {
                durationMillis = SHAKE_MILLIS
                -10f at 45
                10f at 105
                -7f at 165
                7f at 225
                -3f at 285
            },
        )
    }
}

@Composable
fun rememberAnswerFeedback(): AnswerFeedback {
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    return remember(haptics, scope) { AnswerFeedback(haptics, scope) }
}

fun Modifier.shakenBy(feedback: AnswerFeedback): Modifier = graphicsLayer { translationX = feedback.shakeDp.dp.toPx() }
