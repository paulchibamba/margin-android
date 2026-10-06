package com.paulchibamba.margin.feature.drop

import androidx.activity.compose.BackHandler
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
import com.paulchibamba.margin.feature.feed.readAhead
import com.paulchibamba.margin.feature.feed.readSource

@Composable
fun DropRoute(
    onClose: () -> Unit,
    onOpenNote: (NoteId, PostId?) -> Unit,
    viewModel: DropViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val pageCount by rememberUpdatedState(state.pageCount)
    val pagerState = rememberPagerState(initialPage = state.currentIndex) { pageCount }
    val keepGoing = { viewModel.onContinue(); onClose() }
    val close = { viewModel.onDismiss(); onClose() }
    BackHandler(onBack = close)
    LifecycleResumeEffect(viewModel) {
        viewModel.onFeedShown(true)
        onPauseOrDispose { viewModel.onFeedShown(false) }
    }
    DropScreen(
        state = state,
        pagerState = pagerState,
        onPageEntered = { index ->
            if (state.isDropContinuePage(index)) keepGoing() else viewModel.onPageEntered(index)
        },
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
        onNudgeDismiss = viewModel::onNudgeDismiss,
        onKeepGoing = keepGoing,
        onClose = close,
    )
}
