package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.Note
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.NoteOutline
import com.paulchibamba.margin.domain.repository.ContentRepository
import com.paulchibamba.margin.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveNote @Inject constructor(
    private val content: ContentRepository,
    private val progress: ProgressRepository,
) {
    operator fun invoke(id: NoteId): Flow<NoteReading> = progress.observeReading()
        .map { reading -> id in reading.readNotes }
        .distinctUntilChanged()
        .map { isRead -> readingOf(id, isRead) }
        .filterNotNull()

    private suspend fun readingOf(id: NoteId, isRead: Boolean): NoteReading? {
        val note = content.note(id) ?: return null
        val bookNotes = content.noteOutlines().filter { it.bookSlug == note.bookSlug }.sortedBy(NoteOutline::position)
        val index = bookNotes.indexOfFirst { it.id == id }
        return NoteReading(
            note = note,
            chapterTitle = chapterTitleOf(note),
            place = PlaceInChapter.of(id, bookNotes),
            isRead = isRead,
            previous = bookNotes.getOrNull(index - 1)?.id,
            next = bookNotes.getOrNull(index + 1)?.id,
        )
    }

    private suspend fun chapterTitleOf(note: Note): String = content.chapters()
        .firstOrNull { it.bookSlug == note.bookSlug && it.number == note.position.chapter }
        ?.title.orEmpty()
}
