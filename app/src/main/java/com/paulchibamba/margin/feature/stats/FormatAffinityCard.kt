package com.paulchibamba.margin.feature.stats

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.signals.FormatAffinity

@Composable
fun FormatAffinityCard(affinity: List<Pair<Format, Double>>, modifier: Modifier = Modifier) {
    StatsCard("Format affinity", modifier) {
        if (affinity.isEmpty()) EmptyStatsLine("No posts seen yet")
        affinity.forEach { (format, value) -> FormatBarRow(format, value, isStrong = value > FormatAffinity.NEUTRAL) }
    }
}

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
