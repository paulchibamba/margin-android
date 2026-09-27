package com.paulchibamba.margin.feature.feed.post

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography

private val CodeCardShape = RoundedCornerShape(18.dp)
private val CODE_FADE_WIDTH = 28.dp

@Composable
fun CodeCard(code: String, verdict: CodeVerdict?, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .background(MarginColors.InkText, CodeCardShape)
            .border(1.dp, MarginColors.White.copy(alpha = 0.08f), CodeCardShape)
            .padding(start = 14.dp, top = 12.dp, end = 14.dp, bottom = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        verdict?.let { VerdictTag(it) }
        ScrollingCode(code)
    }
}

@Composable
private fun ScrollingCode(code: String) {
    val scroll = rememberScrollState()
    Text(
        code,
        Modifier.fadeRightEdge(isVisible = scroll.canScrollForward).horizontalScroll(scroll),
        style = MarginTypography.code,
        color = MarginColors.CodeText,
        softWrap = false,
    )
}

private fun Modifier.fadeRightEdge(isVisible: Boolean): Modifier = drawWithContent {
    drawContent()
    if (!isVisible) return@drawWithContent
    val fadeWidth = CODE_FADE_WIDTH.toPx()
    val left = size.width - fadeWidth
    drawRect(
        Brush.horizontalGradient(listOf(Color.Transparent, MarginColors.InkText), startX = left, endX = size.width),
        topLeft = Offset(left, 0f),
        size = Size(fadeWidth, size.height),
    )
}

@Composable
private fun VerdictTag(verdict: CodeVerdict) {
    val (label, color, icon) = when (verdict) {
        CodeVerdict.SAFE -> Triple("Safe", MarginColors.Lime, MarginIcons.CheckCircle)
        CodeVerdict.UNSAFE -> Triple("Unsafe", MarginColors.Wrong, MarginIcons.Cancel)
    }
    TagChip(label, color, icon)
}

@Composable
private fun TagChip(label: String, color: Color, @DrawableRes icon: Int) {
    Row(
        Modifier
            .background(color.copy(alpha = 0.14f), RoundedCornerShape(6.dp))
            .padding(start = 5.dp, top = 3.dp, end = 8.dp, bottom = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painterResource(icon), null, Modifier.size(14.dp), color)
        Text(label, style = MarginTypography.railLabel, color = color)
    }
}
