package com.paulchibamba.margin.domain.drop

import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.ReviewLogEntry
import com.paulchibamba.margin.domain.memory.CardState
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.rewards.BookCompletionCalculator

object DropEvidenceFinder {

    fun strongest(today: TodaysLearning): DropEvidence {
        val remembered = today.reviews.filter { review -> movedToRemembered(review, today.state) }
            .map(ReviewLogEntry::conceptId).distinct().size
        val passed = today.reviews.filter { review -> review.rating != Rating.AGAIN }
            .map(ReviewLogEntry::conceptId).distinct().size
        return when {
            remembered > 0 -> DropEvidence.Remembered(remembered)
            passed > 0 -> DropEvidence.Passed(passed)
            today.introduced > 0 -> DropEvidence.Introduced(today.introduced)
            else -> DropEvidence.PostsSeen(today.postsSeen)
        }
    }

    private fun movedToRemembered(review: ReviewLogEntry, state: FeedState): Boolean =
        review.stabilityBefore < MATURE_DAYS && isRemembered(review.conceptId, state)

    private fun isRemembered(concept: ConceptId, state: FeedState): Boolean {
        val card = state.progressOf(concept).card ?: return false
        return card.state == CardState.REVIEW && card.scheduledDays >= MATURE_DAYS
    }

    private const val MATURE_DAYS = BookCompletionCalculator.DEFAULT_MATURE_DAYS
}
