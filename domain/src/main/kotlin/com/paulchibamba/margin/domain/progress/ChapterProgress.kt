package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.model.Chapter
import com.paulchibamba.margin.domain.model.ChapterRef
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.NotePosition
import java.time.LocalDate

data class ChapterProgress(
    val chapter: Chapter,
    val bookTitle: String,
    val introduced: List<Concept>,
    val remaining: List<Concept>,
    val remembered: List<Concept>,
    val frontier: NotePosition?,
    val firstIntroducedOn: LocalDate?,
) {
    val ref: ChapterRef
        get() = ChapterRef(chapter.bookSlug, chapter.number)

    val total: Int
        get() = introduced.size + remaining.size

    val isFullyIntroduced: Boolean
        get() = total > 0 && remaining.isEmpty()

    val isFullyRemembered: Boolean
        get() = total > 0 && remembered.size == total

    fun contains(concept: Concept): Boolean = concept.bookSlug == chapter.bookSlug && concept.chapter == chapter.number
}
