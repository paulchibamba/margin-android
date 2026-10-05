package com.paulchibamba.margin.feature.stats

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.rollup.PostRollup

@Composable
fun AttentionByFormatCard(ratioByFormat: List<Pair<Format, Double>>, modifier: Modifier = Modifier) {
    StatsCard("Attention by format", modifier) {
        if (ratioByFormat.isEmpty()) EmptyStatsLine("No posts seen yet")
        ratioByFormat.forEach { (format, ratio) ->
            FormatBarRow(format, ratio, isStrong = ratio >= PostRollup.DEEP_RATIO)
        }
    }
}

@Preview(widthDp = 360, backgroundColor = 0xFF0D0D11, showBackground = true)
@Composable
private fun AttentionByFormatCardPreview() {
    AttentionByFormatCard(StatsPreviewData.busyAttention.ratioByFormat, Modifier.padding(16.dp))
}
