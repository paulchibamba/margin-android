package com.paulchibamba.margin.domain.progression

import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Concept

data class ReadingOnlyChapters(private val chaptersByBook: Map<BookSlug, Set<Int>>) {

    fun contains(book: BookSlug, chapter: Int): Boolean = chapter in chaptersOf(book)

    operator fun contains(concept: Concept): Boolean = contains(concept.bookSlug, concept.chapter)

    fun chaptersOf(book: BookSlug): Set<Int> = chaptersByBook[book].orEmpty()

    companion object {
        val None = ReadingOnlyChapters(emptyMap())
    }
}
