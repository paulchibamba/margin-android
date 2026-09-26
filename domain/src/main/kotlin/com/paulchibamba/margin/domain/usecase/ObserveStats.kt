package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.actions.ActionLogEntry
import com.paulchibamba.margin.domain.feed.Confidence
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.ReviewLogEntry
import com.paulchibamba.margin.domain.progression.ReadingState
import com.paulchibamba.margin.domain.repository.ContentRepository
import com.paulchibamba.margin.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ObserveStats @Inject constructor(
    private val content: ContentRepository,
    private val progress: ProgressRepository,
) {
    operator fun invoke(): Flow<Stats> = combine(
        progress.observeFeedState(),
        progress.observeActions(),
        progress.observeReviews(),
        progress.observeReading(),
    ) { state, actions, reviews, reading -> statsOf(state, actions, reviews, reading) }

    private suspend fun statsOf(
        state: FeedState?,
        actions: List<ActionLogEntry>,
        reviews: List<ReviewLogEntry>,
        reading: ReadingState,
    ): Stats {
        val readingProgress = reading.progressWith(content.noteOutlines())
        return Stats(
            affinity = state?.affinity?.values.orEmpty(),
            postsSeen = state?.seenPosts?.size ?: 0,
            actionCounts = actions.groupingBy(ActionLogEntry::action).eachCount(),
            lostConcepts = content.concepts().filter { state?.progressOf(it)?.confidence == Confidence.LOST },
            reviewCounts = reviews.groupingBy(ReviewLogEntry::rating).eachCount(),
            frontiers = content.books().associate { book -> book.slug to readingProgress.frontierOf(book.slug) },
        )
    }
}
