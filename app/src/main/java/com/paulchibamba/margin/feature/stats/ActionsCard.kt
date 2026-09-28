package com.paulchibamba.margin.feature.stats

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.component.labelOf
import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.usecase.StatsReport

@Composable
fun ActionsCard(report: StatsReport, modifier: Modifier = Modifier) {
    StatsCard("Actions by type", modifier) {
        PostAction.entries.forEach { action -> StatsCountRow(labelOf(action), countLabel(report.countOf(action))) }
    }
}

@Preview(widthDp = 360, backgroundColor = 0xFF0D0D11, showBackground = true)
@Composable
private fun ActionsCardPreview() {
    ActionsCard(StatsPreviewData.busy, Modifier.padding(16.dp))
}
