package com.paulchibamba.margin.data.cover

import org.junit.Test
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class CoverDownloaderTest {

    private val imageUrl = "https://covers.example/appsec.png"
    private val image = ByteArray(2_048) { index -> index.toByte() }
    private val opened = mutableListOf<URL>()

    @Test
    fun `an https image is downloaded`() {
        val downloader = downloaderServing { url -> FakeConnection(url, image) }

        assertContentEquals(image, downloader.download(imageUrl))
    }

    @Test
    fun `an http address is refused before any connection opens`() {
        val downloader = downloaderServing { url -> FakeConnection(url, image) }

        assertFailsWith<IOException> { downloader.download("http://covers.example/appsec.png") }
        assertFailsWith<IOException> { downloader.download("data:image/png;base64,iVBORw0KGgo=") }
        assertTrue(opened.isEmpty())
    }

    @Test
    fun `a redirect away from https is refused`() {
        val downloader = downloaderServing { url ->
            FakeConnection(url, image, finalUrl = URL("http://covers.example/appsec.png"))
        }

        assertFailsWith<IOException> { downloader.download(imageUrl) }
    }

    @Test
    fun `a body that is not an image is refused`() {
        val downloader = downloaderServing { url -> FakeConnection(url, "<html>".toByteArray(), "text/html") }

        assertFailsWith<IOException> { downloader.download(imageUrl) }
    }

    @Test
    fun `a missing content type is refused`() {
        val downloader = downloaderServing { url -> FakeConnection(url, image, contentType = null) }

        assertFailsWith<IOException> { downloader.download(imageUrl) }
    }

    @Test
    fun `an error response is refused`() {
        val downloader = downloaderServing { url ->
            FakeConnection(url, image, code = HttpURLConnection.HTTP_NOT_FOUND)
        }

        assertFailsWith<IOException> { downloader.download(imageUrl) }
    }

    @Test
    fun `a body declared over the cap is refused`() {
        val downloader = downloaderServing(maxBytes = 1_024) { url -> FakeConnection(url, image) }

        assertFailsWith<IOException> { downloader.download(imageUrl) }
    }

    @Test
    fun `a body that runs over the cap without a declared length is refused`() {
        var connection: FakeConnection? = null
        val downloader = downloaderServing(maxBytes = 1_024) { url ->
            FakeConnection(url, image, declaredLength = -1).also { connection = it }
        }

        assertFailsWith<IOException> { downloader.download(imageUrl) }
        assertTrue(connection!!.isDisconnected)
    }

    private fun downloaderServing(maxBytes: Int = 10 * 1024 * 1024, serve: (URL) -> FakeConnection) =
        CoverDownloader(open = { url -> opened += url; serve(url) }, maxBytes = maxBytes)
}
