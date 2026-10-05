package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.NoteOutline

internal object UpcomingNotesFinder {
    private const val NOTES_PER_BOOK = 4

    fun of(context: ProgressContext): List<UpcomingNote> =
        context.sources.books
            .map { book -> book.slug }
            .filter(context.sources.activeBooks::contains)
            .flatMap { book -> upcomingIn(book, context) }

    private fun upcomingIn(book: BookSlug, context: ProgressContext): List<UpcomingNote> {
        val frontier = context.frontierOf(book)
        return context.sources.noteOutlines
            .filter { note -> note.bookSlug == book && (frontier == null || note.position > frontier) }
            .filterNot { note -> note.id in context.sources.reading.readNotes }
            .sortedBy(NoteOutline::position)
            .take(NOTES_PER_BOOK)
            .mapIndexed { index, note -> UpcomingNote(note, notesAway = index + 1, context.conceptsOn(note)) }
    }
}
