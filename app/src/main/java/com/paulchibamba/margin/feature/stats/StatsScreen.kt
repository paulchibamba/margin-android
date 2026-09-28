package com.paulchibamba.margin.feature.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.designsystem.StatusBarFollowsSkin
import com.paulchibamba.margin.domain.usecase.StatsReport

@Composable
fun StatsScreen(state: StatsUiState, onBack: () -> Unit, onCopy: () -> Unit, modifier: Modifier = Modifier) {
    MarginTheme(Skins.Ink) {
        StatusBarFollowsSkin()
        Column(
            modifier
                .fillMaxSize()
                .background(Skins.Ink.background)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(start = 4.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            StatsTopBar(state.isCopied, onBack, onCopy = { if (!state.isLoading) onCopy() })
            state.report?.let { report -> StatsSections(report, Modifier.padding(start = 12.dp)) }
        }
    }
}

@Composable
private fun StatsSections(report: StatsReport, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        StatsTiles(report)
        FormatAffinityCard(report.affinityByStrength)
        ReviewGradesCard(report)
        FrontierCard(report.frontiers)
        ActionsCard(report)
    }
}

@Preview(widthDp = 360, heightDp = 780)
@Composable
private fun StatsScreenBusyPreview() {
    StatsScreen(StatsUiState(StatsPreviewData.busy), onBack = {}, onCopy = {})
}

@Preview(widthDp = 360, heightDp = 780)
@Composable
private fun StatsScreenEmptyPreview() {
    StatsScreen(StatsUiState(StatsPreviewData.empty), onBack = {}, onCopy = {})
}
