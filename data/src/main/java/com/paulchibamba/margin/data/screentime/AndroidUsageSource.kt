package com.paulchibamba.margin.data.screentime

import android.Manifest
import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Process
import com.paulchibamba.margin.domain.repository.UsageSource
import com.paulchibamba.margin.domain.screentime.AppCategory
import com.paulchibamba.margin.domain.screentime.AppInfo
import com.paulchibamba.margin.domain.screentime.PackageName
import com.paulchibamba.margin.domain.screentime.TimeWindow
import com.paulchibamba.margin.domain.screentime.UsageEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidUsageSource @Inject constructor(@ApplicationContext private val context: Context) : UsageSource {
    private val packageManager: PackageManager get() = context.packageManager

    override val ownPackage = PackageName(context.packageName)

    override fun hasAccess(): Boolean {
        val mode = context.getSystemService(AppOpsManager::class.java)
            .unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
        if (mode != AppOpsManager.MODE_DEFAULT) return mode == AppOpsManager.MODE_ALLOWED
        val permission = context.checkSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS)
        return permission == PackageManager.PERMISSION_GRANTED
    }

    override suspend fun events(window: TimeWindow): List<UsageEvent>? = withContext(Dispatchers.IO) {
        val usageStats = context.getSystemService(UsageStatsManager::class.java)
        val events = usageStats.queryEvents(window.start.toEpochMilli(), window.end.toEpochMilli())
        events?.let { usageEventsOf(it, homePackages()) }
    }

    override fun appInfo(packageName: PackageName): AppInfo = try {
        val info = packageManager.getApplicationInfo(packageName.value, 0)
        AppInfo(packageManager.getApplicationLabel(info).toString(), AppCategories.of(info.category))
    } catch (_: PackageManager.NameNotFoundException) {
        AppInfo(packageName.value, AppCategory.OTHER)
    }

    private fun usageEventsOf(events: UsageEvents, homePackages: Set<String>): List<UsageEvent> = buildList {
        val event = UsageEvents.Event()
        while (events.getNextEvent(event)) usageEventOf(event, homePackages)?.let(::add)
    }

    private fun usageEventOf(event: UsageEvents.Event, homePackages: Set<String>): UsageEvent? {
        val at = Instant.ofEpochMilli(event.timeStamp)
        val packageName = PackageName(event.packageName)
        return when (event.eventType) {
            UsageEvents.Event.ACTIVITY_RESUMED -> resumedOf(packageName, at, homePackages)
            UsageEvents.Event.ACTIVITY_PAUSED -> UsageEvent.Paused(at, packageName)
            UsageEvents.Event.SCREEN_NON_INTERACTIVE, UsageEvents.Event.DEVICE_SHUTDOWN -> UsageEvent.NothingInFront(at)
            else -> null
        }
    }

    private fun resumedOf(packageName: PackageName, at: Instant, homePackages: Set<String>): UsageEvent =
        if (packageName.value in homePackages) UsageEvent.NothingInFront(at) else UsageEvent.Resumed(at, packageName)

    private fun homePackages(): Set<String> {
        val home = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        return packageManager.queryIntentActivities(home, PackageManager.MATCH_DEFAULT_ONLY)
            .mapTo(mutableSetOf()) { it.activityInfo.packageName }
    }
}
