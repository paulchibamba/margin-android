package com.paulchibamba.margin.feature.read

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.domain.usecase.NoteTally

@Composable
fun ReadingProgressBar(
    fraction: Float,
    modifier: Modifier = Modifier,
    height: Dp = 3.dp,
    color: Color = MarginColors.InkText,
    track: Color = MarginColors.InkText.copy(alpha = 0.12f),
) {
    Box(modifier.fillMaxWidth().height(height).clip(CircleShape).background(track)) {
        Box(Modifier.fillMaxHeight().fillMaxWidth(fraction.coerceIn(0f, 1f)).clip(CircleShape).background(color))
    }
}

fun NoteTally.progress(): Float = if (noteCount == 0) 0f else notesRead.toFloat() / noteCount

@Preview(widthDp = 200)
@Composable
private fun ReadingProgressBarPreview() {
    Box(Modifier.background(MarginColors.Paper)) { ReadingProgressBar(fraction = 0.62f) }
}
