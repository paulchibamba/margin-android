package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.drop.DailyDrop
import com.paulchibamba.margin.domain.feed.Candidate
import com.paulchibamba.margin.domain.feed.FeedItem
import com.paulchibamba.margin.domain.progress.GeneratedPost
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.DailyDropRepository
import com.paulchibamba.margin.domain.repository.GeneratedPostRepository
import com.paulchibamba.margin.domain.repository.ProgressRepository
import com.paulchibamba.margin.domain.repository.SettingsRepository
import com.paulchibamba.margin.domain.time.today
import javax.inject.Inject

class EnterDropItem @Inject constructor(
    private val drops: DailyDropRepository,
    private val stateSource: FeedStateSource,
    private val progress: ProgressRepository,
    private val generatedPosts: GeneratedPostRepository,
    private val settings: SettingsRepository,
    private val engines: LearningEngines,
    private val clock: Clock,
    private val lock: FeedStateLock,
) {
    suspend operator fun invoke(index: Int, candidate: Candidate): FeedItem? = lock.withLock {
        val drop = drops.forDate(clock.today())?.takeIf { index in it.items.indices } ?: return@withLock null
        val step = drop.items[index].enteredAtStep
        if (step != null) return@withLock null
        record(drop, index, candidate)
    }

    private suspend fun record(drop: DailyDrop, index: Int, candidate: Candidate): FeedItem {
        val now = clock.now()
        val shown = engines.feedEngine(settings.desiredRetention()).show(stateSource.current(), candidate, now)
        progress.saveFeedState(shown.state, now)
        if (GeneratedPost.isGenerated(candidate.post.id)) generatedPosts.markShown(candidate.post.id, now)
        drops.save(drop.entered(index, shown.state.step))
        return shown.item
    }
}
