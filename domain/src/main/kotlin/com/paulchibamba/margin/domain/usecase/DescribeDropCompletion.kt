package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.drop.DropCompletion
import com.paulchibamba.margin.domain.drop.DropEvidenceFinder
import com.paulchibamba.margin.domain.drop.TodaysLearning
import com.paulchibamba.margin.domain.feed.ReviewLogEntry
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.ProgressRepository
import com.paulchibamba.margin.domain.time.today
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class DescribeDropCompletion @Inject constructor(
    private val observeStreak: ObserveStreak,
    private val progress: ProgressRepository,
    private val stateSource: FeedStateSource,
    private val buildFacts: BuildProgressFacts,
    private val clock: Clock,
) {
    suspend operator fun invoke(): DropCompletion {
        val learning = TodaysLearning(stateSource.current(), reviewsToday(), buildFacts().introducedToday.size, postsToday())
        return DropCompletion(observeStreak().first().currentStreak, DropEvidenceFinder.strongest(learning))
    }

    private suspend fun reviewsToday(): List<ReviewLogEntry> =
        progress.observeReviews().first().filter { review -> review.at.atZone(clock.zone()).toLocalDate() == clock.today() }

    private suspend fun postsToday(): Int =
        progress.observeActivity().first().firstOrNull { activity -> activity.date == clock.today() }?.postsSeen ?: 0
}
