package com.paulchibamba.margin.data.startup

import com.paulchibamba.margin.data.pack.ImportResult
import com.paulchibamba.margin.data.pack.PackImporter
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StartupInitializer @Inject constructor(private val importer: PackImporter) {
    private val mutex = Mutex()
    private var result: ImportResult? = null

    suspend fun ensureImported(): ImportResult = mutex.withLock {
        result ?: importer.importIfChanged().also { result = it }
    }
}
