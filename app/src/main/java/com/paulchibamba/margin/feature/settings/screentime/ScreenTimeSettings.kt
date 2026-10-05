package com.paulchibamba.margin.feature.settings.screentime

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ScreenTimeSettings(onOpenDoomApps: () -> Unit, viewModel: ScreenTimeSettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LifecycleResumeEffect(viewModel) {
        viewModel.onResume()
        onPauseOrDispose {}
    }
    ScreenTimeCard(state, viewModel::onUseScreenTimeClick, onOpenDoomApps, viewModel::onDeleteClick)
    ScreenTimeDialogs(
        state.dialog,
        onOpenUsageAccess = {
            viewModel.onDialogDismissed()
            openUsageAccess(context)
        },
        onDeleteConfirmed = viewModel::onDeleteConfirmed,
        onDismiss = viewModel::onDialogDismissed,
    )
}

private fun openUsageAccess(context: Context) {
    val forMargin = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS, Uri.fromParts("package", context.packageName, null))
    try {
        context.startActivity(forMargin)
    } catch (_: ActivityNotFoundException) {
        context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
    }
}
