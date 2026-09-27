package com.paulchibamba.margin.feature.feed.post

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CarouselStateTest {

    @Test
    fun `stepping forward stops at the last slide`() {
        val carousel = CarouselState(slideCount = 3)

        repeat(5) { carousel.next() }

        assertEquals(2, carousel.slide)
        assertTrue(carousel.isOnLastSlide)
    }

    @Test
    fun `stepping back stops at the first slide`() {
        val carousel = CarouselState(slideCount = 3, initialSlide = 1)

        repeat(3) { carousel.previous() }

        assertEquals(0, carousel.slide)
        assertFalse(carousel.isOnLastSlide)
    }

    @Test
    fun `the counter and segments follow the current slide`() {
        val carousel = CarouselState(slideCount = 4)
        carousel.next()

        assertEquals("2 / 4", carousel.counterLabel)
        assertEquals(1, carousel.segments.current)
    }

    @Test
    fun `a one-slide carousel starts on its last slide`() {
        assertTrue(CarouselState(slideCount = 1).isOnLastSlide)
    }
}
