package com.paulchibamba.margin.feature.feed

import com.paulchibamba.margin.domain.actions.ActionLogEntry
import com.paulchibamba.margin.domain.actions.ActionOutcome
import com.paulchibamba.margin.domain.actions.Nudge
import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.feed.ExitOutcome
import com.paulchibamba.margin.domain.feed.FeedResult
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.BookCover
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.FeedTone
import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.signals.PostExit
import com.paulchibamba.margin.domain.usecase.CaughtUp
import com.paulchibamba.margin.domain.usecase.RecordedExit
import com.paulchibamba.margin.domain.usecase.StreakSummary
import java.time.Instant
import java.time.LocalDate
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.flow.MutableStateFlow

class FakeFeedUseCases(posts: List<Post> = List(10) { index -> tipPost(index) }) : FeedUseCases {
    val upcoming = ArrayDeque(posts)
    val streak = MutableStateFlow(StreakSummary(currentStreak = 3, week = emptyList(), today = LocalDate.EPOCH))
    val covers = MutableStateFlow(emptyMap<BookSlug, BookCover>())
    val exits = mutableListOf<Pair<Post, PostExit>>()
    val actions = mutableListOf<Pair<Post, PostAction>>()
    var nextPostCalls = 0
    var feedTone = FeedTone.ANY
    var caughtUp = CaughtUp(nextNote = null, nextReviewIn = null)
    var intervals: Map<Rating, Duration> =
        mapOf(Rating.AGAIN to 1.minutes, Rating.HARD to 6.minutes, Rating.GOOD to 10.minutes, Rating.EASY to 8.days)

    override fun observeStreak() = streak

    override fun observeBookCovers() = covers

    override suspend fun nextPost(): FeedResult {
        nextPostCalls++
        val post = upcoming.removeFirstOrNull() ?: return FeedResult.CaughtUp
        return FeedResult.Next(itemOf(post), emptyState)
    }

    override suspend fun describe(post: Post) = contextOf(post)

    override suspend fun recordExit(post: Post, exit: PostExit): RecordedExit {
        exits += post to exit
        return RecordedExit(ExitOutcome(emptyState, engagement = 0.5, grade = null, reviewLogEntry = null), false)
    }

    override suspend fun applyAction(post: Post, action: PostAction): ActionOutcome {
        actions += post to action
        val logEntry = ActionLogEntry(Instant.EPOCH, step = 0, post.id, post.conceptId, action)
        return ActionOutcome(emptyState, nudgeFor(action), logEntry)
    }

    override suspend fun caughtUp() = caughtUp

    override suspend fun previewIntervals(post: Post) = intervals

    override suspend fun feedTone() = feedTone

    private fun nudgeFor(action: PostAction): Nudge? = when (action) {
        PostAction.GOT -> Nudge.TEST_COMING_SOON
        PostAction.LOST -> Nudge.ANOTHER_ANGLE_COMING
        else -> null
    }

    private companion object {
        val emptyState = FeedState(delightAtStep = 99)
    }
}
