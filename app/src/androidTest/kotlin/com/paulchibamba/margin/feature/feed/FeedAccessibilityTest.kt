package com.paulchibamba.margin.feature.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.model.PostContent
import com.paulchibamba.margin.feature.feed.post.CHOICE_OPTION_TAG
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FeedAccessibilityTest {

    @get:Rule
    val compose = createComposeRule()

    private val mcq = PostContent.Mcq(
        title = "Stored XSS",
        question = "Which control stops stored XSS?",
        options = listOf("Length limit", "Output encoding", "HTTPS", "Block script"),
        answerIndex = 1,
        explanation = "Encoding stops them all at output.",
    )

    @Test
    fun `the streak chip is read once as a sentence`() {
        show(mcq)

        val chip = compose.onNodeWithContentDescription("7 day streak").fetchSemanticsNode()

        assertNull(chip.config.getOrNull(SemanticsProperties.Text))
    }

    @Test
    fun `rail toggles report their state and Read does not`() {
        show(mcq)

        val save = compose.onNodeWithContentDescription("Save").fetchSemanticsNode()
        val read = compose.onNodeWithContentDescription("Read").fetchSemanticsNode()

        assertEquals(false, save.config.getOrNull(SemanticsProperties.Selected))
        assertNull(read.config.getOrNull(SemanticsProperties.Selected))
    }

    @Test
    fun `a quiz answer announces the right and the wrong option and locks the rest`() {
        show(mcq)
        compose.onRoot().printToLog(WALK_THROUGH_TAG)

        compose.onNodeWithText("Block script").performClick()
        compose.onRoot().printToLog(WALK_THROUGH_TAG)

        val options = compose.onAllNodesWithTag(CHOICE_OPTION_TAG)
        assertEquals("Right answer", options[1].fetchSemanticsNode().config[SemanticsProperties.StateDescription])
        assertEquals("Your answer, wrong", options[3].fetchSemanticsNode().config[SemanticsProperties.StateDescription])
        (0 until 4).forEach { index -> options[index].assertIsNotEnabled() }
    }

    private fun show(content: PostContent) {
        var page by mutableStateOf(pageOf(content))
        val callbacks = PostBodyCallbacks(onRespond = { response ->
            page = page.copy(answer = FeedPreviewData.answerTo(content, response))
        })
        compose.setContent {
            Box(Modifier.requiredSize(360.dp, 780.dp)) { FeedPostPage(page, 7, null, {}, callbacks, {}, {}) }
        }
    }

    private fun pageOf(content: PostContent): FeedPage =
        FeedPreviewData.page(Skins.Ink, content, CandidateSource.REVIEW, readingAhead = null)

    private companion object {
        const val WALK_THROUGH_TAG = "FeedTalkBack"
    }
}
