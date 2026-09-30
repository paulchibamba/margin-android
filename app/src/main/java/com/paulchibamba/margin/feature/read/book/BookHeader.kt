package com.paulchibamba.margin.feature.read.book

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSurfacePalette
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.feature.read.BookCover
import com.paulchibamba.margin.feature.read.StripedCover

private val CoverShape = RoundedCornerShape(8.dp)

@Composable
fun BookHeader(state: BookUiState, onCoverClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier.padding(start = 18.dp, end = 18.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val palette = LocalSurfacePalette.current
        HeaderCover(state, onCoverClick)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                state.title,
                style = MarginTypography.bookTitle,
                color = palette.text,
                modifier = Modifier.semantics { heading() },
            )
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                val total = state.conceptCount
                BookCount(MarginIcons.MenuBook, "${state.introduced}/$total introduced", palette.text)
                BookCount(MarginIcons.Psychology, "${state.remembered}/$total remembered", palette.faintText)
            }
        }
    }
}

@Composable
private fun HeaderCover(state: BookUiState, onClick: () -> Unit) {
    BookCover(
        state.coverPath,
        CoverShape,
        Modifier.shadow(10.dp, CoverShape, ambientColor = Color.Black, spotColor = Color.Black)
            .size(width = 84.dp, height = 116.dp)
            .clip(CoverShape)
            .clickable(onClickLabel = "Change cover", role = Role.Button, onClick = onClick)
            .semantics { contentDescription = "Cover of ${state.title}" },
        placeholder = { placeholderModifier -> StripedCover(CoverShape, placeholderModifier, stripeWidth = 6.dp) },
    )
}

@Composable
private fun BookCount(icon: Int, label: String, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(painterResource(icon), null, Modifier.size(17.dp), color)
        Text(label, style = MarginTypography.count, color = color)
    }
}

@Preview(widthDp = 360, backgroundColor = 0xFFF3F0E9, showBackground = true)
@Composable
private fun BookHeaderPreview() {
    BookHeader(BookPreviewData.book, onCoverClick = {})
}
