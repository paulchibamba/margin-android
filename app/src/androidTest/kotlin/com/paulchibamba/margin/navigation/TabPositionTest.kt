package com.paulchibamba.margin.navigation

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToNode
import com.paulchibamba.margin.MainActivity
import org.junit.Rule
import org.junit.Test

class TabPositionTest {

    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun `Read keeps its scroll position after switching to Feed and back`() {
        selectTab("Read")
        scrollTo("Row 40")
        selectTab("Feed")
        selectTab("Read")
        compose.onNodeWithText("Row 40").assertIsDisplayed()
    }

    @Test
    fun `Feed keeps its page after switching to Read and back`() {
        compose.waitUntil(IMPORT_TIMEOUT_MILLIS) {
            compose.onAllNodes(hasTestTag("feed-page-1")).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithTag("feed-pager").performScrollToIndex(1)
        selectTab("Read")
        selectTab("Feed")
        compose.onNodeWithTag("feed-page-1").assertIsDisplayed()
    }

    @Test
    fun `the book stays open in Read after visiting Feed`() {
        selectTab("Read")
        compose.onNodeWithText("Open a book").performClick()
        selectTab("Feed")
        selectTab("Read")
        compose.onNodeWithText("Book · placeholder-book").assertIsDisplayed()
    }

    private companion object {
        const val IMPORT_TIMEOUT_MILLIS = 15_000L
    }

    private fun selectTab(label: String) {
        compose.onNode(hasText(label) and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab))
            .performClick()
        compose.waitForIdle()
    }

    private fun scrollTo(text: String) {
        compose.onNode(hasScrollAction()).performScrollToNode(hasText(text))
    }
}
