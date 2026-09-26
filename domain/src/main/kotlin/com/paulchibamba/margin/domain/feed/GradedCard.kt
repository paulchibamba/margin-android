package com.paulchibamba.margin.domain.feed

import com.paulchibamba.margin.domain.memory.MemoryCard

data class GradedCard(val card: MemoryCard, val logEntry: ReviewLogEntry)
