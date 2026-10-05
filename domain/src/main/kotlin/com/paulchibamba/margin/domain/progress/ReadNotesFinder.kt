package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.model.NoteOutline

internal object ReadNotesFinder {

    fun unquoted(context: ProgressContext): List<ReadNote> {
        val quoted = context.sources.history.quotedNotes()
        return context.sources.noteOutlines
            .filter { note -> note.id in context.sources.reading.readNotes && note.id !in quoted }
            .sortedWith(compareBy({ it.bookSlug.value }, NoteOutline::position))
            .map { note -> ReadNote(note, context.bookTitleOf(note.bookSlug), context.conceptsOn(note)) }
    }
}
