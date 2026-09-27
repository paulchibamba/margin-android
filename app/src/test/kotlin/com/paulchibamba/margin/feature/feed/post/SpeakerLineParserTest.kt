package com.paulchibamba.margin.feature.feed.post

import org.junit.Test
import kotlin.test.assertEquals

class SpeakerLineParserTest {

    @Test
    fun `a line starting with a name and a colon is spoken by that name`() {
        assertEquals(SpeakerLine("Bob", "Is this fine?"), SpeakerLineParser.parse("Bob: Is this fine?"))
    }

    @Test
    fun `a versus side keeps its bracketed label`() {
        val line = SpeakerLineParser.parse("Authentication (AuthN): 'Who are you?' - verifies identity.")

        assertEquals(SpeakerLine("Authentication (AuthN)", "'Who are you?' - verifies identity."), line)
    }

    @Test
    fun `a line without a speaker is all text`() {
        assertEquals(SpeakerLine(null, "Just a sentence."), SpeakerLineParser.parse("  Just a sentence. "))
    }

    @Test
    fun `a colon after a full sentence is not a speaker`() {
        assertEquals(null, SpeakerLineParser.parse("It ends here. Then: more").speaker)
    }

    @Test
    fun `a colon inside a time or url is not a speaker`() {
        assertEquals(null, SpeakerLineParser.parse("Meet at 10:30 today").speaker)
        assertEquals(null, SpeakerLineParser.parse("See https://example.com for more").speaker)
    }

    @Test
    fun `a very long prefix is not a speaker`() {
        val prefix = "A".repeat(41)

        assertEquals(null, SpeakerLineParser.parse("$prefix: text").speaker)
    }
}
