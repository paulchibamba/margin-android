package com.paulchibamba.margin.feature.feed

import com.paulchibamba.margin.data.startup.StartupInitializer
import com.paulchibamba.margin.designsystem.SystemDarkTheme
import com.paulchibamba.margin.domain.actions.ActionOutcome
import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.feed.FeedResult
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.signals.PostExit
import com.paulchibamba.margin.domain.usecase.ApplyPostAction
import com.paulchibamba.margin.domain.usecase.DescribePost
import com.paulchibamba.margin.domain.usecase.GetCaughtUp
import com.paulchibamba.margin.domain.usecase.GetNextPost
import com.paulchibamba.margin.domain.usecase.ObserveAppearance
import com.paulchibamba.margin.domain.usecase.ObserveBookCovers
import com.paulchibamba.margin.domain.usecase.ObserveStreak
import com.paulchibamba.margin.domain.usecase.PreviewIntervals
import com.paulchibamba.margin.domain.usecase.RecordPostExit
import com.paulchibamba.margin.domain.usecase.RecordedExit
import com.paulchibamba.margin.feature.bake.BakeScheduler
import com.paulchibamba.margin.feature.celebration.CelebrationTrigger
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class DomainFeedUseCases @Inject constructor(
    private val startup: StartupInitializer,
    private val getNextPost: GetNextPost,
    private val describePost: DescribePost,
    private val recordPostExit: RecordPostExit,
    private val applyPostAction: ApplyPostAction,
    private val getCaughtUp: GetCaughtUp,
    private val observeStreak: ObserveStreak,
    private val observeBookCovers: ObserveBookCovers,
    private val previewIntervals: PreviewIntervals,
    private val celebrations: CelebrationTrigger,
    private val observeAppearance: ObserveAppearance,
    private val systemDarkTheme: SystemDarkTheme,
    private val bakeScheduler: BakeScheduler,
) : FeedUseCases {

    override fun observeStreak() = observeStreak.invoke()

    override fun observeBookCovers() = observeBookCovers.invoke()

    override suspend fun nextPost(): FeedResult {
        startup.ensureImported()
        return getNextPost()
    }

    override suspend fun describe(post: Post) = describePost(post)

    override suspend fun recordExit(post: Post, exit: PostExit): RecordedExit {
        val recorded = recordPostExit(post, exit)
        if (recorded.outcome.reviewLogEntry?.rating == Rating.AGAIN) bakeScheduler.bakeReExplainsNow()
        celebrations.onStreakSignal(recorded.isStreakExtended)
        celebrations.checkBadges()
        return recorded
    }

    override suspend fun applyAction(post: Post, action: PostAction): ActionOutcome {
        val outcome = applyPostAction(post, action)
        if (action == PostAction.LOST) bakeScheduler.bakeReExplainsNow()
        celebrations.checkBadges()
        return outcome
    }

    override suspend fun caughtUp() = getCaughtUp()

    override suspend fun previewIntervals(post: Post) = previewIntervals.invoke(post)

    override suspend fun feedTone() = observeAppearance().first().feedTone(systemDarkTheme.isOn())
}
