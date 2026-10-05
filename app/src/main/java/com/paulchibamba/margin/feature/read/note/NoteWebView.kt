package com.paulchibamba.margin.feature.read.note

import android.content.Context
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.paulchibamba.margin.designsystem.LocalSurfacePalette
import com.paulchibamba.margin.domain.tracking.ScrollPosition
import kotlin.math.roundToInt

const val NOTE_BASE_URL = "file:///android_asset/pack/"
private const val PERCENT = 100

@Composable
fun NoteWebView(
    html: String,
    modifier: Modifier = Modifier,
    onScroll: (ScrollPosition) -> Unit = {},
    onZoomedIn: () -> Unit = {},
) {
    val background = LocalSurfacePalette.current.card.toArgb()
    val scrolled by rememberUpdatedState(onScroll)
    val zoomedIn by rememberUpdatedState(onZoomedIn)
    AndroidView(
        factory = { context ->
            lockedDownWebView(context).apply {
                this.onScroll = { position -> scrolled(position) }
                this.onZoomedIn = { zoomedIn() }
            }
        },
        modifier = modifier,
        update = { webView ->
            webView.setBackgroundColor(background)
            loadIfChanged(webView, html)
        },
        onRelease = WebView::destroy,
    )
}

fun lockedDownWebView(context: Context): NoteBodyView = NoteBodyView(context).apply {
    settings.javaScriptEnabled = false
    settings.allowFileAccess = false
    settings.allowContentAccess = false
    settings.blockNetworkLoads = true
    settings.setSupportZoom(true)
    settings.builtInZoomControls = true
    settings.displayZoomControls = false
    settings.textZoom = textZoomFor(context.resources.configuration.fontScale)
    webViewClient = StayOnNoteClient()
}

fun textZoomFor(fontScale: Float): Int = (fontScale * PERCENT).roundToInt()

private fun loadIfChanged(webView: WebView, html: String) {
    if (webView.tag == html) return
    webView.tag = html
    webView.loadDataWithBaseURL(NOTE_BASE_URL, html, "text/html", "utf-8", null)
}

private class StayOnNoteClient : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean = true

    override fun onPageFinished(view: WebView, url: String?) {
        (view as? NoteBodyView)?.onPageShown()
    }

    override fun onScaleChanged(view: WebView, oldScale: Float, newScale: Float) {
        (view as? NoteBodyView)?.onScaleChanged(newScale)
    }
}
