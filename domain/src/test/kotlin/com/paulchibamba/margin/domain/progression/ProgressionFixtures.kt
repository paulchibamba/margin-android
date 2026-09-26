package com.paulchibamba.margin.domain.progression

import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.NoteId

val appSec = BookSlug("alice-bob-appsec")
val grokking = BookSlug("grokking-web-app-security")

fun noteIn(book: BookSlug, chapter: Int, order: Int) =
    NoteId("${book.value}/ch${chapter.toString().padStart(2, '0')}/n${order.toString().padStart(3, '0')}")

fun conceptAt(book: BookSlug, chapter: Int, order: Int) = Concept(
    id = ConceptId("${book.value}/ch$chapter/$order"),
    bookSlug = book,
    chapter = chapter,
    order = order,
    title = "Concept $chapter.$order",
    summary = "",
    section = "",
    sourceNoteId = noteIn(book, chapter, order),
)
