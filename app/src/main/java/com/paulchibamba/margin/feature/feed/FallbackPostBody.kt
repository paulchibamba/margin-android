package com.paulchibamba.margin.feature.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skin
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.model.PostContent

@Composable
fun FallbackPostBody(content: PostContent, modifier: Modifier = Modifier) {
    val skin = LocalSkin.current
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 22.dp, top = 48.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Text(content.title, style = MarginTypography.postTitle, color = skin.content)
        content.readableText.drop(1).forEach { paragraph ->
            Text(
                paragraph,
                style = MarginTypography.body,
                color = skin.mutedContent,
                modifier = Modifier.widthIn(max = 290.dp),
            )
        }
    }
}

@Composable
private fun FallbackPostBodyPreviewOn(skin: Skin) {
    MarginTheme(skin) {
        Box(Modifier.background(skin.background)) {
            FallbackPostBody(
                PostContent.Tip(
                    title = "Never trust the client.",
                    text = "Client-side checks are for UX, not security. Anyone with a proxy skips them in seconds.",
                ),
            )
        }
    }
}

@Preview(widthDp = 290, heightDp = 420)
@Composable
private fun FallbackPostBodyInkPreview() = FallbackPostBodyPreviewOn(Skins.Ink)

@Preview(widthDp = 290, heightDp = 420)
@Composable
private fun FallbackPostBodyPaperPreview() = FallbackPostBodyPreviewOn(Skins.Paper)
