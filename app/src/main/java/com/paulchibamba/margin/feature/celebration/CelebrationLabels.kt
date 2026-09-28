package com.paulchibamba.margin.feature.celebration

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.domain.rewards.BadgeKind
import com.paulchibamba.margin.domain.rewards.BookCompletionCalculator
import com.paulchibamba.margin.domain.rewards.EarnedBadge
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

const val STREAK_RULE = "5 posts or 1 note a day keeps it going."

private const val MATURE_DAYS = BookCompletionCalculator.DEFAULT_MATURE_DAYS

fun BadgeKind.title(): String = when (this) {
    BadgeKind.INTRODUCED -> "Introduced"
    BadgeKind.REMEMBERED -> "Remembered"
}

fun BadgeKind.icon(): Int = when (this) {
    BadgeKind.INTRODUCED -> MarginIcons.MenuBook
    BadgeKind.REMEMBERED -> MarginIcons.Psychology
}

fun badgeDetail(earned: EarnedBadge, emphasis: Color): AnnotatedString = buildAnnotatedString {
    val total = earned.completion.total
    when (earned.badge.kind) {
        BadgeKind.INTRODUCED -> {
            append("You've met all $total ideas. Next: ")
            withStyle(SpanStyle(color = emphasis, fontWeight = FontWeight.Bold)) {
                append(BadgeKind.REMEMBERED.title())
            }
            append(", when every idea holds for $MATURE_DAYS+ days.")
        }
        BadgeKind.REMEMBERED -> append("All $total ideas hold for $MATURE_DAYS+ days. Reviews keep them there.")
    }
}

fun rememberedProgressLabel(earned: EarnedBadge): String =
    "${earned.completion.remembered}/${earned.completion.total} remembered"

fun shareText(earned: EarnedBadge): String {
    val total = earned.completion.total
    val title = earned.book.title
    return when (earned.badge.kind) {
        BadgeKind.INTRODUCED -> "📘 Introduced: I've met all $total ideas in $title. Learning it on Margin."
        BadgeKind.REMEMBERED -> "🧠 Remembered: all $total ideas in $title hold for $MATURE_DAYS+ days. On Margin."
    }
}

fun weekDayLetter(date: LocalDate): String = date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault())
