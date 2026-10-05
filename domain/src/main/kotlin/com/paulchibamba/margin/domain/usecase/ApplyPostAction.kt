package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.actions.ActionOutcome
import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.progress.GeneratedPost
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.GeneratedPostRepository
import com.paulchibamba.margin.domain.repository.ProgressRepository
import com.paulchibamba.margin.domain.repository.SettingsRepository
import javax.inject.Inject

class ApplyPostAction @Inject constructor(
    private val stateSource: FeedStateSource,
    private val progress: ProgressRepository,
    private val settings: SettingsRepository,
    private val engines: LearningEngines,
    private val clock: Clock,
    private val lock: FeedStateLock,
    private val generatedPosts: GeneratedPostRepository,
) {
    suspend operator fun invoke(post: Post, action: PostAction): ActionOutcome = lock.withLock {
        val now = clock.now()
        val outcome = engines.actionHandler(settings.desiredRetention()).apply(stateSource.current(), post, action, now)
        progress.saveFeedState(outcome.state, now)
        progress.appendAction(outcome.logEntry)
        outcome.reviewLogEntry?.let { entry -> progress.appendReview(entry) }
        if (action == PostAction.LESS && GeneratedPost.isGenerated(post.id)) generatedPosts.markLess(post.id)
        outcome
    }
}
