package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.repository.ContentRepository
import com.paulchibamba.margin.domain.repository.ProgressRepository
import com.paulchibamba.margin.domain.repository.SettingsRepository
import com.paulchibamba.margin.domain.rewards.Badge
import com.paulchibamba.margin.domain.rewards.BadgeDetector
import com.paulchibamba.margin.domain.rewards.BookCompletionCalculator
import javax.inject.Inject

class ConsumeNewBadges @Inject constructor(
    private val content: ContentRepository,
    private val progress: ProgressRepository,
    private val settings: SettingsRepository,
    private val lock: FeedStateLock,
) {
    private val detector = BadgeDetector()

    suspend operator fun invoke(): List<Badge> = lock.withLock {
        val state = progress.loadFeedState() ?: return@withLock emptyList()
        val calculator = BookCompletionCalculator(settings.readingOnlyChapters())
        val concepts = content.concepts()
        val shown = progress.shownBadges()
        val earned = content.books().flatMap { book ->
            detector.newlyEarned(calculator.completionOf(book.slug, concepts, state.conceptProgress), shown)
        }
        if (earned.isNotEmpty()) progress.markBadgesShown(earned)
        earned
    }
}
