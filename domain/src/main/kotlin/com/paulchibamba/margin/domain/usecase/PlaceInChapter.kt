package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.NoteOutline

data class PlaceInChapter(val order: Int, val noteCount: Int) {

    val fraction: Float
        get() = if (noteCount == 0) 0f else order.toFloat() / noteCount

    companion object {
        fun of(note: NoteId, bookNotes: List<NoteOutline>): PlaceInChapter {
            val outline = bookNotes.first { it.id == note }
            val chapterNotes = bookNotes.filter { it.position.chapter == outline.position.chapter }
            return PlaceInChapter(order = chapterNotes.indexOf(outline) + 1, noteCount = chapterNotes.size)
        }
    }
}
