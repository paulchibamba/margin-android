package com.paulchibamba.margin.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.MultiContentMeasurePolicy
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize

@Composable
fun FeedLayout(
    topBar: @Composable () -> Unit,
    body: @Composable () -> Unit,
    caption: @Composable () -> Unit,
    rail: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    bodyPlacement: FeedBodyPlacement = FeedBodyPlacement.BesideRail,
    isSnackbarVisible: Boolean = false,
    snackbar: @Composable () -> Unit = {},
) {
    val railLift by animateFloatAsState(if (isSnackbarVisible) 1f else 0f, label = "rail lift")
    Layout(
        contents = listOf(
            { Box(Modifier.statusBarsPadding()) { topBar() } },
            body,
            { AnimatedVisibility(!isSnackbarVisible, enter = fadeIn(), exit = fadeOut()) { caption() } },
            rail,
            { AnimatedVisibility(isSnackbarVisible, enter = snackbarEnter, exit = snackbarExit) { snackbar() } },
        ),
        modifier = modifier.fillMaxSize(),
        measurePolicy = FeedLayoutMeasurePolicy(bodyPlacement, railLift),
    )
}

private val snackbarEnter = fadeIn() + slideInVertically { it / 2 }
private val snackbarExit = fadeOut() + slideOutVertically { it / 2 }

private class FeedLayoutMeasurePolicy(
    private val bodyPlacement: FeedBodyPlacement,
    private val railLiftFraction: Float,
) : MultiContentMeasurePolicy {

    override fun MeasureScope.measure(measurables: List<List<Measurable>>, constraints: Constraints): MeasureResult {
        val screen = IntSize(constraints.maxWidth, constraints.maxHeight)
        val chrome = measureChrome(measurables, screen)
        val geometry = geometry(screen, chrome.topBar, chrome.rail, chrome.caption.heightOrZero(),
            chrome.snackbar.heightOrZero())
        val bodyBounds = geometry.bodyBounds(bodyPlacement)
        val body = measurables[BODY].measureFirst(Constraints.fixed(bodyBounds.width, bodyBounds.height))
        return layout(screen.width, screen.height) {
            placeAt(body, bodyBounds)
            placeAt(chrome.caption, geometry.captionBounds)
            placeAt(chrome.snackbar, geometry.snackbarBounds)
            placeAt(chrome.rail, geometry.railBounds)
            chrome.topBar?.place(0, 0)
        }
    }

    private fun MeasureScope.measureChrome(measurables: List<List<Measurable>>, screen: IntSize): MeasuredChrome {
        val loose = Constraints(maxWidth = screen.width, maxHeight = screen.height)
        val topBar = measurables[TOP_BAR].measureFirst(loose)
        val rail = measurables[RAIL].measureFirst(loose)
        val widths = geometry(screen, topBar, rail, captionHeight = 0, snackbarHeight = 0)
        val caption = measurables[CAPTION].measureFirst(Constraints(maxWidth = widths.captionWidth))
        val snackbar = measurables[SNACKBAR].measureFirst(Constraints(maxWidth = widths.snackbarWidth))
        return MeasuredChrome(topBar, rail, caption, snackbar)
    }

    private fun MeasureScope.geometry(
        screen: IntSize,
        topBar: Placeable?,
        rail: Placeable?,
        captionHeight: Int,
        snackbarHeight: Int,
    ) = FeedLayoutGeometry(
        density = this,
        screen = screen,
        topBarHeight = topBar.heightOrZero(),
        rail = IntSize(rail?.width ?: 0, rail.heightOrZero()),
        captionHeight = captionHeight,
        snackbarHeight = snackbarHeight,
        railLiftFraction = railLiftFraction,
    )

    private fun Placeable.PlacementScope.placeAt(placeable: Placeable?, bounds: IntRect) {
        placeable?.place(bounds.left, bounds.top)
    }

    private fun List<Measurable>.measureFirst(constraints: Constraints): Placeable? =
        firstOrNull()?.measure(constraints)

    private fun Placeable?.heightOrZero(): Int = this?.height ?: 0

    private class MeasuredChrome(
        val topBar: Placeable?,
        val rail: Placeable?,
        val caption: Placeable?,
        val snackbar: Placeable?,
    )

    private companion object {
        const val TOP_BAR = 0
        const val BODY = 1
        const val CAPTION = 2
        const val RAIL = 3
        const val SNACKBAR = 4
    }
}
