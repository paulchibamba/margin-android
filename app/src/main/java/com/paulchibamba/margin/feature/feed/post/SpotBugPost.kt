package com.paulchibamba.margin.feature.feed.post

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.model.PostContent

@Composable
fun SpotBugPost(
    content: PostContent.SpotBug,
    state: TestPostState,
    onRespond: (TestResponse) -> Unit,
    modifier: Modifier = Modifier,
) {
    PostColumn(modifier, top = TEST_POST_TOP, spacing = TEST_POST_SPACING) {
        CodeCard(content.code, verdict = null)
        QuizQuestion(content.question)
        ChoiceOptions(content, state.chosenIndex, onChoose = { index -> onRespond(TestResponse.Choice(index)) })
    }
}

private val previewSpotBug = PostContent.SpotBug(
    title = "Spot the bug",
    code = "fun find(id: String) {\n  val q = \"SELECT * FROM users WHERE id=\" + id\n  return db.raw(q)\n}",
    question = "What's the flaw?",
    options = listOf("The function name", "String concatenation into SQL", "Returning raw rows", "Nothing"),
    answerIndex = 1,
    explanation = "Concatenation lets the input change the query's shape. Use a bound parameter: WHERE id = ?",
)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun SpotBugPostPaperPreview() = PostPagePreview(Skins.Paper, previewSpotBug)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun SpotBugPostAnsweredPreview() = PostPagePreview(Skins.Ink, previewSpotBug, response = TestResponse.Choice(1))
