package com.paulchibamba.margin.domain.memory

enum class CardState(val value: Int) {
    NEW(0),
    LEARNING(1),
    REVIEW(2),
    RELEARNING(3),
}
