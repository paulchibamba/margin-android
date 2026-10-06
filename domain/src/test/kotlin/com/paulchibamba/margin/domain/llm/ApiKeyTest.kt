package com.paulchibamba.margin.domain.llm

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class ApiKeyTest {

    @Test
    fun `a pasted key is trimmed`() {
        assertEquals(ApiKey(FAKE_KEY), ApiKey.parse("  $FAKE_KEY\n"))
    }

    @Test
    fun `blank text or text with spaces is not a key`() {
        assertNull(ApiKey.parse("   "))
        assertNull(ApiKey.parse("sk-one two"))
    }

    @Test
    fun `the masked key shows only the last four characters`() {
        assertEquals("••••WXYZ", ApiKey(FAKE_KEY).masked)
    }

    @Test
    fun `printing a key never shows it`() {
        val key = ApiKey(FAKE_KEY)

        assertFalse(key.toString().contains(FAKE_KEY))
        assertFalse("$key".contains("sk-test"))
    }

    @Test
    fun `a rejected key is not echoed in the error`() {
        val error = runCatching { ApiKey("sk-test bad") }.exceptionOrNull()

        assertFalse(error?.message.orEmpty().contains("sk-test"))
    }

    private companion object {
        const val FAKE_KEY = "sk-test-0123456789abcdefWXYZ"
    }
}
