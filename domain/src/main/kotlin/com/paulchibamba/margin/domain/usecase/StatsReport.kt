package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.Format
import java.time.LocalDate

data class StatsReport(
    val date: LocalDate,
    val postsSeen: Int,
    val currentStreak: Int,
    val affinity: Map<Format, Double>,
    val actionCounts: Map<PostAction, Int>,
    val lostConcepts: List<Concept>,
    val reviewCounts: Map<Rating, Int>,
    val frontiers: List<BookFrontier>,
) {
    val reviewCount: Int
        get() = reviewCounts.values.sum()

    val reviewAccuracy: Double?
        get() = if (reviewCount == 0) null else 1.0 - shareOf(Rating.AGAIN)

    val affinityByStrength: List<Pair<Format, Double>>
        get() = affinity.entries.sortedByDescending { it.value }.map { it.toPair() }

    fun countOf(action: PostAction): Int = actionCounts[action] ?: 0

    fun countOf(grade: Rating): Int = reviewCounts[grade] ?: 0

    fun shareOf(grade: Rating): Double = if (reviewCount == 0) 0.0 else countOf(grade).toDouble() / reviewCount

    companion object {
        val GRADES = listOf(Rating.AGAIN, Rating.HARD, Rating.GOOD)
    }
}
