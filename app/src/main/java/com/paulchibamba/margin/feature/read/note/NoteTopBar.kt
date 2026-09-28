package com.paulchibamba.margin.feature.read.note

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.feature.feed.readingTimeLabel
import com.paulchibamba.margin.feature.read.ReadingProgressBar

@Composable
fun NoteTopBar(state: NoteUiState, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        ReadingProgressBar(
            fraction = state.place.fraction,
            height = 3.dp,
            track = MarginColors.InkText.copy(alpha = 0.08f),
        )
        Row(
            Modifier.fillMaxWidth().height(56.dp).padding(start = 12.dp, end = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (state.fromPost != null) BackToFeedButton(onBack) else BackButton(onBack)
            Text(
                "${state.positionLabel} · ${readingTimeLabel(state.readingTime)}",
                style = MarginTypography.monoStrong,
                color = MarginColors.PaperTextFaint,
            )
        }
    }
}

@Composable
private fun BackToFeedButton(onClick: () -> Unit) {
    Row(
        Modifier
            .clip(CircleShape)
            .background(MarginColors.InkText)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(start = 10.dp, top = 8.dp, end = 14.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painterResource(MarginIcons.ArrowBack), null, Modifier.size(18.dp), MarginColors.White)
        Text("Back to feed", style = MarginTypography.smallButton, color = MarginColors.White)
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(painterResource(MarginIcons.ArrowBack), "Back", Modifier.size(24.dp), MarginColors.InkText)
    }
}

@Preview(widthDp = 360, backgroundColor = 0xFFFBFAF7, showBackground = true)
@Composable
private fun NoteTopBarFromPostPreview() {
    NoteTopBar(NotePreviewData.fromPost, onBack = {})
}

@Preview(widthDp = 360, backgroundColor = 0xFFFBFAF7, showBackground = true)
@Composable
private fun NoteTopBarPreview() {
    NoteTopBar(NotePreviewData.fromBook, onBack = {})
}
