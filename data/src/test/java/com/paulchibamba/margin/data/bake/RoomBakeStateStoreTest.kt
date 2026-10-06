package com.paulchibamba.margin.data.bake

import com.paulchibamba.margin.data.database.DatabaseTest
import com.paulchibamba.margin.data.database.MetaKey
import com.paulchibamba.margin.domain.bake.BakeState
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RoomBakeStateStoreTest : DatabaseTest() {

    private val store by lazy { RoomBakeStateStore(database) }

    @Test
    fun `nothing has been baked at first`() = runTest {
        assertEquals(BakeState(), store.load())
    }

    @Test
    fun `the last bake, the seen seeds and the headline are read back`() = runTest {
        val state = BakeState(Instant.parse("2026-10-06T06:30:00Z"), setOf("b2", "a1"), "3 passes since 25 Sep")

        store.save(state)

        assertEquals(state, store.load())
    }

    @Test
    fun `a bake without a headline clears the old one`() = runTest {
        store.save(BakeState(Instant.EPOCH, setOf("a1"), "Old headline"))

        store.save(BakeState(Instant.EPOCH, setOf("a1"), nextDropHeadline = null))

        assertNull(store.load().nextDropHeadline)
        assertNull(database.metaDao().get(MetaKey.NEXT_DROP_HEADLINE))
    }
}
