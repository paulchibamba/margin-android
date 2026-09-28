package com.paulchibamba.margin.feature.celebration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.designsystem.StatusBarFollowsSkin
import com.paulchibamba.margin.domain.rewards.BadgeKind
import com.paulchibamba.margin.domain.rewards.EarnedBadge
import com.paulchibamba.margin.feature.read.ReadingProgressBar

private val faintWhite = MarginColors.White.copy(alpha = 0.6f)
private val bookTitleWhite = MarginColors.White.copy(alpha = 0.85f)

@Composable
fun BadgeUnlockedScreen(
    earned: EarnedBadge,
    onKeepLearning: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MarginTheme(Skins.Ink) {
        StatusBarFollowsSkin()
        Column(
            modifier
                .fillMaxSize()
                .background(Skins.Ink.background)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(start = 24.dp, top = 64.dp, end = 24.dp, bottom = 16.dp),
        ) {
            BadgeDiamond(earned.badge.kind)
            BadgeText(earned, Modifier.padding(top = 10.dp))
            if (earned.badge.kind == BadgeKind.INTRODUCED) RememberedProgress(earned)
            Spacer(Modifier.weight(1f))
            CelebrationButton("Keep learning", MarginColors.Lime, onKeepLearning)
            ShareButton(onShare, Modifier.padding(top = 10.dp))
        }
    }
}

@Composable
private fun BadgeText(earned: EarnedBadge, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("BADGE UNLOCKED", style = MarginTypography.badgeEyebrow, color = MarginColors.Lime)
        Text(
            earned.badge.kind.title(),
            style = MarginTypography.badgeTitle,
            color = MarginColors.White,
            modifier = Modifier.semantics { heading() },
        )
        Text(earned.book.title, style = MarginTypography.celebrationBook, color = bookTitleWhite)
        Text(
            badgeDetail(earned, emphasis = MarginColors.White),
            style = MarginTypography.celebrationDetail,
            color = MarginColors.White.copy(alpha = 0.65f),
            modifier = Modifier.widthIn(max = 270.dp),
        )
    }
}

@Composable
private fun RememberedProgress(earned: EarnedBadge) {
    val completion = earned.completion
    Column(Modifier.padding(top = 16.dp).width(220.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        ReadingProgressBar(
            fraction = if (completion.total == 0) 0f else completion.remembered.toFloat() / completion.total,
            height = 6.dp,
            track = MarginColors.White.copy(alpha = 0.12f),
            color = MarginColors.White,
        )
        Text(rememberedProgressLabel(earned), style = MarginTypography.label, color = faintWhite)
    }
}

@Preview(widthDp = 360, heightDp = 780)
@Composable
private fun BadgeUnlockedIntroducedPreview() {
    BadgeUnlockedScreen(CelebrationPreviewData.introduced, onKeepLearning = {}, onShare = {})
}

@Preview(widthDp = 360, heightDp = 780)
@Composable
private fun BadgeUnlockedRememberedPreview() {
    BadgeUnlockedScreen(CelebrationPreviewData.remembered, onKeepLearning = {}, onShare = {})
}
