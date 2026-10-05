package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.Concept

internal object ComebackFinder {
    private const val PASSES_NEEDED = 2

    fun of(context: ProgressContext): List<Comeback> =
        context.countedConcepts.mapNotNull { concept -> comebackOf(concept, outcomesOf(concept, context), context) }

    private fun comebackOf(concept: Concept, outcomes: List<RecallOutcome>, context: ProgressContext): Comeback? {
        val lastFail = outcomes.indexOfLast { outcome -> !outcome.isPass }
        val passCount = outcomes.size - lastFail - 1
        if (lastFail < 0 || passCount < PASSES_NEEDED) return null
        val failRun = outcomes.take(lastFail + 1).takeLastWhile { outcome -> !outcome.isPass }
        return Comeback(concept, context.dateOf(failRun.first().at), failRun.size, passCount)
    }

    private fun outcomesOf(concept: Concept, context: ProgressContext): List<RecallOutcome> {
        val reviews = context.sources.reviews
            .filter { review -> review.conceptId == concept.id }
            .map { review -> RecallOutcome(review.at, isPass = review.rating != Rating.AGAIN) }
        val losts = context.sources.actions
            .filter { action -> action.conceptId == concept.id && action.action == PostAction.LOST }
            .map { action -> RecallOutcome(action.at, isPass = false) }
        return (reviews + losts).sortedBy(RecallOutcome::at)
    }
}
