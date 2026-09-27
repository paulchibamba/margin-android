package com.paulchibamba.margin.feature.feed.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.PostContent

private val AnswerShape = RoundedCornerShape(20.dp)

@Composable
fun RecallPost(
    content: PostContent.Recall,
    state: TestPostState,
    onReveal: () -> Unit,
    onRespond: (TestResponse) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isRevealed by rememberSaveable(content) { mutableStateOf(false) }
    PostColumn(modifier, top = TEST_POST_TOP, spacing = 16.dp) {
        Text(content.question, style = MarginTypography.prompt, color = LocalSkin.current.content)
        if (isRevealed || state.isAnswered) {
            RecallAnswer(content.answer)
            SelfGradeButtons(state.intervals, state.selfGrade, onGrade = { onRespond(TestResponse.SelfGrade(it)) })
        } else {
            RevealButton("Tap to reveal", onClick = { isRevealed = true; onReveal() })
        }
    }
}

@Composable
private fun RecallAnswer(answer: String) {
    val skin = LocalSkin.current
    Text(
        answer,
        Modifier
            .fillMaxWidth()
            .background(skin.surface, AnswerShape)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        style = MarginTypography.bodySmall,
        color = skin.content,
    )
}

private val previewRecall = PostContent.Recall(
    title = "Factors",
    question = "Name the three factors of authentication.",
    answer = "Something you know, something you have, and something you are.",
)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun RecallPostForestPreview() = PostPagePreview(Skins.Forest, previewRecall)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun RecallPostGradedPreview() =
    PostPagePreview(Skins.Forest, previewRecall, response = TestResponse.SelfGrade(Rating.GOOD))
