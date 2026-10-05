package com.paulchibamba.margin.domain.repository

import com.paulchibamba.margin.domain.rollup.DailyRollup
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface RollupStore {
    suspend fun save(rollup: DailyRollup)
    suspend fun computedTimes(): Map<LocalDate, Instant>
    fun observeFrom(date: LocalDate): Flow<List<DailyRollup>>
}
