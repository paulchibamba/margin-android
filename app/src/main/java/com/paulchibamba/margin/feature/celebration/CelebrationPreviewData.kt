package com.paulchibamba.margin.feature.celebration

import com.paulchibamba.margin.domain.model.Book
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.rewards.Badge
import com.paulchibamba.margin.domain.rewards.BadgeKind
import com.paulchibamba.margin.domain.rewards.BookCompletion
import com.paulchibamba.margin.domain.rewards.EarnedBadge
import com.paulchibamba.margin.domain.rewards.StreakDay
import com.paulchibamba.margin.domain.rewards.StreakDayStatus
import com.paulchibamba.margin.domain.usecase.StreakSummary
import java.time.LocalDate

object CelebrationPreviewData {
    private val monday = LocalDate.parse("2026-09-28")
    private val friday = monday.plusDays(4)
    private val appSec = Book(BookSlug("alice-bob-appsec"), "Alice & Bob Learn AppSec")

    val streak = StreakSummary(
        currentStreak = 8,
        week = (0L..6L).map { offset ->
            val day = monday.plusDays(offset)
            StreakDay(day, if (day <= friday) StreakDayStatus.DONE else StreakDayStatus.PENDING)
        },
        today = friday,
    )

    val introduced = EarnedBadge(
        Badge(appSec.slug, BadgeKind.INTRODUCED),
        appSec,
        BookCompletion(appSec.slug, introduced = 48, remembered = 12, total = 48),
    )

    val remembered = EarnedBadge(
        Badge(appSec.slug, BadgeKind.REMEMBERED),
        appSec,
        BookCompletion(appSec.slug, introduced = 48, remembered = 48, total = 48),
    )
}
