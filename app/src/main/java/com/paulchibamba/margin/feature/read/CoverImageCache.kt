package com.paulchibamba.margin.feature.read

import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal object CoverImageCache {
    private const val MAX_BYTES = 16 * 1024 * 1024
    private const val BYTES_PER_PIXEL = 4

    private val images = object : LruCache<String, ImageBitmap>(MAX_BYTES) {
        override fun sizeOf(key: String, value: ImageBitmap): Int = value.width * value.height * BYTES_PER_PIXEL
    }

    fun cached(imagePath: String?): ImageBitmap? = imagePath?.let(images::get)

    suspend fun load(imagePath: String): ImageBitmap? = cached(imagePath) ?: decode(imagePath)?.also { image ->
        images.put(imagePath, image)
    }

    private suspend fun decode(imagePath: String): ImageBitmap? =
        withContext(Dispatchers.IO) { BitmapFactory.decodeFile(imagePath)?.asImageBitmap() }
}
