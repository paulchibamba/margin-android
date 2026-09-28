package com.paulchibamba.margin.feature.celebration

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.rewards.EarnedBadge

@Composable
fun CelebrationRoute(onFinished: () -> Unit, viewModel: CelebrationViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val finished by rememberUpdatedState(onFinished)
    LaunchedEffect(state.isFinished) {
        if (state.isFinished) finished()
    }
    val celebration = state.celebration
    BackHandler(enabled = celebration != null) { celebration?.let(viewModel::onDone) }
    CelebrationContent(state, onDone = viewModel::onDone)
}

@Composable
private fun CelebrationContent(state: CelebrationUiState, onDone: (Celebration) -> Unit) {
    val context = LocalContext.current
    val streak = state.streak
    when (val celebration = state.celebration) {
        Celebration.StreakExtended if streak != null ->
            StreakExtendedScreen(streak, onContinue = { onDone(celebration) })
        is Celebration.BadgeUnlocked -> BadgeUnlockedScreen(
            celebration.earned,
            onKeepLearning = { onDone(celebration) },
            onShare = { shareBadge(context, celebration.earned) },
        )
        else -> Box(Modifier.fillMaxSize().background(Skins.Ink.background))
    }
}

private fun shareBadge(context: Context, earned: EarnedBadge) {
    val send = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, shareText(earned))
    context.startActivity(Intent.createChooser(send, null))
}
