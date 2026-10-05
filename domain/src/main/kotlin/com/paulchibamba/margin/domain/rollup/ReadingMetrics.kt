package com.paulchibamba.margin.domain.rollup

import com.paulchibamba.margin.domain.model.BookSlug

data class ReadingMetrics(
    val notesRead: Int,
    val paceByBook: Map<BookSlug, ReadingPace>,
    val paceByTimeOfDay: Map<TimeOfDay, ReadingPace>,
)
