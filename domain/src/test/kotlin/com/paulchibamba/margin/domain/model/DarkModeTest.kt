package com.paulchibamba.margin.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DarkModeTest {

    @Test
    fun `off is never dark, whatever the system theme`() {
        assertFalse(DarkMode.OFF.isDark(isSystemDark = false))
        assertFalse(DarkMode.OFF.isDark(isSystemDark = true))
    }

    @Test
    fun `always is dark, whatever the system theme`() {
        assertTrue(DarkMode.ALWAYS.isDark(isSystemDark = false))
        assertTrue(DarkMode.ALWAYS.isDark(isSystemDark = true))
    }

    @Test
    fun `follow system is dark only when the system theme is`() {
        assertFalse(DarkMode.FOLLOW_SYSTEM.isDark(isSystemDark = false))
        assertTrue(DarkMode.FOLLOW_SYSTEM.isDark(isSystemDark = true))
    }

    @Test
    fun `off is the default, and names that are unknown or missing read as the default`() {
        assertEquals(DarkMode.OFF, DarkMode.DEFAULT)
        assertEquals(DarkMode.ALWAYS, DarkMode.fromName("ALWAYS"))
        assertEquals(DarkMode.OFF, DarkMode.fromName("SEPIA"))
        assertEquals(DarkMode.OFF, DarkMode.fromName(null))
    }
}
