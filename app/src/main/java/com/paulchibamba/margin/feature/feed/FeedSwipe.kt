package com.paulchibamba.margin.feature.feed

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.TargetedFlingBehavior
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.unit.Velocity
import kotlin.math.absoluteValue

object FeedSwipe {
    const val PAGE_FRACTION_TO_COMMIT = 0.15f
    const val PAGES_PER_FLING = 1

    @Composable
    fun flingBehavior(state: PagerState): TargetedFlingBehavior =
        PagerDefaults.flingBehavior(
            state = state,
            pagerSnapDistance = PagerSnapDistance.atMost(PAGES_PER_FLING),
            snapPositionalThreshold = PAGE_FRACTION_TO_COMMIT,
        )

    @Composable
    fun pageNestedScrollConnection(state: PagerState): NestedScrollConnection {
        val default = PagerDefaults.pageNestedScrollConnection(state, Orientation.Vertical)
        return remember(state, default) { FlingPassesToPagerBetweenPosts(state, default) }
    }
}

private class FlingPassesToPagerBetweenPosts(
    private val state: PagerState,
    private val default: NestedScrollConnection,
) : NestedScrollConnection by default {

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity =
        if (isBetweenPosts()) Velocity.Zero else default.onPostFling(consumed, available)

    private fun isBetweenPosts(): Boolean = state.currentPageOffsetFraction.absoluteValue > SETTLED_TOLERANCE

    private companion object {
        const val SETTLED_TOLERANCE = 1e-6f
    }
}
