package com.paulchibamba.margin.feature.feed.post

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.model.PostContent

private val CalloutShape = RoundedCornerShape(18.dp)

@Composable
fun TipPost(content: PostContent.Tip, modifier: Modifier = Modifier) {
    val tip = TipText.of(content.text)
    PostColumn(modifier, top = 48.dp, spacing = 18.dp) {
        PostTitle(content.title, style = headlineStyleFor(content.title))
        tip.body?.let { body -> PostParagraph(body) }
        TipCallout(tip.callout)
    }
}

@Composable
private fun TipCallout(text: String) {
    val skin = LocalSkin.current
    Row(
        Modifier
            .widthIn(max = 250.dp)
            .background(skin.surface, CalloutShape)
            .border(1.dp, skin.content.copy(alpha = 0.08f), CalloutShape)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(painterResource(MarginIcons.VerifiedUser), null, Modifier.size(22.dp), skin.correct)
        Text(text, style = MarginTypography.callout, color = skin.content)
    }
}

private val previewTip = PostContent.Tip(
    title = "Never trust the client.",
    text = "Client-side checks are for UX, not security. Anyone with an intercepting proxy skips them in seconds. " +
        "Validate again on the server, on every request.",
)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun TipPostInkPreview() = PostPagePreview(Skins.Ink, previewTip)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun TipPostPaperPreview() = PostPagePreview(Skins.Paper, previewTip)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun TipPostEmberPreview() = PostPagePreview(Skins.Ember, previewTip)
