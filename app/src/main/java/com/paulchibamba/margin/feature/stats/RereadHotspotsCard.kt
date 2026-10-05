package com.paulchibamba.margin.feature.stats

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RereadHotspotsCard(hotspots: List<Pair<String, Int>>, modifier: Modifier = Modifier) {
    StatsCard("Re-read hotspots", modifier) {
        if (hotspots.isEmpty()) EmptyStatsLine("Nothing re-read yet")
        hotspots.forEach { (title, count) -> StatsCountRow(title, countLabel(count)) }
    }
}

@Preview(widthDp = 360, backgroundColor = 0xFF0D0D11, showBackground = true)
@Composable
private fun RereadHotspotsCardPreview() {
    RereadHotspotsCard(StatsPreviewData.busyAttention.hotspots, Modifier.padding(16.dp))
}
