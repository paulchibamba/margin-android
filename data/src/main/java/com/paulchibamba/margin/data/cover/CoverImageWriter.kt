package com.paulchibamba.margin.data.cover

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import java.io.File

class CoverImageWriter {

    fun write(source: ImageDecoder.Source, target: File) {
        val bitmap = decodeScaled(source)
        try {
            writeWebp(bitmap, target)
        } finally {
            bitmap.recycle()
        }
    }

    private fun decodeScaled(source: ImageDecoder.Source): Bitmap =
        ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            val size = CoverSize.fit(info.size.width, info.size.height)
            decoder.setTargetSize(size.width, size.height)
        }

    private fun writeWebp(bitmap: Bitmap, target: File) {
        target.parentFile?.mkdirs()
        val partial = File(target.parentFile, "${target.name}.part")
        partial.outputStream().use { stream -> bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, QUALITY, stream) }
        check(partial.renameTo(target)) { "Couldn't move the cover into place" }
    }

    private companion object {
        const val QUALITY = 85
    }
}
