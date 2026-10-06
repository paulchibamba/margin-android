package com.paulchibamba.margin.data.llm

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class ScriptedConnection(url: URL, private val reply: Reply) : HttpURLConnection(url) {
    private val sent = ByteArrayOutputStream()
    val headers = mutableMapOf<String, String>()
    val body: String get() = sent.toString(Charsets.UTF_8)

    override fun connect() = Unit
    override fun disconnect() = Unit
    override fun usingProxy(): Boolean = false

    override fun setRequestProperty(key: String, value: String) {
        headers[key] = value
    }

    override fun getOutputStream(): OutputStream = sent

    override fun getResponseCode(): Int = when (reply) {
        is Reply.Status -> reply.code
        is Reply.Throws -> throw reply.error
    }

    override fun getInputStream(): InputStream = (reply as Reply.Status).body.byteInputStream()

    override fun getErrorStream(): InputStream? = (reply as? Reply.Status)?.body?.byteInputStream()

    sealed interface Reply {
        data class Status(val code: Int, val body: String) : Reply
        data class Throws(val error: IOException) : Reply
    }
}
