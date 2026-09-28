package com.paulchibamba.margin.data.startup

import androidx.test.core.app.ApplicationProvider
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

@RunWith(RobolectricTestRunner::class)
class PreferencesClockOffsetStoreTest {

    private fun store() = PreferencesClockOffsetStore(ApplicationProvider.getApplicationContext())

    @Test
    fun `with nothing saved the offset is zero`() {
        assertEquals(Duration.ZERO, store().load())
    }

    @Test
    fun `a saved offset is loaded by a new store`() {
        store().save(1.days + 1.hours)

        assertEquals(25.hours, store().load())
    }
}
