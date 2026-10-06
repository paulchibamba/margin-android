package com.paulchibamba.margin.domain.repository

import java.time.LocalTime
import kotlinx.coroutines.flow.Flow

interface DropTimeStore {
    fun observeLearned(): Flow<LocalTime?>
    suspend fun saveLearned(slot: LocalTime)
}
