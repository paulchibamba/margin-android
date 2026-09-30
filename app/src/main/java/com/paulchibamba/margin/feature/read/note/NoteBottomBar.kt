package com.paulchibamba.margin.feature.read.note

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSurfacePalette
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography

@Composable
fun NoteBottomBar(
    state: NoteUiState,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalSurfacePalette.current
    Column(modifier.fillMaxWidth().background(palette.card)) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(palette.divider))
        Row(
            Modifier.fillMaxWidth().navigationBarsPadding().padding(PaddingValues(16.dp, 12.dp, 16.dp, 16.dp)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PreviousButton(isEnabled = state.previous != null, onClick = onPrevious)
            ReadMark(isRead = state.isRead)
            NextButton(label = if (state.next == null) "Done" else "Next", onClick = onNext)
        }
    }
}

@Composable
private fun PreviousButton(isEnabled: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .alpha(if (isEnabled) 1f else 0f)
            .clip(CircleShape)
            .clickable(enabled = isEnabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val faintText = LocalSurfacePalette.current.faintText
        Icon(painterResource(MarginIcons.ChevronLeft), null, Modifier.size(20.dp), faintText)
        Text("Prev", style = MarginTypography.readerButton, color = faintText)
    }
}

@Composable
private fun ReadMark(isRead: Boolean) {
    Row(
        Modifier.alpha(if (isRead) 1f else 0f).semantics { liveRegion = LiveRegionMode.Polite },
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val correct = LocalSurfacePalette.current.correct
        Icon(painterResource(MarginIcons.CheckCircle), null, Modifier.size(16.dp), correct)
        Text(if (isRead) "Read" else "", style = MarginTypography.label, color = correct)
    }
}

@Composable
private fun NextButton(label: String, onClick: () -> Unit) {
    val palette = LocalSurfacePalette.current
    Row(
        Modifier
            .clip(CircleShape)
            .background(palette.accent)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(start = 20.dp, top = 12.dp, end = 16.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MarginTypography.readerButtonStrong, color = palette.onAccent)
        Icon(painterResource(MarginIcons.ChevronRight), null, Modifier.size(20.dp), palette.onAccent)
    }
}

@Preview(widthDp = 360)
@Composable
private fun NoteBottomBarReadPreview() {
    NoteBottomBar(NotePreviewData.fromPost, onPrevious = {}, onNext = {})
}

@Preview(widthDp = 360)
@Composable
private fun NoteBottomBarLastNotePreview() {
    NoteBottomBar(NotePreviewData.fromBook.copy(next = null, isRead = false), onPrevious = {}, onNext = {})
}
