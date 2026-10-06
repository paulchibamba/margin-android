package com.paulchibamba.margin.data.drop

import com.paulchibamba.margin.data.database.DatabaseTest
import com.paulchibamba.margin.data.database.MetaKey
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RoomDropTimeStoreTest : DatabaseTest() {

    private val store by lazy { RoomDropTimeStore(database) }

    @Test
    fun `nothing is learned at first`() = runTest {
        assertNull(store.observeLearned().first())
    }

    @Test
    fun `the learned slot is stored as hours and minutes`() = runTest {
        store.saveLearned(LocalTime.of(21, 30))

        assertEquals(LocalTime.of(21, 30), store.observeLearned().first())
        assertEquals("21:30", database.metaDao().get(MetaKey.DROP_TIME_LEARNED))
    }
}
