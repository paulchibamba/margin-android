package com.paulchibamba.margin.feature.stats

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.domain.usecase.BookFrontier

@Composable
fun FrontierCard(frontiers: List<BookFrontier>, modifier: Modifier = Modifier) {
    StatsCard("Frontier per book", modifier) {
        frontiers.forEach { (book, frontier) -> StatsCountRow(book.title, frontierLabel(frontier)) }
    }
}

@Preview(widthDp = 360, backgroundColor = 0xFF0D0D11, showBackground = true)
@Composable
private fun FrontierCardPreview() {
    FrontierCard(StatsPreviewData.busy.frontiers, Modifier.padding(16.dp))
}
