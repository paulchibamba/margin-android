package com.paulchibamba.margin.feature.settings.screentime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun DoomAppsRoute(onBack: () -> Unit, viewModel: DoomAppsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    DoomAppsScreen(state, onBack = onBack, onDoomChange = viewModel::onDoomChange)
}
