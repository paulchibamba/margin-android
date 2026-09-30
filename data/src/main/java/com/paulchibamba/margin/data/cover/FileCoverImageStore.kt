package com.paulchibamba.margin.data.cover

import android.content.ContentResolver
import android.graphics.ImageDecoder
import android.net.Uri
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.CoverSource
import com.paulchibamba.margin.domain.repository.CoverImageStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.ByteBuffer
import java.time.Instant

class FileCoverImageStore(
    private val contentResolver: ContentResolver,
    private val directory: CoverDirectory,
    private val downloader: CoverDownloader = CoverDownloader(),
    private val writer: CoverImageWriter = CoverImageWriter(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CoverImageStore {

    override suspend fun save(book: BookSlug, source: CoverSource, at: Instant): String? = withContext(dispatcher) {
        val target = directory.newFile(book, at)
        try {
            writer.write(ImageDecoder.createSource(ByteBuffer.wrap(bytesOf(source))), target)
            target.path
        } catch (unreadable: IOException) {
            null
        } catch (revoked: SecurityException) {
            null
        }
    }

    override suspend fun delete(imagePath: String) {
        withContext(dispatcher) { File(imagePath).takeIf(directory::owns)?.delete() }
    }

    private fun bytesOf(source: CoverSource): ByteArray = when (source) {
        is CoverSource.GalleryImage -> contentResolver.openInputStream(Uri.parse(source.uri))
            ?.use { picked -> picked.readAtMost(MAX_PICKED_BYTES) }
            ?: throw FileNotFoundException(source.uri)
        is CoverSource.WebImage -> downloader.download(source.url)
    }

    private companion object {
        const val MAX_PICKED_BYTES = 40 * 1024 * 1024
    }
}
