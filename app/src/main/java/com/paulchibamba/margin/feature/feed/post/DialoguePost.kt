package com.paulchibamba.margin.feature.feed.post

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.model.PostContent

private val LeftBubbleShape = bubbleShape(bottomStart = 4.dp, bottomEnd = 14.dp)
private val RightBubbleShape = bubbleShape(bottomStart = 14.dp, bottomEnd = 4.dp)

@Composable
fun DialoguePost(content: PostContent.Dialogue, modifier: Modifier = Modifier) {
    val lines = SpeakerLineParser.parseAll(content.lines)
    val firstSpeaker = lines.firstNotNullOfOrNull(SpeakerLine::speaker)
    PostColumn(modifier, spacing = 8.dp) {
        PostTitle(content.title, Modifier.padding(bottom = 4.dp), MarginTypography.screenTitle)
        lines.forEach { line -> ChatBubble(line, isFromFirstSpeaker = line.speaker == firstSpeaker) }
    }
}

@Composable
private fun ChatBubble(line: SpeakerLine, isFromFirstSpeaker: Boolean) {
    Row(
        Modifier.fillMaxWidth().semantics(mergeDescendants = true) {
            contentDescription = listOfNotNull(line.speaker, line.text).joinToString(": ")
        },
        horizontalArrangement = Arrangement.spacedBy(6.dp, if (isFromFirstSpeaker) Alignment.Start else Alignment.End),
        verticalAlignment = Alignment.Bottom,
    ) {
        if (isFromFirstSpeaker) Avatar(line.speaker, isFilled = false)
        Bubble(line.text, isFilled = !isFromFirstSpeaker, Modifier.weight(1f, fill = false))
        if (!isFromFirstSpeaker) Avatar(line.speaker, isFilled = true)
    }
}

@Composable
private fun Bubble(text: String, isFilled: Boolean, modifier: Modifier = Modifier) {
    val skin = LocalSkin.current
    val shape = if (isFilled) RightBubbleShape else LeftBubbleShape
    Box(
        modifier
            .background(if (isFilled) skin.content else skin.surface, shape)
            .padding(horizontal = 12.dp, vertical = 7.dp)
            .clearAndSetSemantics {},
    ) {
        Text(text, style = MarginTypography.bubble, color = if (isFilled) skin.background else skin.content)
    }
}

@Composable
private fun Avatar(speaker: String?, isFilled: Boolean) {
    val skin = LocalSkin.current
    Box(Modifier.size(24.dp).clearAndSetSemantics {}.avatarCircle(isFilled, skin.content), Alignment.Center) {
        Text(initialOf(speaker), style = MarginTypography.meta, color = if (isFilled) skin.background else skin.content)
    }
}

private fun Modifier.avatarCircle(isFilled: Boolean, color: Color): Modifier =
    if (isFilled) background(color, CircleShape) else border(1.5.dp, color, CircleShape)

private fun bubbleShape(bottomStart: Dp, bottomEnd: Dp) =
    RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp, bottomEnd = bottomEnd, bottomStart = bottomStart)

private fun initialOf(speaker: String?): String = speaker?.firstOrNull()?.uppercase() ?: "?"

private val previewDialogue = PostContent.Dialogue(
    title = "Keeping the same session ID",
    lines = listOf(
        "Bob: I log users in and keep the same session ID. Fine?",
        "Alice: That's session fixation. An attacker plants an ID before login, then rides it.",
        "Bob: So what do I do?",
        "Alice: Issue a fresh session ID the moment someone logs in.",
    ),
)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun DialoguePostPaperPreview() = PostPagePreview(Skins.Paper, previewDialogue)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun DialoguePostInkPreview() = PostPagePreview(Skins.Ink, previewDialogue)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun DialoguePostEmberPreview() = PostPagePreview(Skins.Ember, previewDialogue)
