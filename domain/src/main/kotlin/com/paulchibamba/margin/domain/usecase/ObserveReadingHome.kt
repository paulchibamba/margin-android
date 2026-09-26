package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.NoteOutline
import com.paulchibamba.margin.domain.progression.ReadingState
import com.paulchibamba.margin.domain.repository.ContentRepository
import com.paulchibamba.margin.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveReadingHome @Inject constructor(
    private val content: ContentRepository,
    private val progress: ProgressRepository,
) {
    operator fun invoke(): Flow<ReadingHome> = progress.observeReading().map { reading -> homeFor(reading) }

    private suspend fun homeFor(reading: ReadingState): ReadingHome {
        val notesByBook = content.noteOutlines().sortedBy(NoteOutline::position).groupBy(NoteOutline::bookSlug)
        return ReadingHome(
            continueNote = reading.lastNote?.let { last -> noteAfter(last, notesByBook) },
            books = content.books().map { book ->
                BookReading(book, NoteTally.of(notesByBook[book.slug].orEmpty(), reading.readNotes))
            },
        )
    }

    private fun noteAfter(note: NoteId, notesByBook: Map<BookSlug, List<NoteOutline>>): NoteOutline? {
        val bookNotes = notesByBook[note.bookSlug].orEmpty()
        return bookNotes.getOrNull(bookNotes.indexOfFirst { it.id == note } + 1)
    }
}
