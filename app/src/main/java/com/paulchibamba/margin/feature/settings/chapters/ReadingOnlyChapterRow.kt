package com.paulchibamba.margin.feature.settings.chapters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Chapter

@Composable
fun ReadingOnlyChapterRow(chapter: Chapter, onChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth().toggleable(chapter.isReadingOnly, role = Role.Checkbox, onValueChange = onChange),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "${chapter.number}",
            style = MarginTypography.monoNumber,
            color = MarginColors.PaperTextFaint,
            modifier = Modifier.widthIn(min = 20.dp),
        )
        Text(
            chapter.title,
            style = MarginTypography.chapterTitle,
            color = MarginColors.InkText,
            modifier = Modifier.weight(1f),
        )
        Checkbox(checked = chapter.isReadingOnly, onCheckedChange = null, colors = checkboxColors())
    }
}

@Composable
private fun checkboxColors() = CheckboxDefaults.colors(
    checkedColor = MarginColors.InkText,
    uncheckedColor = MarginColors.PaperTextAhead,
    checkmarkColor = MarginColors.White,
)

@Preview(widthDp = 328, backgroundColor = 0xFFFBFAF7, showBackground = true)
@Composable
private fun ReadingOnlyChapterRowPreview() {
    ReadingOnlyChapterRow(Chapter(BookSlug("preview"), 1, "Introduction", isReadingOnly = true), onChange = {})
}
