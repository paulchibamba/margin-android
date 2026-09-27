package com.paulchibamba.margin.feature.feed

import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.PostId

@Composable
fun FeedRoute(onOpenNote: (NoteId, PostId?) -> Unit, viewModel: FeedViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val pageCount by rememberUpdatedState(state.pageCount)
    val pagerState = rememberPagerState(initialPage = state.currentIndex) { pageCount }
    FeedScreen(
        state = state,
        pagerState = pagerState,
        onPageEntered = viewModel::onPageEntered,
        onPageLeft = { index, dwell -> viewModel.onPageLeft(index, dwell) },
        onAction = { index, action ->
            if (action == PostAction.READ) readSource(state, index, viewModel, onOpenNote)
            else viewModel.onAction(index, action)
        },
        onReadAhead = { index -> readAhead(state, index, onOpenNote) },
        onEngaged = viewModel::onEngaged,
        onReadOn = { next -> onOpenNote(next.outline.id, null) },
        onMore = viewModel::onMore,
        onSheetDismiss = viewModel::onSheetDismiss,
        onNudgeDismiss = viewModel::onNudgeDismiss,
        onCaughtUpShown = viewModel::onCaughtUpShown,
    )
}

private fun readSource(
    state: FeedUiState,
    index: Int,
    viewModel: FeedViewModel,
    onOpenNote: (NoteId, PostId?) -> Unit,
) {
    val page = state.pages.getOrNull(index) ?: return
    val note = page.context.sourceNote ?: return
    viewModel.onReadSource(index)
    onOpenNote(note, page.item.post.id)
}

private fun readAhead(state: FeedUiState, index: Int, onOpenNote: (NoteId, PostId?) -> Unit) {
    val page = state.pages.getOrNull(index) ?: return
    val note = page.context.readingAhead?.firstNote ?: return
    onOpenNote(note, page.item.post.id)
}
