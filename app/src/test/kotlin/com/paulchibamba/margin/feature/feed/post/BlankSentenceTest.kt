package com.paulchibamba.margin.feature.feed.post

import org.junit.Test
import kotlin.test.assertEquals

class BlankSentenceTest {

    @Test
    fun `the blank splits the sentence in two`() {
        assertEquals(
            BlankSentence("", " means data has not been altered."),
            BlankSentence.parse("____ means data has not been altered."),
        )
    }

    @Test
    fun `a blank of several words is one gap`() {
        assertEquals(
            BlankSentence("Use ", " in case one layer fails."),
            BlankSentence.parse("Use ____ ____ in case one layer fails."),
        )
    }

    @Test
    fun `a sentence without a blank gets its gap at the end`() {
        assertEquals(BlankSentence("The answer is ", ""), BlankSentence.parse("The answer is"))
    }
}
