package com.paulchibamba.margin.feature.feed.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.model.PostContent

private const val STICKER_TILT_DEGREES = -4f

@Composable
fun MemePost(content: PostContent.Meme, modifier: Modifier = Modifier) {
    PostColumn(modifier, top = 38.dp) {
        Box {
            MemeFrame(content, Modifier.padding(top = 14.dp))
            BonusSticker(Modifier.offset(x = 12.dp))
        }
        PostParagraph(content.caption, style = MarginTypography.bodySmall)
    }
}

@Composable
private fun MemeFrame(content: PostContent.Meme, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(22.dp))
            .background(MarginColors.InkRaised),
    ) {
        PackImage(content.imagePath, descriptionOf(content), Modifier.fillMaxSize())
    }
}

@Composable
private fun BonusSticker(modifier: Modifier = Modifier) {
    Row(
        modifier
            .rotate(STICKER_TILT_DEGREES)
            .shadow(6.dp, CircleShape)
            .background(MarginColors.Lime, CircleShape)
            .padding(start = 8.dp, top = 5.dp, end = 11.dp, bottom = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painterResource(MarginIcons.AutoAwesome), null, Modifier.size(16.dp), MarginColors.InkText)
        Text("Bonus", style = MarginTypography.sticker, color = MarginColors.InkText)
    }
}

private fun descriptionOf(content: PostContent.Meme): String =
    content.imageText.joinToString(" / ").ifBlank { content.title }

private val previewMeme = PostContent.Meme(
    title = "The proxy",
    imagePath = "preview/meme.jpg",
    caption = "Client-side checks are for UX. The proxy skips them in seconds.",
    imageText = listOf("Me: added client-side validation", "The proxy:"),
)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun MemePostInkPreview() = PostPagePreview(Skins.Ink, previewMeme)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun MemePostPaperPreview() = PostPagePreview(Skins.Paper, previewMeme)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun MemePostCobaltPreview() = PostPagePreview(Skins.Cobalt, previewMeme)
