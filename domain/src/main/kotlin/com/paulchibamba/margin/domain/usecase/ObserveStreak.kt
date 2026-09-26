package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.ProgressRepository
import com.paulchibamba.margin.domain.rewards.StreakCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveStreak @Inject constructor(private val progress: ProgressRepository, private val clock: Clock) {

    operator fun invoke(): Flow<StreakSummary> = progress.observeActivity().map { activities ->
        val calculator = StreakCalculator(clock.zone())
        val today = calculator.today(clock.now())
        StreakSummary(calculator.currentStreak(activities, today), calculator.weekStrip(activities, today))
    }
}
