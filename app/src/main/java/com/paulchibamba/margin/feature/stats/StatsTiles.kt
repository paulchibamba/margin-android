package com.paulchibamba.margin.feature.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.domain.usecase.StatsReport

@Composable
fun StatsTiles(report: StatsReport, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatTile(countLabel(report.postsSeen), "Posts seen")
            StatTile(countLabel(report.currentStreak), "Day streak", hasFlame = true)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatTile(countLabel(report.lostConcepts.size), "Marked lost")
            StatTile(report.reviewAccuracy?.let(::percentLabel) ?: "–", "Review accuracy")
        }
    }
}

@Preview(widthDp = 360, backgroundColor = 0xFF0D0D11, showBackground = true)
@Composable
private fun StatsTilesPreview() {
    StatsTiles(StatsPreviewData.busy, Modifier.padding(16.dp))
}
