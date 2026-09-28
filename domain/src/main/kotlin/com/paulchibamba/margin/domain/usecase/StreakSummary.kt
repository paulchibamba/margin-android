package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.rewards.StreakDay
import java.time.LocalDate

data class StreakSummary(val currentStreak: Int, val week: List<StreakDay>, val today: LocalDate)
