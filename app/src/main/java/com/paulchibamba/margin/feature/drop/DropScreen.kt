package com.paulchibamba.margin.feature.drop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.Skin
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.designsystem.StatusBarFollowsSkin
import com.paulchibamba.margin.designsystem.component.SegmentProgress
import com.paulchibamba.margin.designsystem.component.TopBarDrop
import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.tracking.InteractionKind
import com.paulchibamba.margin.feature.feed.FeedPage
import com.paulchibamba.margin.feature.feed.FeedPostPage
import com.paulchibamba.margin.feature.feed.FeedPreviewData
import com.paulchibamba.margin.feature.feed.FeedSwipe
import com.paulchibamba.margin.feature.feed.FeedUiState
import com.paulchibamba.margin.feature.feed.PageVisits
import com.paulchibamba.margin.feature.feed.PostBodyCallbacks
import com.paulchibamba.margin.feature.feed.ScrollActivity
import com.paulchibamba.margin.feature.feed.post.TestResponse
import kotlin.time.Duration

private const val COMPLETION_KEY = -1
private const val CONTINUE_KEY = -2

@Composable
fun DropScreen(
    state: FeedUiState,
    pagerState: PagerState,
    onPageEntered: (Int) -> Unit,
    onPageLeft: (Int, Duration) -> Unit,
    onAction: (Int, PostAction) -> Unit,
    onReadAhead: (Int) -> Unit,
    onEngaged: (Int) -> Unit,
    onRespond: (Int, TestResponse) -> Unit,
    onInteraction: (Int, InteractionKind) -> Unit,
    onScrolling: (Boolean) -> Unit,
    onNudgeDismiss: () -> Unit,
    onKeepGoing: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize().background(Skins.Ink.background)) {
        if (state.isLoading) return@Box
        MarginTheme(skinAt(state, pagerState.currentPage)) { StatusBarFollowsSkin() }
        PageVisits(state, pagerState, onPageEntered, onPageLeft)
        ScrollActivity(pagerState, onScrolling)
        VerticalPager(
            pagerState,
            Modifier.fillMaxSize().testTag("drop-pager"),
            beyondViewportPageCount = 1,
            flingBehavior = FeedSwipe.flingBehavior(pagerState),
            pageNestedScrollConnection = FeedSwipe.pageNestedScrollConnection(pagerState),
            key = { index -> keyOf(state, index) },
        ) { index ->
            val callbacks = callbacksFor(index, onAction, onReadAhead, onEngaged, onRespond, onInteraction)
            when {
                state.isDropCompletionPage(index) -> CompletionPageOf(state, onKeepGoing, onClose)
                state.isDropContinuePage(index) -> DropContinuePage()
                else -> DropPostPage(state, index, onAction, callbacks, onNudgeDismiss, onClose)
            }
        }
    }
}

@Composable
private fun CompletionPageOf(state: FeedUiState, onKeepGoing: () -> Unit, onClose: () -> Unit) {
    val drop = state.drop ?: return
    DropCompletionPage(drop.completion, state.streak, drop.size, onKeepGoing, onClose)
}

@Composable
private fun DropPostPage(
    state: FeedUiState,
    index: Int,
    onAction: (Int, PostAction) -> Unit,
    callbacks: PostBodyCallbacks,
    onNudgeDismiss: () -> Unit,
    onClose: () -> Unit,
) {
    val page = state.pages[index]
    FeedPostPage(
        page = page,
        streak = state.streak,
        nudge = state.nudgeOn(index),
        onAction = { action -> onAction(index, action) },
        callbacks = callbacks,
        onMore = {},
        onNudgeDismiss = onNudgeDismiss,
        modifier = Modifier.testTag("drop-page-$index"),
        drop = progressOf(page, state.drop?.size ?: 0, onClose),
    )
}

private fun progressOf(page: FeedPage, size: Int, onClose: () -> Unit): TopBarDrop? {
    val dropIndex = page.dropIndex?.takeIf { size > 0 } ?: return null
    return TopBarDrop.Progress(DropLabels.progress(dropIndex, size), SegmentProgress(size, dropIndex), onClose)
}

private fun callbacksFor(
    index: Int,
    onAction: (Int, PostAction) -> Unit,
    onReadAhead: (Int) -> Unit,
    onEngaged: (Int) -> Unit,
    onRespond: (Int, TestResponse) -> Unit,
    onInteraction: (Int, InteractionKind) -> Unit,
) = PostBodyCallbacks(
    onReadSource = { onAction(index, PostAction.READ) },
    onReadAhead = { onReadAhead(index) },
    onEngaged = { onEngaged(index) },
    onRespond = { response -> onRespond(index, response) },
    onInteraction = { kind -> onInteraction(index, kind) },
)

private fun keyOf(state: FeedUiState, index: Int): Int = when {
    state.isDropCompletionPage(index) -> COMPLETION_KEY
    state.isDropContinuePage(index) -> CONTINUE_KEY
    else -> index
}

private fun skinAt(state: FeedUiState, index: Int): Skin = state.pages.getOrNull(index)?.skin ?: Skins.Ink

@Preview(widthDp = 360, heightDp = 780)
@Composable
private fun DropScreenPreview() {
    val page = FeedPreviewData.page(Skins.Ink).copy(dropIndex = 2)
    val state = FeedUiState(pages = listOf(page), streak = 3, drop = DropRun(size = 6))
    DropScreen(
        state = state,
        pagerState = rememberPagerState { state.pageCount },
        onPageEntered = {},
        onPageLeft = { _, _ -> },
        onAction = { _, _ -> },
        onReadAhead = {},
        onEngaged = {},
        onRespond = { _, _ -> },
        onInteraction = { _, _ -> },
        onScrolling = {},
        onNudgeDismiss = {},
        onKeepGoing = {},
        onClose = {},
    )
}
