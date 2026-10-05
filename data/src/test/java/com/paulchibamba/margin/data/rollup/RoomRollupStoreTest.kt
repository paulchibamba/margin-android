package com.paulchibamba.margin.data.rollup

import com.paulchibamba.margin.data.database.DatabaseTest
import com.paulchibamba.margin.domain.rollup.DailyRollup
import com.paulchibamba.margin.domain.rollup.RollupFixture
import com.paulchibamba.margin.domain.rollup.RollupMetrics
import java.time.Instant
import java.time.LocalDate
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.jsonObject
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RoomRollupStoreTest : DatabaseTest() {
    private val store by lazy { RoomRollupStore(database) }
    private val metrics = RollupMetrics.of(RollupFixture.day)

    private fun rollupOn(date: String, computedAt: Long = 9_000) =
        DailyRollup(LocalDate.parse(date), metrics, Instant.ofEpochMilli(computedAt))

    @Test
    fun `a saved rollup reads back with every metric, time to the hundredth of a minute`() = runTest {
        store.save(rollupOn("2026-10-04"))

        val read = store.observeFrom(LocalDate.parse("2026-10-04")).first().single()

        assertEquals(metrics.copy(time = read.metrics.time), read.metrics)
        assertTrue(abs((read.metrics.time.active - metrics.time.active).inWholeMilliseconds) <= 300)
    }

    @Test
    fun `saving a day again replaces it, and only days from the start date are observed`() = runTest {
        store.save(rollupOn("2026-10-01"))
        store.save(rollupOn("2026-10-04", computedAt = 1_000))
        store.save(rollupOn("2026-10-04", computedAt = 2_000))

        val observed = store.observeFrom(LocalDate.parse("2026-10-02")).first()

        assertEquals(listOf(rollupOn("2026-10-04").date), observed.map(DailyRollup::date))
        assertEquals(Instant.ofEpochMilli(2_000), store.computedTimes()[LocalDate.parse("2026-10-04")])
    }

    @Test
    fun `the stored metrics use the schema names and leave screen time and loop health empty`() = runTest {
        store.save(rollupOn("2026-10-04"))

        val stored = database.query("SELECT metrics, schemaVersion FROM daily_rollup", null).use { cursor ->
            cursor.moveToFirst()
            assertEquals(RoomRollupStore.SCHEMA_VERSION, cursor.getInt(1))
            Json.parseToJsonElement(cursor.getString(0)).jsonObject
        }

        assertEquals(0.25, stored.getValue("glanceRate").toString().toDouble())
        listOf("screenMin", "doomMin", "marginShare", "topDoomApps", "dropCompleted", "notificationOpenRate")
            .forEach { key -> assertEquals(JsonNull, stored.getValue(key), key) }
    }
}
