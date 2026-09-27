package com.paulchibamba.margin.designsystem.component

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FeedLayoutGeometryTest {

    private val density = Density(3.5f)

    @Test
    fun `the body never overlaps the rail at 360 and 430 dp, beside or above it, with or without a snackbar`() {
        allLayouts().forEach { (name, geometry) ->
            FeedBodyPlacement.entries.forEach { placement ->
                assertApart(geometry.bodyBounds(placement), geometry.railBounds, "$name $placement body")
            }
        }
    }

    @Test
    fun `the caption and the snackbar never overlap the rail`() {
        allLayouts().forEach { (name, geometry) ->
            assertApart(geometry.captionBounds, geometry.railBounds, "$name caption")
            assertApart(geometry.snackbarBounds, geometry.railBounds, "$name snackbar")
        }
    }

    @Test
    fun `the rail stays on screen`() {
        allLayouts().forEach { (name, geometry) ->
            val rail = geometry.railBounds
            assertTrue(rail.left >= 0 && rail.top >= 0, "$name rail is at $rail")
        }
    }

    @Test
    fun `the rail rises 14 dp above a shown snackbar`() {
        val geometry = geometryAt(widthDp = 360, snackbarDp = 62)
        assertEquals(dp(14), geometry.snackbarBounds.top - geometry.railBounds.bottom)
    }

    @Test
    fun `the caption sits 16 dp from the left and ends 76 dp from the right`() {
        val geometry = geometryAt(widthDp = 360, snackbarDp = 0)
        assertEquals(dp(16), geometry.captionBounds.left)
        assertEquals(dp(360 - 76), geometry.captionBounds.right)
    }

    @Test
    fun `halfway through the snackbar animation the rail is between its two positions`() {
        val resting = geometryAt(widthDp = 360, snackbarDp = 62, railLift = 0f).railBounds.bottom
        val halfway = geometryAt(widthDp = 360, snackbarDp = 62, railLift = 0.5f).railBounds.bottom
        val raised = geometryAt(widthDp = 360, snackbarDp = 62, railLift = 1f).railBounds.bottom
        assertTrue(halfway in (raised + 1) until resting, "$raised < $halfway < $resting")
    }

    private fun allLayouts(): List<Pair<String, FeedLayoutGeometry>> =
        listOf(360 to 703, 430 to 855).flatMap { (width, height) ->
            listOf(0, 62).map { snackbar ->
                "${width}dp snackbar=$snackbar" to geometryAt(width, snackbar, heightDp = height)
            }
        }

    private fun geometryAt(widthDp: Int, snackbarDp: Int, heightDp: Int = 703, railLift: Float? = null) =
        FeedLayoutGeometry(
            density = density,
            screen = IntSize(dp(widthDp), dp(heightDp)),
            topBarHeight = dp(80),
            rail = IntSize(dp(56), dp(304)),
            captionHeight = dp(105),
            snackbarHeight = dp(snackbarDp),
            railLiftFraction = railLift ?: if (snackbarDp > 0) 1f else 0f,
        )

    private fun assertApart(first: IntRect, second: IntRect, what: String) {
        assertFalse(first.overlaps(second), "$what $first overlaps the rail $second")
    }

    private fun dp(value: Int): Int = (value * density.density).toInt()
}
