package com.paulchibamba.margin.domain.screentime

import com.paulchibamba.margin.domain.repository.UsageSource

class FakeUsageSource(override val ownPackage: PackageName = PackageName("com.paulchibamba.margin")) : UsageSource {
    var isGranted = true
    var isLocked = false
    val events = mutableListOf<UsageEvent>()
    val apps = mutableMapOf<PackageName, AppInfo>()
    val queried = mutableListOf<TimeWindow>()

    override fun hasAccess(): Boolean = isGranted

    override suspend fun events(window: TimeWindow): List<UsageEvent>? {
        if (isLocked) return null
        queried += window
        return events.filter { !it.at.isBefore(window.start) && it.at.isBefore(window.end) }
    }

    override fun appInfo(packageName: PackageName): AppInfo =
        apps[packageName] ?: AppInfo(packageName.value, AppCategory.OTHER)
}
