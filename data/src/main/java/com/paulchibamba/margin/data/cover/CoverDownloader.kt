package com.paulchibamba.margin.data.cover

import com.paulchibamba.margin.data.io.readAtMost
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.URISyntaxException
import java.net.URL

class CoverDownloader(
    private val open: (URL) -> HttpURLConnection = { url -> url.openConnection() as HttpURLConnection },
    private val maxBytes: Int = MAX_BYTES,
) {

    fun download(url: String): ByteArray {
        val connection = open(httpsUrlOf(url)).apply { configure() }
        try {
            checkResponse(connection)
            return connection.inputStream.use { body -> body.readAtMost(maxBytes) }
        } finally {
            connection.disconnect()
        }
    }

    private fun httpsUrlOf(url: String): URL {
        val uri = try {
            URI(url)
        } catch (malformed: URISyntaxException) {
            throw IOException("Not a web address", malformed)
        }
        if (!uri.scheme.equals(HTTPS, ignoreCase = true)) throw IOException("Only https images are downloaded")
        return uri.toURL()
    }

    private fun HttpURLConnection.configure() {
        connectTimeout = TIMEOUT_MILLIS
        readTimeout = TIMEOUT_MILLIS
        instanceFollowRedirects = true
        useCaches = false
        setRequestProperty("Accept", "image/*")
    }

    private fun checkResponse(connection: HttpURLConnection) {
        if (connection.responseCode != HttpURLConnection.HTTP_OK) throw IOException("The server didn't send the image")
        if (!connection.url.protocol.equals(HTTPS, ignoreCase = true)) throw IOException("Redirected away from https")
        if (connection.contentType?.startsWith("image/", ignoreCase = true) != true) throw IOException("Not an image")
        if (connection.contentLengthLong > maxBytes) throw IOException("The image is over $maxBytes bytes")
    }

    private companion object {
        const val HTTPS = "https"
        const val MAX_BYTES = 10 * 1024 * 1024
        const val TIMEOUT_MILLIS = 15_000
    }
}
