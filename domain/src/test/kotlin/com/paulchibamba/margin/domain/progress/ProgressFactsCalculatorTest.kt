package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.progression.ReadingOnlyChapters
import com.paulchibamba.margin.domain.tracking.Event
import com.paulchibamba.margin.domain.tracking.NoteOpenVia
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class ProgressFactsCalculatorTest {
    private val fixture = ProgressFixture()

    @Test
    fun `a chapter counts its introduced, remaining and remembered concepts`() {
        fixture.introduce(cia, leastPrivilege)
        fixture.remember(cia)

        val chapter = fixture.facts().chapters.first { it.chapter == fundamentals }

        assertEquals(listOf(cia, leastPrivilege), chapter.introduced)
        assertEquals(listOf(needToKnow, defenceInDepth), chapter.remaining)
        assertEquals(listOf(cia), chapter.remembered)
        assertEquals(4, chapter.total)
    }

    @Test
    fun `reading-only chapters are left out`() {
        fixture.readingOnlyChapters = ReadingOnlyChapters(mapOf(appSec.slug to setOf(2)))

        assertTrue(fixture.facts().chapters.none { it.chapter == inputHandling })
    }

    @Test
    fun `a concept is introduced on the day its first post was seen`() {
        fixture.introduce(cia, on = daysAgo(3))
        fixture.firstSeen[mcqPostOf(cia).id] = daysAgo(1)

        assertEquals(listOf(Introduction(cia, LocalDate.of(2026, 10, 2))), fixture.facts().introductions)
    }

    @Test
    fun `concepts introduced today and a week or more ago are told apart`() {
        fixture.introduce(cia)
        fixture.introduce(leastPrivilege, on = daysAgo(7))
        fixture.introduce(sqlInjection, on = daysAgo(6))

        val facts = fixture.facts()

        assertEquals(listOf(cia), facts.introducedToday.map(Introduction::concept))
        assertEquals(listOf(leastPrivilege), facts.introducedAtLeastDaysAgo(7).map(Introduction::concept))
    }

    @Test
    fun `a comeback needs a fail then two passes in a row`() {
        fixture.introduce(cia, on = daysAgo(20))
        fixture.review(cia, Rating.AGAIN, daysAgo(10))
        fixture.review(cia, Rating.AGAIN, daysAgo(9))
        fixture.review(cia, Rating.GOOD, daysAgo(5))
        fixture.review(cia, Rating.HARD, daysAgo(2))

        val comeback = fixture.facts().comebacks.single()

        assertEquals(Comeback(cia, LocalDate.of(2026, 9, 25), failCount = 2, passCount = 2), comeback)
    }

    @Test
    fun `one pass after a fail is not a comeback yet`() {
        fixture.review(cia, Rating.AGAIN, daysAgo(10))
        fixture.review(cia, Rating.GOOD, daysAgo(5))

        assertTrue(fixture.facts().comebacks.isEmpty())
    }

    @Test
    fun `a fail after the passes resets the comeback`() {
        fixture.review(cia, Rating.AGAIN, daysAgo(10))
        fixture.review(cia, Rating.GOOD, daysAgo(5))
        fixture.review(cia, Rating.GOOD, daysAgo(3))
        fixture.review(cia, Rating.AGAIN, daysAgo(1))

        assertTrue(fixture.facts().comebacks.isEmpty())
    }

    @Test
    fun `a Lost counts as the fail of a comeback`() {
        fixture.lost(cia, daysAgo(6))
        fixture.review(cia, Rating.GOOD, daysAgo(4))
        fixture.review(cia, Rating.GOOD, daysAgo(1))

        assertEquals(1, fixture.facts().comebacks.single().failCount)
    }

    @Test
    fun `two re-reads of a concept within the window make it a hotspot`() {
        val note = cia.sourceNoteId!!
        fixture.log(Event.NoteOpen(note, NoteOpenVia.CHAPTER, openCount = 2))
        fixture.log(Event.NoteExposure(note, 60.seconds, 0.seconds, 300, 200, 100, scrollBacks = 1, false))

        assertEquals(listOf(ConceptHotspot(cia, rereads = 2)), fixture.facts().attention.hotspots)
    }

    @Test
    fun `re-reads older than 14 days are not counted`() {
        val note = cia.sourceNoteId!!
        fixture.log(Event.NoteOpen(note, NoteOpenVia.CHAPTER, openCount = 2), at = daysAgo(15))
        fixture.log(Event.NoteOpen(note, NoteOpenVia.CHAPTER, openCount = 3), at = daysAgo(15))

        assertTrue(fixture.facts().attention.hotspots.isEmpty())
    }

    @Test
    fun `three impressions all under one and a half seconds are glanced only`() {
        fixture.glance(cia, times = 3, activeMs = 900)
        fixture.glance(leastPrivilege, times = 2, activeMs = 900)

        assertEquals(setOf(cia.id), fixture.facts().attention.glancedOnly)
    }

    @Test
    fun `one attended impression means the concept was really seen`() {
        fixture.glance(cia, times = 3, activeMs = 900)
        fixture.glance(cia, times = 1, activeMs = 2000)

        assertEquals(emptySet<ConceptId>(), fixture.facts().attention.glancedOnly)
    }

    @Test
    fun `the next unread notes past each active frontier are listed, at most four, with sections`() {
        fixture.readNotes = setOf(noteIdOf(fundamentals, 0), noteIdOf(fundamentals, 1))

        val upcoming = fixture.facts().upcomingNotes.filter { it.note.bookSlug == appSec.slug }

        val expected = listOf(2, 3, 4).map { order -> noteIdOf(fundamentals, order) } + noteIdOf(inputHandling, 0)
        assertEquals(expected, upcoming.map { it.note.id })
        assertEquals(listOf(1, 2, 3, 4), upcoming.map(UpcomingNote::notesAway))
        assertEquals(listOf(leastPrivilege), upcoming.first().concepts)
        assertEquals("Section 2", upcoming.first().note.section)
    }

    @Test
    fun `read notes already used for a quote are left out`() {
        fixture.readNotes = setOf(noteIdOf(fundamentals, 1), noteIdOf(fundamentals, 2))
        fixture.history = RewardHistory(listOf(pastReward(RewardKind.Quote, note = noteIdOf(fundamentals, 1))))

        assertEquals(listOf(noteIdOf(fundamentals, 2)), fixture.facts().unquotedReadNotes.map { it.note.id })
    }

    @Test
    fun `a concept reviewed into long-term memory since the last bake is newly remembered`() {
        fixture.remember(cia, lastReview = daysAgo(1))
        fixture.remember(leastPrivilege, lastReview = daysAgo(5))
        fixture.history = RewardHistory(lastBakeAt = daysAgo(2))

        assertEquals(listOf(cia), fixture.facts().newlyRemembered)
    }

    @Test
    fun `the angles already shown are the seen posts of a struggling concept`() {
        fixture.introduce(cia)
        fixture.see(factPostOf(cia))
        fixture.lost(cia, daysAgo(1))

        assertEquals(listOf(Angle("fact", "Fact on The CIA Triad")), fixture.facts().anglesShown[cia.id])
    }
}
