package com.paulchibamba.margin.domain.feed

import com.paulchibamba.margin.domain.memory.CardState
import kotlin.time.Duration

data class MemorySnapshot(
    val state: CardState,
    val stability: Double,
    val difficulty: Double,
    val recall: Double,
    val dueIn: Duration,
    val reps: Int,
    val lapses: Int,
)
