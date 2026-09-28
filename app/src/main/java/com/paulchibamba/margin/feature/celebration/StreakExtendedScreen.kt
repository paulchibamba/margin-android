package com.paulchibamba.margin.feature.celebration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.MarginIcons
import com.paulchibamba.margin.designsystem.MarginTheme
import com.paulchibamba.margin.designsystem.MarginTypography
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.designsystem.StatusBarFollowsSkin
import com.paulchibamba.margin.domain.usecase.StreakSummary
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private val AUTO_CLOSE = 2.seconds
private const val FLAME_START_SCALE = 0.3f
private val TODAY_FILL_DELAY = 350.milliseconds

@Composable
fun StreakExtendedScreen(streak: StreakSummary, onContinue: () -> Unit, modifier: Modifier = Modifier) {
    AutoClose(onContinue)
    RisingHapticEffect()
    MarginTheme(Skins.Ink) {
        StatusBarFollowsSkin()
        Column(
            modifier
                .fillMaxSize()
                .background(Skins.Ink.background)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(start = 24.dp, top = 94.dp, end = 24.dp, bottom = 20.dp),
        ) {
            StreakFlame()
            StreakCount(streak.currentStreak)
            val todayFill by rememberPopIn(from = 0f, delay = TODAY_FILL_DELAY)
            WeekStrip(streak, todayFill, Modifier.padding(top = 28.dp))
            Text(
                STREAK_RULE,
                style = MarginTypography.celebrationDetail,
                color = MarginColors.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 20.dp).widthIn(max = 260.dp),
            )
            Spacer(Modifier.weight(1f))
            CelebrationButton("Continue", MarginColors.White, onContinue)
        }
    }
}

@Composable
private fun AutoClose(onClose: () -> Unit) {
    val close by rememberUpdatedState(onClose)
    LaunchedEffect(Unit) {
        delay(AUTO_CLOSE)
        close()
    }
}

@Composable
private fun StreakFlame() {
    val scale by rememberPopIn(from = FLAME_START_SCALE)
    Icon(
        painterResource(MarginIcons.LocalFireDepartment),
        contentDescription = null,
        tint = MarginColors.Streak,
        modifier = Modifier
            .celebrationGlow(MarginColors.Streak.copy(alpha = 0.35f), radius = 104.dp)
            .size(120.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale },
    )
}

@Composable
private fun StreakCount(count: Int) {
    Text(
        count.toString(),
        style = MarginTypography.display,
        color = MarginColors.White,
        modifier = Modifier.padding(top = 6.dp),
    )
    Text(
        "day streak",
        style = MarginTypography.barTitle,
        color = MarginColors.White,
        modifier = Modifier.padding(top = 6.dp).semantics { heading() },
    )
}

@Preview(widthDp = 360, heightDp = 780)
@Composable
private fun StreakExtendedScreenPreview() {
    StreakExtendedScreen(CelebrationPreviewData.streak, onContinue = {})
}
