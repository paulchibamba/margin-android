package com.paulchibamba.margin.feature.feed.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.model.PostContent

private val SituationShape = RoundedCornerShape(16.dp)

@Composable
fun ScenarioPost(
    content: PostContent.Scenario,
    state: TestPostState,
    onRespond: (TestResponse) -> Unit,
    modifier: Modifier = Modifier,
) {
    PostColumn(modifier, top = TEST_POST_TOP, spacing = TEST_POST_SPACING) {
        Situation(content.situation)
        QuizQuestion(content.question)
        ChoiceOptions(content, state.chosenIndex, onChoose = { index -> onRespond(TestResponse.Choice(index)) })
    }
}

@Composable
private fun Situation(text: String) {
    val skin = LocalSkin.current
    Text(
        text,
        Modifier
            .fillMaxWidth()
            .background(skin.surface, SituationShape)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        style = MarginTypography.bodySmall,
        color = skin.content,
    )
}

private val previewScenario = PostContent.Scenario(
    title = "Which pillar breaks?",
    situation = "An attacker changes a transfer from $100 to $10,000 before it reaches the server.",
    question = "Which part of the triad is violated?",
    options = listOf("Confidentiality", "Availability", "None of the above", "Integrity"),
    answerIndex = 3,
    explanation = "Altering data in transit so it's no longer accurate violates integrity.",
)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun ScenarioPostCobaltPreview() = PostPagePreview(Skins.Cobalt, previewScenario)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun ScenarioPostAnsweredPreview() =
    PostPagePreview(Skins.Ember, previewScenario, response = TestResponse.Choice(0))
