package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.Book
import com.paulchibamba.margin.domain.model.BookSettings
import com.paulchibamba.margin.domain.model.Priority
import com.paulchibamba.margin.domain.progression.ReadingOnlyChapters
import com.paulchibamba.margin.domain.repository.ContentRepository
import com.paulchibamba.margin.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ObserveLearningSettings @Inject constructor(
    private val content: ContentRepository,
    private val settings: SettingsRepository,
) {
    operator fun invoke(): Flow<LearningSettings> = combine(
        settings.observeBookSettings(),
        settings.observeReadingOnlyChapters(),
        settings.observeDesiredRetention(),
        settings.observeReviewReminder(),
        settings.observeDarkMode(),
    ) { bookSettings, readingOnly, retention, isReminderOn, darkMode ->
        LearningSettings(booksWith(bookSettings, readingOnly), retention, isReminderOn, darkMode)
    }

    private suspend fun booksWith(
        bookSettings: List<BookSettings>,
        readingOnly: ReadingOnlyChapters,
    ): List<BookLearningSettings> {
        val settingsByBook = bookSettings.associateBy(BookSettings::bookSlug)
        val chaptersByBook = content.chapters().sortedBy { it.number }.groupBy { it.bookSlug }
        return content.books().map { book ->
            BookLearningSettings(
                book = book,
                settings = settingsByBook[book.slug] ?: inactive(book),
                chapters = chaptersByBook[book.slug].orEmpty()
                    .map { it.copy(isReadingOnly = readingOnly.contains(book.slug, it.number)) },
            )
        }
    }

    private fun inactive(book: Book) = BookSettings(book.slug, isActive = false, priority = Priority.LOW)
}
