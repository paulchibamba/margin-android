package com.paulchibamba.margin.feature.feed

import com.paulchibamba.margin.data.startup.StartupInitializer
import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.feed.FeedResult
import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.signals.PostExit
import com.paulchibamba.margin.domain.usecase.ApplyPostAction
import com.paulchibamba.margin.domain.usecase.DescribePost
import com.paulchibamba.margin.domain.usecase.GetCaughtUp
import com.paulchibamba.margin.domain.usecase.GetNextPost
import com.paulchibamba.margin.domain.usecase.ObserveStreak
import com.paulchibamba.margin.domain.usecase.RecordPostExit
import javax.inject.Inject

class DomainFeedUseCases @Inject constructor(
    private val startup: StartupInitializer,
    private val getNextPost: GetNextPost,
    private val describePost: DescribePost,
    private val recordPostExit: RecordPostExit,
    private val applyPostAction: ApplyPostAction,
    private val getCaughtUp: GetCaughtUp,
    private val observeStreak: ObserveStreak,
) : FeedUseCases {

    override fun observeStreak() = observeStreak.invoke()

    override suspend fun nextPost(): FeedResult {
        startup.ensureImported()
        return getNextPost()
    }

    override suspend fun describe(post: Post) = describePost(post)

    override suspend fun recordExit(post: Post, exit: PostExit) = recordPostExit(post, exit)

    override suspend fun applyAction(post: Post, action: PostAction) = applyPostAction(post, action)

    override suspend fun caughtUp() = getCaughtUp()
}
