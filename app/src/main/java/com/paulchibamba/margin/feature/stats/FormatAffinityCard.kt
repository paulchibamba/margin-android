package com.paulchibamba.margin.feature.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.component.BarRow
import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.signals.FormatAffinity
import com.paulchibamba.margin.feature.feed.label
import java.util.Locale

private val BarShape = RoundedCornerShape(4.dp)

@Composable
fun FormatAffinityCard(affinity: List<Pair<Format, Double>>, modifier: Modifier = Modifier) {
    StatsCard("Format affinity", modifier) {
        if (affinity.isEmpty()) EmptyStatsLine("No posts seen yet")
        affinity.forEach { (format, value) -> AffinityRow(format, value) }
    }
}

@Composable
private fun AffinityRow(format: Format, value: Double) {
    BarRow(labelWidth = 96.dp, label = { modifier -> FormatName(format, modifier) }) {
        AffinityBar(value, Modifier.weight(1f))
        Text(
            String.format(Locale.ROOT, "%.2f", value),
            style = MarginTypography.monoSmall,
            color = MarginColors.White,
            textAlign = TextAlign.End,
            softWrap = false,
            modifier = Modifier.widthIn(min = 32.dp),
        )
    }
}

@Composable
private fun FormatName(format: Format, modifier: Modifier) {
    Text(
        format.label(),
        style = MarginTypography.footnote,
        color = MarginColors.White,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )
}

@Composable
private fun AffinityBar(value: Double, modifier: Modifier = Modifier) {
    Box(modifier.height(8.dp).background(MarginColors.White.copy(alpha = 0.08f), BarShape)) {
        Box(
            Modifier
                .fillMaxWidth(value.coerceIn(0.0, 1.0).toFloat())
                .fillMaxHeight()
                .background(barColorOf(value), BarShape),
        )
    }
}

private fun barColorOf(value: Double): Color =
    if (value > FormatAffinity.NEUTRAL) MarginColors.Lime else MarginColors.White.copy(alpha = 0.4f)

@Preview(widthDp = 360, backgroundColor = 0xFF0D0D11, showBackground = true)
@Composable
private fun FormatAffinityCardPreview() {
    FormatAffinityCard(StatsPreviewData.busy.affinityByStrength, Modifier.padding(16.dp))
}

@Preview(widthDp = 360, backgroundColor = 0xFF0D0D11, showBackground = true, fontScale = 2f)
@Composable
private fun FormatAffinityCardLargeTextPreview() {
    FormatAffinityCard(StatsPreviewData.busy.affinityByStrength, Modifier.padding(16.dp))
}
