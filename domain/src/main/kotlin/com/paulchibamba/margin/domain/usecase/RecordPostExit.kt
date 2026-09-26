package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.ProgressRepository
import com.paulchibamba.margin.domain.repository.SettingsRepository
import com.paulchibamba.margin.domain.rewards.StreakCalculator
import com.paulchibamba.margin.domain.signals.PostExit
import javax.inject.Inject

class RecordPostExit @Inject constructor(
    private val stateSource: FeedStateSource,
    private val progress: ProgressRepository,
    private val settings: SettingsRepository,
    private val engines: LearningEngines,
    private val clock: Clock,
    private val lock: FeedStateLock,
) {
    suspend operator fun invoke(post: Post, exit: PostExit): RecordedExit = lock.withLock {
        val now = clock.now()
        val outcome = engines.exitHandler(settings.desiredRetention()).apply(stateSource.current(), post, exit, now)
        progress.saveFeedState(outcome.state, now)
        progress.recordExit(post.id, exit, outcome.engagement)
        outcome.reviewLogEntry?.let { entry -> progress.appendReview(entry) }
        val streak = StreakCalculator(clock.zone())
        val activity = progress.addActivity(streak.today(now), postsSeen = 1, notesRead = 0)
        RecordedExit(outcome, streak.isExtendedBy(activity.before, activity.after))
    }
}
