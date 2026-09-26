package com.paulchibamba.margin.data.pack

import android.content.res.AssetManager

class AndroidAssetSource(private val assets: AssetManager) : AssetSource {

    override fun readText(path: String): String = assets.open(path).bufferedReader().use { it.readText() }
}
