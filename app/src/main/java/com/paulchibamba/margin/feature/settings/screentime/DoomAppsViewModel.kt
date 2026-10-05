package com.paulchibamba.margin.feature.settings.screentime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulchibamba.margin.domain.screentime.PackageName
import com.paulchibamba.margin.domain.usecase.ObserveScreenTimeApps
import com.paulchibamba.margin.domain.usecase.SetAppDoom
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val STOP_TIMEOUT_MILLIS = 5_000L

@HiltViewModel
class DoomAppsViewModel @Inject constructor(
    observeScreenTimeApps: ObserveScreenTimeApps,
    private val setAppDoom: SetAppDoom,
) : ViewModel() {

    val uiState: StateFlow<DoomAppsUiState> = observeScreenTimeApps()
        .map { apps -> DoomAppsUiState(apps, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), DoomAppsUiState())

    fun onDoomChange(packageName: PackageName, isDoom: Boolean) {
        viewModelScope.launch { setAppDoom(packageName, isDoom) }
    }
}
