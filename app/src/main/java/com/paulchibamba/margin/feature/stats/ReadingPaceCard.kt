package com.paulchibamba.margin.feature.stats

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.domain.usecase.AttentionReport

@Composable
fun ReadingPaceCard(attention: AttentionReport, modifier: Modifier = Modifier) {
    StatsCard("Reading pace", modifier) {
        if (attention.paceByBook.isEmpty()) EmptyStatsLine("No timed reading yet")
        attention.paceByBook.forEach { (book, wordsPerMinute) -> StatsCountRow(book.title, paceLabel(wordsPerMinute)) }
        attention.paceByTimeOfDay.forEach { (time, wordsPerMinute) ->
            StatsCountRow(time.label(), paceLabel(wordsPerMinute))
        }
    }
}

@Preview(widthDp = 360, backgroundColor = 0xFF0D0D11, showBackground = true)
@Composable
private fun ReadingPaceCardPreview() {
    ReadingPaceCard(StatsPreviewData.busyAttention, Modifier.padding(16.dp))
}
