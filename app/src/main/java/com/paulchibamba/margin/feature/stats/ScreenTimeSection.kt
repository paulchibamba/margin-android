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
import com.paulchibamba.margin.domain.usecase.ScreenTimeReport
import com.paulchibamba.margin.feature.settings.screentime.minutesLabel

@Composable
fun ScreenTimeSection(screenTime: ScreenTimeReport, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ScreenTimeHeading()
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatTile(minutesLabel(screenTime.margin), "Margin")
            StatTile(minutesLabel(screenTime.doom), "Doom apps")
        }
        screenTime.marginShare?.let { share ->
            EmptyStatsLine("Margin got ${percentLabel(share)} of the time you spent in Margin and doom apps")
        }
        ScreenTimeByDayCard(screenTime.days, Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun ScreenTimeHeading() {
    Column(Modifier.padding(top = 8.dp, bottom = 4.dp).semantics(mergeDescendants = true) { heading() }) {
        Text("Screen time", style = MarginTypography.barTitle, color = MarginColors.White)
        Text("Last 7 days", style = MarginTypography.label, color = MarginColors.White.copy(alpha = 0.6f))
    }
}

@Preview(widthDp = 360, backgroundColor = 0xFF0D0D11, showBackground = true)
@Composable
private fun ScreenTimeSectionPreview() {
    ScreenTimeSection(StatsPreviewData.screenTime, Modifier.padding(16.dp))
}
