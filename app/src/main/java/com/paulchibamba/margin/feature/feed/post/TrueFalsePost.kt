package com.paulchibamba.margin.feature.feed.post

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.model.PostContent

@Composable
fun TrueFalsePost(
    content: PostContent.TrueFalse,
    state: TestPostState,
    onRespond: (TestResponse) -> Unit,
    modifier: Modifier = Modifier,
) {
    val feedback = rememberAnswerFeedback()
    val saysTrue = state.saysTrue
    val respond = { answer: Boolean ->
        feedback.play(isCorrect = answer == content.isTrue)
        onRespond(TestResponse.Verdict(answer))
    }
    Column(
        modifier
            .fillMaxSize()
            .testTag(POST_BODY_TAG)
            .padding(start = 22.dp, top = TEST_POST_TOP, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        SwipeCard(content.statement, saysTrue, respond, Modifier.shakenBy(feedback))
        if (saysTrue == null) VerdictButtons(respond) else TrueFalseResult(content, saysTrue)
    }
}

@Composable
private fun VerdictButtons(onVerdict: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(28.dp, Alignment.CenterHorizontally)) {
        VerdictButton("False", MarginIcons.Close, LocalSkin.current.wrong) { onVerdict(false) }
        VerdictButton("True", MarginIcons.Check, LocalSkin.current.correct) { onVerdict(true) }
    }
}

@Composable
private fun VerdictButton(label: String, icon: Int, color: Color, onClick: () -> Unit) {
    val skin = LocalSkin.current
    Column(
        Modifier.clickable(role = Role.Button, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            painterResource(icon),
            contentDescription = null,
            modifier = Modifier
                .size(62.dp)
                .background(if (skin.isLight) MarginColors.PaperCard else MarginColors.InkButton, CircleShape)
                .border(2.dp, color, CircleShape)
                .padding(15.dp),
            tint = color,
        )
        Text(label, style = MarginTypography.smallButton, color = skin.content)
    }
}

@Composable
private fun TrueFalseResult(content: PostContent.TrueFalse, saysTrue: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        VerdictLine(isRight = saysTrue == content.isTrue, truth = if (content.isTrue) "It's true." else "It's false.")
        PostParagraph(content.explanation, style = MarginTypography.bodySmall)
    }
}

@Composable
private fun VerdictLine(isRight: Boolean, truth: String) {
    val skin = LocalSkin.current
    val color = if (isRight) skin.correct else skin.wrong
    val icon = if (isRight) MarginIcons.CheckCircle else MarginIcons.Cancel
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(painterResource(icon), contentDescription = null, modifier = Modifier.size(20.dp), tint = color)
        Text(if (isRight) "Right. $truth" else "Not quite. $truth", style = MarginTypography.cardTitle, color = color)
    }
}

private val previewStatement = PostContent.TrueFalse(
    title = "SameSite",
    statement = "SameSite=Lax cookies are sent on a cross-site POST.",
    isTrue = false,
    explanation = "Lax cookies go with top-level GET navigations only, so a cross-site POST arrives without them.",
)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun TrueFalsePostInkPreview() = PostPagePreview(Skins.Ink, previewStatement)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun TrueFalsePostPaperPreview() = PostPagePreview(Skins.Paper, previewStatement)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun TrueFalsePostAnsweredPreview() =
    PostPagePreview(Skins.Cobalt, previewStatement, response = TestResponse.Verdict(saysTrue = true))
