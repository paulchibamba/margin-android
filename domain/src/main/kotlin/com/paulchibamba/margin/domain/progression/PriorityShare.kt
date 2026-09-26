package com.paulchibamba.margin.domain.progression

import com.paulchibamba.margin.domain.model.BookSettings
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Priority

class PriorityShare(private val shares: Map<Priority, Double> = DEFAULT_SHARES) {

    fun targetShare(book: BookSlug, settings: Collection<BookSettings>): Double {
        val activeBooks = settings.filter(BookSettings::isActive)
        val bookPriority = activeBooks.firstOrNull { it.bookSlug == book }?.priority ?: return 0.0
        return shareOf(bookPriority) / activeBooks.sumOf { shareOf(it.priority) }
    }

    fun actualShare(book: BookSlug, introducedCounts: Map<BookSlug, Int>, settings: Collection<BookSettings>): Double {
        val activeBooks = settings.filter(BookSettings::isActive).map(BookSettings::bookSlug).toSet()
        val introducedInActiveBooks = introducedCounts.filterKeys(activeBooks::contains).values.sum()
        if (introducedInActiveBooks == 0) return 0.0
        return introducedCounts[book].orZero() / introducedInActiveBooks.toDouble()
    }

    private fun shareOf(priority: Priority): Double = shares[priority] ?: FALLBACK_SHARE

    private fun Int?.orZero(): Int = this ?: 0

    companion object {
        val DEFAULT_SHARES = mapOf(Priority.MAIN to 0.6, Priority.NORMAL to 0.2, Priority.LOW to 0.1)
        private const val FALLBACK_SHARE = 0.2
    }
}
