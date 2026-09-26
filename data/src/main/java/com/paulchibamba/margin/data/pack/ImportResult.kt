package com.paulchibamba.margin.data.pack

sealed interface ImportResult {
    data class Skipped(val version: String) : ImportResult
    data class Imported(val version: String, val warnings: PackWarnings) : ImportResult
    data class Failed(val cause: Throwable) : ImportResult
}
