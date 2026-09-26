package com.paulchibamba.margin

import android.app.Application
import com.paulchibamba.margin.data.startup.StartupInitializer
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

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch { startupInitializer.ensureImported() }
    }
}
