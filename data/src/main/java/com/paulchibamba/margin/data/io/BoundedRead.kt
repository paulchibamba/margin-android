package com.paulchibamba.margin.data.io

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

private const val BUFFER_BYTES = 64 * 1024

internal fun InputStream.readAtMost(maxBytes: Int): ByteArray {
    val bytes = ByteArrayOutputStream()
    val buffer = ByteArray(BUFFER_BYTES)
    while (true) {
        val count = read(buffer)
        if (count < 0) return bytes.toByteArray()
        bytes.write(buffer, 0, count)
        if (bytes.size() > maxBytes) throw IOException("The download is over $maxBytes bytes")
    }
}
