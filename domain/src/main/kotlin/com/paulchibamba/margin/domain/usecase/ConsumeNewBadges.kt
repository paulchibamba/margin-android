package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.repository.ContentRepository
import com.paulchibamba.margin.domain.repository.ProgressRepository
import com.paulchibamba.margin.domain.repository.SettingsRepository
import com.paulchibamba.margin.domain.rewards.Badge
import com.paulchibamba.margin.domain.rewards.BadgeDetector
import com.paulchibamba.margin.domain.rewards.BookCompletionCalculator
import com.paulchibamba.margin.domain.rewards.EarnedBadge
import javax.inject.Inject

class ConsumeNewBadges @Inject constructor(
    private val content: ContentRepository,
    private val progress: ProgressRepository,
    private val settings: SettingsRepository,
    private val lock: FeedStateLock,
) {
    private val detector = BadgeDetector()

    suspend operator fun invoke(): List<EarnedBadge> = lock.withLock {
        val state = progress.loadFeedState() ?: return@withLock emptyList()
        val earned = earnedBadges(state, progress.shownBadges())
        if (earned.isNotEmpty()) progress.markBadgesShown(earned.map(EarnedBadge::badge))
        earned
    }

    private suspend fun earnedBadges(state: FeedState, shown: Set<Badge>): List<EarnedBadge> {
        val calculator = BookCompletionCalculator(settings.readingOnlyChapters())
        val concepts = content.concepts()
        return content.books().flatMap { book ->
            val completion = calculator.completionOf(book.slug, concepts, state.conceptProgress)
            detector.newlyEarned(completion, shown).map { badge -> EarnedBadge(badge, book, completion) }
        }
    }
}
