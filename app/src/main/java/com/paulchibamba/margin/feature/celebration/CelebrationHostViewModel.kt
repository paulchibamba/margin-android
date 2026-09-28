package com.paulchibamba.margin.feature.celebration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CelebrationHostViewModel @Inject constructor(
    queue: CelebrationQueue,
    trigger: CelebrationTrigger,
) : ViewModel() {

    val hasPending: StateFlow<Boolean> = queue.pending.map { it.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        viewModelScope.launch { trigger.checkBadges() }
    }
}
