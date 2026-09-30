package com.paulchibamba.margin.feature.read.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSurfacePalette
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.withFontScaleCap
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.feature.read.BookCover
import kotlin.math.roundToInt

private const val FULL_CIRCLE = 360f
private const val TWELVE_O_CLOCK = -90f
private const val PERCENT = 100
private const val INACTIVE_ALPHA = 0.5f
private val RING_SIZE = 68.dp
private val RING_WIDTH = 3.dp
private val OUTLINE_WIDTH = 1.5.dp
private val COVER_INSET = 6.dp

@Composable
fun BookRing(ring: BookRingState, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val ringColor = LocalSurfacePalette.current.text
    Column(
        modifier
            .width(72.dp)
            .alpha(if (ring.isActive) 1f else INACTIVE_ALPHA)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics(mergeDescendants = true) { contentDescription = ringDescription(ring) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(Modifier.size(RING_SIZE)) {
            Canvas(Modifier.fillMaxSize()) {
                if (ring.isActive) drawProgress(ring.progress, ringColor) else drawOutline(ringColor)
            }
            BookCover(ring.coverPath, CircleShape, Modifier.fillMaxSize().padding(COVER_INSET))
        }
        Text(
            ring.label,
            style = MarginTypography.pill.withFontScaleCap().copy(hyphens = Hyphens.Auto),
            color = ringColor,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun DrawScope.drawProgress(progress: Float, ringColor: Color) {
    drawRingArc(ringColor.copy(alpha = 0.12f), sweep = FULL_CIRCLE)
    drawRingArc(ringColor, sweep = FULL_CIRCLE * progress.coerceIn(0f, 1f))
}

private fun DrawScope.drawRingArc(color: Color, sweep: Float) {
    val stroke = RING_WIDTH.toPx()
    drawArc(
        color = color,
        startAngle = TWELVE_O_CLOCK,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = Offset(stroke / 2, stroke / 2),
        size = Size(size.width - stroke, size.height - stroke),
        style = Stroke(stroke),
    )
}

private fun DrawScope.drawOutline(ringColor: Color) {
    val stroke = OUTLINE_WIDTH.toPx()
    val radius = size.minDimension / 2 - stroke / 2
    drawCircle(ringColor.copy(alpha = 0.2f), radius = radius, style = Stroke(stroke))
}

private fun ringDescription(ring: BookRingState): String {
    val state = if (ring.isActive) "${(ring.progress * PERCENT).roundToInt()} percent read" else "inactive"
    return "${ring.label}, $state"
}

@Preview(backgroundColor = 0xFFF3F0E9, showBackground = true)
@Composable
private fun BookRingPreview() {
    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        BookRing(BookRingState(BookSlug("a"), "Application Security", 0.62f, isActive = true), onClick = {})
        BookRing(BookRingState(BookSlug("b"), "Secure Coding", 0.18f, isActive = true), onClick = {})
        BookRing(BookRingState(BookSlug("c"), "Practical AI Security", 0f, isActive = false), onClick = {})
    }
}
