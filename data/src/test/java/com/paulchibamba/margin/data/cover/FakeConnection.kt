package com.paulchibamba.margin.data.cover

import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class FakeConnection(
    url: URL,
    private val body: ByteArray = ByteArray(0),
    private val contentType: String? = "image/png",
    private val code: Int = HTTP_OK,
    private val declaredLength: Long = body.size.toLong(),
    private val finalUrl: URL = url,
) : HttpURLConnection(url) {
    var isDisconnected = false
        private set

    override fun connect() = Unit
    override fun disconnect() {
        isDisconnected = true
    }

    override fun usingProxy(): Boolean = false
    override fun getResponseCode(): Int = code
    override fun getContentType(): String? = contentType
    override fun getContentLengthLong(): Long = declaredLength
    override fun getInputStream(): InputStream = body.inputStream()
    override fun getURL(): URL = finalUrl
}
