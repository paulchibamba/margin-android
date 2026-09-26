package com.paulchibamba.margin.data.pack

fun interface AssetSource {
    fun readText(path: String): String
}
