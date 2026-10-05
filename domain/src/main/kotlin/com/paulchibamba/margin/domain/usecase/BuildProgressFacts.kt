package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.model.BookSettings
import com.paulchibamba.margin.domain.progress.ProgressFacts
import com.paulchibamba.margin.domain.progress.ProgressFactsCalculator
import com.paulchibamba.margin.domain.progress.ProgressSources
import com.paulchibamba.margin.domain.progress.RewardHistory
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.ContentRepository
import com.paulchibamba.margin.domain.repository.EventLog
import com.paulchibamba.margin.domain.repository.ProgressRepository
import com.paulchibamba.margin.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import java.time.Instant
import javax.inject.Inject

class BuildProgressFacts @Inject constructor(
    private val content: ContentRepository,
    private val progress: ProgressRepository,
    private val settings: SettingsRepository,
    private val eventLog: EventLog,
    private val clock: Clock,
) {
    suspend operator fun invoke(history: RewardHistory = RewardHistory.Empty): ProgressFacts {
        val now = clock.now()
        return ProgressFactsCalculator().factsOf(sourcesAt(now, history), now, clock.zone())
    }

    private suspend fun sourcesAt(now: Instant, history: RewardHistory) = ProgressSources(
        books = content.books(),
        chapters = content.chapters(),
        concepts = content.concepts(),
        posts = content.posts(),
        noteOutlines = content.noteOutlines(),
        activeBooks = settings.bookSettings().filter(BookSettings::isActive).map(BookSettings::bookSlug).toSet(),
        readingOnlyChapters = settings.readingOnlyChapters(),
        reading = progress.reading(),
        feedState = progress.loadFeedState() ?: FeedState(rewardAtStep = 0),
        reviews = progress.observeReviews().first(),
        actions = progress.observeActions().first(),
        firstSeen = progress.firstSeenTimes(),
        events = eventLog.between(ProgressFactsCalculator.windowStart(now), now),
        history = history,
    )
}
