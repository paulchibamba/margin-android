package com.paulchibamba.margin.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val TILE_CORNER = 0.28f
private const val RULE_LEFT = 0.31f
private const val RULE_TOP = 0.25f
private const val RULE_WIDTH = 0.08f
private const val RULE_HEIGHT = 0.5f
private const val LINES_LEFT = 0.47f
private const val LINES_WIDTH = 0.33f
private const val LINES_TOP = 0.39f
private const val LINE_HEIGHT = 0.075f
private const val LINE_GAP = 0.13f
private const val LINE_ALPHA = 0.92f
private val LINE_LENGTHS = listOf(1f, 0.56f)
private val MINIMUM_LINE_HEIGHT = 2.dp

@Composable
fun BrandMark(size: Dp, tile: BrandTile, modifier: Modifier = Modifier, ruleProgress: Float = 1f) {
    Canvas(modifier.size(size)) {
        val side = this.size.minDimension
        drawTile(side, tile)
        drawRule(side, tile.rule, ruleProgress)
        drawLines(side, tile.lines)
    }
}

private fun DrawScope.drawTile(side: Float, tile: BrandTile) {
    val corner = CornerRadius(side * TILE_CORNER)
    drawRoundRect(tile.background, cornerRadius = corner)
    if (tile.outline != Color.Transparent) {
        val inset = 0.5.dp.toPx()
        drawRoundRect(
            color = tile.outline,
            topLeft = Offset(inset, inset),
            size = Size(side - 2 * inset, side - 2 * inset),
            cornerRadius = corner,
            style = Stroke(width = 1.dp.toPx()),
        )
    }
}

private fun DrawScope.drawRule(side: Float, color: Color, progress: Float) {
    val width = side * RULE_WIDTH
    val height = side * RULE_HEIGHT * progress.coerceIn(0f, 1f)
    drawPill(color, Offset(side * RULE_LEFT, side * RULE_TOP), Size(width, height))
}

private fun DrawScope.drawLines(side: Float, color: Color) {
    val lineHeight = maxOf(MINIMUM_LINE_HEIGHT.toPx(), side * LINE_HEIGHT)
    LINE_LENGTHS.forEachIndexed { index, length ->
        val top = side * LINES_TOP + index * (lineHeight + side * LINE_GAP)
        val size = Size(side * LINES_WIDTH * length, lineHeight)
        drawPill(color.copy(alpha = LINE_ALPHA), Offset(side * LINES_LEFT, top), size)
    }
}

private fun DrawScope.drawPill(color: Color, topLeft: Offset, size: Size) {
    val radius = minOf(size.width, size.height) / 2
    drawRoundRect(color, topLeft, size, CornerRadius(radius))
}

@Preview
@Composable
private fun BrandMarkTilesPreview() {
    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        BrandTile.entries.forEach { BrandMark(size = 112.dp, tile = it) }
    }
}

@Preview
@Composable
private fun BrandMarkSizesPreview() {
    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        listOf(20.dp, 24.dp, 58.dp).forEach { BrandMark(size = it, tile = BrandTile.Dark) }
    }
}
