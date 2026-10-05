package com.paulchibamba.margin.domain.tracking

import kotlin.test.Test
import kotlin.test.assertEquals

class ScrollBackDetectorTest {
    private val detector = ScrollBackDetector()

    private fun scrollTo(vararg positions: Int) =
        positions.forEach { y -> detector.onScroll(ScrollPosition(y, viewportHeight = 1000, contentHeight = 5000)) }

    @Test
    fun `scrolling back up 30 percent of the screen after going down is one scroll-back`() {
        scrollTo(0, 800, 1600, 1300)

        assertEquals(1, detector.scrollBacks)
    }

    @Test
    fun `a small nudge back up is not a scroll-back`() {
        scrollTo(0, 1600, 1400)

        assertEquals(0, detector.scrollBacks)
    }

    @Test
    fun `one long scroll up counts once`() {
        scrollTo(0, 3000, 2500, 1500, 500)

        assertEquals(1, detector.scrollBacks)
    }

    @Test
    fun `going down again and back up counts again`() {
        scrollTo(0, 2000, 1500, 2600, 2200)

        assertEquals(2, detector.scrollBacks)
    }

    @Test
    fun `the furthest point reached is kept as a percentage of the note`() {
        scrollTo(0, 2000, 500)

        assertEquals(60, detector.maxScrollPercent)
    }

    @Test
    fun `a note that fits on the screen is fully seen`() {
        detector.onScroll(ScrollPosition(0, viewportHeight = 1000, contentHeight = 900))

        assertEquals(100, detector.maxScrollPercent)
    }
}
