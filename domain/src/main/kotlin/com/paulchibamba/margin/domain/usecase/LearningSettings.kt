package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.progression.ActiveBookLimit

data class LearningSettings(
    val books: List<BookLearningSettings>,
    val desiredRetention: Double,
    val isReviewReminderOn: Boolean = false,
) {

    val activeCount: Int get() = books.count { it.settings.isActive }

    fun canToggle(book: BookLearningSettings): Boolean {
        val countAfterToggle = if (book.settings.isActive) activeCount - 1 else activeCount + 1
        return ActiveBookLimit.allows(countAfterToggle)
    }
}
