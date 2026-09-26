package com.paulchibamba.margin.domain.progression

import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.NotePosition

data class ReadingProgress(
    val readNotes: Set<NoteId>,
    val knownChapterEnds: Set<NoteId>,
) {
    fun frontierOf(book: BookSlug): NotePosition? =
        (readNotes.asSequence() + knownChapterEnds.asSequence())
            .filter { note -> note.bookSlug == book }
            .mapNotNull(NoteId::position)
            .maxOrNull()

    companion object {
        val NothingRead = ReadingProgress(readNotes = emptySet(), knownChapterEnds = emptySet())
    }
}
