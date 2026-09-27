package com.paulchibamba.margin.feature.feed.post

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.model.PostContent

@Composable
fun FactPost(content: PostContent.Fact, modifier: Modifier = Modifier) {
    PostColumn(modifier, top = 44.dp, spacing = 18.dp) {
        Icon(painterResource(MarginIcons.AutoAwesome), null, Modifier.size(28.dp), LocalSkin.current.correct)
        PostTitle(content.title, style = headlineStyleFor(content.title))
        PostParagraph(content.text)
    }
}

private val previewFact = PostContent.Fact(
    title = "Most breaches start small.",
    text = "A single leaked key or unpatched library is often the whole way in. Attackers rarely need anything clever.",
)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun FactPostInkPreview() = PostPagePreview(Skins.Ink, previewFact)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun FactPostPaperPreview() = PostPagePreview(Skins.Paper, previewFact)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun FactPostForestPreview() = PostPagePreview(Skins.Forest, previewFact)
