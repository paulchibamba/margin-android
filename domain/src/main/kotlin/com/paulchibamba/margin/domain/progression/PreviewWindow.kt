package com.paulchibamba.margin.domain.progression

import com.paulchibamba.margin.domain.model.NotePosition

class PreviewWindow(private val notesAhead: Int = DEFAULT_NOTES_AHEAD) {

    fun contains(position: NotePosition, frontier: NotePosition?): Boolean {
        if (frontier == null) return isNearChapterStart(position)
        if (position <= frontier) return false
        return isJustAheadInSameChapter(position, frontier) || isNearStartOfNextChapter(position, frontier)
    }

    private fun isJustAheadInSameChapter(position: NotePosition, frontier: NotePosition): Boolean =
        position.chapter == frontier.chapter && position.order <= frontier.order + notesAhead

    private fun isNearStartOfNextChapter(position: NotePosition, frontier: NotePosition): Boolean =
        position.chapter == frontier.chapter + 1 && isNearChapterStart(position)

    private fun isNearChapterStart(position: NotePosition): Boolean = position.order < notesAhead

    companion object {
        const val DEFAULT_NOTES_AHEAD = 4
    }
}
