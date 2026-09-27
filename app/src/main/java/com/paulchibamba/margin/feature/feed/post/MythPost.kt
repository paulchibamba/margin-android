package com.paulchibamba.margin.feature.feed.post

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.model.PostContent

@Composable
fun MythPost(content: PostContent.Myth, onEngaged: () -> Unit, modifier: Modifier = Modifier) {
    var isRevealed by rememberSaveable(content) { mutableStateOf(false) }
    val skin = LocalSkin.current
    PostColumn(modifier) {
        PostTitle(content.title)
        VerdictLabel("Myth", skin.wrong, MarginIcons.Cancel)
        MythText(textAfterLabel(content.myth, "Myth"), isRevealed)
        if (isRevealed) {
            VerdictLabel("Reality", skin.correct, MarginIcons.CheckCircle)
            PostParagraph(textAfterLabel(content.reality, "Reality"), style = MarginTypography.body)
        } else {
            RevealButton(onClick = { isRevealed = true; onEngaged() })
        }
    }
}

@Composable
private fun MythText(text: String, isRevealed: Boolean) {
    val skin = LocalSkin.current
    Text(
        text,
        Modifier.widthIn(max = 290.dp),
        style = MarginTypography.slide,
        color = if (isRevealed) skin.mutedContent else skin.content,
        textDecoration = if (isRevealed) TextDecoration.LineThrough else null,
    )
}

@Composable
private fun VerdictLabel(label: String, color: Color, @DrawableRes icon: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(painterResource(icon), null, Modifier.size(18.dp), color)
        Text(label, style = MarginTypography.cardTitle, color = color)
    }
}

@Composable
private fun RevealButton(onClick: () -> Unit) {
    val skin = LocalSkin.current
    Row(
        Modifier
            .background(skin.content, CircleShape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painterResource(MarginIcons.Visibility), null, Modifier.size(20.dp), skin.background)
        Text("Reveal the reality", style = MarginTypography.button, color = skin.background)
    }
}

private fun textAfterLabel(text: String, label: String): String {
    val line = SpeakerLineParser.parse(text)
    return if (line.speaker.equals(label, ignoreCase = true)) line.text else text
}

private val previewMyth = PostContent.Myth(
    title = "Myth: HTTPS makes a site safe",
    myth = "Myth: The padlock means the site is secure.",
    reality = "Reality: HTTPS protects the connection, not the code on either end.",
)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun MythPostInkPreview() = PostPagePreview(Skins.Ink, previewMyth)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun MythPostPaperPreview() = PostPagePreview(Skins.Paper, previewMyth)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun MythPostEmberPreview() = PostPagePreview(Skins.Ember, previewMyth)
