package com.paulchibamba.margin.feature.feed

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.designsystem.component.FeedTopBar
import com.paulchibamba.margin.domain.usecase.CaughtUp
import com.paulchibamba.margin.domain.usecase.NextNote
import com.paulchibamba.margin.feature.read.BookCover

private val CardShape = RoundedCornerShape(22.dp)
private val CoverShape = RoundedCornerShape(6.dp)
private const val STRIPE_SPACING_DP = 12f
private val faintWhite = MarginColors.White.copy(alpha = 0.55f)

@Composable
fun CaughtUpState(
    caughtUp: CaughtUp,
    streak: Int,
    onReadOn: (NextNote) -> Unit,
    onMore: () -> Unit,
    onShown: () -> Unit,
    modifier: Modifier = Modifier,
    coverPath: String? = null,
) {
    LaunchedEffect(Unit) { onShown() }
    MarginTheme(Skins.Ink) {
        Column(modifier.fillMaxSize().background(Skins.Ink.background).statusBarsPadding()) {
            FeedTopBar(streak = streak, segments = null, onMoreClick = onMore)
            Column(
                Modifier.verticalScroll(rememberScrollState()).padding(start = 24.dp, top = 70.dp, end = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                DoneRing()
                CaughtUpMessage(hasNextNote = caughtUp.nextNote != null)
                caughtUp.nextNote?.let { note -> NextNoteCard(note, coverPath, onReadOn = { onReadOn(note) }) }
                caughtUp.nextReviewIn?.let { dueIn ->
                    Text("Next review due ${dueInLabel(dueIn)}", style = MarginTypography.footnote, color = faintWhite)
                }
            }
        }
    }
}

@Composable
private fun DoneRing() {
    Box(Modifier.size(92.dp), contentAlignment = Alignment.Center) {
        val ring = Brush.sweepGradient(listOf(MarginColors.Lime, MarginColors.Mint, MarginColors.Lime))
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 3.dp.toPx()
            drawCircle(ring, radius = size.minDimension / 2 - stroke / 2, style = Stroke(stroke))
        }
        Icon(painterResource(MarginIcons.Check), contentDescription = null, Modifier.size(44.dp), MarginColors.Lime)
    }
}

@Composable
private fun CaughtUpMessage(hasNextNote: Boolean) {
    Column(Modifier.padding(top = 6.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("You're all caught up", style = MarginTypography.screenTitle, color = MarginColors.White)
        Text(
            if (hasNextNote) READ_ON_MESSAGE else ALL_READ_MESSAGE,
            style = MarginTypography.bodySmall,
            color = MarginColors.White.copy(alpha = 0.72f),
            modifier = Modifier.widthIn(max = 270.dp),
        )
    }
}

@Composable
private fun NextNoteCard(note: NextNote, coverPath: String?, onReadOn: () -> Unit) {
    Column(
        Modifier.padding(top = 14.dp).fillMaxWidth().background(MarginColors.InkRaised, CardShape).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            BookCover(coverPath, CoverShape, Modifier.size(width = 44.dp, height = 60.dp)) { placeholderModifier ->
                InkStripes(placeholderModifier)
            }
            NoteSummary(note)
        }
        ReadOnButton(onReadOn)
    }
}

@Composable
private fun InkStripes(modifier: Modifier) {
    Canvas(modifier.clip(CoverShape).background(MarginColors.InkStripe)) {
        val spacing = STRIPE_SPACING_DP.dp.toPx()
        val band = spacing / 2
        var start = -size.height
        while (start < size.width) {
            drawLine(MarginColors.InkSnackbar, Offset(start, size.height), Offset(start + size.height, 0f), band)
            start += spacing
        }
    }
}

@Composable
private fun NoteSummary(note: NextNote) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        val chapter = chapterLabel(note.outline.position.chapter, note.chapterTitle)
        Text("Next note · $chapter", style = MarginTypography.label, color = MarginColors.White.copy(alpha = 0.6f))
        Text(note.outline.section, style = MarginTypography.noteTitle, color = MarginColors.White)
        Text(
            nextNoteDetail(note.outline.readingTime, note.unlockedPosts),
            style = MarginTypography.label,
            color = MarginColors.Lime,
        )
    }
}

@Composable
private fun ReadOnButton(onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clip(CircleShape).background(MarginColors.Lime)
            .clickable(role = Role.Button, onClick = onClick).padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("Read on", style = MarginTypography.button, color = MarginColors.InkText)
        Icon(painterResource(MarginIcons.ArrowForward), null, Modifier.size(20.dp), MarginColors.InkText)
    }
}

private const val READ_ON_MESSAGE =
    "You've seen every unlocked idea and cleared your reviews. Read on to grow your feed."
private const val ALL_READ_MESSAGE =
    "You've seen every idea in your active books and cleared your reviews. Come back when a review is due."

@Preview(widthDp = 360, heightDp = 780)
@Composable
private fun CaughtUpStatePreview() {
    CaughtUpState(FeedPreviewData.caughtUp, streak = 7, onReadOn = {}, onMore = {}, onShown = {})
}

@Preview(widthDp = 360, heightDp = 780)
@Composable
private fun CaughtUpStateAllReadPreview() {
    CaughtUpState(CaughtUp(nextNote = null, nextReviewIn = null), streak = 7, onReadOn = {}, onMore = {}, onShown = {})
}
