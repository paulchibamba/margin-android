package com.paulchibamba.margin.domain.progression

import com.paulchibamba.margin.domain.model.BookSettings
import com.paulchibamba.margin.domain.model.BookSlug

class ReadingOrder(private val books: List<BookSlug>, private val settings: List<BookSettings>) {

    fun booksToReadNext(): List<BookSlug> = activeBooks().ifEmpty { books }

    fun activeFirst(): List<BookSlug> = activeBooks().let { active -> active + books.filterNot { it in active } }

    private fun activeBooks(): List<BookSlug> = settings
        .filter { it.isActive && it.bookSlug in books }
        .sortedWith(compareBy<BookSettings> { it.priority }.thenBy { books.indexOf(it.bookSlug) })
        .map(BookSettings::bookSlug)
}
