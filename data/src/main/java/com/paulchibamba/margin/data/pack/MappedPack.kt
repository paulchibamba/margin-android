package com.paulchibamba.margin.data.pack

import com.paulchibamba.margin.data.database.PackContent
import com.paulchibamba.margin.data.database.entity.BookSettingsEntity
import com.paulchibamba.margin.data.database.entity.ReadingOnlyChapterEntity

data class MappedPack(
    val version: String,
    val content: PackContent,
    val defaultBookSettings: List<BookSettingsEntity>,
    val defaultReadingOnlyChapters: List<ReadingOnlyChapterEntity>,
    val warnings: PackWarnings,
)
