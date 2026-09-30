package com.paulchibamba.margin.feature.feed.post

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalPostCardStyle
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.PostCardStyle
import kotlin.math.abs
import kotlinx.coroutines.launch

const val SWIPE_CARD_TAG = "swipe-card"

private val CardShape = RoundedCornerShape(26.dp)
private val StampShape = RoundedCornerShape(10.dp)
private const val COMMIT_FRACTION = 0.3f
private const val COMMIT_VELOCITY = 1_500f
private const val MAX_TILT_DEGREES = 12f

@Composable
fun SwipeCard(statement: String, saysTrue: Boolean?, onVerdict: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val offset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var width by remember { mutableIntStateOf(1) }
    val commitDistance = width * COMMIT_FRACTION
    val dragState = rememberDraggableState { delta -> scope.launch { offset.snapTo(offset.value + delta) } }
    Box(modifier.fillMaxWidth()) {
        CardBehind(Modifier.matchParentSize())
        Column(
            Modifier
                .onSizeChanged { width = it.width }
                .draggable(
                    dragState,
                    Orientation.Horizontal,
                    enabled = saysTrue == null,
                    onDragStopped = { velocity ->
                        verdictOf(offset.value, velocity, commitDistance)?.let(onVerdict)
                        offset.animateTo(0f)
                    },
                )
                .graphicsLayer {
                    translationX = offset.value
                    rotationZ = offset.value / width * MAX_TILT_DEGREES
                }
                .testTag(SWIPE_CARD_TAG),
        ) {
            CardFace(statement, stampOf(saysTrue, offset.value), stampAlphaOf(saysTrue, offset.value, commitDistance))
        }
    }
}

@Composable
private fun CardBehind(modifier: Modifier) {
    val skin = LocalSkin.current
    val color = when {
        skin.isLight -> MarginColors.PaperDivider
        LocalPostCardStyle.current == PostCardStyle.DARK -> skin.surface
        else -> MarginColors.InkCard
    }
    Box(
        modifier
            .graphicsLayer {
                scaleX = 0.94f
                scaleY = 0.94f
                translationY = 14.dp.toPx()
            }
            .background(color, CardShape),
    )
}

@Composable
private fun CardFace(statement: String, stamp: Boolean?, stampAlpha: Float) {
    val colors = CardColors.on(LocalSkin.current.isLight, LocalPostCardStyle.current)
    Column(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 240.dp)
            .background(colors.background, CardShape)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            FormatChip(colors.content)
            Stamp(stamp ?: false, colors, Modifier.alpha(if (stamp == null) 0f else stampAlpha))
        }
        Text(statement, style = MarginTypography.statement, color = colors.content)
    }
}

@Composable
private fun FormatChip(color: Color) {
    Text(
        "True or false",
        Modifier
            .background(color.copy(alpha = 0.08f), RoundedCornerShape(7.dp))
            .padding(horizontal = 9.dp, vertical = 4.dp),
        style = MarginTypography.pill,
        color = color,
    )
}

@Composable
private fun Stamp(saysTrue: Boolean, colors: CardColors, modifier: Modifier) {
    val color = if (saysTrue) colors.trueStamp else colors.falseStamp
    Text(
        if (saysTrue) "TRUE" else "FALSE",
        modifier
            .rotate(if (saysTrue) -12f else 12f)
            .border(3.dp, color, StampShape)
            .padding(horizontal = 10.dp, vertical = 2.dp),
        style = MarginTypography.stamp,
        color = color,
    )
}

private fun verdictOf(offset: Float, velocity: Float, commitDistance: Float): Boolean? = when {
    abs(offset) > commitDistance -> offset > 0f
    abs(velocity) > COMMIT_VELOCITY -> velocity > 0f
    else -> null
}

private fun stampOf(saysTrue: Boolean?, offset: Float): Boolean? = saysTrue ?: when {
    offset > 0f -> true
    offset < 0f -> false
    else -> null
}

private fun stampAlphaOf(saysTrue: Boolean?, offset: Float, commitDistance: Float): Float =
    if (saysTrue != null) 1f else (abs(offset) / commitDistance).coerceIn(0f, 1f)

private class CardColors(val background: Color, val content: Color, val trueStamp: Color, val falseStamp: Color) {
    companion object {
        private val Dark = CardColors(MarginColors.InkCard, MarginColors.White, MarginColors.Lime, MarginColors.Wrong)
        private val Light =
            CardColors(MarginColors.Paper, MarginColors.InkText, MarginColors.CorrectOnLight, MarginColors.WrongStamp)

        fun on(isLightSkin: Boolean, style: PostCardStyle): CardColors =
            if (isLightSkin || style == PostCardStyle.DARK) Dark else Light
    }
}
