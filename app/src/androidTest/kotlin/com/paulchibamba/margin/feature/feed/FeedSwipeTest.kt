package com.paulchibamba.margin.feature.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.Skins
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class FeedSwipeTest {

    @get:Rule
    val compose = createComposeRule()

    private lateinit var pagerState: PagerState

    @Test
    fun `a slow quarter page swipe lands on the next post`() {
        showFeed()

        dragUp(pageFraction = 0.25f, durationMillis = SLOW_DRAG_MILLIS)

        assertEquals(1, settledPage())
    }

    @Test
    fun `a slow swipe under a tenth of the page snaps back`() {
        showFeed()

        dragUp(pageFraction = 0.08f, durationMillis = SLOW_DRAG_MILLIS)

        assertEquals(0, settledPage())
    }

    @Test
    fun `a short fast flick lands on the next post`() {
        showFeed()

        dragUp(pageFraction = 0.08f, durationMillis = FLICK_MILLIS)

        assertEquals(1, settledPage())
    }

    @Test
    fun `a hard full page fling moves exactly one post`() {
        showFeed()

        dragUp(pageFraction = 0.9f, durationMillis = HARD_FLING_MILLIS)

        assertEquals(1, settledPage())
    }

    private fun showFeed() {
        val state = FeedUiState(pages = List(POST_COUNT) { FeedPreviewData.page(Skins.Ink) }, streak = 7)
        compose.setContent {
            pagerState = rememberPagerState { state.pageCount }
            Box(Modifier.requiredSize(360.dp, 780.dp)) { FeedScreenOf(state, pagerState) }
        }
    }

    @Composable
    private fun FeedScreenOf(state: FeedUiState, pagerState: PagerState) {
        FeedScreen(
            state = state,
            pagerState = pagerState,
            onPageEntered = {},
            onPageLeft = { _, _ -> },
            onAction = { _, _ -> },
            onReadAhead = {},
            onEngaged = {},
            onRespond = { _, _ -> },
            onReadOn = {},
            onMore = {},
            onSheetDismiss = {},
            onNudgeDismiss = {},
            onCaughtUpShown = {},
        )
    }

    private fun dragUp(pageFraction: Float, durationMillis: Long) {
        compose.onNodeWithTag("feed-pager").performTouchInput {
            val startY = centerY + height * pageFraction / 2
            swipeUp(startY, startY - height * pageFraction, durationMillis)
        }
    }

    private fun settledPage(): Int = compose.runOnIdle { pagerState.settledPage }

    private companion object {
        const val POST_COUNT = 4
        const val SLOW_DRAG_MILLIS = 1_500L
        const val FLICK_MILLIS = 40L
        const val HARD_FLING_MILLIS = 60L
    }
}
