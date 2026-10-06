package com.paulchibamba.margin.domain.repository

import com.paulchibamba.margin.domain.drop.DailyDrop
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface DailyDropRepository {
    suspend fun forDate(date: LocalDate): DailyDrop?
    fun observe(date: LocalDate): Flow<DailyDrop?>
    suspend fun save(drop: DailyDrop)
}
