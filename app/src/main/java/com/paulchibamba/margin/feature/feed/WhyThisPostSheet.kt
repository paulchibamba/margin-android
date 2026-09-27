package com.paulchibamba.margin.feature.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.domain.feed.FeedItem
import com.paulchibamba.margin.domain.feed.MemorySnapshot
import com.paulchibamba.margin.domain.feed.ranking.ScorePart
import java.util.Locale
import kotlin.math.roundToInt

private val SheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
private val ChipShape = RoundedCornerShape(8.dp)
private val TileShape = RoundedCornerShape(14.dp)
private val BarShape = RoundedCornerShape(4.dp)
private const val SCRIM_ALPHA = 0.55f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhyThisPostSheet(item: FeedItem, onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = SheetShape,
        containerColor = MarginColors.InkSheet,
        contentColor = MarginColors.White,
        scrimColor = Color.Black.copy(alpha = SCRIM_ALPHA),
        dragHandle = { DragHandle() },
    ) {
        WhyThisPostContent(item, onClose = onDismiss)
    }
}

@Composable
fun WhyThisPostContent(item: FeedItem, onClose: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 30.dp).navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SheetHeader(onClose)
        RankChips(item)
        ScoreBars(item)
        MemoryTiles(item.memory)
        FiltersPassed(item.appliedFilters)
    }
}

@Composable
private fun DragHandle() {
    Box(
        Modifier.padding(top = 10.dp, bottom = 6.dp).size(width = 36.dp, height = 4.dp)
            .background(MarginColors.White.copy(alpha = 0.3f), BarShape),
    )
}

@Composable
private fun SheetHeader(onClose: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            "Why you're seeing this",
            style = MarginTypography.sheetTitle,
            color = MarginColors.White,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onClose) {
            Icon(painterResource(MarginIcons.Close), "Close", Modifier.size(22.dp).alpha(0.6f), MarginColors.White)
        }
    }
}

@Composable
private fun RankChips(item: FeedItem) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Chip(item.source.label(), MarginColors.Lime, MarginColors.InkText)
        Chip("Rank ${item.rank} of ${item.poolSize}")
        Chip("Score ${twoDecimals(item.score.total)}")
        if (item.wasExploration) Chip("Exploring")
    }
}

@Composable
private fun Chip(
    label: String,
    background: Color = MarginColors.White.copy(alpha = 0.1f),
    color: Color = MarginColors.White,
) {
    Text(
        label,
        style = MarginTypography.chip,
        color = color,
        modifier = Modifier.background(background, ChipShape).padding(horizontal = 10.dp, vertical = 5.dp),
    )
}

@Composable
private fun ScoreBars(item: FeedItem) {
    val parts = item.score.parts.entries.sortedByDescending { it.value }
    val total = item.score.total.takeIf { it > 0 } ?: 1.0
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        parts.forEach { (part, value) -> ScoreBar(part, value, (value / total).toFloat().coerceIn(0f, 1f)) }
    }
}

@Composable
private fun ScoreBar(part: ScorePart, value: Double, fraction: Float) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            part.label(),
            style = MarginTypography.caption,
            color = MarginColors.White.copy(alpha = 0.7f),
            modifier = Modifier.width(72.dp),
        )
        Box(Modifier.weight(1f).height(8.dp).background(MarginColors.White.copy(alpha = 0.08f), BarShape)) {
            Box(Modifier.fillMaxHeight().fillMaxWidth(fraction).background(MarginColors.Lime, BarShape))
        }
        Text(
            twoDecimals(value),
            style = MarginTypography.mono,
            color = MarginColors.White,
            textAlign = TextAlign.End,
            modifier = Modifier.width(40.dp),
        )
    }
}

@Composable
private fun MemoryTiles(memory: MemorySnapshot?) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        MemoryTile(memory?.state?.label() ?: "Unseen", "State", Modifier.weight(1f))
        MemoryTile(memory?.let { "${(it.recall * 100).roundToInt()}%" } ?: "–", "Recall", Modifier.weight(1f))
        MemoryTile(memory?.let { "${oneDecimal(it.stability)} d" } ?: "–", "Stability", Modifier.weight(1f))
    }
}

@Composable
private fun MemoryTile(value: String, label: String, modifier: Modifier) {
    Column(
        modifier.background(MarginColors.White.copy(alpha = 0.06f), TileShape).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Text(value, style = MarginTypography.tileValue, color = MarginColors.White, maxLines = 1)
        Text(label, style = MarginTypography.meta, color = MarginColors.White.copy(alpha = 0.6f))
    }
}

@Composable
private fun FiltersPassed(filters: List<String>) {
    val passed = filters.ifEmpty { listOf("none needed") }.joinToString(" · ")
    Text("Passed: $passed", style = MarginTypography.label, color = MarginColors.White.copy(alpha = 0.55f))
}

private fun twoDecimals(value: Double): String = String.format(Locale.US, "%.2f", value)

private fun oneDecimal(value: Double): String = String.format(Locale.US, "%.1f", value)

@Preview(widthDp = 360)
@Composable
private fun WhyThisPostContentPreview() {
    Box(Modifier.background(MarginColors.InkSheet)) {
        WhyThisPostContent(FeedPreviewData.item, onClose = {}, modifier = Modifier.padding(top = 20.dp))
    }
}
