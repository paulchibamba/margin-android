package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.rewards.StreakDay

data class StreakSummary(val currentStreak: Int, val week: List<StreakDay>)
