package com.paulchibamba.margin.feature.read.cover

import android.content.Context
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

private const val HTTPS = "https"

@Composable
fun CoverSearchWebView(
    url: String,
    onProgress: (Int) -> Unit,
    onImageLongPress: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val latestOnProgress by rememberUpdatedState(onProgress)
    val latestOnImageLongPress by rememberUpdatedState(onImageLongPress)
    val history = remember { BrowserHistory() }
    BackHandler(enabled = history.canGoBack, onBack = history::goBack)
    AndroidView(
        factory = { context ->
            forgetBrowsing()
            val webView = searchWebView(
                context,
                onProgress = { progress -> latestOnProgress(progress) },
                onImageLongPress = { image -> latestOnImageLongPress(image) },
                onHistoryChanged = history::update,
            )
            webView.apply { loadUrl(url) }
        },
        modifier = modifier,
        onRelease = { webView ->
            history.forget()
            closeAndForget(webView)
        },
    )
}

private fun searchWebView(
    context: Context,
    onProgress: (Int) -> Unit,
    onImageLongPress: (String) -> Unit,
    onHistoryChanged: (WebView) -> Unit,
): WebView =
    WebView(context).apply {
        lockDown(settings)
        webViewClient = HttpsOnlyClient(onHistoryChanged)
        webChromeClient = ProgressClient(onProgress)
        setOnLongClickListener { offerImageUnderTouch(onImageLongPress) }
    }

private fun lockDown(settings: WebSettings) {
    settings.javaScriptEnabled = true
    settings.allowFileAccess = false
    settings.allowContentAccess = false
    settings.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
    settings.setSupportMultipleWindows(false)
    settings.javaScriptCanOpenWindowsAutomatically = false
    settings.setGeolocationEnabled(false)
}

private fun WebView.offerImageUnderTouch(onImageLongPress: (String) -> Unit): Boolean {
    val image = imageUrlUnderTouch() ?: return false
    onImageLongPress(image)
    return true
}

private fun WebView.imageUrlUnderTouch(): String? {
    val hit = hitTestResult
    val isImage = hit.type == WebView.HitTestResult.IMAGE_TYPE ||
        hit.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE
    return hit.extra.takeIf { isImage }
}

private fun closeAndForget(webView: WebView) {
    webView.stopLoading()
    webView.clearHistory()
    webView.clearFormData()
    webView.clearCache(true)
    forgetBrowsing()
    webView.destroy()
}

private fun forgetBrowsing() {
    CookieManager.getInstance().removeAllCookies(null)
    CookieManager.getInstance().flush()
    WebStorage.getInstance().deleteAllData()
}

private class BrowserHistory {
    private var webView: WebView? = null
    var canGoBack by mutableStateOf(false)
        private set

    fun update(view: WebView) {
        webView = view
        canGoBack = view.canGoBack()
    }

    fun goBack() {
        webView?.goBack()
    }

    fun forget() {
        webView = null
        canGoBack = false
    }
}

private class HttpsOnlyClient(private val onHistoryChanged: (WebView) -> Unit) : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean =
        !request.url.scheme.equals(HTTPS, ignoreCase = true)

    override fun doUpdateVisitedHistory(view: WebView, url: String?, isReload: Boolean) {
        onHistoryChanged(view)
    }
}

private class ProgressClient(private val onProgress: (Int) -> Unit) : WebChromeClient() {
    override fun onProgressChanged(view: WebView, newProgress: Int) {
        onProgress(newProgress)
    }
}
