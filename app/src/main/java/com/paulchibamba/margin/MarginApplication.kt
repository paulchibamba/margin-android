package com.paulchibamba.margin

import android.app.Application
import com.paulchibamba.margin.data.startup.StartupInitializer
import com.paulchibamba.margin.feature.bake.BakeJobs
import com.paulchibamba.margin.feature.drop.DropTimeJobs
import com.paulchibamba.margin.feature.tracking.TrackingJobs
import com.paulchibamba.margin.feature.tracking.UserPresence
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MarginApplication : Application() {

    @Inject
    lateinit var startupInitializer: StartupInitializer

    @Inject
    lateinit var userPresence: UserPresence

    @Inject
    lateinit var trackingJobs: TrackingJobs

    @Inject
    lateinit var bakeJobs: BakeJobs

    @Inject
    lateinit var dropTimeJobs: DropTimeJobs

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch { startupInitializer.ensureImported() }
        userPresence.start()
        trackingJobs.schedule()
        bakeJobs.start()
        dropTimeJobs.start()
    }
}
