package com.paulchibamba.margin.domain.repository

import com.paulchibamba.margin.domain.screentime.AppInfo
import com.paulchibamba.margin.domain.screentime.PackageName
import com.paulchibamba.margin.domain.screentime.TimeWindow
import com.paulchibamba.margin.domain.screentime.UsageEvent

interface UsageSource {
    val ownPackage: PackageName
    fun hasAccess(): Boolean
    suspend fun events(window: TimeWindow): List<UsageEvent>?
    fun appInfo(packageName: PackageName): AppInfo
}
