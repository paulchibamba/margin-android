package com.paulchibamba.margin.feature.celebration

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import kotlinx.coroutines.delay
import kotlin.time.Duration

private const val POP_DAMPING = 0.45f

@Composable
fun rememberPopIn(from: Float, delay: Duration = Duration.ZERO): State<Float> {
    val isPreview = LocalInspectionMode.current
    val progress = remember { Animatable(if (isPreview) 1f else from) }
    LaunchedEffect(Unit) {
        delay(delay)
        progress.animateTo(1f, spring(dampingRatio = POP_DAMPING, stiffness = Spring.StiffnessLow))
    }
    return progress.asState()
}
