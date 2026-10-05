package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.feed.ReviewLogEntry
import com.paulchibamba.margin.domain.memory.CardState
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.NoteId
import java.time.Duration
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RewardExpiryTest {
    private val created = daysAgo(2)
    private val upcomingNote = noteIdOf(fundamentals, 3)

    private fun postOf(kind: RewardKind, concepts: List<ConceptId> = listOf(leastPrivilege.id), note: NoteId? = null) =
        GeneratedPost.from(
            RewardSeed(kind, concepts, note, mapOf("k" to kind.key)),
            RewardDraft("Title", "Body"),
            GeneratedPost.TEMPLATE_WRITER,
            created,
        )

    private fun review(rating: Rating, at: Instant) =
        ReviewLogEntry(at, leastPrivilege.id, mcqPostOf(leastPrivilege).id, rating, CardState.REVIEW, 1.0, 1.0, null)

    private fun expiry(readNotes: Set<NoteId> = emptySet(), reviews: List<ReviewLogEntry> = emptyList()) =
        RewardExpiry(readNotes, reviews, allConcepts.map { it.id }.toSet())

    @Test
    fun `a re-explain expires on the next passing review, not on a failing one`() {
        val reExplain = postOf(RewardKind.ReExplain)

        assertFalse(expiry(reviews = listOf(review(Rating.AGAIN, daysAgo(1)))).isExpired(reExplain, NOW))
        assertFalse(expiry(reviews = listOf(review(Rating.GOOD, daysAgo(3)))).isExpired(reExplain, NOW))
        assertTrue(expiry(reviews = listOf(review(Rating.HARD, daysAgo(1)))).isExpired(reExplain, NOW))
    }

    @Test
    fun `a coming up post expires when its note is read, however long it waits`() {
        val comingUp = postOf(RewardKind.ComingUp, note = upcomingNote)
        val muchLater = NOW.plus(Duration.ofDays(60))

        assertNull(comingUp.expiresAt)
        assertFalse(expiry().isExpired(comingUp, muchLater))
        assertTrue(expiry(readNotes = setOf(upcomingNote)).isExpired(comingUp, NOW))
    }

    @Test
    fun `other kinds expire after 7 days unseen`() {
        val callback = postOf(RewardKind.Callback)

        assertEquals(created.plus(Duration.ofDays(7)), callback.expiresAt)
        assertFalse(expiry().isExpired(callback, created.plus(Duration.ofDays(7)).minusSeconds(1)))
        assertTrue(expiry().isExpired(callback, created.plus(Duration.ofDays(7))))
    }

    @Test
    fun `a post whose concept no longer exists is an orphan and not fresh`() {
        val orphan = postOf(RewardKind.Quote, concepts = listOf(ConceptId("gone")))

        assertTrue(expiry().isOrphan(orphan))
        assertFalse(expiry().isFresh(orphan, NOW))
    }

    @Test
    fun `a shown post or one marked Less is not fresh`() {
        val post = postOf(RewardKind.Quote)

        assertTrue(expiry().isFresh(post, NOW))
        assertFalse(expiry().isFresh(post.copy(shownAt = NOW), NOW))
        assertFalse(expiry().isFresh(post.copy(isLessPressed = true), NOW))
    }

    @Test
    fun `a generated post id is gen, kind and facts hash`() {
        val post = postOf(RewardKind.Quote)

        assertEquals("gen/quote/${post.factsHash}", post.id.value)
        assertTrue(GeneratedPost.isGenerated(post.id))
    }
}
