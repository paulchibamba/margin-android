package com.paulchibamba.margin.feature.celebration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CelebrationHost(onCelebrate: () -> Unit, viewModel: CelebrationHostViewModel = hiltViewModel()) {
    val hasPending by viewModel.hasPending.collectAsStateWithLifecycle()
    val celebrate by rememberUpdatedState(onCelebrate)
    LaunchedEffect(hasPending) {
        if (hasPending) celebrate()
    }
}
