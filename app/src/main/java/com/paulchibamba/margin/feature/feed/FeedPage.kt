package com.paulchibamba.margin.feature.feed

import com.paulchibamba.margin.designsystem.Skin
import com.paulchibamba.margin.domain.actions.PostViewState
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.FeedItem
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.PostRole
import com.paulchibamba.margin.domain.signals.ExpectedReadTime
import com.paulchibamba.margin.domain.tracking.PostVisit
import com.paulchibamba.margin.domain.usecase.PostContext
import com.paulchibamba.margin.feature.feed.post.TestPostState
import java.time.Instant
import kotlin.time.Duration

data class FeedPage(
    val item: FeedItem,
    val context: PostContext,
    val skin: Skin,
    val viewState: PostViewState = PostViewState(),
    val isExitRecorded: Boolean = false,
    val isEngaged: Boolean = false,
    val enteredAt: Instant? = null,
    val intervals: Map<Rating, Duration> = emptyMap(),
    val answer: TestAnswer? = null,
) {
    val isLockedPreview: Boolean
        get() = item.source == CandidateSource.PREVIEW && context.readingAhead != null && item.post.rewardKind == null

    val isTest: Boolean
        get() = item.post.role == PostRole.TEST

    val testState: TestPostState
        get() = TestPostState(answer?.response, intervals)

    fun visitAt(index: Int): PostVisit {
        val post = item.post
        return PostVisit(
            pageIndex = index,
            postId = post.id,
            conceptId = post.conceptId,
            format = post.format,
            skinName = skin.name,
            source = item.source,
            step = item.step,
            words = post.content.wordCount(),
            expectedTime = ExpectedReadTime.of(post.content),
        )
    }
}
