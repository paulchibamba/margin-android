package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.Book
import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.rollup.TimeOfDay

data class AttentionReport(
    val glanceRate: Double?,
    val deepRate: Double?,
    val ratioByFormat: List<Pair<Format, Double>>,
    val hotspots: List<Pair<String, Int>>,
    val paceByBook: List<Pair<Book, Int>>,
    val paceByTimeOfDay: List<Pair<TimeOfDay, Int>>,
) {
    companion object {
        const val DAYS = 7L
    }
}
