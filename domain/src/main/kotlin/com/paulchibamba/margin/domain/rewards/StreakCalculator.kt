package com.paulchibamba.margin.domain.rewards

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

class StreakCalculator(private val zone: ZoneId, private val rule: StreakRule = StreakRule()) {

    fun today(now: Instant): LocalDate = now.atZone(zone).toLocalDate()

    fun currentStreak(activities: Collection<DailyActivity>, today: LocalDate): Int {
        val byDate = activities.associateBy(DailyActivity::date)
        val lastDay = if (rule.countsTowardStreak(byDate[today])) today else today.minusDays(1)
        return generateSequence(lastDay) { day -> day.minusDays(1) }
            .takeWhile { day -> rule.countsTowardStreak(byDate[day]) }
            .count()
    }

    fun isKeptOn(day: LocalDate, activities: Collection<DailyActivity>): Boolean =
        rule.countsTowardStreak(activities.firstOrNull { it.date == day })

    fun weekStrip(activities: Collection<DailyActivity>, today: LocalDate): List<StreakDay> {
        val byDate = activities.associateBy(DailyActivity::date)
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        return (0L until DAYS_PER_WEEK).map { offset ->
            val day = monday.plusDays(offset)
            StreakDay(day, statusOf(day, byDate[day], today))
        }
    }

    fun isExtendedBy(before: DailyActivity?, after: DailyActivity): Boolean =
        !rule.countsTowardStreak(before) && rule.countsTowardStreak(after)

    private fun statusOf(day: LocalDate, activity: DailyActivity?, today: LocalDate): StreakDayStatus = when {
        rule.countsTowardStreak(activity) -> StreakDayStatus.DONE
        day == today -> StreakDayStatus.TODAY
        day < today -> StreakDayStatus.MISSED
        else -> StreakDayStatus.PENDING
    }

    private companion object {
        const val DAYS_PER_WEEK = 7L
    }
}
