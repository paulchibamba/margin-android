package com.paulchibamba.margin.feature.read.home

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.designsystem.StatusBarFollowsSkin
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.NoteId

@Composable
fun ReadHomeScreen(
    state: ReadHomeUiState,
    onOpenBook: (BookSlug) -> Unit,
    onOpenNote: (NoteId) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MarginTheme(Skins.Paper) {
        StatusBarFollowsSkin()
        Column(
            modifier
                .fillMaxSize()
                .background(Skins.Paper.background)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(start = 18.dp, top = 10.dp, end = 18.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            ReadHomeHeader(state.streak, onOpenSettings)
            if (!state.isLoading) ReadHomeContent(state, onOpenBook, onOpenNote)
        }
    }
}

@Composable
private fun ReadHomeContent(state: ReadHomeUiState, onOpenBook: (BookSlug) -> Unit, onOpenNote: (NoteId) -> Unit) {
    BookRings(state.rings, onOpenBook)
    state.continueNote?.let { note -> ContinueCard(note, onOpen = { onOpenNote(note.outline.id) }) }
    Text(
        "Library",
        style = MarginTypography.button,
        color = MarginColors.InkText,
        modifier = Modifier.padding(top = 2.dp).semantics { heading() },
    )
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        state.library.forEach { row -> LibraryRow(row, onClick = { onOpenBook(row.book) }) }
    }
}

@Composable
private fun BookRings(rings: List<BookRingState>, onOpenBook: (BookSlug) -> Unit) {
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        rings.forEach { ring -> BookRing(ring, onClick = { onOpenBook(ring.book) }) }
    }
}

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun ReadHomeThreeBooksPreview() {
    ReadHomeScreen(ReadHomePreviewData.threeBooks, onOpenBook = {}, onOpenNote = {}, onOpenSettings = {})
}

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun ReadHomeOneBookPreview() {
    ReadHomeScreen(ReadHomePreviewData.oneBook, onOpenBook = {}, onOpenNote = {}, onOpenSettings = {})
}

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun ReadHomeAllReadPreview() {
    ReadHomeScreen(
        ReadHomePreviewData.oneBook.copy(continueNote = null),
        onOpenBook = {},
        onOpenNote = {},
        onOpenSettings = {},
    )
}
