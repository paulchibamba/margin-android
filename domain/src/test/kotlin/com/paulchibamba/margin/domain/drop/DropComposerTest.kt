package com.paulchibamba.margin.domain.drop

import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.FeedConfig
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.LearningLibrary
import com.paulchibamba.margin.domain.feed.RecallEstimate
import com.paulchibamba.margin.domain.feed.cia
import com.paulchibamba.margin.domain.feed.defenceInDepth
import com.paulchibamba.margin.domain.feed.freshState
import com.paulchibamba.margin.domain.feed.generatedPostOf
import com.paulchibamba.margin.domain.feed.introducedProgress
import com.paulchibamba.margin.domain.feed.learnedCard
import com.paulchibamba.margin.domain.feed.leastPrivilege
import com.paulchibamba.margin.domain.feed.libraryWith
import com.paulchibamba.margin.domain.feed.mcqOf
import com.paulchibamba.margin.domain.feed.memeOf
import com.paulchibamba.margin.domain.feed.now
import com.paulchibamba.margin.domain.feed.readingEverything
import com.paulchibamba.margin.domain.feed.sameOrigin
import com.paulchibamba.margin.domain.feed.testScheduler
import com.paulchibamba.margin.domain.feed.tipOf
import com.paulchibamba.margin.domain.feed.withIntroduced
import com.paulchibamba.margin.domain.feed.withSeen
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.progress.GeneratedPost
import com.paulchibamba.margin.domain.progress.RewardKind
import java.time.Duration
import java.time.LocalDate
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class DropComposerTest {
    private val composer = DropComposer(RecallEstimate(testScheduler, FeedConfig()), Random(3))
    private val date = LocalDate.parse("2026-10-01")
    private val zoomOut = generatedPostOf(RewardKind.ZoomOut, cia, index = 1)
    private val comeback = generatedPostOf(RewardKind.Comeback, leastPrivilege, index = 2)
    private val quote = generatedPostOf(RewardKind.Quote, cia, index = 3)

    private fun reviewedDaysAgo(days: Long) =
        introducedProgress().copy(card = learnedCard(now.minus(Duration.ofDays(days))), introducedAtStep = days.toInt())

    private val allDue = freshState
        .withIntroduced(cia, progress = reviewedDaysAgo(40))
        .withIntroduced(leastPrivilege, progress = reviewedDaysAgo(90))
        .withIntroduced(sameOrigin, progress = reviewedDaysAgo(60))

    private val nothingDue = freshState.withIntroduced(cia, leastPrivilege, sameOrigin)

    private fun library(vararg generated: GeneratedPost) =
        libraryWith(readNotes = readingEverything(), generatedPosts = generated.toList())

    private fun compose(state: FeedState, library: LearningLibrary, request: (DropRequest) -> DropRequest = { it }) =
        composer.compose(date, request(DropRequest(library, state, now)))

    private fun DailyDrop.slots() = items.map(DropItem::slot)

    private fun DailyDrop.kindOf(slot: DropSlot) = GeneratedPost.kindOf(items.single { it.slot == slot }.postId)

    @Test
    fun `a full drop has seven items that open easy and end on progress`() {
        val drop = compose(allDue, library(zoomOut, comeback, quote))

        val expected = listOf(
            DropSlot.OPENING, DropSlot.NEW, DropSlot.REVIEW, DropSlot.SURPRISE,
            DropSlot.REVIEW, DropSlot.REVIEW, DropSlot.CLOSING,
        )
        assertEquals(expected, drop.slots())
        assertEquals(DropComposer.MAXIMUM_SIZE, drop.size)
        assertEquals(drop.size, drop.items.map(DropItem::postId).distinct().size)
    }

    @Test
    fun `reviews come most urgent first, as tests`() {
        val drop = compose(allDue, library(zoomOut, comeback))

        val reviews = drop.items.filter { it.slot == DropSlot.REVIEW }
        assertEquals(listOf(leastPrivilege, sameOrigin, cia).map(::mcqOf).map { it.id }, reviews.map { it.postId })
        assertTrue(reviews.all { it.source == CandidateSource.REVIEW })
    }

    @Test
    fun `the closing is a progress post of a different kind from the opening`() {
        val drop = compose(allDue, library(zoomOut, comeback, generatedPostOf(RewardKind.ZoomOut, sameOrigin, 4)))

        assertEquals(DropSlot.CLOSING, drop.items.last().slot)
        assertNotEquals(drop.kindOf(DropSlot.OPENING), drop.kindOf(DropSlot.CLOSING))
    }

    @Test
    fun `with nothing due the drop is new, progress and a surprise`() {
        val drop = compose(nothingDue, library(zoomOut, comeback))

        assertEquals(listOf(DropSlot.OPENING, DropSlot.NEW, DropSlot.SURPRISE, DropSlot.CLOSING), drop.slots())
        assertEquals(tipOf(defenceInDepth).id, drop.items[1].postId)
        assertEquals(CandidateSource.NEW, drop.items[1].source)
    }

    @Test
    fun `the post the headline was written for closes the drop`() {
        val drop = compose(allDue, library(zoomOut, comeback)) { it.copy(headlinePost = zoomOut.id) }

        assertEquals(zoomOut.id, drop.items.last().postId)
    }

    @Test
    fun `without progress posts it opens on the newest idea and closes on a template`() {
        val template = generatedPostOf(RewardKind.NowYouCan, cia, index = 9).toPost(cia.bookSlug)!!
        val state = allDue.withIntroduced(leastPrivilege, progress = introducedProgress().copy(introducedAtStep = 95))

        val drop = compose(state, library()) { it.copy(fallbackClosings = listOf(template)) }

        assertEquals(template.id, drop.items.last().postId)
        assertEquals(DropItem(tipOf(leastPrivilege).id, DropSlot.OPENING, CandidateSource.ANGLE), drop.items.first())
    }

    @Test
    fun `a thin day is padded to four with other ideas`() {
        val everythingIntroduced = nothingDue.withIntroduced(defenceInDepth).withSeen(*memesOf(allIntroduced))

        val drop = compose(everythingIntroduced, library(zoomOut))

        assertEquals(DropComposer.MINIMUM_SIZE, drop.size)
        assertEquals(listOf(DropSlot.OPENING, DropSlot.EXTRA, DropSlot.EXTRA, DropSlot.CLOSING), drop.slots())
    }

    @Test
    fun `the drop remembers when it was composed`() {
        val drop = compose(allDue.copy(step = 42), library(zoomOut))

        assertEquals(date, drop.date)
        assertEquals(now, drop.composedAt)
        assertEquals(42, drop.composedAtStep)
    }

    private val allIntroduced = listOf(cia, leastPrivilege, sameOrigin, defenceInDepth)

    private fun memesOf(concepts: List<Concept>) = concepts.map(::memeOf).toTypedArray()
}
