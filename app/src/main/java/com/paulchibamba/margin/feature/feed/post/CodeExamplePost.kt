package com.paulchibamba.margin.feature.feed.post

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.model.PostContent

@Composable
fun CodeExamplePost(content: PostContent.CodeExample, modifier: Modifier = Modifier) {
    PostColumn(modifier) {
        PostTitle(content.title)
        CodeCard(content.code, CodeVerdict.of(content.title, content.caption))
        content.caption?.let { caption -> PostParagraph(caption, style = MarginTypography.bodySmall) }
    }
}

private val previewCode = PostContent.CodeExample(
    title = "Bind it. Don't build it.",
    code = "db.query(\n  \"SELECT * FROM users\n   WHERE id = ?\", id)",
    caption = "Parameters keep data as data, so the query's shape can't change, and it stays safe.",
)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun CodeExamplePostPaperPreview() = PostPagePreview(Skins.Paper, previewCode)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun CodeExamplePostInkPreview() = PostPagePreview(Skins.Ink, previewCode)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun CodeExamplePostForestPreview() = PostPagePreview(Skins.Forest, previewCode)
