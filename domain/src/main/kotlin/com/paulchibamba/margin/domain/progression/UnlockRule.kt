package com.paulchibamba.margin.domain.progression

import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.NotePosition

class UnlockRule(private val readingOnlyChapters: ReadingOnlyChapters) {

    fun isUnlocked(concept: Concept, frontier: NotePosition?): Boolean =
        frontier != null && concept.position <= frontier && concept !in readingOnlyChapters
}
