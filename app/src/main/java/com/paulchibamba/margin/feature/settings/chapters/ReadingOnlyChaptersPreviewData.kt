package com.paulchibamba.margin.feature.settings.chapters

import com.paulchibamba.margin.domain.model.Book
import com.paulchibamba.margin.domain.model.BookSettings
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Chapter
import com.paulchibamba.margin.domain.model.Priority
import com.paulchibamba.margin.domain.usecase.BookLearningSettings

object ReadingOnlyChaptersPreviewData {
    val state = ReadingOnlyChaptersUiState(
        books = listOf(
            book("Alice & Bob Learn AppSec", listOf("Introduction", "Security fundamentals", "Threat modeling")),
            book("The Tangled Web", listOf("Security in the world of web applications", "It starts with a URL")),
        ),
        isLoading = false,
    )

    private fun book(title: String, chapterTitles: List<String>): BookLearningSettings {
        val slug = BookSlug(title.lowercase().replace(' ', '-'))
        return BookLearningSettings(
            book = Book(slug, title),
            settings = BookSettings(slug, isActive = true, priority = Priority.NORMAL),
            chapters = chapterTitles.mapIndexed { index, chapterTitle ->
                Chapter(slug, index + 1, chapterTitle, isReadingOnly = index == 0)
            },
        )
    }
}
