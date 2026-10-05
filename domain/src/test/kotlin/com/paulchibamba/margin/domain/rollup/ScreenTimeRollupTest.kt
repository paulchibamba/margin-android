package com.paulchibamba.margin.domain.rollup

import com.paulchibamba.margin.domain.screentime.AppCategory
import com.paulchibamba.margin.domain.screentime.AppScreenTime
import com.paulchibamba.margin.domain.screentime.PackageName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.minutes

class ScreenTimeRollupTest {

    private fun app(label: String, minutes: Int, isDoom: Boolean, category: AppCategory = AppCategory.OTHER) =
        AppScreenTime(PackageName(label.lowercase()), label, category, isDoom, minutes.minutes)

    @Test
    fun `a day without screen time has no screen-time metrics`() {
        assertNull(ScreenTimeRollup.of(emptyList()))
    }

    @Test
    fun `doom and margin minutes, margin share and the top three doom apps come from the day's apps`() {
        val apps = listOf(
            app("Margin", 15, isDoom = false, AppCategory.MARGIN),
            app("Notes", 15, isDoom = false),
            app("Reddit", 10, isDoom = true),
            app("Instagram", 50, isDoom = true),
            app("YouTube", 20, isDoom = true),
            app("Chess", 5, isDoom = true),
        )

        val metrics = ScreenTimeRollup.of(apps)!!

        assertEquals(115.minutes, metrics.screen)
        assertEquals(15.minutes, metrics.margin)
        assertEquals(85.minutes, metrics.doom)
        assertEquals(listOf("Instagram", "YouTube", "Reddit"), metrics.topDoomApps)
        assertEquals(0.15, metrics.marginShare)
    }

    @Test
    fun `margin share is unknown when there is neither margin nor doom time`() {
        val metrics = ScreenTimeRollup.of(listOf(app("Notes", 30, isDoom = false)))!!

        assertNull(metrics.marginShare)
    }
}
