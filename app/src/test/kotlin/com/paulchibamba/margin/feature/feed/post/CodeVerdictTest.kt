package com.paulchibamba.margin.feature.feed.post

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CodeVerdictTest {

    @Test
    fun `a caption that calls the code vulnerable marks it unsafe`() {
        assertEquals(CodeVerdict.UNSAFE, CodeVerdict.of("String-built SQL", "This query is vulnerable to injection."))
    }

    @Test
    fun `a title about the safe way marks it safe`() {
        assertEquals(CodeVerdict.SAFE, CodeVerdict.of("The safe way: parameterized query", caption = null))
    }

    @Test
    fun `unsafe is not read as safe`() {
        assertEquals(CodeVerdict.UNSAFE, CodeVerdict.of("Unsafe concatenation", caption = null))
    }

    @Test
    fun `code that says nothing either way has no tag`() {
        assertNull(CodeVerdict.of("Loading a model", "Load a pretrained classifier first."))
    }
}
