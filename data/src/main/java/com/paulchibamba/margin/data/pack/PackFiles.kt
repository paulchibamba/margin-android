package com.paulchibamba.margin.data.pack

data class PackFiles(
    val manifest: ManifestDto,
    val library: LibraryDto,
    val books: List<BookFileDto>,
)
