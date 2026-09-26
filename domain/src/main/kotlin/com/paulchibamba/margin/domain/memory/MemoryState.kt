package com.paulchibamba.margin.domain.memory

internal data class MemoryState(val stability: Double, val difficulty: Double) {

    val isUnset: Boolean
        get() = stability == 0.0 && difficulty == 0.0
}
