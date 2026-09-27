package com.paulchibamba.margin.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.LocalSkin
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skin
import com.paulchibamba.margin.designsystem.Skins

private val BadgeShape = RoundedCornerShape(7.dp)

@Composable
fun PostCaption(
    formatLabel: String,
    concept: String,
    bookTitle: String,
    chapterLabel: String,
    introduced: Int,
    total: Int,
    isReview: Boolean,
    isPreview: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = FeedChromeColors.of(LocalSkin.current.isLight)
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        CaptionBadges(formatLabel, isReview, isPreview, colors)
        val shadowed = MarginTypography.conceptTitle.withChromeShadow(colors, 6.dp, 0.3f)
        Text(concept, style = shadowed, color = colors.content)
        Text(
            "$bookTitle · $chapterLabel",
            style = MarginTypography.caption.withChromeShadow(colors, 6.dp, 0.3f),
            color = colors.content,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.alpha(0.82f),
        )
        BookProgress(introduced, total, colors)
    }
}

@Composable
private fun CaptionBadges(formatLabel: String, isReview: Boolean, isPreview: Boolean, colors: FeedChromeColors) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            formatLabel,
            style = MarginTypography.pill,
            color = colors.content,
            modifier = Modifier.background(colors.pill, BadgeShape).padding(horizontal = 9.dp, vertical = 4.dp),
        )
        if (isReview) OutlineBadge(MarginIcons.Replay, "Review", colors)
        if (isPreview) OutlineBadge(MarginIcons.Visibility, "Preview", colors)
    }
}

@Composable
private fun OutlineBadge(@DrawableRes icon: Int, label: String, colors: FeedChromeColors) {
    Row(
        Modifier
            .border(1.5.dp, colors.outline, BadgeShape)
            .padding(start = 6.dp, top = 3.dp, end = 8.dp, bottom = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painterResource(icon), contentDescription = null, Modifier.size(14.dp), colors.content)
        Text(label, style = MarginTypography.pill, color = colors.content)
    }
}

@Composable
private fun BookProgress(introduced: Int, total: Int, colors: FeedChromeColors) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        val fraction = if (total > 0) (introduced.toFloat() / total).coerceIn(0f, 1f) else 0f
        Box(
            Modifier.weight(1f, fill = false).widthIn(min = 40.dp, max = 96.dp).fillMaxWidth()
                .height(3.dp).clip(RoundedCornerShape(3.dp)).background(colors.progressTrack),
        ) {
            Box(Modifier.fillMaxHeight().fillMaxWidth(fraction).background(colors.content))
        }
        Text(
            "$introduced/$total introduced",
            style = MarginTypography.meta.withChromeShadow(colors, 6.dp, 0.3f),
            color = colors.content,
            maxLines = 1,
            modifier = Modifier.alpha(0.78f),
        )
    }
}

@Composable
private fun PostCaptionPreviewOn(skin: Skin, isReview: Boolean, isPreview: Boolean) {
    MarginTheme(skin) {
        Box(Modifier.background(skin.background).padding(16.dp)) {
            PostCaption(
                formatLabel = "Tip",
                concept = "Server-side validation",
                bookTitle = "Alice & Bob Learn AppSec",
                chapterLabel = "Ch 3 · Input",
                introduced = 12,
                total = 48,
                isReview = isReview,
                isPreview = isPreview,
                modifier = Modifier.widthIn(max = 268.dp),
            )
        }
    }
}

@Preview(widthDp = 300)
@Composable
private fun PostCaptionInkPreview() = PostCaptionPreviewOn(Skins.Ink, isReview = false, isPreview = false)

@Preview(widthDp = 300)
@Composable
private fun PostCaptionPaperPreview() = PostCaptionPreviewOn(Skins.Paper, isReview = false, isPreview = false)

@Preview(widthDp = 300)
@Composable
private fun PostCaptionForestReviewPreview() = PostCaptionPreviewOn(Skins.Forest, isReview = true, isPreview = false)

@Preview(widthDp = 300)
@Composable
private fun PostCaptionEmberPreviewPreview() = PostCaptionPreviewOn(Skins.Ember, isReview = false, isPreview = true)
