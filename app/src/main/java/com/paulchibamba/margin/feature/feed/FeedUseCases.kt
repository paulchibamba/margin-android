package com.paulchibamba.margin.feature.feed

import com.paulchibamba.margin.domain.actions.ActionOutcome
import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.feed.FeedResult
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.signals.PostExit
import com.paulchibamba.margin.domain.usecase.CaughtUp
import com.paulchibamba.margin.domain.usecase.PostContext
import com.paulchibamba.margin.domain.usecase.RecordedExit
import com.paulchibamba.margin.domain.usecase.StreakSummary
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface FeedUseCases {
    fun observeStreak(): Flow<StreakSummary>
    suspend fun nextPost(): FeedResult
    suspend fun describe(post: Post): PostContext
    suspend fun recordExit(post: Post, exit: PostExit): RecordedExit
    suspend fun applyAction(post: Post, action: PostAction): ActionOutcome
    suspend fun caughtUp(): CaughtUp
    suspend fun previewIntervals(post: Post): Map<Rating, Duration>
}
