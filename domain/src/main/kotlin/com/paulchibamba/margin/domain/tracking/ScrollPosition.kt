package com.paulchibamba.margin.domain.tracking

data class ScrollPosition(val scrollY: Int, val viewportHeight: Int, val contentHeight: Int) {
    val reachedPercent: Int
        get() {
            if (contentHeight <= viewportHeight) return FULL
            return ((scrollY + viewportHeight) * FULL / contentHeight).coerceIn(0, FULL)
        }

    private companion object {
        const val FULL = 100
    }
}
