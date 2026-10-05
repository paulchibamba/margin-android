package com.paulchibamba.margin.data.screentime

import com.paulchibamba.margin.data.database.DatabaseTest
import com.paulchibamba.margin.domain.screentime.AppCategory
import com.paulchibamba.margin.domain.screentime.AppScreenTime
import com.paulchibamba.margin.domain.screentime.PackageName
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RoomScreenTimeStoreTest : DatabaseTest() {
    private val store by lazy { RoomScreenTimeStore(database) }
    private val instagram = PackageName("com.instagram.android")
    private val notes = PackageName("com.samsung.android.app.notes")
    private val firstDay = LocalDate.parse("2026-10-03")
    private val secondDay = LocalDate.parse("2026-10-04")

    private fun instagramFor(minutes: Int, label: String = "Instagram") =
        AppScreenTime(instagram, label, AppCategory.SOCIAL, isDoom = true, minutes.minutes)

    private fun notesFor(minutes: Int) =
        AppScreenTime(notes, "Samsung Notes", AppCategory.PRODUCTIVITY, isDoom = false, minutes.minutes)

    @Test
    fun `saving a day again replaces its apps`() = runTest {
        store.saveDay(secondDay, listOf(instagramFor(10), notesFor(5)))
        store.saveDay(secondDay, listOf(instagramFor(40)))

        assertEquals(listOf(instagramFor(40)), store.appsOn(secondDay))
    }

    @Test
    fun `totals add up each app's days and keep its latest label`() = runTest {
        store.saveDay(firstDay, listOf(instagramFor(10, label = "Instagram Lite"), notesFor(30)))
        store.saveDay(secondDay, listOf(instagramFor(40)))

        val totals = store.observeTotalsFrom(firstDay).first()

        assertEquals(listOf(instagramFor(50), notesFor(30)), totals)
        assertEquals(listOf(instagramFor(40)), store.observeTotalsFrom(secondDay).first())
    }

    @Test
    fun `marking an app doom changes its saved days and is remembered`() = runTest {
        store.saveDay(firstDay, listOf(notesFor(30)))
        store.saveDay(secondDay, listOf(instagramFor(40), notesFor(5)))

        store.setDoom(notes, isDoom = true)

        assertEquals(listOf(firstDay, secondDay), store.datesWith(notes))
        assertTrue(store.appsOn(firstDay).single().isDoom)
        assertEquals(mapOf(notes to true), store.overrides())
    }

    @Test
    fun `deleting removes every day but keeps the doom choices`() = runTest {
        store.saveDay(secondDay, listOf(instagramFor(40)))
        store.setDoom(instagram, isDoom = false)
        store.markIngestedThrough(secondDay)

        store.deleteAll(ingestFrom = LocalDate.parse("2026-10-05"))

        assertEquals(emptyList(), store.dates())
        assertEquals(mapOf(instagram to false), store.overrides())
        assertEquals(LocalDate.parse("2026-10-05"), store.ingestFrom())
        assertEquals(secondDay, store.ingestedThrough())
    }

    @Test
    fun `nothing has been ingested at first`() = runTest {
        assertNull(store.ingestedThrough())
        assertNull(store.ingestFrom())
    }
}
