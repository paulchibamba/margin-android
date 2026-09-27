package com.paulchibamba.margin.feature.feed.post

import android.content.res.AssetManager
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val PACK_FOLDER = "pack"

@Composable
fun PackImage(path: String, contentDescription: String?, modifier: Modifier = Modifier) {
    val assets = LocalContext.current.assets
    val image by produceState<ImageBitmap?>(null, path) {
        value = withContext(Dispatchers.IO) { loadPackImage(assets, path) }
    }
    image?.let { bitmap -> Image(bitmap, contentDescription, modifier, contentScale = ContentScale.Fit) }
}

private fun loadPackImage(assets: AssetManager, path: String): ImageBitmap? = runCatching {
    assets.open("$PACK_FOLDER/$path").use(BitmapFactory::decodeStream)?.asImageBitmap()
}.getOrNull()
