package com.paulchibamba.margin.feature.read.cover

import android.content.Context
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
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
    AndroidView(
        factory = { context ->
            forgetBrowsing()
            val webView = searchWebView(
                context,
                onProgress = { progress -> latestOnProgress(progress) },
                onImageLongPress = { image -> latestOnImageLongPress(image) },
            )
            webView.apply { loadUrl(url) }
        },
        modifier = modifier,
        onRelease = ::closeAndForget,
    )
}

private fun searchWebView(context: Context, onProgress: (Int) -> Unit, onImageLongPress: (String) -> Unit): WebView =
    WebView(context).apply {
        lockDown(settings)
        webViewClient = HttpsOnlyClient()
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

private class HttpsOnlyClient : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean =
        !request.url.scheme.equals(HTTPS, ignoreCase = true)
}

private class ProgressClient(private val onProgress: (Int) -> Unit) : WebChromeClient() {
    override fun onProgressChanged(view: WebView, newProgress: Int) {
        onProgress(newProgress)
    }
}
