package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.actions.ActionLogEntry
import com.paulchibamba.margin.domain.feed.Confidence
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.ReviewLogEntry
import com.paulchibamba.margin.domain.progression.ReadingState
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.ContentRepository
import com.paulchibamba.margin.domain.repository.ProgressRepository
import com.paulchibamba.margin.domain.rewards.DailyActivity
import com.paulchibamba.margin.domain.rewards.StreakCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ObserveStats @Inject constructor(
    private val content: ContentRepository,
    private val progress: ProgressRepository,
    private val clock: Clock,
) {
    operator fun invoke(): Flow<StatsReport> = combine(
        progress.observeFeedState(),
        progress.observeActions(),
        progress.observeReviews(),
        progress.observeReading(),
        progress.observeActivity(),
    ) { state, actions, reviews, reading, activity -> reportOf(state, actions, reviews, reading, activity) }

    private suspend fun reportOf(
        state: FeedState?,
        actions: List<ActionLogEntry>,
        reviews: List<ReviewLogEntry>,
        reading: ReadingState,
        activity: List<DailyActivity>,
    ): StatsReport {
        val streak = StreakCalculator(clock.zone())
        val today = streak.today(clock.now())
        return StatsReport(
            date = today,
            postsSeen = state?.seenPosts?.size ?: 0,
            currentStreak = streak.currentStreak(activity, today),
            affinity = state?.affinity?.values.orEmpty(),
            actionCounts = actions.groupingBy(ActionLogEntry::action).eachCount(),
            lostConcepts = content.concepts().filter { state?.progressOf(it)?.confidence == Confidence.LOST },
            reviewCounts = reviews.groupingBy(ReviewLogEntry::rating).eachCount(),
            frontiers = frontiersOf(reading),
        )
    }

    private suspend fun frontiersOf(reading: ReadingState): List<BookFrontier> {
        val readingProgress = reading.progressWith(content.noteOutlines())
        return content.books().map { book -> BookFrontier(book, readingProgress.frontierOf(book.slug)) }
    }
}
