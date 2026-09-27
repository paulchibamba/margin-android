package com.paulchibamba.margin.feature.feed.post

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.centerRight
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.model.PostContent
import com.paulchibamba.margin.feature.feed.FeedPage
import com.paulchibamba.margin.feature.feed.FeedPostPage
import com.paulchibamba.margin.feature.feed.FeedPreviewData
import com.paulchibamba.margin.feature.feed.FeedScreen
import com.paulchibamba.margin.feature.feed.FeedUiState
import com.paulchibamba.margin.feature.feed.PostBodyCallbacks
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class InteractivePostEngagementTest {

    @get:Rule
    val compose = createComposeRule()

    private var engagedCount = 0

    private val carousel = PostContent.Carousel("Three factors", listOf("Know", "Have", "Are"))

    @Test
    fun `a carousel is engaged only once its last slide is reached`() {
        show(carousel)

        tapRightOfBody()
        assertEquals(0, engagedCount)
        tapRightOfBody()

        compose.onNodeWithText("3 / 3").assertIsDisplayed()
        assertEquals(1, engagedCount)
    }

    @Test
    fun `a myth is engaged when its reality is revealed`() {
        show(PostContent.Myth("Myth: padlocks", "Myth: The padlock means safe.", "Reality: It means encrypted."))
        assertEquals(0, engagedCount)

        compose.onNodeWithText("Reveal the reality").performClick()

        compose.onNodeWithText("It means encrypted.").assertIsDisplayed()
        assertEquals(1, engagedCount)
    }

    @Test
    fun `a checklist is engaged by its first tick and not by unticking`() {
        show(PostContent.Checklist("Before you ship", listOf("Rate-limit logins.", "Hash passwords.")))
        assertEquals(0, engagedCount)

        compose.onNodeWithText("Rate-limit logins.").performClick()
        compose.onNodeWithText("Rate-limit logins.").performClick()

        assertEquals(1, engagedCount)
    }

    @Test
    fun `a dialogue shows every line and has nothing to engage with`() {
        show(PostContent.Dialogue("Session IDs", listOf("Bob: Keep the same ID?", "Alice: No, issue a new one.")))

        compose.onNodeWithContentDescription("Bob: Keep the same ID?").assertIsDisplayed()
        compose.onNodeWithContentDescription("Alice: No, issue a new one.").assertIsDisplayed()
        assertEquals(0, engagedCount)
    }

    @Test
    fun `a versus shows both sides and has nothing to engage with`() {
        show(PostContent.Versus("AuthN vs AuthZ", "Authentication: who are you?", "Authorization: may you?"))

        compose.onNodeWithText("Authentication").assertIsDisplayed()
        compose.onNodeWithText("may you?").assertIsDisplayed()
        assertEquals(0, engagedCount)
    }

    @Test
    fun `tapping through a carousel does not page the feed but swiping up does`() {
        val pages = listOf(pageOf(carousel), pageOf(PostContent.Tip("Next", "The next post.")))
        lateinit var pagerState: PagerState
        compose.setContent {
            pagerState = rememberPagerState { pages.size }
            Box(Modifier.requiredSize(360.dp, 703.dp)) { FeedScreenOf(FeedUiState(pages = pages), pagerState) }
        }

        tapRightOfBody()
        compose.onNodeWithText("2 / 3").assertIsDisplayed()
        assertEquals(0, pagerState.currentPage)

        compose.onNodeWithTag("feed-pager").performTouchInput { swipeUp() }
        compose.waitForIdle()
        assertEquals(1, pagerState.currentPage)
    }

    private fun show(content: PostContent) {
        compose.setContent {
            Box(Modifier.requiredSize(360.dp, 703.dp)) {
                FeedPostPage(pageOf(content), 7, null, {}, PostBodyCallbacks(onEngaged = { engagedCount++ }), {}, {})
            }
        }
    }

    private fun tapRightOfBody() {
        compose.onAllNodesWithTag(POST_BODY_TAG)[0].performTouchInput { click(centerRight - Offset(20f, 0f)) }
        compose.waitForIdle()
    }

    private fun pageOf(content: PostContent): FeedPage =
        FeedPreviewData.page(Skins.Ink, content, CandidateSource.NEW, readingAhead = null)
}

@Composable
private fun FeedScreenOf(state: FeedUiState, pagerState: PagerState) {
    FeedScreen(
        state = state,
        pagerState = pagerState,
        onPageEntered = {},
        onPageLeft = { _, _ -> },
        onAction = { _, _ -> },
        onReadAhead = {},
        onEngaged = {},
        onReadOn = {},
        onMore = {},
        onSheetDismiss = {},
        onNudgeDismiss = {},
        onCaughtUpShown = {},
    )
}
