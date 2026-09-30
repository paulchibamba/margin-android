package com.paulchibamba.margin.feature.appearance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulchibamba.margin.domain.model.DarkMode
import com.paulchibamba.margin.domain.usecase.Appearance
import com.paulchibamba.margin.domain.usecase.ObserveAppearance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppearanceViewModel @Inject constructor(observeAppearance: ObserveAppearance) : ViewModel() {

    val appearance: StateFlow<Appearance> = observeAppearance()
        .stateIn(viewModelScope, SharingStarted.Eagerly, Appearance(DarkMode.DEFAULT, isDarkPostsOn = false))
}
