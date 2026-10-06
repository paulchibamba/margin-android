package com.paulchibamba.margin.domain.drop

import com.paulchibamba.margin.domain.repository.DailyDropRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeDailyDropRepository : DailyDropRepository {
    val drops = MutableStateFlow<Map<LocalDate, DailyDrop>>(emptyMap())

    override suspend fun forDate(date: LocalDate): DailyDrop? = drops.value[date]

    override fun observe(date: LocalDate): Flow<DailyDrop?> = drops.map { saved -> saved[date] }

    override suspend fun save(drop: DailyDrop) {
        drops.value = drops.value + (drop.date to drop)
    }
}
