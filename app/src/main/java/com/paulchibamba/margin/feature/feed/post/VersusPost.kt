package com.paulchibamba.margin.feature.feed.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.model.PostContent

private val PanelShape = RoundedCornerShape(16.dp)
private val PLACEHOLDER_LABEL = Regex("""^[XY]\s*\((.+)\)$""")

@Composable
fun VersusPost(content: PostContent.Versus, modifier: Modifier = Modifier) {
    val skin = LocalSkin.current
    PostColumn(modifier, spacing = 10.dp) {
        PostTitle(content.title, Modifier.padding(bottom = 6.dp))
        VersusPanel(SpeakerLineParser.parse(content.left), skin.surface, skin.content)
        Text("vs", Modifier.padding(start = 14.dp), style = MarginTypography.mono, color = skin.mutedContent)
        VersusPanel(SpeakerLineParser.parse(content.right), skin.content, skin.background)
    }
}

@Composable
private fun VersusPanel(side: SpeakerLine, background: Color, foreground: Color) {
    Column(
        Modifier.fillMaxWidth().background(background, PanelShape).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        side.speaker?.let { label -> Text(labelOf(label), style = MarginTypography.cardTitle, color = foreground) }
        Text(side.text, style = MarginTypography.bubble, color = foreground)
    }
}

private fun labelOf(speaker: String): String = PLACEHOLDER_LABEL.find(speaker)?.groupValues?.get(1) ?: speaker

private val previewVersus = PostContent.Versus(
    title = "AuthN vs AuthZ",
    left = "Authentication: who are you? It proves identity.",
    right = "Authorization: should you be here? It decides access.",
)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun VersusPostCobaltPreview() = PostPagePreview(Skins.Cobalt, previewVersus)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun VersusPostPaperPreview() = PostPagePreview(Skins.Paper, previewVersus)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun VersusPostForestPreview() = PostPagePreview(Skins.Forest, previewVersus)
