package com.paulchibamba.margin.domain.progression

import com.paulchibamba.margin.domain.model.ChapterRef
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.NoteOutline

data class ReadingState(
    val readNotes: Set<NoteId> = emptySet(),
    val knownChapters: Set<ChapterRef> = emptySet(),
    val lastNote: NoteId? = null,
) {
    fun isKnown(chapter: ChapterRef): Boolean = chapter in knownChapters

    fun progressWith(outlines: List<NoteOutline>): ReadingProgress =
        ReadingProgress(readNotes = readNotes, knownChapterEnds = lastNotesOfKnownChapters(outlines))

    private fun lastNotesOfKnownChapters(outlines: List<NoteOutline>): Set<NoteId> = outlines
        .filter { outline -> ChapterRef(outline.bookSlug, outline.position.chapter) in knownChapters }
        .groupBy { outline -> ChapterRef(outline.bookSlug, outline.position.chapter) }
        .values
        .map { chapterNotes -> chapterNotes.maxBy(NoteOutline::position).id }
        .toSet()
}
