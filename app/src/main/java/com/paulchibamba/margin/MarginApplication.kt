package com.paulchibamba.margin

import android.app.Application
import com.paulchibamba.margin.data.startup.StartupInitializer
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

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch { startupInitializer.ensureImported() }
        userPresence.start()
    }
}
