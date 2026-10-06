package com.paulchibamba.margin.feature.feed

import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.feature.debug.DebugTools
import com.paulchibamba.margin.feature.debug.DebugToolsRoute

@Composable
fun FeedRoute(
    onOpenNote: (NoteId, PostId?) -> Unit,
    onOpenDrop: () -> Unit,
    viewModel: FeedViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ReportDrawnWhen { state.pages.isNotEmpty() || state.isCaughtUp }
    val pageCount by rememberUpdatedState(state.pageCount)
    val pagerState = rememberPagerState(initialPage = state.currentIndex) { pageCount }
    LifecycleResumeEffect(viewModel) {
        viewModel.onFeedShown(true)
        onPauseOrDispose { viewModel.onFeedShown(false) }
    }
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
        onRespond = viewModel::onRespond,
        onInteraction = viewModel::onInteraction,
        onScrolling = viewModel::onScrolling,
        onReadOn = { next -> onOpenNote(next.outline.id, null) },
        onMore = viewModel::onMore,
        onSheetDismiss = viewModel::onSheetDismiss,
        onNudgeDismiss = viewModel::onNudgeDismiss,
        onCaughtUpShown = viewModel::onCaughtUpShown,
        onOpenDrop = onOpenDrop,
        debugTools = debugToolsFor(viewModel),
    )
}

private fun debugToolsFor(viewModel: FeedViewModel): DebugToolsSlot? =
    if (DebugTools.isEnabled) { modifier -> DebugToolsRoute(viewModel::onClockChanged, modifier) } else null

internal fun readSource(
    state: FeedUiState,
    index: Int,
    viewModel: PostPagerViewModel,
    onOpenNote: (NoteId, PostId?) -> Unit,
) {
    val page = state.pages.getOrNull(index) ?: return
    val note = page.context.sourceNote ?: return
    viewModel.onReadSource(index)
    onOpenNote(note, page.item.post.id)
}

internal fun readAhead(state: FeedUiState, index: Int, onOpenNote: (NoteId, PostId?) -> Unit) {
    val page = state.pages.getOrNull(index) ?: return
    val note = page.context.readingAhead?.firstNote ?: return
    onOpenNote(note, page.item.post.id)
}
