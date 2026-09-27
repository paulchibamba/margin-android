package com.paulchibamba.margin.feature.feed.post

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skin
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.model.ChoiceQuestion
import com.paulchibamba.margin.domain.model.PostContent

const val CHOICE_OPTION_TAG = "choice-option"

private val OptionShape = RoundedCornerShape(14.dp)

@Composable
fun ChoiceOptions(
    question: ChoiceQuestion,
    chosenIndex: Int?,
    onChoose: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val feedback = rememberAnswerFeedback()
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        question.options.forEachIndexed { index, option ->
            ChoiceOption(
                text = option,
                look = lookOf(index, question.answerIndex, chosenIndex),
                onClick = {
                    feedback.play(question.isCorrect(index))
                    onChoose(index)
                },
                modifier = if (index == chosenIndex) Modifier.shakenBy(feedback) else Modifier,
            )
        }
        if (chosenIndex != null) Explanation(question.explanation)
    }
}

@Composable
private fun ChoiceOption(text: String, look: OptionLook, onClick: () -> Unit, modifier: Modifier) {
    val skin = LocalSkin.current
    val ring = look.ringOn(skin)
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .background(look.backgroundOn(skin), OptionShape)
            .then(if (ring != null) Modifier.border(1.5.dp, ring, OptionShape) else Modifier)
            .clickable(enabled = look == OptionLook.UNANSWERED, role = Role.Button, onClick = onClick)
            .semantics { look.stateDescription?.let { stateDescription = it } }
            .testTag(CHOICE_OPTION_TAG)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text, Modifier.weight(1f), style = look.textStyle, color = look.textColorOn(skin))
        look.icon?.let { icon -> Icon(painterResource(icon), null, Modifier.size(20.dp), ring ?: Color.Unspecified) }
    }
}

@Composable
private fun Explanation(text: String) {
    PostParagraph(text, Modifier.padding(top = 4.dp), style = MarginTypography.bodySmall)
}

private fun lookOf(index: Int, answerIndex: Int, chosenIndex: Int?): OptionLook = when {
    chosenIndex == null -> OptionLook.UNANSWERED
    index == answerIndex -> OptionLook.RIGHT
    index == chosenIndex -> OptionLook.WRONG
    else -> OptionLook.OTHER
}

private enum class OptionLook(val icon: Int?, val stateDescription: String?) {
    UNANSWERED(icon = null, stateDescription = null),
    RIGHT(MarginIcons.CheckCircle, "Right answer"),
    WRONG(MarginIcons.Cancel, "Your answer, wrong"),
    OTHER(icon = null, stateDescription = null);

    val textStyle
        get() = if (this == RIGHT || this == WRONG) {
            MarginTypography.option.copy(fontWeight = FontWeight.SemiBold)
        } else {
            MarginTypography.option
        }

    fun ringOn(skin: Skin): Color? = when (this) {
        RIGHT -> skin.correct
        WRONG -> skin.wrong
        else -> null
    }

    fun backgroundOn(skin: Skin): Color = ringOn(skin)?.copy(alpha = 0.14f) ?: skin.surface

    fun textColorOn(skin: Skin): Color = if (this == OTHER) skin.mutedContent else skin.content
}

private val previewQuestion = PostContent.Mcq(
    title = "Stored XSS",
    question = "Which control stops stored XSS in an HTML body?",
    options = listOf("Input length limit", "Context-aware output encoding", "HTTPS everywhere", "Block <script>"),
    answerIndex = 1,
    explanation = "Filters miss <img onerror> and hundreds of other vectors. Encoding stops them all at output.",
)

@Composable
private fun ChoiceOptionsPreviewOn(skin: Skin, chosenIndex: Int?) {
    MarginTheme(skin) {
        Box(Modifier.background(skin.background).padding(16.dp)) {
            ChoiceOptions(previewQuestion, chosenIndex, onChoose = {})
        }
    }
}

@Preview(widthDp = 300)
@Composable
private fun ChoiceOptionsUnansweredPreview() = ChoiceOptionsPreviewOn(Skins.Ink, chosenIndex = null)

@Preview(widthDp = 300)
@Composable
private fun ChoiceOptionsWrongPreview() = ChoiceOptionsPreviewOn(Skins.Ink, chosenIndex = 3)

@Preview(widthDp = 300)
@Composable
private fun ChoiceOptionsPaperPreview() = ChoiceOptionsPreviewOn(Skins.Paper, chosenIndex = 3)
