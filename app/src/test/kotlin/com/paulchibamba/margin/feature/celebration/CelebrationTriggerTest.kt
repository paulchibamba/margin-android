package com.paulchibamba.margin.feature.celebration

import com.paulchibamba.margin.domain.feed.appSec
import com.paulchibamba.margin.domain.feed.cia
import com.paulchibamba.margin.domain.feed.defenceInDepth
import com.paulchibamba.margin.domain.feed.freshState
import com.paulchibamba.margin.domain.feed.leastPrivilege
import com.paulchibamba.margin.domain.feed.withIntroduced
import com.paulchibamba.margin.domain.rewards.Badge
import com.paulchibamba.margin.domain.rewards.BadgeKind
import com.paulchibamba.margin.domain.usecase.ConsumeNewBadges
import com.paulchibamba.margin.domain.usecase.FakeContentRepository
import com.paulchibamba.margin.domain.usecase.FakeProgressRepository
import com.paulchibamba.margin.domain.usecase.FakeSettingsRepository
import com.paulchibamba.margin.domain.usecase.FeedStateLock
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class CelebrationTriggerTest {

    private val progress = FakeProgressRepository()
    private val queue = CelebrationQueue()
    private val trigger = CelebrationTrigger(
        queue,
        ConsumeNewBadges(FakeContentRepository(), progress, FakeSettingsRepository(), FeedStateLock()),
    )

    @Test
    fun `a streak signal queues the streak celebration only once`() {
        trigger.onStreakSignal(isStreakExtended = true)
        trigger.onStreakSignal(isStreakExtended = true)

        assertEquals(listOf<Celebration>(Celebration.StreakExtended), queue.pending.value)
    }

    @Test
    fun `a post that does not extend the streak queues nothing`() {
        trigger.onStreakSignal(isStreakExtended = false)

        assertEquals(emptyList(), queue.pending.value)
    }

    @Test
    fun `a new badge is queued once and not again after it is shown`() = runTest {
        progress.feedState.value = freshState.withIntroduced(cia, leastPrivilege, defenceInDepth)

        trigger.checkBadges()
        val celebration = assertIs<Celebration.BadgeUnlocked>(queue.pending.value.single())
        queue.markShown(celebration)
        trigger.checkBadges()

        assertEquals(Badge(appSec.slug, BadgeKind.INTRODUCED), celebration.earned.badge)
        assertEquals(emptyList(), queue.pending.value)
    }

    @Test
    fun `celebrations are shown in the order they arrive`() = runTest {
        progress.feedState.value = freshState.withIntroduced(cia, leastPrivilege, defenceInDepth)

        trigger.onStreakSignal(isStreakExtended = true)
        trigger.checkBadges()

        assertEquals(Celebration.StreakExtended, queue.pending.value.first())
        assertIs<Celebration.BadgeUnlocked>(queue.pending.value.last())
    }
}
