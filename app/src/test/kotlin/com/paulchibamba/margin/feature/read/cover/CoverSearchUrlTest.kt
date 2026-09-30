package com.paulchibamba.margin.feature.read.cover

import org.junit.Test
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import kotlin.test.assertEquals

class CoverSearchUrlTest {

    @Test
    fun `the search is a safe https image search for the book's cover`() {
        val uri = URI(CoverSearchUrl.of("Designing Data-Intensive Applications (2nd ed.) & more"))
        val query = uri.rawQuery.split("&").associate { part -> part.substringBefore("=") to part.substringAfter("=") }

        assertEquals("https", uri.scheme)
        assertEquals("duckduckgo.com", uri.host)
        assertEquals("images", query["iax"])
        assertEquals("1", query["kp"])
        assertEquals(
            "Designing Data-Intensive Applications (2nd ed.) & more book cover",
            URLDecoder.decode(query["q"], StandardCharsets.UTF_8.name()),
        )
    }
}
