package com.paulchibamba.margin.feature.appearance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulchibamba.margin.domain.model.DarkMode
import com.paulchibamba.margin.domain.usecase.ObserveDarkMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppearanceViewModel @Inject constructor(observeDarkMode: ObserveDarkMode) : ViewModel() {

    val darkMode: StateFlow<DarkMode> = observeDarkMode()
        .stateIn(viewModelScope, SharingStarted.Eagerly, DarkMode.DEFAULT)
}
