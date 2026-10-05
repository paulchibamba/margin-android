package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.repository.ScreenTimeStore
import com.paulchibamba.margin.domain.screentime.PackageName
import javax.inject.Inject

class SetAppDoom @Inject constructor(private val store: ScreenTimeStore, private val rollUp: RollUpEvents) {

    suspend operator fun invoke(packageName: PackageName, isDoom: Boolean) {
        store.setDoom(packageName, isDoom)
        rollUp.refreshScreenTime(store.datesWith(packageName))
    }
}
