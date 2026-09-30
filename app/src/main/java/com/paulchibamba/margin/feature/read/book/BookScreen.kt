package com.paulchibamba.margin.feature.read.book

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.designsystem.StatusBarFollowsSkin
import com.paulchibamba.margin.designsystem.component.FeedSnackbar
import com.paulchibamba.margin.domain.model.ChapterRef
import com.paulchibamba.margin.domain.model.NoteId
import kotlinx.coroutines.delay

private const val UNDO_MILLIS = 8_000L

@Composable
fun BookScreen(
    state: BookUiState,
    onBack: () -> Unit,
    onOpenNote: (NoteId) -> Unit,
    onMarkKnown: (ChapterRef) -> Unit,
    onUndo: () -> Unit,
    onUndoDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onChooseCover: () -> Unit = {},
    onRemoveCover: () -> Unit = {},
) {
    var isCoverSheetOpen by rememberSaveable { mutableStateOf(false) }
    MarginTheme(Skins.Paper) {
        StatusBarFollowsSkin()
        Box(modifier.fillMaxSize().background(Skins.Paper.background).statusBarsPadding()) {
            Column {
                BackBar(onBack)
                if (!state.isLoading) ChapterList(state, onOpenNote, onMarkKnown) { isCoverSheetOpen = true }
            }
            state.undoChapter?.let { chapter -> UndoSnackbar(chapter, onUndo, onUndoDismiss) }
        }
        if (isCoverSheetOpen) CoverSheet(state.hasCover, onChooseCover, onRemoveCover) { isCoverSheetOpen = false }
    }
}

@Composable
private fun CoverSheet(hasCover: Boolean, onChooseCover: () -> Unit, onRemoveCover: () -> Unit, onClose: () -> Unit) {
    BookCoverSheet(
        hasCover = hasCover,
        onChooseFromGallery = {
            onClose()
            onChooseCover()
        },
        onRemove = {
            onClose()
            onRemoveCover()
        },
        onDismiss = onClose,
    )
}

@Composable
private fun BackBar(onBack: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(52.dp).padding(horizontal = 4.dp), contentAlignment = Alignment.CenterStart) {
        IconButton(onClick = onBack) {
            Icon(painterResource(MarginIcons.ArrowBack), "Back", Modifier.size(24.dp), MarginColors.InkText)
        }
    }
}

@Composable
private fun ChapterList(
    state: BookUiState,
    onOpenNote: (NoteId) -> Unit,
    onMarkKnown: (ChapterRef) -> Unit,
    onCoverClick: () -> Unit,
) {
    LazyColumn(contentPadding = PaddingValues(bottom = 96.dp)) {
        item { BookHeader(state, onCoverClick) }
        item { Box(Modifier.fillMaxWidth().height(1.dp).background(MarginColors.InkText.copy(alpha = 0.08f))) }
        items(state.chapters, key = { it.chapter.chapter }) { row ->
            SwipeableChapterRow(
                row = row,
                onOpenNote = { row.nextNote?.let(onOpenNote) },
                onMarkKnown = { onMarkKnown(row.chapter) },
            )
        }
    }
}

@Composable
private fun UndoSnackbar(chapter: ChapterRef, onUndo: () -> Unit, onDismiss: () -> Unit) {
    LaunchedEffect(chapter) {
        delay(UNDO_MILLIS)
        onDismiss()
    }
    Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.BottomCenter) {
        FeedSnackbar(
            message = "Chapter ${chapter.chapter} marked as known",
            detail = "Its ideas can now reach your feed",
            actionLabel = "Undo",
            onAction = onUndo,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun BookScreenPreview() {
    BookScreen(BookPreviewData.book, onBack = {}, onOpenNote = {}, onMarkKnown = {}, onUndo = {}, onUndoDismiss = {})
}

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun BookScreenUndoPreview() {
    BookScreen(
        BookPreviewData.book.copy(undoChapter = BookPreviewData.book.chapters.last().chapter),
        onBack = {},
        onOpenNote = {},
        onMarkKnown = {},
        onUndo = {},
        onUndoDismiss = {},
    )
}

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun BookScreenFinishedPreview() {
    BookScreen(
        BookPreviewData.finished,
        onBack = {},
        onOpenNote = {},
        onMarkKnown = {},
        onUndo = {},
        onUndoDismiss = {},
    )
}
