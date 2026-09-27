package com.paulchibamba.margin.feature.feed.post

import org.junit.Test
import kotlin.test.assertEquals

class TipTextTest {

    @Test
    fun `the last sentence of a longer tip becomes the callout`() {
        val tip = TipText.of("Client checks are for UX. Proxies skip them. Validate again on the server.")

        assertEquals("Client checks are for UX. Proxies skip them.", tip.body)
        assertEquals("Validate again on the server.", tip.callout)
    }

    @Test
    fun `a one-sentence tip is all callout`() {
        assertEquals(TipText(body = null, callout = "Always salt passwords."), TipText.of(" Always salt passwords. "))
    }

    @Test
    fun `a full stop inside a name or number does not split the tip`() {
        assertEquals(null, TipText.of("Use v2.1 of node.js for this.").body)
    }
}
