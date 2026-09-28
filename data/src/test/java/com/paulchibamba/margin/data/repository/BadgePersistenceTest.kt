package com.paulchibamba.margin.data.repository

import com.paulchibamba.margin.data.database.DatabaseTest
import com.paulchibamba.margin.domain.feed.appSec
import com.paulchibamba.margin.domain.feed.cia
import com.paulchibamba.margin.domain.feed.defenceInDepth
import com.paulchibamba.margin.domain.feed.freshState
import com.paulchibamba.margin.domain.feed.grokking
import com.paulchibamba.margin.domain.feed.leastPrivilege
import com.paulchibamba.margin.domain.feed.now
import com.paulchibamba.margin.domain.feed.sameOrigin
import com.paulchibamba.margin.domain.feed.withIntroduced
import com.paulchibamba.margin.domain.rewards.Badge
import com.paulchibamba.margin.domain.rewards.BadgeKind
import com.paulchibamba.margin.domain.usecase.ConsumeNewBadges
import com.paulchibamba.margin.domain.usecase.FakeContentRepository
import com.paulchibamba.margin.domain.usecase.FakeSettingsRepository
import com.paulchibamba.margin.domain.usecase.FeedStateLock
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class BadgePersistenceTest : DatabaseTest() {

    private val appSecIntroduced = freshState.withIntroduced(cia, leastPrivilege, defenceInDepth)

    private fun consumeNewBadges() = ConsumeNewBadges(
        FakeContentRepository(),
        RoomProgressRepository(database),
        FakeSettingsRepository(),
        FeedStateLock(),
    )

    @Test
    fun `a book's badge is shown once, even after the app restarts`() = runTest {
        RoomProgressRepository(database).saveFeedState(appSecIntroduced, now)

        val first = consumeNewBadges()().map { it.badge }
        val afterRestart = consumeNewBadges()().map { it.badge }

        assertEquals(listOf(Badge(appSec.slug, BadgeKind.INTRODUCED)), first)
        assertEquals(emptyList(), afterRestart)
    }

    @Test
    fun `another book earning the same badge still gets its own celebration`() = runTest {
        val progress = RoomProgressRepository(database)
        progress.saveFeedState(appSecIntroduced, now)
        consumeNewBadges()()

        progress.saveFeedState(appSecIntroduced.withIntroduced(sameOrigin), now)
        val later = consumeNewBadges()().map { it.badge }

        assertEquals(listOf(Badge(grokking.slug, BadgeKind.INTRODUCED)), later)
    }
}
