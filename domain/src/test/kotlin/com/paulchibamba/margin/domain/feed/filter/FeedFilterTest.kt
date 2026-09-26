package com.paulchibamba.margin.domain.feed.filter

import com.paulchibamba.margin.domain.feed.*
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FeedFilterTest {

    @Test
    fun `a post shown within the last thirty posts is held back`() {
        val filter = NotRecentlyShown(window = 30)
        val shownAtStepOne = freshState.afterShowing(tipOf(cia))

        assertFalse(filter.keeps(Candidate(tipOf(cia), CandidateSource.RESURFACE), shownAtStepOne.copy(step = 30)))
        assertTrue(filter.keeps(Candidate(tipOf(cia), CandidateSource.RESURFACE), shownAtStepOne.copy(step = 31)))
        assertTrue(filter.keeps(Candidate(mythOf(cia), CandidateSource.ANGLE), shownAtStepOne))
    }

    @Test
    fun `a concept shown in the last three posts is held back`() {
        val filter = NoConceptRepeat(spacing = 3)
        val state = freshState.afterShowing(tipOf(cia), tipOf(leastPrivilege), tipOf(sameOrigin))

        val explanation = Candidate(mythOf(cia), CandidateSource.ANGLE)

        assertFalse(filter.keeps(explanation, state))
        assertTrue(filter.keeps(explanation, state.afterShowing(tipOf(defenceInDepth))))
    }

    @Test
    fun `a lost concept may be re-taught sooner, but not straight after itself`() {
        val filter = NoConceptRepeat(spacing = 3)
        val lost = introducedProgress().copy(confidence = Confidence.LOST, lostAtStep = 1)
        val reteach = Candidate(mythOf(cia), CandidateSource.ANGLE)
        val afterAGap = freshState.afterShowing(tipOf(cia), tipOf(sameOrigin)).withIntroduced(cia, progress = lost)
        val straightAfter = freshState.afterShowing(tipOf(cia)).withIntroduced(cia, progress = lost)

        assertTrue(filter.keeps(reteach, afterAGap))
        assertFalse(filter.keeps(reteach, straightAfter))
        assertFalse(filter.keeps(Candidate(mcqOf(cia), CandidateSource.REVIEW), afterAGap))
    }

    @Test
    fun `the same format never appears twice in a row`() {
        val state = freshState.afterShowing(tipOf(cia))

        assertFalse(NoFormatRepeat().keeps(Candidate(tipOf(sameOrigin), CandidateSource.NEW), state))
        assertTrue(NoFormatRepeat().keeps(Candidate(mythOf(sameOrigin), CandidateSource.NEW), state))
    }

    @Test
    fun `after two tests in a row the next post is not a test`() {
        val filter = TestStreakCap(maxTestsInRow = 2)
        val twoTests = freshState.afterShowing(mcqOf(cia), trueFalseOf(sameOrigin))
        val testThenTeach = freshState.afterShowing(mcqOf(cia), tipOf(sameOrigin))

        assertFalse(filter.keeps(Candidate(mcqOf(leastPrivilege), CandidateSource.REVIEW), twoTests))
        assertTrue(filter.keeps(Candidate(tipOf(leastPrivilege), CandidateSource.NEW), twoTests))
        assertTrue(filter.keeps(Candidate(mcqOf(leastPrivilege), CandidateSource.REVIEW), testThenTeach))
        val oneTest = freshState.afterShowing(mcqOf(cia))
        assertTrue(filter.keeps(Candidate(mcqOf(leastPrivilege), CandidateSource.REVIEW), oneTest))
    }

    @Test
    fun `previews are spaced at least six posts apart`() {
        val filter = PreviewSpacing(previewEvery = 6)
        val preview = Candidate(tipOf(cia), CandidateSource.PREVIEW)
        val lastPreviewAtThree = freshState.copy(lastPreviewAtStep = 3)

        assertTrue(filter.keeps(preview, freshState))
        assertFalse(filter.keeps(preview, lastPreviewAtThree.copy(step = 8)))
        assertTrue(filter.keeps(preview, lastPreviewAtThree.copy(step = 9)))
        assertTrue(filter.keeps(Candidate(tipOf(cia), CandidateSource.NEW), lastPreviewAtThree.copy(step = 4)))
    }
}
