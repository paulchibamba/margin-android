package com.paulchibamba.margin.domain.tracking

class ScrollBackDetector(private val viewportFraction: Double = SCROLL_BACK_FRACTION) {
    var scrollBacks = 0
        private set
    var maxScrollPercent = 0
        private set
    private var lastScrollY: Int? = null
    private var deepestScrollY = 0
    private var isCountedUntilScrollingDown = false

    fun onScroll(position: ScrollPosition) {
        maxScrollPercent = maxOf(maxScrollPercent, position.reachedPercent)
        val previous = lastScrollY
        lastScrollY = position.scrollY
        when {
            previous == null -> deepestScrollY = position.scrollY
            position.scrollY > previous -> onScrolledDown(position.scrollY)
            isScrollBack(position) -> countScrollBack()
        }
    }

    private fun onScrolledDown(scrollY: Int) {
        deepestScrollY = if (isCountedUntilScrollingDown) scrollY else maxOf(deepestScrollY, scrollY)
        isCountedUntilScrollingDown = false
    }

    private fun isScrollBack(position: ScrollPosition): Boolean =
        !isCountedUntilScrollingDown && deepestScrollY - position.scrollY >= viewportFraction * position.viewportHeight

    private fun countScrollBack() {
        scrollBacks++
        isCountedUntilScrollingDown = true
    }

    companion object {
        const val SCROLL_BACK_FRACTION = 0.3
    }
}
