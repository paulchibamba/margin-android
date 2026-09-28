package com.paulchibamba.margin.feature.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography
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

@Composable
private fun RowScope.StatTile(value: String, label: String, hasFlame: Boolean = false) {
    Column(
        Modifier
            .weight(1f)
            .background(MarginColors.InkSheet, StatsCardShape)
            .padding(14.dp)
            .clearAndSetSemantics { contentDescription = "$label: $value" },
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            if (hasFlame) {
                Icon(painterResource(MarginIcons.LocalFireDepartment), null, Modifier.size(24.dp), MarginColors.Streak)
            }
            Text(value, style = MarginTypography.statValue, color = MarginColors.White)
        }
        Text(label, style = MarginTypography.label, color = MarginColors.White.copy(alpha = 0.6f))
    }
}

@Preview(widthDp = 360, backgroundColor = 0xFF0D0D11, showBackground = true)
@Composable
private fun StatsTilesPreview() {
    StatsTiles(StatsPreviewData.busy, Modifier.padding(16.dp))
}
