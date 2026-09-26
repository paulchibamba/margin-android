package com.paulchibamba.margin.domain.memory

import java.time.Instant

data class MemoryCard(
    val due: Instant,
    val stability: Double,
    val difficulty: Double,
    val scheduledDays: Int,
    val learningSteps: Int,
    val reps: Int,
    val lapses: Int,
    val state: CardState,
    val lastReview: Instant?,
) {
    internal val memoryState: MemoryState
        get() = MemoryState(stability = stability, difficulty = difficulty)

    internal fun withMemory(memory: MemoryState): MemoryCard =
        copy(stability = memory.stability, difficulty = memory.difficulty)

    companion object {
        fun new(now: Instant): MemoryCard = MemoryCard(
            due = now,
            stability = 0.0,
            difficulty = 0.0,
            scheduledDays = 0,
            learningSteps = 0,
            reps = 0,
            lapses = 0,
            state = CardState.NEW,
            lastReview = null,
        )
    }
}
