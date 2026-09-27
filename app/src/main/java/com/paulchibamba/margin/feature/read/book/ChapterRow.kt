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
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.feature.read.ReadingProgressBar
import com.paulchibamba.margin.feature.read.progress

private const val READING_ONLY_ALPHA = 0.55f

@Composable
fun ChapterRow(row: ChapterRowState, onOpenNote: () -> Unit, modifier: Modifier = Modifier) {
    val content = if (row.isAhead) MarginColors.PaperTextAhead else MarginColors.InkText
    Row(
        modifier
            .fillMaxWidth()
            .background(MarginColors.Paper)
            .background(if (row.isCurrent) MarginColors.InkText.copy(alpha = 0.05f) else MarginColors.Paper)
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
    val detailColor = if (row.isAhead) MarginColors.PaperTextAhead else MarginColors.PaperTextFaint
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
    when {
        row.isDone -> Icon(painterResource(MarginIcons.CheckCircle), "Read", Modifier.size(22.dp), MarginColors.InkText)
        row.isCurrent && row.nextNote != null -> PlayButton()
    }
}

@Composable
private fun PlayButton() {
    Box(Modifier.size(36.dp).background(MarginColors.InkText, CircleShape), contentAlignment = Alignment.Center) {
        Icon(painterResource(MarginIcons.PlayArrow), "Continue", Modifier.size(22.dp), MarginColors.White)
    }
}

@Preview(widthDp = 360)
@Composable
private fun ChapterRowStatesPreview() {
    Column {
        BookPreviewData.book.chapters.forEach { row -> ChapterRow(row, onOpenNote = {}) }
    }
}
