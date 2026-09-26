package com.paulchibamba.margin.data.pack

import java.io.FileNotFoundException

class MapAssetSource(private val files: Map<String, String>) : AssetSource {

    override fun readText(path: String): String = files[path] ?: throw FileNotFoundException(path)
}
