package com.paulchibamba.margin.domain.tracking

import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.usecase.FixedClock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class PostAttentionTest {
    private val clock = FixedClock()
    private val recorded = mutableListOf<Event>()
    private val attention = PostAttention(clock, recorded::add).apply { onFeedShown(true) }

    private fun advance(by: Duration) {
        clock.instant = clock.instant + by.toJavaDuration()
    }

    private fun visit(index: Int, step: Int = 100 + index) = PostVisit(
        pageIndex = index,
        postId = PostId("post-$index"),
        conceptId = ConceptId("concept-$index"),
        format = Format.FACT,
        skinName = "Ink",
        source = CandidateSource.NEW,
        step = step,
        words = 30,
        expectedTime = 7.5.seconds,
    )

    private inline fun <reified T : Event> only(): List<T> = recorded.filterIsInstance<T>()

    @Test
    fun `settling on a post records its impression`() {
        attention.onSettled(visit(0))

        val impression = Event.PostImpression(
            PostId("post-0"), ConceptId("concept-0"), Format.FACT, "Ink", CandidateSource.NEW,
            step = 100,
            isRevisit = false,
        )
        assertEquals<List<Event>>(listOf(impression), recorded)
    }

    @Test
    fun `moving on records the exposure of the post that was left`() {
        attention.onSettled(visit(0))
        advance(60.seconds)
        attention.onSettled(visit(1))

        val exposure = only<Event.PostExposure>().single()
        assertEquals(PostId("post-0"), exposure.postId)
        assertEquals(20.seconds, exposure.activeTime)
        assertEquals(40.seconds, exposure.idleTime)
        assertEquals(false, exposure.exitedSession)
    }

    @Test
    fun `swiping back below the furthest page is a revisit`() {
        attention.onSettled(visit(0))
        attention.onSettled(visit(1))
        attention.onSettled(visit(0))

        val revisit = Event.PostRevisit(PostId("post-0"), fromStep = 101, toStep = 100)
        assertEquals(listOf(revisit), only<Event.PostRevisit>())
        assertEquals(listOf(false, false, true), only<Event.PostImpression>().map { it.isRevisit })
    }

    @Test
    fun `settling again on the same page records nothing new`() {
        attention.onSettled(visit(0))
        attention.onSettled(visit(0))

        assertEquals(1, recorded.size)
    }

    @Test
    fun `scrolling pauses the clock for the post being left`() {
        attention.onSettled(visit(0))
        advance(5.seconds)
        attention.onScrolling(true)
        advance(10.seconds)
        attention.onSettled(visit(1))

        assertEquals(5.seconds, only<Event.PostExposure>().single().activeTime)
    }

    @Test
    fun `an answer remembers whether the source was opened first`() {
        attention.onSettled(visit(0))
        attention.onAction(PostId("post-0"), PostAction.READ)
        attention.onAnswer(PostId("post-0"), isCorrect = false, 4.seconds, Rating.AGAIN)

        assertEquals(true, only<Event.PostAnswer>().single().openedSourceFirst)
        assertEquals(InteractionKind.READ_SOURCE, only<Event.PostInteraction>().single().kind)
        assertEquals(PostAction.READ, only<Event.PostActionTaken>().single().action)
    }

    @Test
    fun `an interaction carries the time since the impression`() {
        attention.onSettled(visit(0))
        advance(3.seconds)
        attention.onInteraction(PostId("post-0"), InteractionKind.REVEAL)

        assertEquals(3.seconds, only<Event.PostInteraction>().single().sinceImpression)
    }

    @Test
    fun `a session ending on a post marks its exposure as the exit, and a new session sees it again`() {
        attention.onSettled(visit(0))
        advance(10.seconds)

        val atEnd = attention.eventsAtEnd()
        val atStart = attention.eventsAtStart()

        val exit = atEnd.single() as Event.PostExposure
        assertEquals(true, exit.exitedSession)
        assertEquals(10.seconds, exit.activeTime)
        assertEquals(PostId("post-0"), (atStart.single() as Event.PostImpression).postId)
    }

    @Test
    fun `with no post on screen a session adds nothing`() {
        assertTrue(attention.eventsAtEnd().isEmpty())
        assertTrue(attention.eventsAtStart().isEmpty())
    }

    @Test
    fun `leaving the posts for the caught-up page records the last exposure`() {
        attention.onSettled(visit(0))
        attention.onLeftPosts()

        assertEquals(1, only<Event.PostExposure>().size)
        assertTrue(attention.eventsAtEnd().isEmpty())
    }
}
