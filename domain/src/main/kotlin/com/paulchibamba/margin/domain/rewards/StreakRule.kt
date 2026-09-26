package com.paulchibamba.margin.domain.rewards

data class StreakRule(val postsPerDay: Int = 5, val notesPerDay: Int = 1) {

    fun countsTowardStreak(activity: DailyActivity?): Boolean =
        activity != null && (activity.postsSeen >= postsPerDay || activity.notesRead >= notesPerDay)
}
