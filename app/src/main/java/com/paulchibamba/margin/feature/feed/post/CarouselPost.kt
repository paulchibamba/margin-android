package com.paulchibamba.margin.feature.feed.post

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.model.PostContent

@Composable
fun CarouselPost(
    content: PostContent.Carousel,
    carousel: CarouselState,
    onEngaged: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val skin = LocalSkin.current
    LaunchedEffect(carousel.isOnLastSlide) { if (carousel.isOnLastSlide) onEngaged() }
    PostColumn(modifier.tapToStep(carousel), top = 43.dp) {
        Text(carousel.counterLabel, style = MarginTypography.mono, color = skin.mutedContent)
        PostTitle(content.title)
        Crossfade(carousel.slide, label = "carousel slide") { slide ->
            Text(
                content.slides[slide],
                Modifier.widthIn(max = 290.dp),
                style = MarginTypography.slide,
                color = skin.content,
            )
        }
    }
}

private fun Modifier.tapToStep(carousel: CarouselState): Modifier = this
    .pointerInput(carousel) {
        detectTapGestures { tap -> if (tap.x > size.width / 2) carousel.next() else carousel.previous() }
    }
    .semantics {
        customActions = listOf(
            CustomAccessibilityAction("Next slide") { carousel.next().let { true } },
            CustomAccessibilityAction("Previous slide") { carousel.previous().let { true } },
        )
    }

private val previewCarousel = PostContent.Carousel(
    title = "Encode for where the data lands.",
    slides = listOf(
        "One escape function isn't enough.",
        "Each context has its own rules: HTML body, attribute, JavaScript and URL.",
        "Encode at the point of output, not on input.",
        "Let the template engine do it for you.",
    ),
)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun CarouselPostCobaltPreview() = PostPagePreview(Skins.Cobalt, previewCarousel)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun CarouselPostPaperPreview() = PostPagePreview(Skins.Paper, previewCarousel)

@Preview(widthDp = 360, heightDp = 703)
@Composable
private fun CarouselPostInkPreview() = PostPagePreview(Skins.Ink, previewCarousel)
