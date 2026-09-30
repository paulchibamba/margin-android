package com.paulchibamba.margin.feature.settings

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsRoute(
    onOpenReadingOnlyChapters: () -> Unit,
    onOpenStats: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(
        state = state,
        onBookSettingsChange = viewModel::onBookSettingsChange,
        onRetentionChange = viewModel::onRetentionChange,
        onRetentionChangeFinished = viewModel::onRetentionChangeFinished,
        onReviewReminderChange = reviewReminderChangeWithPermission(viewModel),
        onDarkModeChange = viewModel::onDarkModeChange,
        onOpenReadingOnlyChapters = onOpenReadingOnlyChapters,
        onOpenStats = onOpenStats,
    )
}

@Composable
private fun reviewReminderChangeWithPermission(viewModel: SettingsViewModel): (Boolean) -> Unit {
    val context = LocalContext.current
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) viewModel.onReviewReminderChange(true)
    }
    return { isOn ->
        if (isOn && needsNotificationPermission(context)) {
            permission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            viewModel.onReviewReminderChange(isOn)
        }
    }
}

private fun needsNotificationPermission(context: Context): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
        PackageManager.PERMISSION_GRANTED
