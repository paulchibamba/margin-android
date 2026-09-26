package com.paulchibamba.margin.navigation

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import org.junit.Rule
import org.junit.Test

class TabPositionTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun `Read keeps its scroll position after switching to Feed and back`() {
        compose.setContent { MarginApp() }
        selectTab("Read")
        scrollTo("Row 40")
        selectTab("Feed")
        selectTab("Read")
        compose.onNodeWithText("Row 40").assertIsDisplayed()
    }

    @Test
    fun `Feed keeps its scroll position after switching to Read and back`() {
        compose.setContent { MarginApp() }
        scrollTo("Row 50")
        selectTab("Read")
        selectTab("Feed")
        compose.onNodeWithText("Row 50").assertIsDisplayed()
    }

    @Test
    fun `the book stays open in Read after visiting Feed`() {
        compose.setContent { MarginApp() }
        selectTab("Read")
        compose.onNodeWithText("Open a book").performClick()
        selectTab("Feed")
        selectTab("Read")
        compose.onNodeWithText("Book · placeholder-book").assertIsDisplayed()
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
