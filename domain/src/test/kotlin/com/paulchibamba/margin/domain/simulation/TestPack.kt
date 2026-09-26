package com.paulchibamba.margin.domain.simulation

import com.paulchibamba.margin.domain.model.Book
import com.paulchibamba.margin.domain.model.BookSettings
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.Note
import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.model.Priority
import com.paulchibamba.margin.domain.progression.ReadingOnlyChapters

data class TestPack(
    val books: List<Book>,
    val concepts: List<Concept>,
    val posts: List<Post>,
    val notes: List<Note>,
    val bookSettings: List<BookSettings>,
    val readingOnlyChapters: ReadingOnlyChapters,
    val previewNotesAhead: Int,
) {
    val activeBooks: List<Book>
        get() = books.filter { book -> bookSettings.any { it.bookSlug == book.slug && it.isActive } }

    val inactiveBooks: List<Book>
        get() = books - activeBooks.toSet()

    val mainBook: Book
        get() = books.single { book -> bookSettings.any { it.bookSlug == book.slug && it.isMain } }

    private val BookSettings.isMain: Boolean
        get() = isActive && priority == Priority.MAIN
}
