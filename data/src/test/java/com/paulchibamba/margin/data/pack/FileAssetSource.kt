package com.paulchibamba.margin.data.pack

import java.io.File

class FileAssetSource(private val root: File) : AssetSource {

    override fun readText(path: String): String = File(root, path).readText()

    companion object {
        private const val PATH_PROPERTY = "margin.contentDirectory"

        fun ofRealPack(): FileAssetSource? = System.getProperty(PATH_PROPERTY)
            ?.let(::File)
            ?.takeIf { File(it, "pack/manifest.json").exists() }
            ?.let(::FileAssetSource)
    }
}
