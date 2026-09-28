package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.ProgressRepository
import com.paulchibamba.margin.domain.repository.SettingsRepository
import com.paulchibamba.margin.domain.rewards.StreakCalculator
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetReviewReminder @Inject constructor(
    private val settings: SettingsRepository,
    private val progress: ProgressRepository,
    private val countDueReviews: CountDueReviews,
    private val clock: Clock,
) {
    suspend operator fun invoke(): ReviewReminder? {
        if (!settings.isReviewReminderOn()) return null
        val activities = progress.observeActivity().first()
        val calculator = StreakCalculator(clock.zone())
        val today = calculator.today(clock.now())
        if (calculator.isKeptOn(today, activities)) return null
        val dueCount = countDueReviews().takeIf { it > 0 } ?: return null
        return ReviewReminder(dueCount, streak = calculator.currentStreak(activities, today))
    }
}
