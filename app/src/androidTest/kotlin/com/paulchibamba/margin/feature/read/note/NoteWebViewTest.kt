package com.paulchibamba.margin.feature.read.note

import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NoteWebViewTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    @Test
    fun javaScriptAndFileAccessAreOff() {
        onMain {
            val webView = lockedDownWebView(instrumentation.targetContext)

            assertFalse(webView.settings.javaScriptEnabled)
            assertFalse(webView.settings.allowFileAccess)
            assertFalse(webView.settings.allowContentAccess)
            assertTrue(webView.settings.blockNetworkLoads)
        }
    }

    @Test
    fun scriptsInNoteHtmlNeverRun() {
        val loaded = CountDownLatch(1)
        lateinit var webView: WebView
        onMain {
            webView = lockedDownWebView(instrumentation.targetContext)
            webView.webChromeClient = FinishedLoading(loaded)
            webView.loadDataWithBaseURL(NOTE_BASE_URL, SCRIPTED_NOTE, "text/html", "utf-8", null)
        }

        assertTrue(loaded.await(10, TimeUnit.SECONDS))
        onMain { assertEquals("Untouched", webView.title) }
    }

    private fun onMain(block: () -> Unit) = instrumentation.runOnMainSync(block)

    private class FinishedLoading(private val loaded: CountDownLatch) : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            if (newProgress == FULLY_LOADED) loaded.countDown()
        }
    }

    private companion object {
        const val FULLY_LOADED = 100
        const val SCRIPTED_NOTE = "<html><head><title>Untouched</title></head><body><p>Words.</p>" +
            "<script>document.title = 'Script ran';</script></body></html>"
    }
}
