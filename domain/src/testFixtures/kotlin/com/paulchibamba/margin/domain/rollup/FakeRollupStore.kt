package com.paulchibamba.margin.domain.rollup

import com.paulchibamba.margin.domain.repository.RollupStore
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeRollupStore : RollupStore {
    private val rollups = MutableStateFlow<Map<LocalDate, DailyRollup>>(emptyMap())

    val saved: Map<LocalDate, DailyRollup> get() = rollups.value

    override suspend fun save(rollup: DailyRollup) {
        rollups.value += rollup.date to rollup
    }

    override suspend fun computedTimes(): Map<LocalDate, Instant> = saved.mapValues { it.value.computedAt }

    override fun observeFrom(date: LocalDate): Flow<List<DailyRollup>> =
        rollups.map { all -> all.values.filter { !it.date.isBefore(date) }.sortedBy(DailyRollup::date) }
}
