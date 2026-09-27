package com.paulchibamba.margin.feature.feed.post

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.semantics.AccessibilityAction
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.PostContent
import com.paulchibamba.margin.feature.feed.FeedPage
import com.paulchibamba.margin.feature.feed.FeedPostPage
import com.paulchibamba.margin.feature.feed.FeedPreviewData
import com.paulchibamba.margin.feature.feed.PostBodyCallbacks
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TestPostAnswerTest {

    @get:Rule
    val compose = createComposeRule()

    private val responses = mutableListOf<TestResponse>()

    private val mcq = PostContent.Mcq(
        title = "Stored XSS",
        question = "Which control stops stored XSS?",
        options = listOf("Length limit", "Output encoding", "HTTPS", "Block script"),
        answerIndex = 1,
        explanation = "Encoding stops them all at output.",
    )

    @Test
    fun `before answering every option has identical semantics and style`() {
        show(mcq)

        val options = compose.onAllNodesWithTag(CHOICE_OPTION_TAG).fetchSemanticsNodes()
        assertEquals(4, options.size)
        assertEquals(1, options.map(::semanticsWithoutText).distinct().size)
        assertEquals(1, optionImages().map(::backgroundOf).distinct().size)
    }

    @Test
    fun `after answering the right and the wrong option look different from the rest`() {
        show(mcq)

        compose.onNodeWithText("Block script").performClick()

        val options = compose.onAllNodesWithTag(CHOICE_OPTION_TAG).fetchSemanticsNodes()
        assertEquals(3, options.map(::semanticsWithoutText).distinct().size)
        val backgrounds = optionImages().map(::backgroundOf)
        assertNotEquals(backgrounds[0], backgrounds[1])
        assertNotEquals(backgrounds[1], backgrounds[3])
        assertEquals(backgrounds[0], backgrounds[2])
    }

    @Test
    fun `a wrong pick marks the right answer and explains it`() {
        show(mcq)

        compose.onNodeWithText("Block script").performClick()

        assertEquals(listOf<TestResponse>(TestResponse.Choice(3)), responses)
        compose.onNodeWithText("Encoding stops them all at output.").assertIsDisplayed()
        val right = compose.onAllNodesWithTag(CHOICE_OPTION_TAG)[1].fetchSemanticsNode()
        assertEquals("Right answer", right.config[SemanticsProperties.StateDescription])
    }

    @Test
    fun `a true or false statement is answered by swiping left for false`() {
        show(PostContent.TrueFalse("SameSite", "Lax cookies go on a cross-site POST.", false, "They do not."))

        compose.onNodeWithTag(SWIPE_CARD_TAG).performTouchInput { swipeLeft() }

        assertEquals(listOf<TestResponse>(TestResponse.Verdict(saysTrue = false)), responses)
        compose.onNodeWithText("Right. It's false.").assertIsDisplayed()
    }

    @Test
    fun `a true or false statement can be answered with the buttons`() {
        show(PostContent.TrueFalse("SameSite", "Lax cookies go on a cross-site POST.", false, "They do not."))

        compose.onNodeWithText("True").performClick()

        assertEquals(listOf<TestResponse>(TestResponse.Verdict(saysTrue = true)), responses)
        compose.onNodeWithText("Not quite. It's false.").assertIsDisplayed()
    }

    @Test
    fun `a recall reveals its answer then offers self-grades with the scheduler intervals`() {
        show(PostContent.Recall("Factors", "Name the three factors.", "Know, have, are."))

        compose.onNodeWithText("Tap to reveal").performClick()
        compose.onNodeWithText("Know, have, are.").assertIsDisplayed()
        compose.onNodeWithText("6m").assertIsDisplayed()
        compose.onNodeWithText("Hard").performClick()

        assertEquals(listOf<TestResponse>(TestResponse.SelfGrade(Rating.HARD)), responses)
    }

    @Test
    fun `a fill the gap shows the answer in the sentence once revealed`() {
        show(PostContent.FillBlank("Gap", "____ means data was not altered.", "Integrity", null))

        compose.onNodeWithText("Tap to fill the gap").performClick()

        compose.onNodeWithText("Integrity means data was not altered.").assertIsDisplayed()
        compose.onNodeWithText("Missed it").assertIsDisplayed()
    }

    private fun show(content: PostContent) {
        var page by mutableStateOf(pageOf(content))
        val callbacks = PostBodyCallbacks(onRespond = { response ->
            responses += response
            page = page.copy(answer = FeedPreviewData.answerTo(content, response))
        })
        compose.setContent {
            Box(Modifier.requiredSize(360.dp, 703.dp)) { FeedPostPage(page, 7, null, {}, callbacks, {}, {}) }
        }
    }

    private fun pageOf(content: PostContent): FeedPage =
        FeedPreviewData.page(Skins.Ink, content, CandidateSource.NEW, readingAhead = null)

    private fun optionImages(): List<ImageBitmap> {
        compose.waitForIdle()
        val count = compose.onAllNodesWithTag(CHOICE_OPTION_TAG).fetchSemanticsNodes().size
        return (0 until count).map { index -> optionAt(index).captureToImage() }
    }

    private fun optionAt(index: Int): SemanticsNodeInteraction = compose.onAllNodesWithTag(CHOICE_OPTION_TAG)[index]

    private fun semanticsWithoutText(node: SemanticsNode): Map<String, Any?> = node.config
        .filter { (key, _) -> key != SemanticsProperties.Text }
        .associate { (key, value) -> key.name to labelOf(value) }

    private fun labelOf(value: Any?): Any? = if (value is AccessibilityAction<*>) "action ${value.label}" else value

    private fun backgroundOf(image: ImageBitmap): Set<Int> {
        val pixels = image.toPixelMap()
        val y = BACKGROUND_ROW_PX
        return (image.width / 4 until image.width * 3 / 4).map { x -> pixels[x, y].hashCode() }.toSet() +
            pixels[image.width - EDGE_INSET_PX, image.height / 2].hashCode()
    }

    private companion object {
        const val BACKGROUND_ROW_PX = 6
        const val EDGE_INSET_PX = 40
    }
}
