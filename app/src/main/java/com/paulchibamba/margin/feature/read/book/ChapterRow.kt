package com.paulchibamba.margin.feature.read.book

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSurfacePalette
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.feature.read.ReadingProgressBar
import com.paulchibamba.margin.feature.read.progress

private const val READING_ONLY_ALPHA = 0.55f

@Composable
fun ChapterRow(row: ChapterRowState, onOpenNote: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalSurfacePalette.current
    val content = if (row.isAhead) palette.aheadText else palette.text
    Row(
        modifier
            .fillMaxWidth()
            .background(palette.background)
            .background(if (row.isCurrent) palette.text.copy(alpha = 0.05f) else palette.background)
            .clickable(enabled = row.nextNote != null, role = Role.Button, onClick = onOpenNote)
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .alpha(if (row.isReadingOnly && row.isDone) READING_ONLY_ALPHA else 1f),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "${row.chapter.chapter}",
            style = MarginTypography.monoNumber,
            color = content,
            modifier = Modifier.widthIn(min = 18.dp),
        )
        ChapterText(row, content, Modifier.weight(1f))
        ChapterTrailing(row)
    }
}

@Composable
private fun ChapterText(row: ChapterRowState, content: Color, modifier: Modifier) {
    val palette = LocalSurfacePalette.current
    val detailColor = if (row.isAhead) palette.aheadText else palette.faintText
    Column(modifier, verticalArrangement = Arrangement.spacedBy(if (row.isCurrent) 5.dp else 2.dp)) {
        val titleStyle = if (row.isCurrent) MarginTypography.currentChapterTitle else MarginTypography.chapterTitle
        Text(row.title, style = titleStyle, color = content)
        if (row.isCurrent) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                ReadingProgressBar(row.tally.progress(), Modifier.weight(1f, fill = false).widthIn(max = 120.dp))
                Text(chapterDetailLabel(row), style = MarginTypography.label, color = detailColor)
            }
        } else {
            Text(chapterDetailLabel(row), style = MarginTypography.label, color = detailColor)
        }
    }
}

@Composable
private fun ChapterTrailing(row: ChapterRowState) {
    val palette = LocalSurfacePalette.current
    when {
        row.isDone -> Icon(painterResource(MarginIcons.CheckCircle), "Read", Modifier.size(22.dp), palette.text)
        row.isCurrent && row.nextNote != null -> PlayButton()
    }
}

@Composable
private fun PlayButton() {
    val palette = LocalSurfacePalette.current
    Box(Modifier.size(36.dp).background(palette.accent, CircleShape), contentAlignment = Alignment.Center) {
        Icon(painterResource(MarginIcons.PlayArrow), "Continue", Modifier.size(22.dp), palette.onAccent)
    }
}

@Preview(widthDp = 360)
@Composable
private fun ChapterRowStatesPreview() {
    Column {
        BookPreviewData.book.chapters.forEach { row -> ChapterRow(row, onOpenNote = {}) }
    }
}
