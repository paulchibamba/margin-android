package com.paulchibamba.margin.data.pack

class PackReader(private val assetSource: AssetSource) {

    fun readManifest(): ManifestDto = PackJson.decodeFromString(assetSource.readText(pathOf(MANIFEST)))

    fun read(): PackFiles {
        val manifest = readManifest()
        return PackFiles(
            manifest = manifest,
            library = PackJson.decodeFromString(assetSource.readText(pathOf(manifest.library))),
            books = manifest.books.map { book -> PackJson.decodeFromString(assetSource.readText(pathOf(book.file))) },
        )
    }

    private fun pathOf(file: String) = "$PACK_DIRECTORY/$file"

    private companion object {
        const val PACK_DIRECTORY = "pack"
        const val MANIFEST = "manifest.json"
    }
}
