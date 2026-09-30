package com.paulchibamba.margin.feature.read.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSurfacePalette
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.domain.usecase.ContinueNote
import com.paulchibamba.margin.feature.feed.chapterLabel
import com.paulchibamba.margin.feature.feed.nextNoteDetail
import com.paulchibamba.margin.feature.read.ReadingProgressBar

private val CardShape = RoundedCornerShape(24.dp)
private val faintWhite = MarginColors.White.copy(alpha = 0.6f)

@Composable
fun ContinueCard(note: ContinueNote, onOpen: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(LocalSurfacePalette.current.emphasisCard)
            .clickable(role = Role.Button, onClickLabel = "Continue reading", onClick = onOpen)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ContinueHeading(note)
        Text(note.outline.section, style = MarginTypography.continueTitle, color = MarginColors.White)
        ReadingProgressBar(
            fraction = note.place.fraction,
            height = 4.dp,
            color = MarginColors.Lime,
            track = MarginColors.White.copy(alpha = 0.18f),
        )
        ContinueFooter(note)
    }
}

@Composable
private fun ContinueHeading(note: ContinueNote) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        val chapter = chapterLabel(note.outline.position.chapter, note.chapterTitle)
        Text("Continue · $chapter", style = MarginTypography.chip, color = faintWhite, modifier = Modifier.weight(1f))
        Text("${note.place.order}/${note.place.noteCount}", style = MarginTypography.monoStrong, color = faintWhite)
    }
}

@Composable
private fun ContinueFooter(note: ContinueNote) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            nextNoteDetail(note.outline.readingTime, note.unlockedPosts),
            style = MarginTypography.caption,
            color = MarginColors.White.copy(alpha = 0.8f),
        )
        Box(Modifier.size(44.dp).background(MarginColors.Lime, CircleShape), contentAlignment = Alignment.Center) {
            Icon(painterResource(MarginIcons.PlayArrow), null, Modifier.size(26.dp), MarginColors.InkText)
        }
    }
}

@Preview(widthDp = 360)
@Composable
private fun ContinueCardPreview() {
    ContinueCard(ReadHomePreviewData.continueNote, onOpen = {}, modifier = Modifier.padding(18.dp))
}
