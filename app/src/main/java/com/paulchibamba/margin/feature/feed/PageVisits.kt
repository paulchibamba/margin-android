package com.paulchibamba.margin.feature.feed

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

private const val NOT_A_POST_KEY = -1

@Composable
internal fun PageVisits(
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

@Composable
internal fun ScrollActivity(pagerState: PagerState, onScrolling: (Boolean) -> Unit) {
    val scrolling by rememberUpdatedState(onScrolling)
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.isScrollInProgress }.distinctUntilChanged().collect { scrolling(it) }
    }
}

private class PageVisit(val key: Int, val start: TimeMark) {
    val isPost: Boolean get() = key != NOT_A_POST_KEY
}

internal fun pageKeyOf(state: FeedUiState, index: Int): Int = if (index < state.pages.size) index else NOT_A_POST_KEY
