package com.paulchibamba.margin.feature.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.Skin
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.designsystem.StatusBarFollowsSkin
import com.paulchibamba.margin.designsystem.component.TopBarDrop
import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.tracking.InteractionKind
import com.paulchibamba.margin.domain.usecase.NextNote
import com.paulchibamba.margin.feature.debug.DebugToolsSheet
import com.paulchibamba.margin.feature.feed.post.TestResponse
import kotlin.time.Duration

@Composable
fun FeedScreen(
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
    onReadOn: (NextNote) -> Unit,
    onMore: () -> Unit,
    onSheetDismiss: () -> Unit,
    onNudgeDismiss: () -> Unit,
    onCaughtUpShown: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenDrop: () -> Unit = {},
    debugTools: DebugToolsSlot? = null,
) {
    var isDebugSheetOpen by rememberSaveable { mutableStateOf(false) }
    Box(modifier.fillMaxSize().background(Skins.Ink.background)) {
        if (state.isLoading) return@Box
        MarginTheme(skinAt(state, pagerState.currentPage)) { StatusBarFollowsSkin() }
        PageVisits(state, pagerState, onPageEntered, onPageLeft)
        ScrollActivity(pagerState, onScrolling)
        VerticalPager(
            pagerState,
            Modifier.fillMaxSize().testTag("feed-pager"),
            beyondViewportPageCount = 1,
            flingBehavior = FeedSwipe.flingBehavior(pagerState),
            pageNestedScrollConnection = FeedSwipe.pageNestedScrollConnection(pagerState),
            key = { index -> pageKeyOf(state, index) },
        ) { index ->
            FeedPagerPage(
                state = state,
                index = index,
                onAction = onAction,
                callbacks = PostBodyCallbacks(
                    onReadSource = { onAction(index, PostAction.READ) },
                    onReadAhead = { onReadAhead(index) },
                    onEngaged = { onEngaged(index) },
                    onRespond = { response -> onRespond(index, response) },
                    onInteraction = { kind -> onInteraction(index, kind) },
                ),
                onReadOn = onReadOn,
                onMore = onMore,
                onCaughtUpMore = { isDebugSheetOpen = debugTools != null },
                onNudgeDismiss = onNudgeDismiss,
                onCaughtUpShown = onCaughtUpShown,
                drop = state.dropPillLabel?.let { label -> TopBarDrop.Pill(label, onOpenDrop) },
            )
        }
        state.sheetPage?.let { page -> WhyThisPostSheet(page.item, onSheetDismiss, debugTools) }
        if (isDebugSheetOpen && debugTools != null) DebugToolsSheet({ isDebugSheetOpen = false }, debugTools)
    }
}

@Composable
private fun FeedPagerPage(
    state: FeedUiState,
    index: Int,
    onAction: (Int, PostAction) -> Unit,
    callbacks: PostBodyCallbacks,
    onReadOn: (NextNote) -> Unit,
    onMore: () -> Unit,
    onCaughtUpMore: () -> Unit,
    onNudgeDismiss: () -> Unit,
    onCaughtUpShown: () -> Unit,
    drop: TopBarDrop?,
) {
    val caughtUp = state.caughtUp
    if (caughtUp != null && state.isCaughtUpPage(index)) {
        val tag = Modifier.testTag("feed-caught-up")
        CaughtUpState(
            caughtUp, state.streak, onReadOn, onCaughtUpMore, onCaughtUpShown, tag, state.caughtUpCoverPath, drop,
        )
        return
    }
    FeedPostPage(
        page = state.pages[index],
        streak = state.streak,
        nudge = state.nudgeOn(index),
        onAction = { action -> onAction(index, action) },
        callbacks = callbacks,
        onMore = onMore,
        onNudgeDismiss = onNudgeDismiss,
        modifier = Modifier.testTag("feed-page-$index"),
        drop = drop,
    )
}

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
        onEngaged = {},
        onRespond = { _, _ -> },
        onInteraction = { _, _ -> },
        onScrolling = {},
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

@Preview(widthDp = 360, heightDp = 780, fontScale = 2f)
@Composable
private fun FeedScreenLargeTextPreview() {
    FeedScreenPreviewOf(FeedUiState(pages = listOf(FeedPreviewData.page(Skins.Ink)), streak = 7))
}

@Preview(widthDp = 360, heightDp = 780)
@Composable
private fun FeedScreenCaughtUpPreview() {
    FeedScreenPreviewOf(FeedUiState(streak = 7, caughtUp = FeedPreviewData.caughtUp))
}
