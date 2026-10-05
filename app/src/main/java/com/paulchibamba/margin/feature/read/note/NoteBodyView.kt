package com.paulchibamba.margin.feature.read.note

import android.content.Context
import android.webkit.WebView
import com.paulchibamba.margin.domain.tracking.ScrollPosition

class NoteBodyView(context: Context) : WebView(context) {
    var onScroll: (ScrollPosition) -> Unit = {}
    var onZoomedIn: () -> Unit = {}
    private val unzoomedScale = context.resources.displayMetrics.density
    private var isZoomedIn = false

    private val position: ScrollPosition
        get() = ScrollPosition(scrollY, computeVerticalScrollExtent(), computeVerticalScrollRange())

    override fun onScrollChanged(left: Int, top: Int, oldLeft: Int, oldTop: Int) {
        super.onScrollChanged(left, top, oldLeft, oldTop)
        onScroll(position)
    }

    fun onPageShown() {
        isZoomedIn = false
        post { onScroll(position) }
    }

    fun onScaleChanged(scale: Float) {
        val zoomedIn = scale > unzoomedScale * ZOOMED_IN_FACTOR
        if (zoomedIn && !isZoomedIn) onZoomedIn()
        isZoomedIn = zoomedIn
    }

    private companion object {
        const val ZOOMED_IN_FACTOR = 1.05f
    }
}
