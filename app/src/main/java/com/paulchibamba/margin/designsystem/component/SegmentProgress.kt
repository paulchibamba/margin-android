package com.paulchibamba.margin.designsystem.component

data class SegmentProgress(val count: Int, val current: Int) {
    init {
        require(count > 0) { "A post with segments has at least one" }
        require(current in 0 until count) { "The current segment $current is outside 0 until $count" }
    }

    fun fillOf(segment: Int): Float = when {
        segment < current -> 1f
        segment == current -> CURRENT_SEGMENT_FILL
        else -> 0f
    }

    private companion object {
        const val CURRENT_SEGMENT_FILL = 0.45f
    }
}
