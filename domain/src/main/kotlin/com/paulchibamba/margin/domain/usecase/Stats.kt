package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.model.NotePosition

data class Stats(
    val affinity: Map<Format, Double>,
    val postsSeen: Int,
    val actionCounts: Map<PostAction, Int>,
    val lostConcepts: List<Concept>,
    val reviewCounts: Map<Rating, Int>,
    val frontiers: Map<BookSlug, NotePosition?>,
)
