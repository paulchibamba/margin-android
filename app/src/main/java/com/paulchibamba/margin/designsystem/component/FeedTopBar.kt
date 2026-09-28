package com.paulchibamba.margin.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.BrandMark
import com.paulchibamba.margin.designsystem.BrandTile
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skins

@Composable
fun FeedTopBar(streak: Int, segments: SegmentProgress?, onMoreClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = FeedChromeColors.of(LocalSkin.current.isLight)
    Column(modifier.fillMaxWidth()) {
        if (segments != null) StorySegments(segments, colors)
        Row(
            Modifier.fillMaxWidth().height(48.dp).padding(start = 12.dp, end = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StreakChip(streak, colors)
            Wordmark(colors)
            MoreButton(onMoreClick, colors)
        }
    }
}

@Composable
private fun StorySegments(segments: SegmentProgress, colors: FeedChromeColors) {
    Row(
        Modifier.fillMaxWidth().padding(start = 14.dp, top = 6.dp, end = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        repeat(segments.count) { segment ->
            Box(Modifier.weight(1f).height(2.5.dp).clip(RoundedCornerShape(2.dp)).background(colors.segmentTrack)) {
                Box(Modifier.fillMaxHeight().fillMaxWidth(segments.fillOf(segment)).background(colors.content))
            }
        }
    }
}

@Composable
private fun StreakChip(streak: Int, colors: FeedChromeColors) {
    Row(
        Modifier
            .background(colors.chip, CircleShape)
            .padding(PaddingValues(start = 8.dp, top = 6.dp, end = 11.dp, bottom = 6.dp))
            .clearAndSetSemantics { contentDescription = "$streak day streak" },
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painterResource(MarginIcons.LocalFireDepartment), null, Modifier.size(19.dp), MarginColors.Streak)
        val style = MarginTypography.streakChip.withChromeShadow(colors, 4.dp, 0.25f)
        Text("$streak", style = style, color = colors.content)
    }
}

@Composable
private fun Wordmark(colors: FeedChromeColors) {
    Row(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalAlignment = Alignment.CenterVertically) {
        BrandMark(size = 24.dp, tile = BrandTile.Dark)
        Text("margin", style = MarginTypography.wordmark.withChromeShadow(colors, 4.dp, 0.25f), color = colors.content)
    }
}

@Composable
private fun MoreButton(onClick: () -> Unit, colors: FeedChromeColors) {
    IconButton(onClick = onClick) {
        Icon(painterResource(MarginIcons.MoreHoriz), "More", Modifier.size(26.dp), colors.content)
    }
}

@Preview(widthDp = 360)
@Composable
private fun FeedTopBarInkPreview() {
    MarginTheme(Skins.Ink) {
        Box(Modifier.background(Skins.Ink.background)) {
            FeedTopBar(streak = 7, segments = null, onMoreClick = {})
        }
    }
}

@Preview(widthDp = 360)
@Composable
private fun FeedTopBarPaperSegmentsPreview() {
    MarginTheme(Skins.Paper) {
        Box(Modifier.background(Skins.Paper.background)) {
            FeedTopBar(streak = 7, segments = SegmentProgress(count = 4, current = 1), onMoreClick = {})
        }
    }
}

@Preview(widthDp = 360)
@Composable
private fun FeedTopBarCobaltSegmentsPreview() {
    MarginTheme(Skins.Cobalt) {
        Box(Modifier.background(Skins.Cobalt.background)) {
            FeedTopBar(streak = 7, segments = SegmentProgress(count = 4, current = 1), onMoreClick = {})
        }
    }
}
