package com.paulchibamba.margin.domain.rollup

import com.paulchibamba.margin.domain.model.Format
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PostRollupTest {
    private val posts = PostRollup.of(RollupFixture.day)

    @Test
    fun `posts seen come from first impressions, not revisits`() {
        assertEquals(3, posts.postsSeen)
        assertEquals(4, posts.exposures)
    }

    @Test
    fun `a glance is under one and a half seconds and a deep read reaches eight tenths of the expected time`() {
        assertEquals(1, posts.glances)
        assertEquals(2, posts.deepReads)
        assertEquals(0.25, posts.glanceRate)
        assertEquals(0.5, posts.deepRate)
    }

    @Test
    fun `the attention ratio per format is the median, strongest first`() {
        val expected = mapOf(Format.MCQ to FormatAttention(1.0, 1), Format.FACT to FormatAttention(0.5, 3))

        assertEquals(expected, posts.ratioByFormat)
        assertEquals(listOf(Format.MCQ, Format.FACT), posts.ratioByFormat.keys.toList())
    }

    @Test
    fun `a day without posts has no rates`() {
        val empty = PostRollup.of(DayEvents(emptyList(), ZoneOffset.UTC))

        assertNull(empty.glanceRate)
        assertNull(empty.deepRate)
    }
}
