package com.paulchibamba.margin.data.cover

import com.paulchibamba.margin.domain.model.BookSlug
import java.io.File
import java.time.Instant

class CoverDirectory(private val root: File) {

    fun newFile(book: BookSlug, at: Instant): File = File(root, "${safeNameOf(book)}-${at.toEpochMilli()}.webp")

    fun fileOf(fileName: String): File = File(root, fileName)

    fun owns(file: File): Boolean = file.canonicalFile.parentFile == root.canonicalFile

    private fun safeNameOf(book: BookSlug): String = book.value.filter { it.isLetterOrDigit() || it == '-' }

    companion object {
        const val NAME = "covers"
    }
}
