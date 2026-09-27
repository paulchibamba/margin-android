package com.paulchibamba.margin.feature.feed.post

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.paulchibamba.margin.designsystem.component.SegmentProgress

@Stable
class CarouselState(val slideCount: Int, initialSlide: Int = 0) {
    init { require(slideCount > 0) { "A carousel needs at least one slide" } }

    var slide by mutableIntStateOf(initialSlide.coerceIn(0, slideCount - 1))
        private set

    val isOnLastSlide: Boolean
        get() = slide == slideCount - 1

    val segments: SegmentProgress
        get() = SegmentProgress(count = slideCount, current = slide)

    val counterLabel: String
        get() = "${slide + 1} / $slideCount"

    fun next() {
        slide = (slide + 1).coerceAtMost(slideCount - 1)
    }

    fun previous() {
        slide = (slide - 1).coerceAtLeast(0)
    }

    companion object {
        fun saver(slideCount: Int): Saver<CarouselState, Int> =
            Saver(save = { it.slide }, restore = { slide -> CarouselState(slideCount, slide) })
    }
}

@Composable
fun rememberCarouselState(key: Any, slideCount: Int): CarouselState =
    rememberSaveable(key, slideCount, saver = CarouselState.saver(slideCount)) { CarouselState(slideCount) }
