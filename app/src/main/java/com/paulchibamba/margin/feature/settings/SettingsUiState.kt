package com.paulchibamba.margin.feature.settings

import com.paulchibamba.margin.domain.memory.DesiredRetention
import com.paulchibamba.margin.domain.model.DarkMode
import com.paulchibamba.margin.domain.progression.ActiveBookLimit
import com.paulchibamba.margin.domain.usecase.LearningSettings

data class SettingsUiState(
    val books: List<BookSettingState> = emptyList(),
    val activeCount: Int = 0,
    val maxActive: Int = ActiveBookLimit.ALLOWED.last,
    val retention: Double = DesiredRetention.DEFAULT,
    val isReviewReminderOn: Boolean = false,
    val darkMode: DarkMode = DarkMode.DEFAULT,
    val isLoading: Boolean = true,
) {
    companion object {
        fun of(settings: LearningSettings, retentionDraft: Double?) = SettingsUiState(
            books = settings.books.map { book ->
                BookSettingState(book.settings, book.book.title, canToggle = settings.canToggle(book))
            },
            activeCount = settings.activeCount,
            retention = retentionDraft ?: settings.desiredRetention,
            isReviewReminderOn = settings.isReviewReminderOn,
            darkMode = settings.darkMode,
            isLoading = false,
        )
    }
}
