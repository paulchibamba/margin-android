package com.paulchibamba.margin.feature.settings

import org.junit.Test
import kotlin.test.assertEquals

class SettingsLabelsTest {

    @Test
    fun `the active count reads against the maximum`() {
        assertEquals("2 of 3 max", activeBooksLabel(activeCount = 2, maxActive = 3))
    }

    @Test
    fun `retention always shows two decimal places`() {
        assertEquals("0.80", retentionLabel(0.8))
        assertEquals("0.93", retentionLabel(0.93))
    }

    @Test
    fun `the workload warning names its threshold`() {
        assertEquals("Above 0.90, reviews rise steeply.", workloadWarning(0.9))
    }
}
