package com.paulchibamba.margin.feature.read

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors

private const val SQRT_TWO = 1.4142135f

@Composable
fun StripedCover(shape: Shape, modifier: Modifier = Modifier, stripeWidth: Dp = 5.dp) {
    Canvas(modifier.clip(shape).background(MarginColors.PaperDivider)) {
        val band = stripeWidth.toPx()
        val spacing = band * 2 * SQRT_TWO
        var start = -size.height
        while (start < size.width + size.height) {
            drawLine(MarginColors.PaperStripe, Offset(start, size.height), Offset(start + size.height, 0f), band)
            start += spacing
        }
    }
}

@Preview
@Composable
private fun StripedCoverPreview() {
    StripedCover(RoundedCornerShape(6.dp), Modifier.size(width = 42.dp, height = 58.dp))
}
