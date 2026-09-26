package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.NoteOutline
import kotlin.time.Duration

data class NoteTally(val notesRead: Int, val noteCount: Int, val timeLeft: Duration) {

    val isFinished: Boolean
        get() = noteCount > 0 && notesRead == noteCount

    companion object {
        fun of(outlines: List<NoteOutline>, readNotes: Set<NoteId>): NoteTally {
            val unread = outlines.filterNot { it.id in readNotes }
            return NoteTally(
                notesRead = outlines.size - unread.size,
                noteCount = outlines.size,
                timeLeft = unread.fold(Duration.ZERO) { total, outline -> total + outline.readingTime },
            )
        }
    }
}
