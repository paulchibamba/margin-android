package com.paulchibamba.margin.feature.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.Skin
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.designsystem.StatusBarFollowsSkin
import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.usecase.NextNote
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

private const val CAUGHT_UP_KEY = -1

@Composable
fun FeedScreen(
    state: FeedUiState,
    pagerState: PagerState,
    onPageEntered: (Int) -> Unit,
    onPageLeft: (Int, Duration) -> Unit,
    onAction: (Int, PostAction) -> Unit,
    onReadAhead: (Int) -> Unit,
    onReadOn: (NextNote) -> Unit,
    onMore: () -> Unit,
    onSheetDismiss: () -> Unit,
    onNudgeDismiss: () -> Unit,
    onCaughtUpShown: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize().background(Skins.Ink.background)) {
        if (state.isLoading) return@Box
        MarginTheme(skinAt(state, pagerState.currentPage)) { StatusBarFollowsSkin() }
        PageVisits(state, pagerState, onPageEntered, onPageLeft)
        VerticalPager(
            pagerState,
            Modifier.fillMaxSize().testTag("feed-pager"),
            beyondViewportPageCount = 1,
            key = { index -> pageKeyOf(state, index) },
        ) { index ->
            FeedPagerPage(state, index, onAction, onReadAhead, onReadOn, onMore, onNudgeDismiss, onCaughtUpShown)
        }
        state.sheetPage?.let { page -> WhyThisPostSheet(page.item, onSheetDismiss) }
    }
}

@Composable
private fun FeedPagerPage(
    state: FeedUiState,
    index: Int,
    onAction: (Int, PostAction) -> Unit,
    onReadAhead: (Int) -> Unit,
    onReadOn: (NextNote) -> Unit,
    onMore: () -> Unit,
    onNudgeDismiss: () -> Unit,
    onCaughtUpShown: () -> Unit,
) {
    val caughtUp = state.caughtUp
    if (caughtUp != null && state.isCaughtUpPage(index)) {
        CaughtUpState(caughtUp, state.streak, onReadOn, onMore, onCaughtUpShown, Modifier.testTag("feed-caught-up"))
        return
    }
    FeedPostPage(
        page = state.pages[index],
        streak = state.streak,
        nudge = state.nudgeOn(index),
        onAction = { action -> onAction(index, action) },
        onReadSource = { onAction(index, PostAction.READ) },
        onReadAhead = { onReadAhead(index) },
        onMore = onMore,
        onNudgeDismiss = onNudgeDismiss,
        modifier = Modifier.testTag("feed-page-$index"),
    )
}

@Composable
private fun PageVisits(
    state: FeedUiState,
    pagerState: PagerState,
    onPageEntered: (Int) -> Unit,
    onPageLeft: (Int, Duration) -> Unit,
) {
    val latestState by rememberUpdatedState(state)
    val entered by rememberUpdatedState(onPageEntered)
    val left by rememberUpdatedState(onPageLeft)
    LaunchedEffect(pagerState) {
        var visit: PageVisit? = null
        snapshotFlow { pagerState.settledPage.let { page -> page to pageKeyOf(latestState, page) } }
            .distinctUntilChanged()
            .collect { (page, key) ->
                visit?.takeIf { it.isPost }?.let { previous -> left(previous.key, previous.start.elapsedNow()) }
                visit = PageVisit(key, TimeSource.Monotonic.markNow())
                entered(page)
            }
    }
}

private class PageVisit(val key: Int, val start: TimeMark) {
    val isPost: Boolean get() = key != CAUGHT_UP_KEY
}

private fun pageKeyOf(state: FeedUiState, index: Int): Int = if (index < state.pages.size) index else CAUGHT_UP_KEY

private fun skinAt(state: FeedUiState, index: Int): Skin = state.pages.getOrNull(index)?.skin ?: Skins.Ink

@Composable
private fun FeedScreenPreviewOf(state: FeedUiState) {
    FeedScreen(
        state = state,
        pagerState = rememberPagerState { state.pageCount },
        onPageEntered = {},
        onPageLeft = { _, _ -> },
        onAction = { _, _ -> },
        onReadAhead = {},
        onReadOn = {},
        onMore = {},
        onSheetDismiss = {},
        onNudgeDismiss = {},
        onCaughtUpShown = {},
    )
}

@Preview(widthDp = 360, heightDp = 780)
@Composable
private fun FeedScreenPreview() {
    FeedScreenPreviewOf(FeedUiState(pages = listOf(FeedPreviewData.page(Skins.Ink)), streak = 7))
}

@Preview(widthDp = 360, heightDp = 780)
@Composable
private fun FeedScreenCaughtUpPreview() {
    FeedScreenPreviewOf(FeedUiState(streak = 7, caughtUp = FeedPreviewData.caughtUp))
}
