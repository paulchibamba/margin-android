package com.paulchibamba.margin.feature.feed.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.model.PostContent

@Composable
fun AnalogyPost(content: PostContent.Analogy, modifier: Modifier = Modifier) {
    PostColumn(modifier, top = 44.dp, spacing = 18.dp) {
        PostTitle(content.title, style = headlineStyleFor(content.title))
        AnalogyCard(content.text)
    }
}

@Composable
private fun AnalogyCard(text: String) {
    val skin = LocalSkin.current
    Column(
        Modifier
            .widthIn(max = 290.dp)
            .background(skin.surface, RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(painterResource(MarginIcons.Autorenew), null, Modifier.size(22.dp), skin.correct)
        Text(text, style = MarginTypography.body, color = skin.content)
    }
}

private val previewAnalogy = PostContent.Analogy(
    title = "A session is a coat-check ticket.",
    text = "The app hands you a ticket so it remembers what's yours as you move around. Lose the ticket, " +
        "and anyone holding it gets your coat.",
)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun AnalogyPostCobaltPreview() = PostPagePreview(Skins.Cobalt, previewAnalogy)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun AnalogyPostPaperPreview() = PostPagePreview(Skins.Paper, previewAnalogy)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun AnalogyPostEmberPreview() = PostPagePreview(Skins.Ember, previewAnalogy)
