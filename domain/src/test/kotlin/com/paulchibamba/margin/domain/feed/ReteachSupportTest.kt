package com.paulchibamba.margin.domain.feed

import com.paulchibamba.margin.domain.feed.source.AngleProvider
import com.paulchibamba.margin.domain.feed.source.ReviewProvider
import com.paulchibamba.margin.domain.feed.source.RewardProvider
import com.paulchibamba.margin.domain.progress.RewardKind
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReteachSupportTest {
    private val due = introducedProgress(due = now.minusSeconds(60))
    private val gotItAndDue = due.copy(confidence = Confidence.GOT)
    private val reExplain = generatedPostOf(RewardKind.ReExplain, cia)
    private val sourceNote = sourceNoteOf(cia, "<p>The book's own words about the CIA triad.</p>")
    private val state = freshState.withIntroduced(cia, progress = gotItAndDue).copy(step = 9, rewardAtStep = 9)
        .withSeen(*postsOf(cia).toTypedArray())

    private fun anglesOf(library: LearningLibrary, state: FeedState = this.state) =
        AngleProvider().candidates(library, state, now).map { it.post.id }

    private fun reviewsOf(library: LearningLibrary, state: FeedState = this.state) =
        ReviewProvider().candidates(library, state, now).map { it.post.conceptId }.distinct()

    @Test
    fun `a re-explain comes through the angle source, even for a concept marked Got it`() {
        val library = libraryWith(generatedPosts = listOf(reExplain))

        assertEquals(listOf(reExplain.id), anglesOf(library))
        assertTrue(RewardProvider().candidates(library, state, now).none { it.post.id == reExplain.id })
    }

    @Test
    fun `reviews of the concept wait until its re-explain has been shown`() {
        val library = libraryWith(generatedPosts = listOf(reExplain))

        assertTrue(reviewsOf(library).isEmpty())
        assertEquals(listOf(cia.id), reviewsOf(library, state.withSeen(reExplain.toPost(cia.bookSlug)!!)))
    }

    @Test
    fun `without a re-explain, a struggling concept gets its unseen From the book post first`() {
        val library = libraryWith(sourceNotes = listOf(sourceNote), struggling = setOf(cia.id))
        val bookWords = library.sourcePostFor(cia)!!

        assertEquals(listOf(bookWords.id), anglesOf(library))
        assertTrue(reviewsOf(library).isEmpty())
        assertEquals(listOf(cia.id), reviewsOf(library, state.withSeen(bookWords)))
    }

    @Test
    fun `a concept that is not struggling gets no support and reviews as usual`() {
        val library = libraryWith(sourceNotes = listOf(sourceNote))

        assertNull(ReteachSupport.pendingFor(cia, library, state))
        assertEquals(listOf(cia.id), reviewsOf(library))
    }

    @Test
    fun `a shown re-explain counts as the re-teach after Lost`() {
        val lost = due.copy(confidence = Confidence.LOST, lostAtStep = 8)
        val lostState = freshState.withIntroduced(cia, progress = lost).copy(step = 9)
        val post = reExplain.toPost(cia.bookSlug)!!

        val after = FeedStepRecorder(RewardSchedule(6..10, kotlin.random.Random(1)))
            .record(lostState, Candidate(post, CandidateSource.ANGLE), now)

        assertTrue(lostState.progressOf(cia).isAwaitingReteach)
        assertFalse(after.progressOf(cia).isAwaitingReteach)
    }
}
