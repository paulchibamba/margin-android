package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.feed.FeedResult
import com.paulchibamba.margin.domain.progress.GeneratedPost
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.GeneratedPostRepository
import com.paulchibamba.margin.domain.repository.ProgressRepository
import com.paulchibamba.margin.domain.repository.SettingsRepository
import java.time.Instant
import javax.inject.Inject

class GetNextPost @Inject constructor(
    private val libraryLoader: LibraryLoader,
    private val stateSource: FeedStateSource,
    private val progress: ProgressRepository,
    private val settings: SettingsRepository,
    private val engines: LearningEngines,
    private val clock: Clock,
    private val lock: FeedStateLock,
    private val generatedPosts: GeneratedPostRepository,
) {
    suspend operator fun invoke(): FeedResult = lock.withLock {
        val now = clock.now()
        val engine = engines.feedEngine(settings.desiredRetention())
        val result = engine.next(libraryLoader.load(), stateSource.current(), now)
        if (result is FeedResult.Next) record(result, now)
        result
    }

    private suspend fun record(result: FeedResult.Next, now: Instant) {
        progress.saveFeedState(result.state, now)
        val post = result.item.post
        if (GeneratedPost.isGenerated(post.id)) generatedPosts.markShown(post.id, now)
    }
}
