package com.paulchibamba.margin.feature.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.domain.usecase.AttentionReport

@Composable
fun AttentionSection(attention: AttentionReport, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AttentionHeading()
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatTile(attention.glanceRate?.let(::percentLabel) ?: "–", "Glance rate")
            StatTile(attention.deepRate?.let(::percentLabel) ?: "–", "Deep reads")
        }
        AttentionByFormatCard(attention.ratioByFormat, Modifier.padding(top = 8.dp))
        RereadHotspotsCard(attention.hotspots, Modifier.padding(top = 8.dp))
        ReadingPaceCard(attention, Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun AttentionHeading() {
    Column(Modifier.padding(top = 8.dp, bottom = 4.dp).semantics(mergeDescendants = true) { heading() }) {
        Text("Attention", style = MarginTypography.barTitle, color = MarginColors.White)
        Text("Last 7 days", style = MarginTypography.label, color = MarginColors.White.copy(alpha = 0.6f))
    }
}

@Preview(widthDp = 360, backgroundColor = 0xFF0D0D11, showBackground = true)
@Composable
private fun AttentionSectionPreview() {
    AttentionSection(StatsPreviewData.busyAttention, Modifier.padding(16.dp))
}

@Preview(widthDp = 360, backgroundColor = 0xFF0D0D11, showBackground = true)
@Composable
private fun AttentionSectionEmptyPreview() {
    AttentionSection(StatsPreviewData.emptyAttention, Modifier.padding(16.dp))
}
