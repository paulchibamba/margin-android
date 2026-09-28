package com.paulchibamba.margin.feature.read.note

import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.usecase.NoteReading
import com.paulchibamba.margin.domain.usecase.PlaceInChapter
import com.paulchibamba.margin.feature.feed.chapterLabel
import kotlin.time.Duration

data class NoteUiState(
    val note: NoteId? = null,
    val html: String = "",
    val place: PlaceInChapter = PlaceInChapter(order = 0, noteCount = 0),
    val readingTime: Duration = Duration.ZERO,
    val isRead: Boolean = false,
    val fromPost: PostId? = null,
    val previous: NoteId? = null,
    val next: NoteId? = null,
) {
    val isLoading: Boolean
        get() = note == null

    val positionLabel: String
        get() = "${place.order}/${place.noteCount}"

    companion object {
        fun of(reading: NoteReading, isReadRuleMet: Boolean, fromPost: PostId?) = NoteUiState(
            note = reading.note.id,
            html = NoteHtml.documentOf(reading.note, noteChapterLabel(reading)),
            place = reading.place,
            readingTime = reading.note.readingTime,
            isRead = reading.isRead || isReadRuleMet,
            fromPost = fromPost,
            previous = reading.previous,
            next = reading.next,
        )

        private fun noteChapterLabel(reading: NoteReading): String {
            val chapter = chapterLabel(reading.note.position.chapter, reading.chapterTitle)
            val note = reading.note
            return if (note.partCount > 1) "$chapter · part ${note.part} of ${note.partCount}" else chapter
        }
    }
}
