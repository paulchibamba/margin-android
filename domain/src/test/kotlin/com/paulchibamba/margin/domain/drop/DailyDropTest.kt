package com.paulchibamba.margin.domain.drop

import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.now
import com.paulchibamba.margin.domain.model.PostId
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class DailyDropTest {
    private val items = (1..4).map { index -> DropItem(PostId("p$index"), DropSlot.EXTRA, CandidateSource.ANGLE) }
    private val drop = DailyDrop(LocalDate.parse("2026-10-01"), now, composedAtStep = 10, items = items)

    private fun seen(vararg steps: Pair<String, Int>) =
        FeedState(rewardAtStep = 99, seenPosts = steps.associate { (post, step) -> PostId(post) to step })

    @Test
    fun `items before the position are done`() {
        assertEquals(listOf(2, 3), drop.left(1).remainingIndices(seen()))
    }

    @Test
    fun `an item seen in the feed after composing counts as done`() {
        val state = seen("p2" to 11, "p3" to 9)

        assertEquals(listOf(0, 2, 3), drop.remainingIndices(state))
        assertEquals(1, drop.doneCount(state))
    }

    @Test
    fun `an item entered in the drop but not left is resumed`() {
        val entered = drop.left(0).entered(1, step = 12)

        assertEquals(listOf(1, 2, 3), entered.remainingIndices(seen("p1" to 11, "p2" to 12)))
    }

    @Test
    fun `an entered item seen again in the feed is done`() {
        val entered = drop.entered(0, step = 12)

        assertEquals(listOf(1, 2, 3), entered.remainingIndices(seen("p1" to 15)))
    }
}
