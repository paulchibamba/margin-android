package com.paulchibamba.margin.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.Skins
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertFalse

class FeedLayoutTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun `the body and caption stay clear of the rail at 360 dp`() =
        assertChromeApart(widthDp = 360, heightDp = 703, isSnackbarVisible = false)

    @Test
    fun `the body and caption stay clear of the rail at 430 dp`() =
        assertChromeApart(widthDp = 430, heightDp = 855, isSnackbarVisible = false)

    @Test
    fun `the body and snackbar stay clear of the raised rail at 360 dp`() =
        assertChromeApart(widthDp = 360, heightDp = 703, isSnackbarVisible = true)

    @Test
    fun `the body and snackbar stay clear of the raised rail at 430 dp`() =
        assertChromeApart(widthDp = 430, heightDp = 855, isSnackbarVisible = true)

    @Test
    fun `every rail action has a content description`() {
        compose.setContent { MarginTheme(Skins.Ink) { ActionRail(ActionRailState(), onAction = {}) } }
        listOf("Got it", "Lost", "Read", "Save", "Less").forEach { label ->
            compose.onAllNodesWithContentDescription(label).assertCountEquals(1)
            compose.onNodeWithContentDescription(label)
                .assert(SemanticsMatcher.keyIsDefined(SemanticsActions.OnClick))
        }
    }

    private fun assertChromeApart(widthDp: Int, heightDp: Int, isSnackbarVisible: Boolean) {
        compose.setContent {
            MarginTheme(Skins.Ink) {
                Box(Modifier.requiredSize(widthDp.dp, heightDp.dp)) { TestFeed(isSnackbarVisible) }
            }
        }
        compose.waitForIdle()
        assertApart("body", "rail")
        assertApart(if (isSnackbarVisible) "snackbar" else "caption", "rail")
    }

    @Composable
    private fun TestFeed(isSnackbarVisible: Boolean) {
        FeedLayout(
            topBar = { FeedTopBar(streak = 7, segments = null, onMoreClick = {}) },
            body = { Box(Modifier.fillMaxSize().testTag("body")) },
            caption = {
                Box(Modifier.testTag("caption")) {
                    PostCaption("Tip", "Server-side validation", "Alice & Bob Learn AppSec", "Ch 3 · Input",
                        12, 48, isReview = true, isPreview = true)
                }
            },
            rail = { Box(Modifier.testTag("rail")) { ActionRail(ActionRailState(), onAction = {}) } },
            isSnackbarVisible = isSnackbarVisible,
            snackbar = {
                Box(Modifier.testTag("snackbar")) {
                    FeedSnackbar("You'll see this again in ~10 min", "Alice & Bob · Ch 4", "Read page", {})
                }
            },
        )
    }

    private fun assertApart(first: String, second: String) {
        val a = boundsOf(first)
        val b = boundsOf(second)
        assertFalse(a.overlaps(b), "$first $a overlaps $second $b")
    }

    private fun boundsOf(tag: String): Rect {
        val bounds = compose.onNodeWithTag(tag).getBoundsInRoot()
        return Rect(bounds.left.value, bounds.top.value, bounds.right.value, bounds.bottom.value)
    }
}
