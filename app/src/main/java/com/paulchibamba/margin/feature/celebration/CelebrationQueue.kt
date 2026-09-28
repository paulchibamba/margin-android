package com.paulchibamba.margin.feature.celebration

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CelebrationQueue @Inject constructor() {

    private val celebrations = MutableStateFlow<List<Celebration>>(emptyList())
    val pending: StateFlow<List<Celebration>> = celebrations.asStateFlow()

    fun add(celebration: Celebration) {
        celebrations.update { pending -> if (celebration in pending) pending else pending + celebration }
    }

    fun markShown(celebration: Celebration) {
        celebrations.update { pending -> pending - celebration }
    }
}
