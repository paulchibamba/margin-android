package com.paulchibamba.margin.data.drop

import com.paulchibamba.margin.data.database.DatabaseTest
import com.paulchibamba.margin.domain.drop.DailyDrop
import com.paulchibamba.margin.domain.drop.DropItem
import com.paulchibamba.margin.domain.drop.DropSlot
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.model.PostId
import java.time.Instant
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RoomDailyDropRepositoryTest : DatabaseTest() {
    private val repository by lazy { RoomDailyDropRepository(database) }
    private val today = LocalDate.parse("2026-10-06")

    private val drop = DailyDrop(
        date = today,
        composedAt = Instant.parse("2026-10-06T07:10:00Z"),
        composedAtStep = 41,
        items = listOf(
            DropItem(PostId("gen/zoom_out/a1"), DropSlot.OPENING, CandidateSource.REWARD, enteredAtStep = 42),
            DropItem(PostId("appsec/ch1/c3/tip"), DropSlot.NEW, CandidateSource.NEW),
            DropItem(PostId("gen/comeback/b2"), DropSlot.CLOSING, CandidateSource.REWARD),
        ),
        headline = "3 passes since 25 Sep",
        position = 1,
    )

    @Test
    fun `there is no drop before one is composed`() = runTest {
        assertNull(repository.forDate(today))
    }

    @Test
    fun `a drop is read back with its items in order`() = runTest {
        repository.save(drop)

        assertEquals(drop, repository.forDate(today))
        assertNull(repository.forDate(today.plusDays(1)))
    }

    @Test
    fun `saving again updates progress through the same day's drop`() = runTest {
        repository.save(drop)
        val completedAt = Instant.parse("2026-10-06T07:15:00Z")
        val finished = drop.left(2).copy(completedAt = completedAt, isContinuedIntoFeed = true)

        repository.save(finished)

        assertEquals(finished, repository.observe(today).first())
    }
}
