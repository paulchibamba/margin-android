package com.paulchibamba.margin.domain.progress.rules

import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.progress.FactKey
import com.paulchibamba.margin.domain.progress.NOW
import com.paulchibamba.margin.domain.progress.ProgressFixture
import com.paulchibamba.margin.domain.progress.RewardHistory
import com.paulchibamba.margin.domain.progress.RewardKind
import com.paulchibamba.margin.domain.progress.StruggleTrigger
import com.paulchibamba.margin.domain.progress.daysAgo
import com.paulchibamba.margin.domain.progress.defenceInDepth
import com.paulchibamba.margin.domain.progress.factPostOf
import com.paulchibamba.margin.domain.progress.leastPrivilege
import com.paulchibamba.margin.domain.progress.pastReward
import com.paulchibamba.margin.domain.progress.storedXss
import com.paulchibamba.margin.domain.tracking.Event
import com.paulchibamba.margin.domain.tracking.NoteOpenVia
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class ReExplainRuleTest {
    private val fixture = ProgressFixture().apply { introduce(leastPrivilege, on = daysAgo(5)) }
    private val rule = ReExplainRule()
    private val note = leastPrivilege.sourceNoteId!!

    private fun triggerOfOnlySeed(): String? = fixture.seedsOf(rule).single().facts[FactKey.TRIGGER]

    @Test
    fun `a Lost makes a re-explain`() {
        fixture.lost(leastPrivilege, NOW.minusSeconds(600))

        assertEquals(StruggleTrigger.LOST.key, triggerOfOnlySeed())
    }

    @Test
    fun `a review graded Again makes a re-explain`() {
        fixture.review(leastPrivilege, Rating.AGAIN, NOW.minusSeconds(600))

        assertEquals(StruggleTrigger.AGAIN.key, triggerOfOnlySeed())
    }

    @Test
    fun `opening the source note a second time makes a re-explain`() {
        fixture.log(Event.NoteOpen(note, NoteOpenVia.FEED_READ, openCount = 2))

        assertEquals(StruggleTrigger.REREAD.key, triggerOfOnlySeed())
    }

    @Test
    fun `scrolling back in the source note makes a re-explain`() {
        fixture.log(Event.NoteExposure(note, 50.seconds, 0.seconds, 300, 200, 90, scrollBacks = 1, false))

        assertEquals(StruggleTrigger.REREAD.key, triggerOfOnlySeed())
    }

    @Test
    fun `three glances make a re-explain`() {
        fixture.glance(leastPrivilege, times = 3, activeMs = 800)

        assertEquals(StruggleTrigger.GLANCE.key, triggerOfOnlySeed())
    }

    @Test
    fun `no struggle, or a pass after it, makes no re-explain`() {
        assertTrue(fixture.seedsOf(rule).isEmpty())

        fixture.lost(leastPrivilege, daysAgo(2))
        fixture.review(leastPrivilege, Rating.GOOD, daysAgo(1))

        assertTrue(fixture.seedsOf(rule).isEmpty())
    }

    @Test
    fun `the seed carries the concept, its summary and the angles already shown`() {
        fixture.see(factPostOf(leastPrivilege))
        fixture.lost(leastPrivilege, NOW.minusSeconds(600))
        fixture.history = RewardHistory(
            listOf(pastReward(RewardKind.ReExplain, listOf(leastPrivilege), createdAt = daysAgo(3), title = "Doors")),
        )

        val facts = fixture.seedsOf(rule).single().facts

        assertEquals(leastPrivilege.title, facts[FactKey.CONCEPT])
        assertEquals(leastPrivilege.summary, facts[FactKey.SUMMARY])
        assertEquals("fact: Fact on Least Privilege | re_explain: Doors", facts[FactKey.ANGLES_SHOWN])
    }

    @Test
    fun `no anchor is invented when nothing is remembered`() {
        fixture.lost(leastPrivilege, NOW.minusSeconds(600))

        assertFalse(FactKey.ANCHOR in fixture.seedsOf(rule).single().facts)
    }

    @Test
    fun `the anchor is a remembered concept, preferably from the same book`() {
        fixture.remember(storedXss, defenceInDepth, lastReview = daysAgo(10))
        fixture.lost(leastPrivilege, NOW.minusSeconds(600))

        assertEquals(defenceInDepth.title, fixture.seedsOf(rule).single().facts[FactKey.ANCHOR])
    }

    @Test
    fun `at most one re-explain a day per concept`() {
        fixture.lost(leastPrivilege, NOW.minusSeconds(600))
        val earlierToday = pastReward(RewardKind.ReExplain, listOf(leastPrivilege), createdAt = NOW.minusSeconds(7200))
        fixture.history = RewardHistory(listOf(earlierToday))

        assertTrue(fixture.seedsOf(rule).isEmpty())
    }

    @Test
    fun `a new struggle the next day makes a new re-explain`() {
        fixture.lost(leastPrivilege, NOW.minusSeconds(600))
        val yesterday = pastReward(RewardKind.ReExplain, listOf(leastPrivilege), createdAt = daysAgo(1))
        fixture.history = RewardHistory(listOf(yesterday))

        assertEquals(1, fixture.seedsOf(rule).size)
    }

    @Test
    fun `a struggle already answered by a re-explain makes no new one`() {
        fixture.lost(leastPrivilege, daysAgo(2))
        val answered = pastReward(RewardKind.ReExplain, listOf(leastPrivilege), createdAt = daysAgo(1))
        fixture.history = RewardHistory(listOf(answered))

        assertTrue(fixture.seedsOf(rule).isEmpty())
    }
}
