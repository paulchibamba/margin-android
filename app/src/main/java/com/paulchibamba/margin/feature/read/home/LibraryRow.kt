package com.paulchibamba.margin.feature.read.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSurfacePalette
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.domain.model.Priority
import com.paulchibamba.margin.feature.read.BookCover
import com.paulchibamba.margin.feature.read.ReadingProgressBar
import com.paulchibamba.margin.feature.read.bookProgressLabel
import com.paulchibamba.margin.feature.read.label
import com.paulchibamba.margin.feature.read.progress

private val CoverShape = RoundedCornerShape(6.dp)
private val TagShape = RoundedCornerShape(5.dp)

@Composable
fun LibraryRow(row: LibraryRowState, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalSurfacePalette.current
    Row(
        modifier.fillMaxWidth().clickable(role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BookCover(row.coverPath, CoverShape, Modifier.size(width = 42.dp, height = 58.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    row.title,
                    style = MarginTypography.libraryTitle,
                    color = palette.text,
                    modifier = Modifier.weight(1f),
                )
                PriorityTag(row.priority)
            }
            ReadingProgressBar(row.tally.progress())
            Text(bookProgressLabel(row.tally), style = MarginTypography.label, color = palette.faintText)
        }
    }
}

@Composable
private fun PriorityTag(priority: Priority?) {
    val isMain = priority == Priority.MAIN
    val palette = LocalSurfacePalette.current
    Text(
        priority?.label() ?: "Inactive",
        style = MarginTypography.tag,
        color = if (isMain) palette.onAccent else palette.text,
        modifier = Modifier
            .background(if (isMain) palette.accent else palette.text.copy(alpha = 0.08f), TagShape)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}

@Preview(widthDp = 360, backgroundColor = 0xFFF3F0E9, showBackground = true)
@Composable
private fun LibraryRowPreview() {
    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        ReadHomePreviewData.threeBooks.library.forEach { row -> LibraryRow(row, onClick = {}) }
    }
}
