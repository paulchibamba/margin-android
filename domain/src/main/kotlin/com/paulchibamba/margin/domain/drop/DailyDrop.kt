package com.paulchibamba.margin.domain.drop

import com.paulchibamba.margin.domain.feed.FeedState
import java.time.Instant
import java.time.LocalDate

data class DailyDrop(
    val date: LocalDate,
    val composedAt: Instant,
    val composedAtStep: Int,
    val items: List<DropItem>,
    val headline: String? = null,
    val position: Int = 0,
    val completedAt: Instant? = null,
    val isContinuedIntoFeed: Boolean = false,
) {
    val size: Int
        get() = items.size

    val isCompleted: Boolean
        get() = completedAt != null

    fun isDone(index: Int, state: FeedState): Boolean =
        index < position || items[index].isSeenOutsideDrop(state, composedAtStep)

    fun doneCount(state: FeedState): Int = items.indices.count { index -> isDone(index, state) }

    fun remainingIndices(state: FeedState): List<Int> = items.indices.filterNot { index -> isDone(index, state) }

    fun entered(index: Int, step: Int): DailyDrop {
        val entered = items[index].copy(enteredAtStep = step)
        return copy(items = items.mapIndexed { itemIndex, item -> if (itemIndex == index) entered else item })
    }

    fun left(index: Int): DailyDrop = copy(position = maxOf(position, index + 1))
}
