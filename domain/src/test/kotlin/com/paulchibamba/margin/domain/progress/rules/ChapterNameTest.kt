package com.paulchibamba.margin.domain.progress.rules

import kotlin.test.Test
import kotlin.test.assertEquals

class ChapterNameTest {

    @Test
    fun `a chapter title loses its chapter number prefix`() {
        assertEquals("Security Fundamentals", ChapterName.of("CHAPTER 1: Security Fundamentals"))
        assertEquals("Browsers", ChapterName.of("3. Browsers"))
    }

    @Test
    fun `a title without a prefix, or with nothing after it, stays as it is`() {
        assertEquals("Security Fundamentals", ChapterName.of("Security Fundamentals"))
        assertEquals("Chapter 4", ChapterName.of("Chapter 4"))
    }
}
