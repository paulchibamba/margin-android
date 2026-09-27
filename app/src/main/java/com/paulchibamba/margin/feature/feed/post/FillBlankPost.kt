package com.paulchibamba.margin.feature.feed.post

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skin
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.PostContent

private const val FIGURE_SPACE = ' '
private const val SHORTEST_BLANK = 6
private const val LONGEST_BLANK = 12

@Composable
fun FillBlankPost(
    content: PostContent.FillBlank,
    state: TestPostState,
    onReveal: () -> Unit,
    onRespond: (TestResponse) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isRevealed by rememberSaveable(content) { mutableStateOf(false) }
    val isShown = isRevealed || state.isAnswered
    val skin = LocalSkin.current
    PostColumn(modifier, top = TEST_POST_TOP, spacing = 16.dp) {
        Text(sentenceOf(content, isShown, skin), style = MarginTypography.prompt, color = skin.content)
        if (isShown) {
            content.explanation?.let { PostParagraph(it, style = MarginTypography.bodySmall) }
            SelfGradeButtons(state.intervals, state.selfGrade, onGrade = { onRespond(TestResponse.SelfGrade(it)) })
        } else {
            RevealButton("Tap to fill the gap", onClick = { isRevealed = true; onReveal() })
        }
    }
}

private fun sentenceOf(content: PostContent.FillBlank, isShown: Boolean, skin: Skin): AnnotatedString {
    val sentence = BlankSentence.parse(content.sentence)
    return buildAnnotatedString {
        append(sentence.before)
        if (isShown) {
            withStyle(SpanStyle(color = skin.correct, background = skin.correct.copy(alpha = 0.14f))) {
                append(content.answer)
            }
        } else {
            withStyle(SpanStyle(textDecoration = TextDecoration.Underline, color = skin.mutedContent)) {
                append(FIGURE_SPACE.toString().repeat(content.answer.length.coerceIn(SHORTEST_BLANK, LONGEST_BLANK)))
            }
        }
        append(sentence.after)
    }
}

private val previewFillBlank = PostContent.FillBlank(
    title = "Fill in the blank",
    sentence = "____ means data has not been altered in transit or storage.",
    answer = "Integrity",
    explanation = "Integrity is the I in the CIA triad.",
)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun FillBlankPostEmberPreview() = PostPagePreview(Skins.Ember, previewFillBlank)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun FillBlankPostRevealedPreview() =
    PostPagePreview(Skins.Ink, previewFillBlank, response = TestResponse.SelfGrade(Rating.HARD))
