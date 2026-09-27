package com.paulchibamba.margin.feature.feed.post

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.model.PostContent

@Composable
fun McqPost(
    content: PostContent.Mcq,
    state: TestPostState,
    onRespond: (TestResponse) -> Unit,
    modifier: Modifier = Modifier,
) {
    PostColumn(modifier, top = TEST_POST_TOP, spacing = TEST_POST_SPACING) {
        QuizQuestion(content.question)
        ChoiceOptions(content, state.chosenIndex, onChoose = { index -> onRespond(TestResponse.Choice(index)) })
    }
}

private val previewMcq = PostContent.Mcq(
    title = "Stored XSS",
    question = "Which control stops stored XSS in an HTML body?",
    options = listOf("Input length limit", "Context-aware output encoding", "HTTPS everywhere", "Block <script>"),
    answerIndex = 1,
    explanation = "Filters miss <img onerror> and hundreds of other vectors. Encoding stops them all at output.",
)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun McqPostInkPreview() = PostPagePreview(Skins.Ink, previewMcq)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun McqPostWrongPreview() = PostPagePreview(Skins.Ink, previewMcq, response = TestResponse.Choice(3))

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun McqPostPaperPreview() = PostPagePreview(Skins.Paper, previewMcq, response = TestResponse.Choice(1))
