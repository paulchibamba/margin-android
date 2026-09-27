package com.paulchibamba.margin.designsystem.component

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp

internal class FeedLayoutGeometry(
    private val density: Density,
    private val screen: IntSize,
    private val topBarHeight: Int,
    private val rail: IntSize,
    private val captionHeight: Int,
    private val snackbarHeight: Int,
    private val railLiftFraction: Float = if (snackbarHeight > 0) 1f else 0f,
) {
    private val chromeBottom = px(CHROME_BOTTOM_DP)

    val railBounds: IntRect
        get() {
            val bottom = screen.height - railLift()
            val left = screen.width - px(RAIL_END_DP) - rail.width
            return IntRect(left, bottom - rail.height, left + rail.width, bottom)
        }

    val captionBounds: IntRect
        get() {
            val bottom = screen.height - chromeBottom
            return IntRect(px(CAPTION_START_DP), bottom - captionHeight, captionRight(), bottom)
        }

    val snackbarBounds: IntRect
        get() {
            val bottom = screen.height - px(SNACKBAR_BOTTOM_DP)
            val side = px(SNACKBAR_SIDE_DP)
            return IntRect(side, bottom - snackbarHeight, screen.width - side, bottom)
        }

    val captionWidth: Int get() = captionRight() - px(CAPTION_START_DP)

    val snackbarWidth: Int get() = screen.width - 2 * px(SNACKBAR_SIDE_DP)

    fun bodyBounds(placement: FeedBodyPlacement): IntRect = when (placement) {
        FeedBodyPlacement.BesideRail -> besideRail()
        FeedBodyPlacement.AboveRail -> aboveRail()
    }

    private fun besideRail(): IntRect {
        val lowestChromeTop = if (snackbarHeight > 0) snackbarBounds.top else captionBounds.top
        val bottom = (lowestChromeTop - px(BODY_GAP_DP)).coerceAtLeast(topBarHeight)
        val right = (railBounds.left - px(BODY_GAP_DP)).coerceAtLeast(0)
        return IntRect(0, topBarHeight, right, bottom)
    }

    private fun aboveRail(): IntRect {
        val bottom = (railBounds.top - px(BODY_GAP_DP)).coerceAtLeast(topBarHeight)
        return IntRect(0, topBarHeight, screen.width, bottom)
    }

    private fun railLift(): Int {
        if (snackbarHeight == 0) return chromeBottom
        val aboveSnackbar = px(SNACKBAR_BOTTOM_DP) + snackbarHeight + px(RAIL_ABOVE_SNACKBAR_DP)
        return lerp(chromeBottom, aboveSnackbar, railLiftFraction.coerceIn(0f, 1f))
    }

    private fun captionRight(): Int = (screen.width - px(CAPTION_END_DP)).coerceAtLeast(px(CAPTION_START_DP))

    private fun px(dp: Int): Int = with(density) { dp.dp.roundToPx() }

    private companion object {
        const val CHROME_BOTTOM_DP = 19
        const val CAPTION_START_DP = 16
        const val CAPTION_END_DP = 76
        const val RAIL_END_DP = 6
        const val SNACKBAR_SIDE_DP = 12
        const val SNACKBAR_BOTTOM_DP = 15
        const val RAIL_ABOVE_SNACKBAR_DP = 14
        const val BODY_GAP_DP = 16
    }
}
